// +----------------------------------------------------------------------+
// | Bitflux Editor                                                       |
// +----------------------------------------------------------------------+
// | Copyright (c) 2001,2002 Bitflux GmbH                                 |
// +----------------------------------------------------------------------+
// | This software is published under the terms of the Apache Software    |
// | License a copy of which has been included with this distribution in  |
// | the LICENSE file and is available through the web at                 |
// | http://bitflux.ch/editor/license.html                                |
// +----------------------------------------------------------------------+
// | Author: Christian Stocker <chregu@bitflux.ch>                        |
// +----------------------------------------------------------------------+
//
// $Id: bitfluxeditor.js,v 1.1 2002/09/13 20:26:49 michicms Exp $

//document.writeln('<script type="text/javascript" language="javascript" src="./RangePatch.js"></script>');
/*********************************
 * Global Vars                   *
 *********************************/

/**
* This variable holds the xslt processor
*
* To be initialized in BX_init();
*/

var BX_xsltProcessor;

/**
* The XML-Document, which holds the data
*
* This document is first the original XML-Document, but
* it will be transformed later with xmltransformfile
*/

var BX_xml;

/**
* The XSL-Document, which holds the Transformation-"Template"
*
* This document is first the original XSL-Document, but
* it will be transformed later with xsltransformfile
*/

var BX_xsl;

/**
*some temporary stuff..
*/
var BX_config;
var BX_xslTR;
var BX_xmlTR;
var BX_xmlTRBack;

var BX_xslViewSource;

/**
* Where the output of the tranformation will be inserted.
* 
* In the default configuration, this is the element with 
*  id="transformLocation"
*/


var BX_transformLocation =  document.getElementById("transformLocation");
/**
* to be documentated
*/

/**
* count the loaded xmlfiles
*/
var BX_xmldone = 0;
var BX_xml_done = 0;

var BX_clipboard;

var BX_undo_buffer = new Array();
var BX_undo_counter = 0;
var BX_undo_max = 0;
var BX_update_buttons = false;
var BX_opa_node = null;
var BX_opa_node_prop = null;
var BX_notEditable = true;


try {
var BX_parser = new DOMParser();
var BX_ser = new XMLSerializer();
}
catch(e) {}
var BX_URLParams = new Array();
var BX_elements = new Array();
var BX_buttonbar;
var BX_xmltransformfile;
var BX_xsltransformfile;
var BX_xmltransformbackfile;

BX_js_files = new Array();

BX_js_files.push("./bxe/js/bitfluxeditor_core.js");
BX_js_files.push("./bxe/js/bitfluxeditor_load.js");

/****************************************
 * Initialization stuff                 *
 ****************************************/

function BX_load(config_file)
{
	try {
		if (navigator.appName != "Netscape" )
		{
	    	BX_config_setLoadMessage("You need Mozilla or Netscape 7 for this Editor. Get it from <a href='http://mozilla.org'>http://mozilla.org</a>");	
			return false;
		}
		if (navigator.productSub >= 20020826)
		{
			BX_mozilla_version = 1.1;
		}
		else if (navigator.productSub >= 20020523)	{
			BX_mozilla_version = 1.0;
		}
		else {
	    	BX_config_setLoadMessage("You're Mozilla seems to be not recent enough. You need at least Mozilla 1.0 or Netscape 7. Get it from <a href='http://mozilla.org'>http://mozilla.org</a>");
			return false;
		}
	
	    BX_config = document.implementation.createDocument("","",null);
	    BX_config_setLoadMessage("Loading Bitflux Editor files....");

    	BX_config.onload = BX_config_loaded;  // set the callback when we are done loading
	    BX_config.load(config_file);
	}
	catch(e)
	{
		BX_init_alert(e);
	}
}

function BX_config_loaded()
{
	try {
    if (! BX_alert_checkParserError(BX_config))
    {
        BX_config_setLoadMessage("Config file had errors...");

        return false;
    }

    BX_config_createURLParams();

    var head = document.getElementsByTagName("head")[0];
	// first load the core js files
	
	for (var i=0; i < BX_js_files.length; i++)
    {
        var scr = document.createElement("script");
        scr.setAttribute("src",BX_js_files[i]);
        scr.setAttribute("language","JavaScript");
        head.appendChild(scr);
    }

	// then load js files from config.xml
    var scripts = BX_config_getContentMultiple("/config/files/scripts/file");
    for (var i=0; i < scripts.length; i++)
    {
        var scr = document.createElement("script");
        scr.setAttribute("src",scripts[i]);
        scr.setAttribute("language","JavaScript");
        //		scr.setAttribute('defer', 'true');
        // do the init, after the last script has loaded
        if (i == scripts.length - 1)
        {
            scr.setAttribute('onload', 'try{BX_init()} catch(e) { BX_init_alert(e)}');
        }
        head.appendChild(scr);
    }

	// now the css files
    var css = BX_config_getContentMultiple("/config/files/css/file");
    for (var i=0; i < css.length; i++)
    {
        var scr = document.createElement("link");
        scr.setAttribute("href",css[i]);
        scr.setAttribute("rel","stylesheet");
        //		scr.setAttribute('defer', 'true');
        // do the init, after the last script has loaded
        head.appendChild(scr);
    }

	}
	catch(e)
	{
		BX_init_alert(e);
	}

}

function BX_config_createURLParams()
{

    var params = window.location.search.substring(1,window.location.search.length).split("&");
    var i = 0;
    for (var param in params)
    {
        var p = params[param].split("=");
        BX_URLParams[p[0]] = p[1];
    }
}

function BX_config_getContent(xpath)
{
    var result = BX_config.evaluate(xpath, BX_config, null, 0, null);
    node = result.iterateNext();
    return BX_config_translateUrl(node);
}

function BX_config_getContentMultiple(xpath)
{
    var result = BX_config.evaluate(xpath, BX_config, null, 0, null);
    var node;
    var resultArray = new Array();
    var i = 0;
    while (node = result.iterateNext())
    {
        resultArray[i] = BX_config_translateUrl(node);
        i++;
    }
    return resultArray;

}

function BX_config_setLoadMessage(text)
{
    document.getElementsByTagName("body")[0].innerHTML = "<span style='font-family: Arial; padding: 5px; background-color: #ffffff'>"+text+"</span>";
}

function BX_alert_checkParserError(docu)
{

    if(docu.documentElement.nodeName=="parsererror")
    {
        var alerttext = "Parse Error: \n \n";
        alerttext += docu.documentElement.firstChild.data +"\n\n";
        alerttext += "Sourcetext:\n\n";
        alerttext += docu.documentElement.childNodes[1].firstChild.data;

        alert(alerttext);
        return false;
    }
    return true;
}

function BX_config_translateUrl(node)
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
	

    if (node.getAttribute("isParam") == "true")
    {
        url = BX_URLParams[node.firstChild.data];
    }
    else
    {
        url = node.firstChild.data;
    }
    if (node.getAttribute("prefix"))
    {
        url = node.getAttribute("prefix") + url;
    }
    return url;
}

function BX_init_alert(e)
{
	    var mes = "ERROR in initialising Bitflux Editor:\n"+e.message +"\n";
        try
        {
            mes += "In File: " + e.filename +"\n";
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
        
        mes += "Type: " + e.name + "\n";
        mes += "Stack:" + e.stack + "\n";
		BX_config_setLoadMessage(mes.replace(/\n/g,"<br /><br />"));

		alert(mes);
}


