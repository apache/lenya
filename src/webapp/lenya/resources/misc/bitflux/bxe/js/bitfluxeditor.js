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
// $Id: bitfluxeditor.js,v 1.4 2002/11/15 13:45:41 ah Exp $

/**
 * @file
 * The main file
 *
 * This file has to be called from your html page and includes
 *  everything needed
 */
//document.writeln('<script type="text/javascript" language="javascript" src="./RangePatch.js"></script>');
/*********************************
 * Global Vars                   *
 *********************************/
BX_debugging = true;
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


var BX_transformLocation ;
/**
* to be documentated
*/

/**
* count the loaded xmlfiles
*/
var BX_xml_done = 0;

var BX_clipboard;

var BX_undo_buffer = new Array();
var BX_undo_counter = 0;
var BX_undo_max = 0;
var BX_update_buttons = false;
var BX_opa_node = null;
var BX_opa_node_prop = null;
var BX_notEditable = true;
var BX_no_events = false;


var BX_parser; 
var BX_ser; 

var BX_elements = new Array(); 
var BX_buttonbar;

BX_js_files = new Array();
BX_js_files.push("js/widgets.js");
BX_js_files.push("js/bitfluxeditor_core.js");
BX_js_files.push("js/bitfluxeditor_load.js");
BX_js_files.push("js/bitfluxeditor_popup.js");
BX_js_files.push("js/xmldoc.js");
BX_js_files.push("js/clipboard.js");
BX_js_files.push("js/copy.js");
BX_js_files.push("js/undo.js");
BX_js_files.push("js/td/http.js");

/****************************************
 * Initialization stuff                 *
 ****************************************/

