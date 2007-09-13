
/**
* @file
* Implements the http TransportDriver 
*
*/

function mozileTransportDriver_http () {

	/**
	* XMLHttpRequest Object
	*
	* We use the same XMLHttpRequest in the whole instance
	* @type Object
	*/
	
}

/*
* Loads a file over http get
* @tparam String filename the filename (can be http://... or just a relative path
*/

mozileTransportDriver_http.prototype.load = function(filename, td, async) {
	if (typeof async == "undefined") {
		async = true;
	}
	var docu = document.implementation.createDocument("","",null);
	docu.loader = this.parent;
	docu.td = td;
	bxe_config.td = td;
	docu.addEventListener("load", this.loadCallback, false);
	docu.async = async;
	var reqObj = new Object();
	reqObj.document = docu;
	try {
		docu.load(filename);
	}
	catch (e) {
		reqObj.isError = true;
		reqObj.status = 404;
		var mes = filename + " could not be loaded\n" + e.message + "\n";
		try
		{
			if (e.filename) {
				mes += "In File: " + e.filename +"\n";
			} else {
				mes += "In File: " + e.fileName +"\n";
			}
			
		}
		catch (e)
		{
			mes += "In File: " + e.fileName +"\n";
		}
		try
		{
			mes += "Linenumber: " + e.lineNumber + "\n";
		}
		catch(e) {}
		reqObj.statusText = mes;
		if (td.loadCallback) {
			td.loadCallback(reqObj);
		} else {
			return reqObj;
		}
	}
	if (td.loadCallback) {
		
	} else {
		reqObj.isError = false;
		reqObj.status = 200;
		reqObj.statusText = "OK";
	}
	return reqObj;
}

mozileTransportDriver_http.prototype.loadCallback = function (e) {
	var p = e.currentTarget;
	var td = p.td;
	//backup Massnahme (sometimes td get's lost with the request...)
	if (!td) {
		td = bxe_config.td;
	}
	
	var reqObj = new Object();
	// if there's no element called parsererror...

	if (p.getElementsByTagNameNS("http://www.mozilla.org/newlayout/xml/parsererror.xml","parsererror").length == 0) {
		reqObj.document = p;
		reqObj.isError = false;
		reqObj.status = 200;
		reqObj.statusText = "OK";
	} else  {
		reqObj =  td.container.parseResponseXML(p);
	}
	td.loadCallback(reqObj);
}

mozileTransportDriver_http.prototype.parseResponseXML = function(responseXML, status) {
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


/**
* Save a file over http post. It just posts the whole xml file without variable
* assignment (in PHP you have to use $HTTP_RAW_POST_DATA or php://input for getting the content)
*/

mozileTransportDriver_http.prototype.save = function(filename, content, td)
{
	this.p = new XMLHttpRequest();
	this.p.overrideMimeType("text/xml");
	this.p.onload = this.saveCallback;
	this.p.td = td;
	this.p.open("POST",filename );
	this.p.send(content,true);
}

mozileTransportDriver_http.prototype.saveCallback = function (e) {
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
		reqObj = td.parseResponseXML(p.responseXML, p.status);
	} else {
		reqObj = td.parseResponseText(p.responseText, p.status);
	}
	reqObj.originalStatus = p.status;
	reqObj.originalStatusTest = p.statusText;
	td.saveCallback(reqObj);
}




