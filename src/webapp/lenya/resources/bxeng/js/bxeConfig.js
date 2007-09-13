// +--------------------------------------------------------------------------+
// | BXE                                                                      |
// +--------------------------------------------------------------------------+
// | Copyright (c) 2003,2004 Bitflux GmbH                                     |
// +--------------------------------------------------------------------------+
// | Licensed under the Apache License, Version 2.0 (the "License");          |
// | you may not use this file except in compliance with the License.         |
// | You may obtain a copy of the License at                                  |
// |     http://www.apache.org/licenses/LICENSE-2.0                           |
// | Unless required by applicable law or agreed to in writing, software      |
// | distributed under the License is distributed on an "AS IS" BASIS,        |
// | WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
// | See the License for the specific language governing permissions and      |
// | limitations under the License.                                           |
// +--------------------------------------------------------------------------+
// | Author: Christian Stocker <chregu@bitflux.ch>                            |
// +--------------------------------------------------------------------------+
//
// $Id: bxeConfig.js 1440 2006-01-03 16:40:18Z chregu $

const OPTION_DEFAULTTABLECLASS = "defaultTableClass";
const MATHMLNS = "http://www.w3.org/1998/Math/MathML";

function  bxeConfig (filename,fromUrl, configArray) {
	
	var td = new mozileTransportDriver("http");
	//td.Docu = this;
	this.parseUrlParams();
	this.configParams = configArray;
	if (fromUrl) {
		filename = this.urlParams[filename];
	}
	bxe_about_box.addText(" (" + filename + ") ...");
	td.bxeConfig = this;
	try {
		debug("td.load " + filename);
		td.load(filename, bxeConfig.parseConfig, false);
	} catch(e) { bxe_catch_alert(e);}
	return true;
	
}

	
bxeConfig.parseConfig = function  (e) {
	
	if (e.isError) {
		alert("Error loading config file \n"+e.statusText);
		return false;
	} 
	if (e.document.documentElement.nodeName != "config") {
		alert("doesn't look like a BXE config file.\n Here's the full output:\n "+ e.document.saveXML(e.document));
		return false;
	}
		
		
	bxe_about_box.addText("Config Loaded");
	var bxe_config = e.td.bxeConfig;
	bxe_config.doc = e.document;
	
	bxe_config.xmlfile = bxe_config.getContent("/config/files/input/file[@name='BX_xmlfile']");
	bxe_config.xmlfile_method = bxe_config.getContent("/config/files/input/file[@name='BX_xmlfile']/@method");
	if (!bxe_config.xmlfile_method) {
		bxe_config.xmlfile_method = "webdav";
	}
	bxe_config.xslfile = bxe_config.getContent("/config/files/input/file[@name='BX_xslfile']");
	bxe_config.xhtmlfile = bxe_config.getContent("/config/files/input/file[@name='BX_xhtmlfile']");
	bxe_config.validationfile = bxe_config.getContent("/config/files/input/file[@name='BX_validationfile']");
	bxe_config.exitdestination = bxe_config.getContent("/config/files/output/file[@name='BX_exitdestination']");
	
	bxe_config.cssfiles = bxe_config.getContentMultiple("/config/files/css/file");
	bxe_config.scriptfiles = bxe_config.getContentMultiple("/config/files/scripts/file");
	
	var ps = bxe_config.getPlugins();
	 
	for (var i=0; i < ps.length; i++) {
		bxe_config.scriptfiles.push("plugins/" + ps[i] + ".js");
	}
	
	var dSIC = bxe_config.doc.evaluate("/config/context[@type='dontShow']/element[not (@blockLevelOnly and @blockLevelOnly ='true')]", bxe_config.doc, null, 0, null); 
	
	bxe_config.dontShowInContext = new Array();
	node = dSIC.iterateNext();
	while (node) {
		bxe_config.dontShowInContext[node.getAttribute("ns")+":"+node.getAttribute("name")] = true;
		node = dSIC.iterateNext();
	}
	
	var dSIC = bxe_config.doc.evaluate("/config/context[@type='dontShow']/element[@blockLevelOnly and @blockLevelOnly ='true']", bxe_config.doc, null, 0, null); 
	
	bxe_config.dontShowInContextBlock = new Array();
	node = dSIC.iterateNext();
	while (node) {
		bxe_config.dontShowInContextBlock[node.getAttribute("ns")+":"+node.getAttribute("name")] = true;
		node = dSIC.iterateNext();
	}
	
	
	
	var dSIC = bxe_config.doc.evaluate("/config/context[@type='dontShow']/attribute", bxe_config.doc, null, 0, null); 
	
	bxe_config.dontShowInAttributeDialog = new Array();
	node = dSIC.iterateNext();
	while (node) {
		bxe_config.dontShowInAttributeDialog[node.getAttribute("name")] = true;
		node = dSIC.iterateNext();
	}
	
	
	var callbackNodes = bxe_config.doc.evaluate("/config/callbacks/element", bxe_config.doc, null, 0, null);
	bxe_config.callbacks = new Array();
	
	node = callbackNodes.iterateNext();

	while (node) {
		var tmpArray = new Array();
		tmpArray["type"] = node.getAttribute("type");
		tmpArray["precheck"] = node.getAttribute("precheck");
		tmpArray["content"] = node.firstChild.data;
		bxe_config.callbacks[node.getAttribute("ns")+":"+node.getAttribute("name")] = tmpArray;
		node = callbackNodes.iterateNext();
	}

	/* check for events */
	bxe_config.events = new Array();
	
	/* default events */
	bxe_config.events["toggleSourceMode"] = "bxe_toggleSourceMode";
	bxe_config.events["toggleTagMode"] = "bxe_toggleTagMode";
	bxe_config.events["toggleNormalMode"] = "bxe_toggleNormalMode";
	bxe_config.events["DocumentSave"] = "__bxeSave";
	bxe_config.events["ToggleTextClass"] = "bxe_toggleTextClass";
	bxe_config.events["appendNode"] = "bxe_appendNode";
	bxe_config.events["appendChildNode"] = "bxe_appendChildNode";
	bxe_config.events["InsertLink"] = "bxe_InsertLink";
	bxe_config.events["DeleteLink"] = "bxe_DeleteLink";
	bxe_config.events["CleanInline"] = "bxe_CleanInline";
	bxe_config.events["InsertTable"] = "bxe_InsertTable";
	bxe_config.events["InsertImage"] = "bxe_InsertObject";
	bxe_config.events["ShowAssetDrawer"] = "bxe_ShowAssetDrawer";
	bxe_config.events["OrderedList"] = "bxe_OrderedList";
	bxe_config.events["UnorderedList"] = "bxe_UnorderedList";
	bxe_config.events["InsertAsset"] = "bxe_InsertAsset";
	bxe_config.events["changeLinesContainer"] = "bxe_changeLinesContainer";
	bxe_config.events["Exit"] = "bxe_exit";
	bxe_config.events["Undo"] = "bxe_history_undo";
	bxe_config.events["Redo"] = "bxe_history_redo";
	bxe_config.events["NodeInsertedBefore"] = "bxe_NodeInsertedBefore";
	bxe_config.events["NodeBeforeDelete"] = "bxe_NodeBeforeDelete";
	bxe_config.events["NodePositionChanged"] = "bxe_NodePositionChanged";
	bxe_config.events["ContextPopup"] = "bxe_ContextPopup";

	var eventNodes = bxe_config.doc.evaluate("/config/events/event", bxe_config.doc, null, 0, null);

	node = eventNodes.iterateNext();
	while (node) {
		bxe_config.events[node.getAttribute("name")] = node.firstChild.data;
		node = eventNodes.iterateNext();
	}

	var configOptions = bxe_config.doc.evaluate("/config/options/option", bxe_config.doc, null, 0, null);
	bxe_config.options = new Array();
	
	bxe_config.options[OPTION_DEFAULTTABLECLASS] = 'ornate';
	node = configOptions.iterateNext();

	while (node) {
		// ignore the attribute namespace
		bxe_config.options[node.getAttribute("name")] = node.firstChild.data;
		node = configOptions.iterateNext();
	}
	config_loaded(bxe_config);
}

