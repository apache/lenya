// $Id: mozileTransportDriver.js 1050 2005-01-20 17:02:49Z chregu $

function mozileTransportDriver (container) {
	this.container = eval(" new mozileTransportDriver_"+container);
}

mozileTransportDriver.prototype.load = function (filename, callback, async) {
	var id = "mozileTransportDriver.load";
	this.callback = this.loadCallback;
	this.userLoadCallback = callback;
	this.filename = filename;
	debug ("load " + filename, { "evalArguments":true});
	return this.container.load(filename, this, async);
}

mozileTransportDriver.prototype.save = function (filename, content, callback) {
	this.callback = this.saveCallback;
	this.userSaveCallback = callback;
	this.filename = filename;
	debug ("save " + filename,  { "evalArguments":true});
	this.container.save(filename, content, this);
}

mozileTransportDriver.prototype.loadCallback = function (reqObj) {
	reqObj.td = this;
	reqObj.filename = this.filename;
	this.document = reqObj.document;
	if (this.userLoadCallback) { 
		this.userLoadCallback(reqObj);
	}
}

mozileTransportDriver.prototype.saveCallback = function (reqObj) {
	reqObj.td = this;
	reqObj.filename = this.filename;

	if (this.userSaveCallback) {
		this.userSaveCallback(reqObj);
	}
}




mozileTransportDriver.prototype.parseResponseXML = function(responseXML, status) {
		var reqObj = new Object();

	// try to find parserror
		var parserErrorNode = responseXML.getElementsByTagNameNS("http://www.mozilla.org/newlayout/xml/parsererror.xml","parsererror")[0];
		if (parserErrorNode)
		{
			alerttext = parserErrorNode.firstChild.data;
			var sourceNode = parserErrorNode.getElementsByTagName("sourcetext")[0];
			if (sourceNode) {
				alerttext += "<pre>\n" + sourceNode.firstChild.data + "\n</pre>";
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

mozileTransportDriver.prototype.parseResponseText = function(responseText, status) {

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

