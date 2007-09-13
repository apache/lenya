
function mozileTransportDriver_webdav() {
	this.p = new DavClient();
}

mozileTransportDriver_webdav.prototype.load = function (filename, td) {
	this.p.request.td = td;
	//backup Massnahme (sometimes td get's lost with the request...)
	bxe_config.td = td;
	this.p.request.onload = this.loadCallback;
	this.p.GET(filename);
}

mozileTransportDriver_webdav.prototype.save = function (filename, content, td) {
	this.p.request.td = td;
	this.p.request.onload = this.saveCallback;
	this.p.PUT(filename, content );
}

mozileTransportDriver_webdav.prototype.loadCallback = function (e) {
	
	var p = e.currentTarget;
	var td = p.td;
	//backup Massnahme (sometimes td get's lost with the request...)
	if (!td) {
		td = bxe_config.td;
	}
	
	var reqObj = new Object();
	// if there's no element called parsererror...
	
	if (!p.responseXML) {
		reqObj = td.container.parseResponseText(p.responseText);
	}
	else if (p.responseXML.getElementsByTagNameNS("http://www.mozilla.org/newlayout/xml/parsererror.xml","parsererror").length == 0) {
		reqObj.document = p.responseXML;
		reqObj.isError = false;
		reqObj.status = 200;
		reqObj.statusText = "OK";
	} else if (p.responseXML) {
		
		//looks like a parse error, try removing script tags inserted by eg. ZoneAlarm
		var parser = new DOMParser();
		
		var xmlstr = p.responseText;
		xmlstr = xmlstr.replace(/<script[^>]+>[^<]*<\/script>/g,"");
		var xmldoc = parser.parseFromString(xmlstr,"text/xml");
		if (xmldoc.documentElement.nodeName != 'parsererror') {
			reqObj.document = xmldoc;
			reqObj.isError = false;
			reqObj.status = 200;
			reqObj.statusText = "OK";
		} else {
		
			reqObj =  td.container.parseResponseXML(p.responseXML);
		}
	}
	else {
		reqObj = td.container.parseResponseText(p.responseText);
	}
	td.loadCallback(reqObj);
}

mozileTransportDriver_webdav.prototype.saveCallback = function (e) {
	var p = e.currentTarget;
	var td = p.td;
	var reqObj = new Object();
	// status code = 204, then it's ok
	if (p.status == 204) {
		reqObj.document = p.responseXML;
		reqObj.isError = false;
		reqObj.status = 200;
		reqObj.statusText = "OK";
	} 
	else if (p.status == 201) {
		reqObj.document = p.responseXML;
		reqObj.isError = false;
		reqObj.status = 201;
		reqObj.statusText = "Created";
	}
	else if (p.responseXML) {
		reqObj = td.container.parseResponseXML(p.responseXML, p.status);
	} else {
		reqObj = td.container.parseResponseText(p.responseText, p.status);
	}
	reqObj.originalStatus = p.status;
	reqObj.originalStatusTest = p.statusText;
	td.saveCallback(reqObj);
}

mozileTransportDriver_webdav.prototype.parseResponseXML = function(responseXML, status) {
		var reqObj = new Object();

	// try to find parserror
		var parserErrorNode = responseXML.getElementsByTagNameNS("http://www.mozilla.org/newlayout/xml/parsererror.xml","parsererror")[0];
		if (parserErrorNode)
		{
			alerttext = parserErrorNode.firstChild.data;
			var sourceNode = parserErrorNode.getElementsByTagName("sourcetext")[0];
			if (sourceNode) {
				alerttext += "\n" + sourceNode.firstChild.data;
			}
			alerttext+= strXML;
		}
		else
		{
			alerttext="Something went wrong:\n\n" + status + "\n\n";
			var objXMLSerializer = new XMLSerializer;
			var strXML = objXMLSerializer.serializeToString(responseXML.documentElement);
			alerttext+= strXML;
		}
		reqObj.isError = true;
		reqObj.statusText = alerttext;
		reqObj.document = responseXML;
		if (status === 0) {
			reqObj.status = 400;
		} else {
			reqObj.status = status;
		}
		return reqObj;
	
}

mozileTransportDriver_webdav.prototype.parseResponseText = function(responseText, status) {

	var reqObj = new Object();

	alerttext="Something went wrong:\n\n";
	alerttext += responseText ;
	reqObj.isError = true;
	reqObj.statusText = alerttext;
	if (status === 0) {
		reqObj.status = 400;
	} else {
		reqObj.status = status;
	}
	return reqObj;
}