bxeConfig.prototype.getButtons = function() {
	
	if (!this.buttons) {
		this.buttons = new Array();
		var node;
		var tmpArray = new Array();
		// get location
		
		var result = this.doc.evaluate("/config/buttons/location", this.doc, null, 0, null);
		node = result.iterateNext();
		if (node) {
			this.buttons["_location"] = node.getAttribute("src");
		}
		// get dimensions
		var result = this.doc.evaluate("/config/buttons/dimension", this.doc, null, 0, null);
		node = result.iterateNext();
		if (!node) {
			alert("no button definitions found in config.xml\nYour config.xml looks like this:\n" + this.doc.saveXML());
		}
		tmpArray.push(node.getAttribute("width"));
		tmpArray.push(node.getAttribute("height"));
		tmpArray.push(node.getAttribute("buttonwidth"));
		tmpArray.push(node.getAttribute("buttonheight"));
		
		this.buttons["Dimension"] = tmpArray;
		
		var result = this.doc.evaluate("/config/buttons/button", this.doc, null, 0, null);
		
		var i = 0;
		var ns;
		while (node = result.iterateNext())
		{
			tmpArray = new Array();
			tmpArray['col'] = node.getAttribute("col");
			tmpArray['row'] = node.getAttribute("row");
			tmpArray['action'] = node.getAttribute("action");
			tmpArray['type'] = node.getAttribute("type");
			ns = node.getAttribute("ns");
			if (ns !== null) {
				tmpArray['ns'] = ns;
			}
			else {
				tmpArray['ns'] = "";
			} 
			if(node.firstChild) {
				tmpArray['data'] = node.firstChild.data;
			}
			this.buttons[node.getAttribute("name")] = tmpArray;
		}
	}
	
	return this.buttons;
	
}