function BX_checkUnsupportedBrowsers() {
    		if (navigator.userAgent.indexOf("Opera") >= 0) {
         
                        alert ("\nBitflux Editor does not work with Opera. You need Mozilla or Netscape 7 for this Editor.");
			document.getElementsByTagName("body")[0].innerHTML = ("Bitflux Editor does currently only work on Mozilla or Netscape 7 for this Editor. Get it from <a href='http://mozilla.org'>http://mozilla.org</a>");
                        return false;
                }
        
                if (navigator.appName != "Netscape" ) {
                        alert("Bitflux Editor does currently only work on Mozilla or Netscape 7 for this Editor. Get it from http://mozilla.org");
						document.getElementsByTagName("body")[0].innerHTML = ("Bitflux Editor does currently only work on Mozilla or Netscape 7 for this Editor. Get it from <a href='http://mozilla.org'>http://mozilla.org</a>");
                        return false;
                }
			var MozillaMajorVersion = navigator.userAgent.match(/Mozilla\/([[0-9a-z\.]*)/)[1];
			if (MozillaMajorVersion < 5) {
				alert( "You're Mozilla/Netscape seems to be not recent enough. You need at least Mozilla 1.0 or Netscape 7. Get it from http://mozilla.org");
			}

	return true;
}


function BX_load(config_file,fromUrl,path) {
		text = "Loading Bitflux Editor files....";

		if (! (BX_checkUnsupportedBrowsers())) {
			return false;
		}
		BX_parser = new DOMParser();
		BX_ser = new XMLSerializer();		
		
		if (!path) {
			BX_root_dir = "./bxe/";
		} else {
			BX_root_dir = path;
		}
 		BX_innerHTML(document.getElementById("bxe_area"),"<br/><img hspace='5' width='314' height='34' src='"+BX_root_dir+"img/bxe_logo.png'/><br/><span style='font-family: Arial; padding: 5px; background-color: #ffffff'>"+text.replace(/\n/g,"<br/><br/>")+"</span>");

	    var head = document.getElementsByTagName("head")[0];
		// first load the core js files
	
		for (var i=0; i < BX_js_files.length; i++) {
    	    var scr = document.createElementNS("http://www.w3.org/1999/xhtml","script");

	        scr.setAttribute("src",BX_root_dir + BX_js_files[i]);
        	scr.setAttribute("language","JavaScript");
	        if (i == BX_js_files.length - 1)
    	    {
        	    scr.setAttribute('onload', 'try{BX_load2("'+config_file+'",'+fromUrl+')} catch(e) { alert(e)}');
       		}			
    	    head.appendChild(scr);
	    }
}

function BX_load2(config_file,fromUrl) {
//stage 2, after all core js files are loaded...
	BXEui = new BXE_widget();

	try {
		BXEui.lm = BXEui.newObject("loadMessage");
	    BXEui.lm.set("Loading Bitflux Editor files....");

		BXE = new BXE_main();	

		//create loader
		BXE_loader = new BXE_TransportDriver();
	
		if (fromUrl) {
			config_file = BXE.urlParams[config_file];
		}
		BX_config = new BXE_XmlDocument();
		BX_config.filename = config_file;
	    BX_config.load(BX_config_loaded);
	}
	catch(e)
	{

		BXEui.newObject("initAlert",e);
	}

}
	
function BX_config_loaded()
{

	try {
    if (! BX_alert_checkParserError(BX_config))
    {
        BXEui.lm.set("Config file had errors...");
        return false;
    }


    var head = document.getElementsByTagName("head")[0];

	// then load js files from config.xml
    var scripts = BX_config_getContentMultiple("/config/files/scripts/file");
    for (var i=0; i < scripts.length; i++)
    {
        var scr = document.createElementNS("http://www.w3.org/1999/xhtml","script");
        scr.setAttribute("src",scripts[i]);
        scr.setAttribute("language","JavaScript");
        //		scr.setAttribute('defer', 'true');
        // do the init, after the last script has loaded
        if (i == scripts.length - 1)
        {
            scr.setAttribute('onload', 'try{BX_init()} catch(e) { BXEui.newObject("initAlert",e)}');
        }
        head.appendChild(scr);
    }

	// now the css files
    var css = BX_config_getContentMultiple("/config/files/css/file");
    for (var i=0; i < css.length; i++)
    {
		if (document.contentType == "text/xml")	{
			scr = document.createProcessingInstruction("xml-stylesheet",'href="'+css[i]+'" type="text/css"');
			document.insertBefore(scr,document.documentElement);

		
		}
		else {
        var scr = document.createElementNS("http://www.w3.org/1999/xhtml","link");
        scr.setAttribute("href",css[i]);
        scr.setAttribute("rel","stylesheet");
        //		scr.setAttribute('defer', 'true');
        // do the init, after the last script has loaded
		
        head.appendChild(scr);
		}
    }

	}
	catch(e)
	{
		BXEui.newObject("initAlert",e);
	}

}

function BX_config_getContent(xpath)
{
    var result = BX_config.doc.evaluate(xpath, BX_config.doc, null, 0, null);
    node = result.iterateNext();
    return BX_config_translateUrl(node);
}

function BX_config_getContentMultiple(xpath)
{
    var result = BX_config.doc.evaluate(xpath, BX_config.doc, null, 0, null);
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

function BX_config_getContentMultipleAssoc(xpath,param)
{
    var result = BX_config.doc.evaluate(xpath, BX_config.doc, null, 0, null);
    var node;
    var resultArray = new Array();
    while (node = result.iterateNext())
    {
	        resultArray[node.getAttribute(param)] = BX_config_translateUrl(node);
    }
    return resultArray;

}

function BX_alert_checkParserError(docu)
{

    if(docu.documentElement && docu.documentElement.nodeName=="parsererror")
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
        url = BXE.urlParams[node.firstChild.data];
    }
    else
    {
        url = node.firstChild.data;
    }
	
	//replace {BX_root_dir} with the corresponding value;
	
	url = url.replace(/\{BX_root_dir\}/,BX_root_dir);
    if (node.getAttribute("prefix"))
    {
        url = node.getAttribute("prefix") + url;
    }
    return url;
}



function BX_debug(object)
{
    var win = window.open("","debug");
	bla = "";
    for (b in object)
    {

        bla += b;
        try {

            bla +=  ": "+object.eval(b) ;
        }
        catch(e)
        {
            bla += ": NOT EVALED";
        };
        bla += "\n";
    }
    win.document.innerHTML = "";

    win.document.writeln("<pre>");
    win.document.writeln(bla);
    win.document.writeln("<hr>");
}


function BX_innerHTML (element,html,append)
{
	try  {
	if (html) {
		html = '<root xmlns="http://www.w3.org/1999/xhtml">'+html+"</root>";
		docfrag = BX_parser.parseFromString(html,"text/xml");
	}
	} catch (e) {
		return false;
	}
	if (!append)
	{
		var len = element.childNodes.length;

		for (var i = 0; i < len ; i++)
		{
			element.removeChild(element.firstChild);
		}
	}
	if (html) {	
		var len = docfrag.documentElement.childNodes.length;
		for (var i = 0; i < len; i++)
		{
			element.appendChild(docfrag.documentElement.firstChild);
		}
	}

}

function BXE_main() {
	
	this.browser = new BXE_browser();
	//create url params
	this.urlParams = Array;
    var params = window.location.search.substring(1,window.location.search.length).split("&");
    var i = 0;
    for (var param in params)
    {
        var p = params[param].split("=");
        this.urlParams[p[0]] = p[1];
    }
}


function BXE_browser() {

	this.isMozilla = false;
	this.isMSIE = false;
	this.isLinux = false;
	this.isMac = false;
	this.isWindows = false;

	this.mozillaVersion = 0;
	this.mozillaRvVersion = 0;
		try {
			this.mozillaRvVersion = navigator.userAgent.match(/rv:([[0-9a-z\.]*)/)[1];
			this.mozillaRvVersionInt = parseFloat(this.mozillaRvVersion);
		}
		catch (e) {
			this.mozillaRvVersion = 0;
		}
		if (navigator.productSub >= 20020910 && this.mozillaRvVersion == "1.2a") { // Mozilla 1.2a 
			this.mozillaVersion = 1.2;
			alert("\nMozilla 1.2a together with the new Type ahead feature, does not work correctly with the Editor.\n The issue is known (Bugzilla #167786) and should be solved in the next days.\n Use Mozilla 1.0 or 1.1 for the time being or turn off Type Ahead Find with:\nuser_pref (\"accessibility.typeaheadfind\", false);\n Nevertheless, you will be sent to the editor in an instant");
		}
		else if (this.mozillaRvVersionInt >= 1.2) {
			this.mozillaVersion = 1.2;
		}

		else if (navigator.productSub >= 20020826 && this.mozillaRvVersionInt  >= 1.0) {
			this.mozillaVersion  = 1.1;
		}
		else if (navigator.productSub >= 20020523)	{
			this.mozillaVersion  = 1.0;
		}
		else {
	    	BXEui.lm.set("\nYou're Mozilla seems to be not recent enough. You need at least Mozilla 1.0 or Netscape 7. Get it from <a href='http://mozilla.org'>http://mozilla.org</a>");
			return false;
		}
	
}

// for whatever reason, jsdoc needs this line