bxeConfig.prototype.getPlugins = function () {
	if (!this.plugins) {
		this.plugins = new Array();
		this.pluginOptions = new Array();
		var head = document.getElementsByTagName("head")[0];
		var result = this.doc.evaluate("/config/plugins/plugin", this.doc, null, 0, null);
		while (node = result.iterateNext()) {
			this.plugins.push(node.getAttribute("name"));
			this.pluginOptions[node.getAttribute("name")] = new Array();
			var options = this.doc.evaluate("option", node, null, 0, null);
			while (onode = options.iterateNext()) {
				var frag = onode.firstChild;
				var fragString = ""; 
				while (frag) {
					fragString += onode.ownerDocument.saveXML(frag);
					frag = frag.nextSibling;
				}
				this.pluginOptions[node.getAttribute("name")][onode.getAttribute("name")]  = fragString;
			}
		}
	}
	return this.plugins;
}

bxeConfig.prototype.getPluginOptions = function(plugin) {
	if (!this.plugins) {
		this.getPlugins();
	}
	return this.pluginOptions[plugin];
}

bxeConfig.prototype.getContentMultiple = function (xpath)
{
    var result = this.doc.evaluate(xpath, this.doc, null, 0, null);
    var node;
    var resultArray = new Array();
    var i = 0;
    while (node = result.iterateNext())
    {
        resultArray[i] = this.translateUrl(node);
        i++;
    }
    return resultArray;

}


bxeConfig.prototype.translateUrl = function (node)
{
    var url;
	
	try {
		if (node.nodeType != 1) { //if nodeType is not a element (==1) return right away}
			return node.value;	
		}
	}
	catch (e) {
		return "";
	}
	
	if (node.getAttribute("isConfigParam") == "true") {
		 url = this.configParams[node.firstChild.data];
	}
    else if (node.getAttribute("isParam") == "true")
    {
        url = this.urlParams[node.firstChild.data];
    }
    else
    {
        url = node.firstChild.data;
    }
	
	//replace {BX_root_dir} with the corresponding value;
	
	//url = url.replace(/\{BX_root_dir\}/,BX_root_dir);
    if (node.getAttribute("prefix"))
    {
        url = node.getAttribute("prefix") + url;
    }
    return url;
}


bxeConfig.prototype.getContent= function (xpath)
{
    var result = this.doc.evaluate(xpath, this.doc, null, 0, null);
    var node = result.iterateNext();

	if (!node) {
		return null;
	} else {
		return this.translateUrl(node);
	}
}


bxeConfig.prototype.parseUrlParams = function () {
	this.urlParams = new Array ();
    var params = window.location.search.substring(1,window.location.search.length).split("&");
    var i = 0;
    for (var param in params)
    {
        var p = params[param].split("=");
		if (typeof p[1] != "undefined") {
			this.urlParams[p[0]] = p[1];
		} 
    }
}
