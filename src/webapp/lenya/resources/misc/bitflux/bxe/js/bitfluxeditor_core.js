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
// $Id: bitfluxeditor_core.js,v 1.3 2002/10/25 10:12:21 felixcms Exp $

/**
 * @file
 * Implements most of the core functions
 *
 * We have to divide all this stuff into more file, it's still a mess here.
 *
 */

/**
* Initializationfunction.
*
* Starts loading of all documents and initiatizes some 
*  global variables
*/

function BX_init() {

    BX_schema_init();

    BX_xmlTR = new BXE_XsltDocument("/config/files/transform/file[@name='BX_xmltransformfile']");
    BX_xmlTR.includes   = BX_config_getContentMultiple("/config/files/transform/includes/file[@name='BX_xmltransformfile']");
    BX_xmlTR.xsltParams = BX_config_getContentMultipleAssoc("/config/files/transform/parameters/param[@name='BX_xmltransformfile']","xsltParamName");

    BX_xslTR  = new BXE_XmlDocument("/config/files/transform/file[@name='BX_xsltransformfile']");

    BX_xmlTRBack = new BXE_XsltDocument("/config/files/transform/file[@name='BX_xmltransformbackfile']");
    BX_xmlTRBack.includes = BX_config_getContentMultiple("/config/files/transform/includes/file[@name='BX_xmltransformbackfile']");

    BX_xslViewSource = new BXE_XsltDocument("/config/files/transform/file[@name='BX_xslViewSourceFile']");
    /* not implemented yet */
    //    var BX_xslViewSourceFile_method = BX_config_getContent("/config/files/transform/file[@name='BX_xslViewSourceFile']/@method");

    BX_xml = new BXE_XmlDocument("/config/files/input/file[@name='BX_xmlfile']");

    BX_xsl = new BXE_XsltDocument("/config/files/input/file[@name='BX_xslfile']");
    BX_xsl.xsltParams = BX_config_getContentMultipleAssoc("/config/files/transform/parameters/param[@name='BX_xslfile']","xsltParamName");

    BX_posturl = new BXE_XmlDocument("/config/files/output/file[@name='BX_posturl']");

    var node;
    var options = BX_config_getNodes("/config/options/option");
    while (node = options.iterateNext()) {
		// don't parse the element BX_root_dir..
		if (node.getAttribute("name") != "BX_root_dir") {
        if (node.firstChild) {
            var nodeValue = BX_config_translateUrl(node);
            //replace quotes typed in config.xml. can go away later.
            nodeValue = "'" + nodeValue.replace(/^'/,"").replace(/'$/,"") + "'";
            // the == "'0'" operator should go away later as well, it
            //  should make transitioning easier.
            if (nodeValue == "'false'" || nodeValue == "'0'") {
                nodeValue = false;
            } else if (nodeValue ==  "'true'") {
                nodeValue = true;
            }
            eval(node.getAttribute("name") + ' = ' +   nodeValue  );
        } else {
            eval(node.getAttribute("name") + " = ''");
        }
		}
    }
    BX_init_page();

    BX_init_buttonBar();

    BX_infobar = document.getElementById("BX_infobar");
    BX_infotext = document.getElementById("BX_infotext");
    BX_infotext2 = document.getElementById("BX_infotext2");
    BX_infoerror = document.getElementById("BX_infoerror");
    BX_infoerror_timeout = false;

    BX_init_adjustButtonbar();
    window.onresize = BX_init_adjustButtonbar;
    BX_infobar.addEventListener("mouseup", BX_event_buttonbarMouseUp, false);

    BX_xml.load();
    BX_xsl.load();

    BX_xmlTR.load();

    BX_xslTR.load();

    BX_xmlTRBack.load(BX_xmlTRBack_loaded);

    BX_transformLocation =  document.getElementById("transformlocation");
    BX_xsltProcessor = new XSLTProcessor();

    document.getElementsByNameAndAttribute = getElementsByNameAndAttribute;

    BX_xml.doc.getElementById = BX_getElementById;

    //temporary ranges. to be used in some functions
    /* it is used in BX_insertContent(), so instead of creating it
    * everytime we insert content, just do it once here 
    */
    BX_tmp_r1 = document.createRange();
    BX_tmp_r2 = document.createRange();

    BX_popup = document.getElementById("BX_popup");

}

function BX_schema_init() {

    BX_schema = new BXE_XmlDocument("/config/files/input/file[@name='BX_schemafile']");

    BX_schema.load(BX_schema_loaded);

}

function BX_schema_loaded(e) {
    BXE_loader.xmlloaded(e);

    BX_xml_removeWhiteSpaceNodes(BX_schema.documentElement);

    if (BX_schema.doc.documentElement.localName != "schema" ||
            BX_schema.doc.documentElement.namespaceURI != "http://www.w3.org/2001/XMLSchema" ) {
        alert ("Schema file "+BX_schemafile +" seems not to be a valid schema document. Check for example your namespaces.\n localName is " + BX_schema.documentElement.localName + "\n namespaceURI is " + BX_schema.documentElement.namespaceURI);
        return false;
    }

    var nsResolver = BX_schema.doc.createNSResolver(BX_schema.doc.documentElement);

    var result = BX_xml_getChildNodesByXpath("/xs:schema/xs:element","/*[name() = 'xs:schema']/*[name() = 'xs:element']", BX_schema.doc.documentElement,nsResolver);
    while (node = result.iterateNext()) {
        var name = node.getAttribute("name");
        BX_elements[name] = new Array();

        // do appinfo stuff
        var appinfo_result = BX_xml_getChildNodesByXpath("xs:annotation/xs:appinfo/*","*[name() = 'xs:annotation']/*[name() = 'xs:appinfo']/*",node,nsResolver);
        while (appinfo_node = appinfo_result.iterateNext()) {
            if (appinfo_node.namespaceURI == "http://bitfluxeditor.org/schema/1.0") {
                switch(appinfo_node.localName) {
                case "returnelement":
                    BX_elements[name]["returnElement"] = appinfo_node.firstChild.data;
                    break;
                case "name":
                    BX_elements[name]["name"] = appinfo_node.firstChild.data;
                    break;
                case "noaddparas":
                    BX_elements[name]["noAddParas"] = appinfo_node.firstChild.data;
                    break;
                case "originalname":
                    BX_elements[name]["originalName"] = appinfo_node.firstChild.data;
                    break;
                case "requiredattributes":
                    BX_elements[name]["requiredAttributes"] = appinfo_node.firstChild.data;
                    break;
                case "addalso":
                    BX_elements[name]["addAlso"] = appinfo_node.firstChild.data;
                    break;
                case "altmenu":
                    BX_elements[name]["altMenu"] = appinfo_node.firstChild.data;
                    break;
				case "afteremptylineelement":
					BX_elements[name]["afterEmptyLineElement"] = appinfo_node.firstChild.data;
                    break;
				case "afteremptylineparent":
					BX_elements[name]["afterEmptyLineParent"] = appinfo_node.firstChild.data;
                    break;


                case "insertafter":
                    insertafter_result = BX_xml_getChildNodesByTagName("bxe:element",appinfo_node,nsResolver);
                    if ( insertafter_node = insertafter_result.iterateNext()) {
                        var inserttext = insertafter_node.firstChild.data;

                        while (insertafter_node = insertafter_result.iterateNext()) {
                            inserttext += " | " + insertafter_node.firstChild.data;
                        }
                        BX_elements[name]["insertAfter"] = inserttext;
                    }
                }
            }
        }

        var complexType_result = BX_xml_getChildNodesByTagName("xs:complexType",node,nsResolver);
        complexType = complexType_result.iterateNext();
        BX_elements[name]["allowedElements"] = "";
        if (complexType) {
            if (complexType.getAttribute("mixed") == "true") {
                BX_elements[name]["allowedElements"] = "#PCDATA | ";
            }
            var elements_result = BX_xml_getChildNodesByXpath("xs:choice/xs:element","*[name() = 'xs:choice']/*[name() = 'xs:element']",complexType,nsResolver);

            while (element = elements_result.iterateNext()) {
                BX_elements[name]["allowedElements"] +=  element.getAttribute("ref") + " | ";
            }
            BX_elements[name]["allowedElements"] = BX_elements[name]["allowedElements"].replace(/ \| $/,"");
        } else {}

    }
    return;

}

function BX_xml_getChildNodesByTagName(name, node, nsResolver) {
    if(BXE.browser.mozillaVersion >= 1.1) {
        var result = node.ownerDocument.evaluate(name, node, nsResolver, 0, null);

    } else {
        var result = node.ownerDocument.evaluate("*[name() = '"+name+"']", node, null, 0, null);
    }
    return result;
}

function BX_xml_getChildNodesByXpath(xpath11, xpath10 ,node, nsResolver) {
    if(BXE.browser.mozillaVersion >= 1.1) {
        var result = node.ownerDocument.evaluate(xpath11, node, nsResolver, 0, null);
    } else {
        var result = node.ownerDocument.evaluate(xpath10, node, null, 0, null);
    }

    return result;
}



function BX_init_adjustButtonbar() {
    BX_infobar.style.width = window.innerWidth + "px";
    BX_infobar.style.top = window.innerHeight - 30 + "px";
}
function BX_transformDoc() {

    if (BXE_loader.xmldone > 5) {
        var ad = new Date();
        var a = ad.getMinutes()*60  + ad.getSeconds() + (ad.getMilliseconds()/1000);

        window.defaultStatus = "Rendering ...";
        if (!BX_xml.done) {
            var xsltransformed = BX_xml.doc.implementation.createDocument("","",null);
            var xmltransformed = BX_xml.doc.implementation.createDocument("","",null);
            BX_xmlTR.includeXsltParams();
            BX_xsl.includeXsltParams();
            /* end param replacement */
            /* include includes */

            BX_xmlTR.includeXsltIncludes();
            BX_xsltProcessor.transformDocument( BX_xml.doc, BX_xmlTR.doc, xmltransformed, null);

            BX_xsltProcessor.transformDocument( BX_xsl.doc, BX_xslTR.doc, xsltransformed, null);
            BX_xsl.doc = xsltransformed;
            BX_xml.doc = xmltransformed;
            BX_xml.done = 1;
            // free them.. not used anymore
            xsltransformed = null;
            BX_xslTR = null;

            document.addEventListener("keypress",BX_noBackspace,false);
        }
        var out = BX_xml.doc.implementation.createDocument("","",null);
        BX_xsltProcessor.transformDocument( BX_xml.doc, BX_xsl.doc, out, null);

        //				<xsl:attribute name="oncontextmenu">BX_RangeCaptureOnContextMenu(event.target);event.preventDefault();</xsl:attribute>

        var result = out.evaluate("//*[@name='bitfluxspan']",out.documentElement, null, 0, null);
        var node = null;
        try {

            while (node = result.iterateNext()) {
                node.addEventListener("contextmenu",BX_RangeCaptureOnContextMenu,false);
                node.addEventListener("mousedown",BX_focusSpan,false);
            }
            var x = out.documentElement;
            var r = BX_xml.doc.createRange();
            r.selectNodeContents(BX_transformLocation);
            r.extractContents();
            r.insertNode(x);

            var bd = new Date();
            var b = bd.getMinutes()*60 + bd.getSeconds() + (bd.getMilliseconds()/1000);

            window.defaultStatus = "Rendering took "+parseInt((b-a)*1000)/1000+" sec";

            BX_dotFocus = BX_transformLocation;

            BX_addEvents();

            BX_undo_save();

        } catch (e) {
            alert(e);
        }

    } else {
        //shitty error message...
        alert ("not all needed xml-files are loaded, please try it again");
    }



}

/**************************************
 * eventHandler Function              *
 **************************************/


/****************************************
* button stuff                         *
****************************************/

/**
* Options for button system
*/
var optInactive = 1;
var optNonclickable = 2;
var optCallback = 4;
var optTransform = 8;
var optSplitNode = 16;
var optElementSensitive = 32;

/**
* in array BXbuttons, all buttons are registered
*
* The array looks like the following:
* BX_buttons[name]["gif"] = gif;
* BX_buttons[name]["options"] = tagoptions;
*/
var BX_buttons = new Array();

/**
* prints a button on the screen
* not much more to say
*
* @tparam String gif    name of the gif-button, without wt_ and .gif
* @tparam Number width  width of the button
* @tparam Number height height of the button
* @tparam String id      id
* @tparam String title   info title
* @treturn Object button
*/
function BX_printButton (gif,width,height,id,title) {
    var button = document.createElementNS("http://www.w3.org/1999/xhtml","img");
    button.setAttribute("border","0");
    if (id) {
        button.setAttribute("id","but_"+id);
    }
    if (title) {
        button.setAttribute("title",title);
    } else {
        button.setAttribute("title",gif);
    }
    button.setAttribute("height",height);
    button.setAttribute("width",width);
    button.setAttribute("src",BX_root_dir+"/img/wt_"+gif+".gif");
    return button;

}

/**
* Register a button in the button system
*
* This function is used to add a button to the button-system.
* The button system is later used to render the buttons if we
* click on some part of the editable text und therefore need
* to update the button (clicked, not clicked, etc...)
*
* The first option parameter is a bitwise or/and'ed value with the
*  values mentioned above.
* The second option parameter is a asociative array with some more
*  possibilities to provide informetion
*
* @tparam String tag    TagName
* @tparam String gif    name of the gif-button, without wt_ and .gif
* @tparam Number height height of the button
* @tparam Number width  width of the button
* @tparam String id      name of the button
* @tparam String title   info title
* @tparam String tagoptions 
* @tparam String tagoptions2 
* @return void
*/
function BX_registerButton(tag,gif,width,height,title,tagoptions,tagoptions2) {

    if (!tagoptions) {
        tagoptions = 0;
    }

    if (!tagoptions2) {
        tagoptions2 = new Array();
    }

    /* if options is inactive, print the inactive button (ending with _p) */
    if (tagoptions & optInactive) {
        if (tagoptions & optCallback) {
            BX_printButton(gif+"_p",width,height,false, title);
        }
    }
    /* if options is nonclickable, print the inactive button (ending with _p)
    but add it to the buttons-system as well;
    */
    else if (tagoptions & optNonclickable) {

        BX_buttonbar.appendChild(BX_printButton(gif+"_p",width,height,gif,title));
        BX_buttons[tag] = new Array();
        BX_buttons[tag]["gif"] = gif;
        BX_buttons[tag]["options"] = tagoptions;
    } else if (tagoptions & optCallback) {

        var ahref = document.createElementNS("http://www.w3.org/1999/xhtml","a");
        ahref.setAttribute("href","javascript:"+tagoptions2);
        ahref.appendChild(BX_printButton(gif+"_n",width,height,gif,title));
        if (optElementSensitive & tagoptions) {
            BX_buttons[tag] = new Array();
            BX_buttons[tag]["gif"] = gif;
            BX_buttons[tag]["options"] = tagoptions;

        }
        BX_buttonbar.appendChild(ahref);


    }

    /* otherwise it's the default behaviour */
    else {
        /* if we want a normal javascript popup, we use a special function... */
        if (tagoptions2["popUp"] == true) {
            var ahref = document.createElementNS("http://www.w3.org/1999/xhtml","a");
            ahref.setAttribute("href","javascript:BX_surroundTagWithPopup('"+tagoptions2["popUpBefore"]+"','"+tagoptions2["popUpAfter"]+"')");
        } else {
            var ahref = document.createElementNS("http://www.w3.org/1999/xhtml","a");

            if (tagoptions & optSplitNode) {
                ahref.setAttribute("href","javascript:BX_add_tag('"+tag+"',false,true);");
            } else {
                ahref.setAttribute("href","javascript:BX_add_tag('"+tag+"');");

            }
        }
        ahref.appendChild(BX_printButton(gif+"_n",width,height,gif,title));
        BX_buttonbar.appendChild(ahref);
        //document.write("</a>");
        BX_buttons[tag] = new Array();
        BX_buttons[tag]["gif"] = gif;
        BX_buttons[tag]["options"] = tagoptions;
    }
}

/****************************
* Helper Functions          *
*****************************/

function getElementsByNameAndAttribute(name,attribute,id) {
    var nodelist = this.getElementsByName(name);
    var z = 0;
    var matchednodes = new Array();
    for (var i=0; i < nodelist.length; i++) {
        var idAttr = nodelist[i].getAttribute(attribute);
        if (!idAttr)
            continue;
        if (idAttr == id) {
            matchednodes[z] = nodelist[i];
            z++;
        }
    }
    return matchednodes;
}

// mozilla can't use getElementById, since it doesn't know, which one is the id...
// this is a hack for that. (i hope it's not to slow)
function BX_getElementById(id,xml) {

    if ( !(BX_xml_nodelist) ) {

        if (xml) {
            BX_xml_nodelist = xml.getElementsByTagName("*");
        } else {
            BX_xml_nodelist = this.getElementsByTagName("*");
        }
    }

    for (var i=0; i < BX_xml_nodelist.length; i++) {
        var idAttr = BX_xml_nodelist[i].getAttribute("id");
        if (!idAttr)
            continue;
        if (idAttr == id) {
            return BX_xml_nodelist[i];
        }
    }
    return null;
}

function BX_focusSpan (w_div,isNode) {
	if (!isNode) {
	    var w_div= BX_find_bitfluxspanNode(w_div.target);
	}

    if (!(BX_dotFocus) || BX_dotFocus != w_div) {

        w_div.setAttribute("bxe_hasfocus","true");
        BX_dotFocus.removeAttribute("bxe_hasfocus");
        BX_dotFocus=w_div;
    }

}




function BX_addEvents() {

    document.removeEventListener("keypress",BX_onkeyup,false);
    document.addEventListener("keypress",BX_keypress,false);
    document.addEventListener("keyup",BX_onkeyup,false);
    document.getElementById("transformlocation").addEventListener("mouseup", BX_RangeCaptureOnMouseUp, false);
    BX_no_events = false;


}


function BX_keypress(e) {

    BX_selection = window.getSelection();
    try {
        BX_range = BX_selection.getRangeAt(0);
    } catch(e) {
        return false;
    }

    if (e.ctrlKey || e.metaKey) {

        switch(String.fromCharCode(e.charCode)) {
        case "c":
            BX_copy_copy();
            break;
        case "x":
            BX_copy_extract();
            break;
        case "v":
            BX_copy_paste();
            e.preventDefault();
            e.stopPropagation();
            break;
        case "z":
            BX_undo_undo();
            e.preventDefault();
            e.stopPropagation();
            break;
        case "Z":
            BX_undo_redo();
            e.preventDefault();
            e.stopPropagation();
            break;
        case "i":
            BX_add_tag('emphasize');
            e.preventDefault();
            e.stopPropagation();

            break;
        case "b":
            BX_add_tag('bold');
            e.preventDefault();
            e.stopPropagation();

            break;

        }

    } else {

        switch (e.keyCode) {

        case e.DOM_VK_BACK_SPACE: // backspace
            BX_undo_save();
            if (BX_selection.anchorOffset ==  BX_selection.focusOffset) {
                BX_cursor_moveLeft();
            }
            BX_key_delete();

            e.preventDefault();
            e.stopPropagation();
            break;
        case e.DOM_VK_DELETE:
            BX_undo_save();
            BX_key_delete();
            e.preventDefault();
            e.stopPropagation();
            break;
        case e.DOM_VK_HOME:
			if (BX_selection.anchorNode.nodeType == 1) {
				BX_selection.anchorNode.normalize();
			} else {
				BX_selection.anchorNode.parentNode.normalize();
			}

            BX_cursor_moveToStartInNode(BX_selection.anchorNode,false);
            BX_updateButtons();
            e.preventDefault();
            e.stopPropagation();
            break;
        case e.DOM_VK_END:
			if (BX_selection.anchorNode.nodeType == 1) {
				BX_selection.anchorNode.normalize();
			} else {
				BX_selection.anchorNode.parentNode.normalize();
			}
			
            BX_cursor_moveToStartInNode(BX_selection.anchorNode,true);
            BX_updateButtons();
            e.preventDefault();
            e.stopPropagation();
            break;

        case e.DOM_VK_RETURN:
            BX_keypress_enter(e);
            return false;
            break;
        default:

            if (e.which!=0) {
                if (e.charCode == e.DOM_VK_SPACE) {
                    // BX_undo_save();
                }

                BX_insertContent(String.fromCharCode(e.charCode));
                e.preventDefault();
                e.stopPropagation();
                // i'm not sure, if this is needed anymore, but if we don't use it, it's muchos faster
                // we need it, otherwise the cursor while typing disappears...
                // better solution wanted
                BX_selection.collapse(BX_selection.focusNode, BX_selection.focusOffset );


            }
        }


    }

    if (BX_update_buttons) {
        window.setTimeout("BX_updateButtonsDelayed()",10);
    }
}

function BX_onkeyup(e) {

    switch (e.keyCode) {

    case e.DOM_VK_UP:
    case e.DOM_VK_DOWN:
    case e.DOM_VK_LEFT:
    case e.DOM_VK_RIGHT:
    case e.DOM_VK_DELETE:
    case e.DOM_VK_BACK_SPACE:
        if (!BX_no_events) {
            BX_update_buttons = true;
            window.setTimeout("BX_updateButtonsDelayed()",10);
			var _node = window.getSelection().anchorNode;
			_node.target = _node;
			BX_focusSpan(_node);

        }
        e.preventDefault();
        e.stopPropagation();
    }
}
function BX_updateButtonsDelayed() {
	
    if (BX_update_buttons) {
        BX_get_selection();
        BX_updateButtons();
        BX_update_buttons = false;

    }
}

function BX_insertContent(content, doNoCollapse) {
    var StartContainer = BX_range.startContainer;
    var StartPosition = BX_range.startOffset;

    if (typeof(content) == "string") {
		try {
    	    bla = BX_range.deleteContents();
	    } catch(e) {}

        if (content.length == 1) {
            content = document.createTextNode(content);
        } else if (content.indexOf("<") >= 0) {

            content = BX_parser.parseFromString("<?xml version='1.0'?><rooot>"+content+"</rooot>","text/xml");
            content = content.childNodes[0];

            BX_tmp_r1 = document.createRange();

            BX_tmp_r1.selectNodeContents(content);
            content = BX_tmp_r1.extractContents();

        } else {
            content = document.createTextNode(content);
        }
        //		BX_range.createContextualFragment(content);
    } else {
		
		// we have to remove all Ranges before going further
		// otherwise we have strange selection stuff on the screen
		// it is only needed, when we insert nodes, so it's only here
		// (and BX_range.deleteContents is doubled..)
		// hope this resolves bug #6
		BX_selection.removeAllRanges();
		try {
    	    BX_range.deleteContents();
	    } catch(e) {}

	}
	
    var startOffBefore = BX_range.startOffset;

    if (StartContainer.nodeType==StartContainer.TEXT_NODE && content.nodeType==content.TEXT_NODE) {
        StartContainer.insertData(StartPosition, content.nodeValue );
        if (startOffBefore == BX_range.startOffset) {
            BX_range.setEnd(BX_range.endContainer ,BX_range.endOffset +1);
            BX_range.collapse(false);
        }
    } else { // if (StartContainer.nodeType == StartContainer.TEXT_NODE)
    

        var startOffBefore = BX_range.startOffset;
        BX_range.insertNodeBX = InsertNodeAtStartOfRange;
        content.normalize();
        try {
            BX_range.insertNodeBX(content);
        } catch(e) {}
        ;

        if (startOffBefore == BX_range.startOffset) {
            BX_range.setEnd(BX_range.endContainer ,BX_range.endOffset +1);
            //	        BX_range.setEnd(EndContainer ,EndPosition +1);
        }

        if (!doNoCollapse) {
            BX_range.collapse(false);
        }
    }
    BX_selection.addRange(BX_range);

    return content;
}


function BX_key_delete() {

    BX_get_selection();
    BX_selection.removeAllRanges();

    if (BX_range.toString().length == 0  && typeof (BX_range.endContainer.data) != "undefined") {
        var rightOfSelection = BX_range.endContainer.data.substring(BX_range.endOffset,BX_range.endContainer.data.length);
        var leftOfSelection = BX_range.endContainer.data.substring(0,BX_range.endOffset);
        // if there is content left of the selection, replace ending spaces with one space, if nor, delete everything

        if (/[^\t\n\r\s]/.test(leftOfSelection)) {
            var stripWS = rightOfSelection.replace(/^[\t\n\r\s]+/," ");
            /*	maybe this would be a solution for inline handling...
            			if (BX_getComputedStyle(BX_range.endContainer.parentNode,"display") == "inline") {
            					var stripWS = rightOfSelection.replace(/^[\t\n\r\s]+/," ");
            				} else {
            					var stripWS = rightOfSelection.replace(/^[\t\n\r\s]+/,"");
            				}*/
        } else {
            var stripWS = rightOfSelection.replace(/^[\t\n\r\s]+$/,"");
        }

        if (stripWS.length == 0 ) {

            var walker = document.createTreeWalker(document,NodeFilter.SHOW_TEXT,
                                                   {
                                           acceptNode : function(node) {
                                                           if (!(/[^\t\n\r ]/.test(node.nodeValue)))
                                                               return NodeFilter.FILTER_REJECT;
                                                           return NodeFilter.FILTER_ACCEPT;
                                                       }
                                                   }
                                                   ,null);

            walker.currentNode = BX_range.endContainer;
            var nextNode = walker.nextNode();

            if (nextNode) {
                BX_range.selectNodeContents(nextNode);
                var rightOfSelection = nextNode.data;
                var stripWS = rightOfSelection.replace(/^[\t\n\r\s]+$/,"").replace(/^[\t\n\r\s]{2,}/," ");
            }

            //            BX_range.collapse(true);
        }

        BX_range.setEnd(BX_range.endContainer, BX_range.endOffset+( rightOfSelection.length - stripWS.length) + 1);
    }
	//maybe using CDATA.deleteData instead of extractContents
	// see mozilla/dom/public/idl/core/nsIDOMCharacterData.idl

    BX_range.extractContents();
    BX_selection.removeAllRanges();
    //    BX_range.collapse(true);
    BX_selection.addRange(BX_range);
    //        BX_range = BX_selection.getRangeAt( BX_selection.rangeCount-1 ).cloneRange();
}


function BX_cursor_moveLeft () {

    // for some strange reasons, we can't emulate LEFT,RIGHT,UP,DOWN....
    /*   var ev = document.createEvent("KeyEvents");
    ev.initKeyEvent("keypress",true,true,null,false,false,false,false,ev.DOM_VK_LEFT,0);
    document.dispatchEvent(ev);
    */
    BX_selection = window.getSelection();

    var stripWS = BX_selection.anchorNode.data.substring(0,BX_selection.anchorOffset).replace(/^[\t\n\r\s]*$/,"").replace(/[\t\n\r\s]{2,}$/," ");
    BX_selection.collapse(BX_selection.anchorNode,stripWS.length);
    // if we are at the beginning of a node, search nextNode..
    if (BX_selection.anchorOffset == 0 ) {
        var walker = document.createTreeWalker(document,NodeFilter.SHOW_TEXT,
                                               {
                                           acceptNode : function(node) {
                                                       if ((/^[\t\n\r\s]*$/.test(node.nodeValue)))
                                                           return NodeFilter.FILTER_REJECT;
                                                       return NodeFilter.FILTER_ACCEPT;
                                                   }
                                               }
                                               ,null);

        walker.currentNode = BX_selection.anchorNode;
        var nextNode = walker.previousNode();

        if (nextNode) {
            var stripWS = nextNode.data.replace(/^[\t\n\r\s]*$/,"").replace(/[\t\n\r\s]{2,}$/," ");
            BX_selection.collapse(nextNode,stripWS.length-1)
        }
    } else {
        BX_selection.collapse(BX_selection.anchorNode, BX_selection.anchorOffset - 1 );
    }
}


function BX_add_tag(tag, afterNodeId,splitNode) {
    if (BX_notEditable) {
        return;
    }

    BX_get_selection();
    if (! BX_find_bitfluxspanNode(BX_range.startContainer)) {
        return;
    }
    var replaceChildrenByAddAlso_done = false;
    BX_undo_save();

    //check if selection already has a node with the tag we'd like to insert
    // if so, delete this node (but not the content) and return
    if (!afterNodeId) {
        var docFrag = BX_deleteTagsWithName(tag);
        if (docFrag) {
            return;
        }
    }
    var element = BX_xml.doc.createElement(tag);
    if (BX_elements[tag]["requiredAttributes"]) {
        var attributes = BX_elements[tag]["requiredAttributes"].split(" | ");
        for (var i = 0 ; i < attributes.length; i++) {
            element.setAttribute(attributes[i],"#"+attributes[i]);
        }
    }

    element.setAttribute("id",'BX_id_'+BX_id_counter);
    element.id = 'BX_id_'+BX_id_counter;
    element.setAttribute("bxe_internalid",'yes');
    BX_id_counter++;
    if (BX_elements[tag]["originalName"]) {
        element.setAttribute("bxe_originalname",BX_elements[tag]["originalName"]);
    }

    var selectNode= false;
    if (BX_range.toString().length == 0) {
        selectNode = true;
        if (BX_elements[tag]["insertContent"]) {
            var frag  = BX_xml.doc.createTextNode(BX_elements[tag]["insertContent"]);
        } else {
            var frag  = BX_xml.doc.createTextNode("#"+tag);
        }

    } else {
        var frag = BX_range.extractContents();
        // if we want for example replace para by listitem if we add an itemized list
        // then this code does this. replaceChildrenByAddAlso replace all nodes with that
        // nodename with the addAlso nodename
        // I THINK, THIS WILL NOT WORK RIGHT NOW WITH THE NEW SCHEMA CODE. CHECK IT!!!!

        if (BX_elements[tag]["replaceChildrenByAddAlso"]) {
            for(var i = 0; i < frag.childNodes.length; i++) {
                if (frag.childNodes[i].nodeName == BX_elements[tag]["replaceChildrenByAddAlso"]) {
                    replaceChildrenByAddAlso_done = true;
                    var newNode = BX_xml.doc.createElement(BX_elements[tag]["addAlso"]);
                    for (var j = 0; j < frag.childNodes[i].childNodes.length; j++) {
                        newNode.appendChild(frag.childNodes[i].childNodes[j].cloneNode(1));
                    }
                    newNode.setAttribute("id","BX_id_"+BX_id_counter);
                    BX_id_counter++;
                    newNode.setAttribute("bxe_internalid",'yes');

                    frag.childNodes[i].parentNode.replaceChild(newNode,frag.childNodes[i]);

                }

            }
        }
    }
    if (splitNode) {
        BX_splitNode();
    }

    if (BX_elements[tag]["addAlso"] && !replaceChildrenByAddAlso_done) {
        var elements = BX_elements[tag]["addAlso"].split(" | ");
        var alsoElementAll = false;
        for (var i = 0; i < elements.length; i++) {
            var noContent = false;
            if (  elements[i].indexOf("<") >= 0) {
                //only first child is taken,,, fix that

                var alsoElementAll = BX_parser.parseFromString("<?xml version='1.0'?><rooot>"+ elements[i]+"</rooot>","text/xml").documentElement;
                alsoElement = alsoElementAll.firstChild;
                noContent = true;
            } else {
                var alsoElement = BX_xml.doc.createElement(elements[i]);

            }


            if (i > 0 || selectNode) {

                if ( (BX_elements[tag]["insertContent"]) && i == 0 && ! noContent) {
                    frag  = BX_xml.doc.createTextNode(BX_elements[tag]["insertContent"]);
                    alsoElement.appendChild(frag);
                } else if (! noContent) {
                    frag = BX_xml.doc.createTextNode("#"+elements[i]);
                    alsoElement.appendChild(frag);
                }
            } else {
                alsoElement.appendChild(frag);
            }
            alsoElement.setAttribute("id",'BX_id_'+BX_id_counter);
            alsoElement.setAttribute("bxe_internalid",'yes');
            alsoElement.id = 'BX_id_'+BX_id_counter;

            BX_id_counter++;
            element.appendChild(alsoElement);
            if (alsoElementAll) {
                var alsoLength = alsoElementAll.childNodes.length;
                for (var j = 0;  j < alsoLength; j++) {
                    element.appendChild(alsoElementAll.firstChild);
                }
            }

        }

    } else {

        element.appendChild(frag);
    }
    if (afterNodeId) {
        //    	document.getElementById = BX_getElementById;
        var node = document.getElementById(afterNodeId);
        if (!(node)) {
            var node = BX_getElementByIdClean(afterNodeId,document,1);
        }
        var next = node.nextSibling;
        while (next != null && next.nodeName == "#text" ) {
            next = next.nextSibling;
        };
        var parentNode = node.parentNode;

        parentNode.insertBefore(element,next);
        var newNode = element;
        //             BX_debug(newNode);

    } else {
          var newNode = BX_insertContent(element);
    }
    //    BX_updateOtherFields ();

    //    BX_range.selectNodeContents(newNode);

    if (BX_elements[tag]["addAlso"]) {
        newNode = newNode.childNodes[0];
    }

    if (!selectNode) {
        BX_range.collapse(false);
    }

    //    BX_cursor_update();



    if (BX_elements[tag]["doTransform"]) {
        //        BX_transform(selectNode);
    }

    BX_popup.style.top = window.innerHeight + "px";

    BX_popup.style.visibility = "hidden";
    BX_addEvents();
    BX_scrollToCursor(node);

    BX_selection.selectAllChildren(newNode);

    BX_update_buttons = true;

}

function BX_get_selection() {
    try {
        BX_selection = window.getSelection();
        BX_range = BX_selection.getRangeAt(0);
    } catch(e) { }
}

function InsertNodeAtStartOfRange( newNode ) {
    //Range.prototype.insertNode = InsertNodeAtStartOfRange;
    //Mozilla has not implemented this method yet.  This code will simulate the
    //required functionality

    try {
        var test_INVALID_STATE_ERR = this.endContainer;
        var test_INVALID_STATE_ERR = this.startContainer;
    } catch(e) {
        throw new Error("INVALID_STATE_ERR")
    }

    var node=newNode;
    //check for invalid node type
    switch( node.nodeType ) {
    case Node.DOCUMENT_NODE:
    case Node.ATTRIBUTE_NODE:
    case Node.ENTITY_NODE:
    case Node.NOTATION_NODE:
        throw new Error("INVALID_NODE_TYPE_ERR")
        break;
    case Node.DOCUMENT_FRAGMENT_NODE:
        var firstChild = node.firstChild;
    }
    if( this.startContainer.nodeType == Node.CDATA_SECTION_NODE ||
            this.startContainer.nodeType == Node.TEXT_NODE) {
        //The start of the range is within a Text or CDATASection node.  The node
        //must be split so that the new node can be inserted.
        this.startContainer.parentNode.insertBefore(
            node, this.startContainer.splitText( this.startOffset));
    } else if( this.startOffset == this.startContainer.childNodes.length) {
        //find the insertion point in the DOM and insert the node.

        //the insertion point is at the end of the startContainer node so just
        //append the node to the startContainer node.
        this.startContainer.appendChild( node );
    } else {
        //the insertion point is NOT at the end of the startContainer node.  Insert
        //the node before the node referenced by the startOffset property
        this.startContainer.insertBefore(
            node, this.startContainer.childNodes.item(this.startOffset) );
    }
    if( node.nodeType == Node.DOCUMENT_FRAGMENT_NODE)
        node = firstChild;
    try {
        this.setStart( node, 0 );
    } catch(err) {}
}

function BX_getCurrentNodeName(node) {

    if (!node) {
        return false;
    }
    if (node.nodeName =="#text") {
        node = node.parentNode;
    }
    if (node.hasAttributes() && node.getAttribute("nodename")) {
        return node.getAttribute("nodename");
    } else {
        return node.nodeName;
    }
}

function BX_transform(selectNode) {

    BX_get_selection();
	BX_node_clean_bg();
    BX_range.collapse(true);
    var BX_cursor = document.createElementNS("http://www.w3.org/1999/xhtml","span");
    //	BX_cursor.appendChild(document.createTextNode("|"));
    BX_cursor.setAttribute("id","bx_cursor");
    BX_insertContent(BX_cursor);
    if (typeof BX_mixedCaseAttributes != "undefined" && BX_mixedCaseAttributes) {
        BX_xml.doc = BX_getResultXML();
        var xmltransformedback = BX_xml.doc.implementation.createDocument("","",null);
        BX_xsltProcessor.transformDocument( BX_xml.doc, BX_xmlTR.doc, xmltransformedback, null);
        BX_xml.doc = xmltransformedback;
    } else {
        BX_updateXML();
    }
    BX_transformDoc();

    BX_cursor = document.getElementById("bx_cursor");


    if (BX_cursor && BX_cursor.nextSibling) {
        BX_selection.collapse(BX_cursor.nextSibling,0);
    } else {
        try {
            BX_selection.collapse(BX_cursor,0);
        } catch(e) {
            return;
        }
    }
    BX_cursor.parentNode.removeChild(BX_cursor);
    if (selectNode) {
        BX_range.selectNodeContents(BX_range.startContainer);
        BX_range.insertNode(BX_cursor);

    }
}

/********************
* XML Update Stuff  *
*********************/

function BX_updateXML() {
    var allDivsXpath = document.evaluate("//*[@name = 'bitfluxspan']",document.documentElement,null,0,null);
    var allDivs = new Array();
    var node;
    while (node = allDivsXpath.iterateNext()) {
        allDivs.push(node);

    }
    BX_clean_nodelist(BX_xml.doc);
    BX_clean_nodelist(document);

    BX_xml.doc.getElementById = BX_getElementById;
    BX_tmp_r1 = BX_xml.doc.createRange();
    BX_tmp_r2 = document.createRange();
    for (var i = 0; i < allDivs.length; i ++) {
        if (allDivs[i].getAttribute("id")) {

            var myNodeAttr = BX_xml.doc.getElementById(allDivs[i].getAttribute("id"));
            //get all children of this <span> tag and add them to a new element with <formfieldid>
            // later we replace that in the BX_xml.
            if (!(myNodeAttr)) {
                continue;
            }
	     try {myNodeAttr.setAttribute("bxe_bitfluxspan","true");}
		catch (e) {
	     }

            BX_tmp_r1.selectNodeContents(myNodeAttr);

            BX_tmp_r1.extractContents();

            BX_tmp_r2.selectNodeContents(BX_getElementById(allDivs[i].getAttribute("id"),document));
			
            if (BX_tmp_r2.toString().length > 0) {
                BX_tmp_r1.insertNode(BX_tmp_r2.cloneContents());
            }
            /**
            //i'm not sure, for what this exactly was.... it works  with the code above...

            for (j  = 0; j < document.getElementById(allDivs[i].id).childNodes.length; j++)
            {
            temp = document.getElementById(allDivs[i].id).childNodes[j].cloneNode(true);
            // mmmh, works wirh NN6.2 and MOZ098. temp.BX_xml works only in MOZ098
            formvalue += ser.serializeToString(document.getElementById(allDivs[i].id).childNodes[j]).replace(/a0:/gi,"").replace(/BX_xmlns:a0="http:\/\/www.w3.org\/1999\/xhtml"/g,"");
            newElement.appendChild(temp);
            }

            BX_xml.getElementById(allDivs[i].id).replaceChild(newElement,myNodeAttr);
            */
        }
    }
    BX_tmp_r1.detach();
    BX_tmp_r2.detach();

}


function BX_RangeCaptureOnMouseUp(e) {

    try {


        if (e.target && BX_elements[e.target.nodeName] && BX_elements[e.target.nodeName]["altMenu"] && BX_find_bitfluxspanNode(e.target)) {

            eval(BX_elements[e.target.nodeName]["altMenu"]+"(e.target)");
            return;
        }

        BX_get_selection();


        if (e.target.nodeName != "#text" && e.target.nodeName != BX_selection.anchorNode.parentNode.nodeName) {

            BX_selection.collapse( e.target,0);
            BX_range = BX_selection.getRangeAt(0);

        }


		var bx_span_anchor = BX_find_bitfluxspanNode(BX_selection.anchorNode);
		var bx_span_offset  = BX_find_bitfluxspanNode(BX_selection.focusNode);
		if (bx_span_anchor != bx_span_offset) {
			if (bx_span_anchor.compareTreePosition(bx_span_offset) & document.TREE_POSITION_PRECEDING) {
				//preceding
				BX_selection.extend(bx_span_anchor,0);																	
			} else {
				BX_selection.extend(bx_span_anchor,bx_span_anchor.childNodes.length);																	

			}
		}

	    if (e.which == 1) {
            BX_popup.style.top = window.innerHeight + "px";
            BX_popup.style.visibility = "hidden";
        }
    } catch(err) {
        BX_errorMessage(err);
    };
    BX_updateButtons();

}

function BX_updateButtons(again) {
    //    window.defaultStatus = "";
    BX_notEditable = false;

    try {
        var startNode =  BX_range.startContainer;
    } catch(e) {
        document.removeEventListener("keypress",BX_keypress,false);
        document.addEventListener("keypress",BX_onkeyup,false);
        return false;
    }

    var sectionDepth= 0;
    var thisNodeName ;
    var BX_infotext_tags = document.getElementById("BX_infotext_tags");
    BX_node_clean_bg();
    if (startNode.nodeName=="#text") {
        var firstnode = startNode.parentNode;
    } else if (startNode.childNodes[0]) {
        var firstnode = startNode;
        startNode= startNode.childNodes[0];
    } else {
        var firstnode = startNode;
    }
    var firstNodeName = BX_getCurrentNodeName(firstnode);
    var parentNodeName = BX_getCurrentNodeName(firstnode.parentNode);
    //    window.defaultStatus="";

    for(var button in BX_buttons) {

        if (document.getElementById("but_"+BX_buttons[button]["gif"])) {

            BX_buttons[button]["highlight"] = false;
            //make it nonClickable, if the element is not allowed in this element
            // or if the element is a splitnode element, but the parent also does not allow it...
            if (BX_buttons[button]["options"] & optNonclickable  ||
                    (BX_elements[firstNodeName] && BX_elements[firstNodeName]["allowedElements"].indexOf(button) < 0)
                    && !(((BX_buttons[button]["options"] & optSplitNode)  && parentNodeName && BX_elements[parentNodeName] && BX_elements[parentNodeName]["allowedElements"].indexOf(button) >= 0))
               ) {

                document.getElementById("but_"+BX_buttons[button]["gif"]).src=BX_root_dir+"/img/wt_"+BX_buttons[button]["gif"]+"_p.gif";
            } else {
                document.getElementById("but_"+BX_buttons[button]["gif"]).src=BX_root_dir+"/img/wt_"+BX_buttons[button]["gif"]+"_n.gif";
            }
        }
    }
    var infotext = " / ";

    var first = 1;

    while(startNode && startNode.parentNode && startNode.parentNode.nodeName.toLowerCase() != "body" && startNode.parentNode.getAttribute("name") != "bitfluxspan") {

        startNode = startNode.parentNode;

        var startNodeId = startNode.getAttribute("id");
        if (startNodeId == null) {
            BX_node_insertID(startNode);
            var startNodeId = startNode.getAttribute("id");

        }
        if (BX_elements[startNode.nodeName]) {
            infotext =  "<a onmouseout=\"BX_hide_node('"+startNodeId+"')\"  onmouseover=\"BX_show_node('"+startNodeId+"')\" href=\"javascript:BX_popup_node('"+startNodeId+"');\" >" + BX_elements[startNode.nodeName]["name"] + "</a>"+ infotext;
        } else {
            infotext = "<a href=\"javascript:BX_popup_node('"+startNode.getAttribute("id")+"');\" >" + startNode.nodeName + "</a>" + infotext;
        }

        if (first == 1) {
            infotext = "" + infotext;
            first = 0;
        }
        infotext =  " / " + infotext ;
        thisNodeName = startNode.nodeName;
        if (thisNodeName == "section") {
            sectionDepth++;
        }
        if (BX_buttons[thisNodeName]) {
            document.getElementById("but_"+BX_buttons[thisNodeName]["gif"]).src=BX_root_dir+"/img/wt_"+BX_buttons[thisNodeName]["gif"]+"_a.gif";
            BX_buttons[thisNodeName]["highlight"] = true;
            //            window.defaultStatus += thisNodeName + " " + BX_buttons[thisNodeName]["gif"];
        }


    }

	if (!startNode) {
        return false;
    }
    startNode = startNode.parentNode;
    var startNodeId = startNode.getAttribute("id");

    
    if  (startNode && startNode.nodeName.toLowerCase() == "body") {
        infotext = "Not Editable";
        var editnode = BX_cursor_moveToNextEditable(firstnode);

        if (editnode && ! again) {
            return BX_updateButtons(true);
        }
        document.removeEventListener("keypress",BX_keypress,false);
        document.addEventListener("keypress",BX_onkeyup,false);
        BX_notEditable = true;
    } else if (BX_elements[startNode.nodeName]) {
        infotext =  "<a onmouseout=\"BX_hide_node('"+startNodeId+"')\"  onmouseover=\"BX_show_node('"+startNodeId+"')\" href=\"javascript:BX_popup_node_bitfluxspan('"+startNodeId+"');\" >" + BX_elements[startNode.nodeName]["name"] + "</a>"+ infotext;

        //		infotext = "<span class=\"tagBelow\" onmouseout=\"BX_hide_node('"+startNodeId+"')\"  onmouseover=\"BX_show_node('"+startNodeId+"')\">" + BX_elements[startNode.nodeName]["name"] +  "</span> " + infotext;
        document.removeEventListener("keypress",BX_onkeyup,false);
        document.addEventListener("keypress",BX_keypress,false);
    } else {
        infotext =  "<span class=\"tagBelow\" onmouseout=\"BX_hide_node('"+startNodeId+"')\"  onmouseover=\"BX_show_node('"+startNodeId+"')\">" + startNode.nodeName + "</span> " + infotext;
        document.addEventListener("keypress",BX_keypress,false);
    }
    BX_innerHTML(BX_infotext_tags,infotext + "<br/>");
    if (startNode.nodeName.toLowerCase() != "body") {
        BX_infobar_printAttributes(firstnode);
    }
    BX_infobar.style.visibility = "visible";
    BX_clearInfoError();
/*    if (BX_doSections && sectionDepth > 0) {
        document.getElementById("but_ebene_"+sectionDepth).src=BX_root_dir+"/img/wt_ebene_"+sectionDepth+"_a.gif";
    }*/
}

function BX_infobar_printAttributes(node) {
    var infotext_attr = document.getElementById("BX_infotext_attr");
    BX_innerHTML(infotext_attr,"");

    if (node.getAttribute("name") != "bitfluxspan") {
        var element_id = node.getAttribute("id");
        var infotext2 = ""+BX_elements[node.nodeName]+"<a href=\"javascript:BX_infobar_printAttributes(BX_getElementByIdClean('"+node.getAttribute("id")+"',document));BX_down()\">down</a><br/>";
        infotext2 += "<table class=\"usual\">\n";

        for (var i=0; i < node.attributes.length; i++ ) {
            if (node.attributes[i].nodeName != "bxe_originalname" && node.attributes[i].nodeName != "bxe_internalid" && node.attributes[i].nodeName != "id" && node.attributes[i].nodeName != "style"  ) {
                BX_innerHTML(infotext_attr,"<a href=\"javascript:BX_infobar_printAttributes(BX_getElementByIdClean('"+element_id+"',document));BX_up();\">"+ node.attributes[i].nodeName + "=" + node.attributes[i].nodeValue+"</a> ");
                infotext2 += "<tr ><td>" + node.attributes[i].nodeName + ": </td>\n";
                infotext2 += "<td><input onchange=\"BX_getElementByIdClean('"+element_id+"',document).setAttribute('"+node.attributes[i].nodeName+"',this.value); \" onfocus=\"javascript: BX_range= null;\" size=\"40\" value=\""+node.attributes[i].nodeValue+"\" /></td></tr>\n";
            }
        }
        /**
        * adding of new attributes. not needed in most of the cases
        infotext2 += "<tr><td ><input id=\"BX_newattr\">: </td>\n";
                                     infotext2 += "<td><input onchange=\"getElementById'"+element_id+"',BX_xml).setAttribute(document.getElementById('BX_newattr').value,this.value); \" onfocus=\"javascript: BX_range= null;\" size=\"20\" ></td></tr>\n";
                                                 */
        infotext2 += "<tr><td colspan='2'><input type='button' value = ' ok '  onclick=\"BX_infobar_printAttributes(BX_getElementByIdClean('"+node.getAttribute("id")+"',document));BX_down()\"/></td></tr>";
        BX_innerHTML(BX_infotext2,infotext2 + "</table>");

    }

}

function BX_clearInfoError() {
    BX_innerHTML(BX_infoerror,"");
    BX_infoerror_timeout = false;
}

function BX_getResultXML() {
    BX_updateXML();

    var xmltransformedback = document.implementation.createDocument("","",null);
    BX_xsltProcessor.transformDocument( BX_xml.doc, BX_xmlTRBack.doc, xmltransformedback, null);
    return xmltransformedback;
}


function calculateMarkup( node, isXML, inRange ) {
    if( arguments.length < 2 )
        isXML = (node.document + "" == "[object Document]");
    var calcRange = false;
    if( inRange )
        calcRange = true;

    //calculate only those nodes that are within the range;
    if( calcRange && !inRange.intersectsNode(node) )
        return "";

    //this function manually calculates the outerHTML of the supplied node.
    switch(node.nodeType) {
        //the following node types are supported for the recursion nature
        //of this function.
    case Node.CDATA_SECTION_NODE: //CDATASections are not parsed
        var retVal = node.data;
        if(  calcRange && inRange.compareNode(node) != Range.NODE_INSIDE ) {
            if( node != inRange.endContainer && node != inRange.startContainer)
                throw new Error("Range Processing CDATASection Node Markup");
            if( node == inRange.endContainer )
                retVal = retVal.substring( 0, inRange.endOffset );
            if( node == inRange.startContainer )
                retVal = retVal.substring( inRange.startOffset, retVal.length + 1 );
        }
        return "<![CDATA[" + retVal + "]]>";

    case Node.TEXT_NODE:
        var retVal = node.data;
        if(  calcRange && inRange.compareNode(node) != Range.NODE_INSIDE ) {
            if( node != inRange.endContainer && node != inRange.startContainer) {
                alert(inRange.compareNode(node));
                throw new Error("Range Processing Text Node Markup");
            }
            if( node == inRange.endContainer )
                retVal = retVal.substring( 0, inRange.endOffset );
            if( node == inRange.startContainer )
                retVal = retVal.substring( inRange.startOffset, retVal.length + 1 );
        }
        //when Mozilla Bug #15118 is fixed then will have to test for entities
        //and replace them.  At this time just replace the known XML entities.
        return retVal.replace(/\&/g, "&amp;").replace(/</g,
                "&lt;").replace(/>/g, "&gt;");

    case Node.COMMENT_NODE:	//Comment nodes are not parsed
        var retVal = node.data;
        if(  calcRange && inRange.compareNode(node) != Range.NODE_INSIDE ) {
            if( node != inRange.endContainer && node != inRange.startContainer)
                throw new Error("Range Processing Comment Node Markup");
            if( node == inRange.endContainer )
                retVal = retVal.substring( 0, inRange.endOffset );
            if( node == inRange.startContainer )
                retVal = retVal.substring( inRange.startOffset, retVal.length + 1 );
        }
        return "<!--" + retVal + "-->";

        //the following nodes outerHTML is it's innerHTML
    case Node.DOCUMENT_FRAGMENT_NODE:
        var retVal = "";
        var ptr = node.firstChild;
        while( ptr ) {
            retVal += calculateMarkup( ptr , isXML, inRange);
            ptr = ptr.nextSibling;
        }
        return retVal;

        //These nodes corrispond to tags.  Calculate the tags value.
    case Node.ELEMENT_NODE:
        var name = node.nodeName;
        var empty = (node.childNodes.length == 0);
        var attr, attrs = node.attributes;
        var len = attrs.length;
        var retVal= "<" + name;
        //get each of the attributes
        if (node.namespaceURI) {
            if (node.parentNode.namespaceURI != node.namespaceURI && node.namespaceURI != "http://www.w3.org/1999/xhtml") {
                if (node.prefix) {
                    retVal += " xmlns:"+node.prefix+'="' + node.namespaceURI + '"';
                } else {
                    retVal += ' xmlns="' + node.namespaceURI + '"';
                }
            }
        }
        for (var i = 0; i < len; i++) {
            attr = attrs.item(i);
            //if it has not been specified than it assumes it default
            //value and does not need to be included.					if( attr.specified )
            retVal += " " + calculateMarkup( attr, isXML, null );
        }

        if( isXML && empty )
            return retVal + "/>";
        retVal += ">";
        if( !isXML && !node.canHaveChildren )
            return retVal;
        var ptr = node.firstChild;
        while( ptr ) {
            retVal += calculateMarkup( ptr , isXML, inRange);
            ptr = ptr.nextSibling;
        }
        return retVal + "</" + name + ">";
    case Node.PROCESSING_INSTRUCTION_NODE:
        return "<?" + node.target + " " + node.data + "?>";

    case Node.DOCUMENT_TYPE_NODE:
        var retVal = "<!DOCTYPE " + node.nodeName;
        if( node.publicId ) {
            retVal += ' PUBLIC "' + node.publicId + '"';
            if( node.systemId )
                retVal += ' "' + node.systemId + '"';
        } else if( node.systemId )
            retVal += ' SYSTEM "' + node.systemId + '"';
        if( node.internalSubset )
            retVal += " " + node.internalSubset;
        return retVal + ">\n";

    case Node.ENTITY_NODE:		//cannot test this code due to Mozilla Bug #15118
        var retVal = "<!ENTITY " + node.nodeName;
        if( node.publicId ) {
            retVal += ' PUBLIC "' + node.publicId + '"';
            if( node.systemId )
                retVal += ' "' + node.systemId + '"';
            if( node.notationName )
                retVal += " NDATA " + node.notationName;
        } else if( node.systemId ) {
            retVal += ' SYSTEM "' + node.systemId + '"';
            if( node.notationName )
                retVal += " NDATA " + node.notationName;
        } else {
            retVal += '"';
            var ptr = node.firstChild;
            while( ptr ) {
                retVal += calculateMarkup( ptr , isXML, inRange);
                ptr = ptr.nextSibling;
            }
            retVal += '"';
        }
        return retVal + ">";

    case Node.NOTATION_NODE:	//cannot test this code due to Mozilla Bug #15118
        var retVal = "<!NOTATION " + node.nodeName;
        if( node.publicId ) {
            retVal += ' PUBLIC "' + node.publicId + '"';
            if( node.systemId )
                retVal += ' "' + node.systemId + '"';
        } else if( node.systemId )
            retVal += ' SYSTEM "' + node.systemId + '"';
        return retVal + ">";

    case Node.ENTITY_REFERENCE_NODE:
        return "&" + node.nodeName + ";";

    case Node.ATTRIBUTE_NODE:
        if( node.specified )
            return node.nodeName + '="' + node.value + '"';
        return "";

    case Node.DOCUMENT_NODE:
        //there is a bug in XML documents.  The doctype tag is NOT in the document
        //sub-tree, but is in an HTML document.  Detect it and surpress it.
        var retVal = "";
        var foundDocType = false;
        var ptr = node.firstChild;
        while( ptr ) {
            if(ptr.nodeType != Node.DOCUMENT_TYPE_NODE )
                foundDocType = true;
            retVal += calculateMarkup( ptr, isXML, inRange );
            ptr = ptr.nextSibling;
        }
        if( !foundDocType && node.doctype )
            retVal = calculateMarkup(node.doctype, isXML, inRange) + retVal;
        if(isXML && ( !calcRange || (node == inRange.startContainer && inRange.startOffset == 0)))
            retVal = '<?xml version="1.0" encoding="'+ node.characterSet + '"?>\n' + retVal;
        return retVal;
    }
}


function BX_scrollToCursor(node) {

    BX_get_selection();

    if (!node) {
        var anchorNode = BX_selection.anchorNode;
        var anchorOffset = BX_selection.anchorOffset;

        if (BX_selection.isCollapsed)	{
            var focusNode = false;
        } else {
            var focusNode = BX_selection.focusNode;
            var focusOffset = BX_selection.focusOffset;
        }
    } else {
        var anchorNode = node;
        var anchorOffset = 0;
        var focusNode =false;
    }

    //	var BX_cursor = document.createElement("span");
    //	BX_cursor.appendChild(document.createTextNode("|"));
    /*    BX_cursor.setAttribute("id","bx_cursor");
    	
    	BX_insertContent(BX_cursor);
    */
	
    if (anchorNode.nodeType == 3) {

        var cursorNode = anchorNode.parentNode;
    } else {
        var cursorNode = anchorNode;
    }
    try {
        var cursorPos = cursorNode.offsetTop + cursorNode.offsetHeight;
    } catch(e) {
        return;
    }

    if (cursorPos == 0) {
        return ;
    }
    if (cursorPos > (window.innerHeight + window.pageYOffset - 230)) {
        window.scrollTo(0,cursorPos - window.innerHeight + 270);
    }
    /*	else if (cursorPos < window.pageYOffset - 15)
    	{
    		window.scrollTo(0,cursorPos - 80);

    	}*/
    //	BX_cursor.parentNode.removeChild(BX_cursor);
    BX_selection.collapse(anchorNode,anchorOffset);
    if(focusNode && ( anchorNode != focusNode || focusOffset != anchorOffset)) {
        BX_selection.extend(focusNode,focusOffset);
    }
}

function BX_range_surroundContents(element) {

    var frag = BX_range.extractContents();
    element.appendChild(frag);
    BX_range.insertNode(element);
    BX_range.collapse(false);

}

function BX_splitNode(id) {
    if (id) {
        var parent = BX_getElementByIdClean(id,document);
    } else {
        var parent = BX_range.startContainer.parentNode;
    }

    BX_tmp_r1 = BX_range.cloneRange();
    BX_tmp_r1.setEndAfter(parent);
    BX_range.setStartBefore(parent);
    var FirstNode = BX_range.extractContents();
    var ThirdNode = BX_tmp_r1.extractContents();

    parent.parentNode.insertBefore(FirstNode,parent);

    //var SecondNode = parent.parentNode.insertBefore(newReturnElement,parent);
    parent.parentNode.insertBefore(ThirdNode,parent);
    BX_selection.collapse(parent.previousSibling,0);
    BX_node_insertID(parent.previousSibling);
    BX_node_insertID(parent.previousSibling.previousSibling);
    BX_range.selectNode(parent.previousSibling.previousSibling);
    BX_range.collapse(false);
    parent.parentNode.removeChild(parent);

    /*	BX_tmp_r1 = BX_range.cloneRange();
    	BX_tmp_r1.setEndAfter(BX_range.startContainer);
    	BX_range.setStartBefore(BX_range.startContainer);
    	parent.insertBefore(BX_range.extractContents(),BX_tmp_r1.startContainer);
    	BX_range.selectNode(BX_tmp_r1.startContainer);
    	BX_range.collapse(true);
    	BX_tmp_r1.detach();*/
}

function BX_getParentNode(startNode,nodename) {

    var thisNodeName = "";
    while(startNode && startNode.parentNode && startNode.parentNode.nodeName != "body" && startNode.parentNode.getAttribute("name") != "bitfluxspan") {
        startNode = startNode.parentNode;
        thisNodeName = startNode.nodeName;
        var tags = nodename.split(" | ");
        for (var i = 0; i < tags.length; i++) {
            if (thisNodeName == tags[i]) {
                return startNode;
            }
        }
    }
    return false;
}



function BX_node_insertID(node) {

    if (node == "[object NodeList]") {
        node=node.item(0);
    }
    node.setAttribute("id",'BX_id_'+BX_id_counter);
    node.setAttribute("bxe_internalid",'yes');
    BX_id_counter++;
    if (BX_elements[node.nodeName] && BX_elements[node.nodeName]["originalName"]) {
        node.setAttribute("bxe_originalname",BX_elements[node.nodeName]["originalName"]);
    }

}



function BX_node_move_up (id) {
    //	var node = BX_getElementByIdClean(id,document);
    var node = BX_getElementById(id,document);

    var anchorNode = BX_selection.anchorNode;
    var anchorOffset = BX_selection.anchorOffset;

    var next = node.previousSibling;

    BX_popup_hide();
    BX_node_clean_bg()	;

    BX_opa_node=node.getAttribute("id");

   

    while (next != null && (next.nodeName == "#text" || next.childNodes.length == 0)) {
        next = next.previousSibling;
    }
    if (next != null) {
        node.parentNode.insertBefore(node,next);
    }
    //  BX_range_updateToCursor();
    BX_selection.collapse(anchorNode,anchorOffset);
    BX_undo_save();
	 try {
        node.setAttribute("bxe_mark","true")
    } catch(e) {}
    BX_scrollToCursor();
    BX_update_buttons = true;
    BX_range = BX_selection.getRangeAt(0);
   document.addEventListener("keypress",BX_keypress,false);
}

function BX_node_move_down (id) {
    //	var node = BX_getElementByIdClean(id,document);
    var node = document.getElementById(id);

    var anchorNode = BX_selection.anchorNode;
    var anchorOffset = BX_selection.anchorOffset;

    var next = node.nextSibling;
    BX_popup_hide();

    BX_node_clean_bg();


    //	try{node.style.borderWidth="thin";}


    while (next != null && next.nodeName == "#text") {
        next = next.nextSibling;
    }

    if (next != null) {
        node.parentNode.insertBefore(node,next.nextSibling);
    }

    //    BX_range_updateToCursor();
    BX_selection.collapse(anchorNode,anchorOffset);
    BX_undo_save();
    BX_opa_node=node.getAttribute("id");
    try {
        node.setAttribute("bxe_mark","true");
    }
    catch(e) {}

    BX_scrollToCursor();
    BX_update_buttons = true;
    BX_range = BX_selection.getRangeAt(0);
   document.addEventListener("keypress",BX_keypress,false);
}

function BX_node_change(node,newNodeName) {
    var nodeChildren = node.childNodes;

    var newNode = BX_xml.doc.createElement(newNodeName);
    for (i = 0; i < nodeChildren.length; i++) {
        newNode.appendChild(nodeChildren[i].cloneNode(1));
    }

    newNode.setAttribute("id","BX_id_"+BX_id_counter);
    BX_id_counter++;
    newNode.setAttribute("bxe_internalid",'yes');

    node.parentNode.replaceChild(newNode,node);

    //	BX_range_updateToCursor();
    BX_undo_save();
    if (BX_elements[newNodeName]["doTransform"]) {
        BX_transform();
    }
    BX_selection.collapse(newNode,0);
    BX_updateButtons();
    BX_scrollToCursor();

}
function BX_node_changeID(id,newNodeName) {

    var node = BX_getElementByIdClean(id,document);
    BX_node_change(node,newNodeName);

}

// mozilla can't use getElementById, since it doesn't know, which one is the id...
// this is a hack for that. (i hope it's not to slow)
function BX_getElementById(id,xml) {

    if (!xml) {
        xml = this;
    }
    if ( !(xml.BX_xml_nodelist) ) {
        BX_xml_nodelist = xml.getElementsByTagName("*");
    }

    for (var i=0; i < xml.BX_xml_nodelist.length; i++) {
        var idAttr = xml.BX_xml_nodelist[i].getAttribute("id");
        if (!idAttr)
            continue;
        if (idAttr == id) {
            return xml.BX_xml_nodelist[i];
        }
    }
    return null;
}

function BX_clean_nodelist(xml) {
    if (xml) {
        xml.BX_xml_nodelist = xml.getElementsByTagName("*");
    } else {
        this.BX_xml_nodelist = this.getElementsByTagName("*");
    }
}

function BX_getElementByIdClean(id,xml) {

    if (xml) {
        xml.BX_xml_nodelist = xml.getElementsByTagName("*");
    } else {
        this.BX_xml_nodelist = this.getElementsByTagName("*");
        xml = this;
    }


    for (var i=0; i < xml.BX_xml_nodelist.length; i++) {
        var idAttr = xml.BX_xml_nodelist[i].getAttribute("id");
        if (!idAttr)
            continue;
        if (idAttr == id) {
            return xml.BX_xml_nodelist[i];
        }
    }
    return null;
}

function BX_node_clean_bg() {
    if (BX_opa_node != null) {

        var Opa_node = BX_getElementByIdClean(BX_opa_node,document);
		try {
        Opa_node.removeAttribute("bxe_mark");
		}
		catch(e) {};
        BX_opa_node = null;
    }
}
function BX_doDrag(e) {
    // Calculates the difference from the last stored position to
    // the current position.
    //
    BX_popup = document.getElementById("BX_popup");
    var difX=e.clientX-window.lastX;
    var difY=e.clientY-window.lastY;
    // Retrieves the X and Y position of editcanvas.
    // Linux does behave strangely...
    if (	navigator.platform.indexOf("Linux") >= 0) {
        var newX = parseInt(window.BX_popupLeft) +difX/2;
        var newY = parseInt(window.BX_popupTop) +difY/2;
    } else {
        var newX = parseInt(window.BX_popupLeft) +difX;
        var newY = parseInt(window.BX_popupTop) +difY;

    }
    // Sets the new position for the editcanvas div element.
    // Note: the table was created inside editcanvas div element;
    // this way, all editcanvas's child elements are affected by
    // positioning.
    //

    if (newY < 0) {
        newY = 0
           };
    if (newX < 0) {
        newX = 0
           };
    if (newY > window.innerHeight - 10) {
        newY = window.innerHeight - 10;
    }
    if (newX > window.innerWidth - 100) {
        newX = window.innerWidth - 100;
    }

    BX_popup.style.left=newX +"px";
    BX_popup.style.top=newY+"px";

    // Stores the current mouse position as last position.
    //
    //	window.lastX=e.clientX;
    //	window.lastY=e.clientY;

}

//----------------------------------------------------------------------------
// When drag begins, this function is called.
// This event handler was registered in the table constructor function (dd_dynatable).
//
function BX_beginDrag(e) {

    // this try is needed for Mozilla 1.1, which can't acces e.target.parentNode within a textarea
    try {
        if (! ((e.target.parentNode.nodeType == e.target.parentNode.ELEMENT_NODE && e.target.parentNode.getAttribute("dragable") == "yes") || (e.target.nodeType == e.target.ELEMENT_NODE &&   e.target.getAttribute("dragable") == "yes"))) {
            return false;
        }
    } catch(e) {
        return false;
    }

    // Stores the current mouse position
    window.lastX=e.clientX;
    window.lastY=e.clientY;
    window.BX_popupLeft= BX_popup.offsetLeft;
    window.BX_popupTop= BX_popup.offsetTop
                        ;

    // Registering doDrag event handler to receive onmousemove events.
    window.onmousemove=BX_doDrag;

    // Registering endDrag event handler to receive onmouseup events.
    window.onmouseup=BX_endDrag;
}

//----------------------------------------------------------------------------
// Called when the mouse button is released.
// This event handler was registered in beginDrag function.
//
function BX_endDrag(e) {

    // Release doDrag event handler assignment.
    window.onmousemove=null;

}

function BX_up() {

    BX_removeEvents();
    BX_infotext.style.visibility = "hidden";
    BX_infotext2.style.visibility = "visible";
    BX_infobar.style.top = window.innerHeight - 200 +"px";
    BX_infobar.style.height = "200px" ;
    BX_range= null;
}

function BX_down() {

    BX_infotext2.style.visibility = "hidden";
    BX_infotext.style.visibility = "visible";
    BX_infobar.style.top = window.innerHeight - 30 +"px";
    BX_infobar.style.height = "30px";
    BX_addEvents();
}

function BX_popup_link() {

    BX_popup_start("Add Link",0,0);
    BX_popup_addLine("External Link","javascript:BX_popup_addTagWithAttributes('ulink','url','http://');");
    BX_popup_addLine("Email","javascript:BX_add_tag('email'),BX_popup_hide();");
    BX_popup_show();

}

function BX_removeEvents() {
    document.removeEventListener("keypress",BX_keypress,false);
    document.addEventListener("keypress",BX_onkeyup,false);

    //    document.removeEventListener("keyup",BX_onkeyup,false);

    //    var allSpans = document.getElementsByName("bitfluxspan");
    /*    for (i = 0; i < allSpans.length; i ++)
        {
            allSpans[i].removeEventListener("mouseup", BX_RangeCaptureOnMouseUp, false );

        }
    */
    BX_transformLocation.removeEventListener("mouseup", BX_RangeCaptureOnMouseUp, false);
}


function BX_popup_addTagWithAttributes(tag,attributes,defaults) {
    BX_popup_hide();
    BX_popup_start( tag,0,0);
    attributes = attributes.replace(/\s+/g,"").split("|");
    defaults = defaults.replace(/\s+\|/g,"|").replace(/\|\s+/g,"|").split("|");

    var html = "<form name='addtag'>";
    html += "<table class=\"usual\">\n";
    html += "<tr ><td>Content: </td>\n";
    if (BX_range.toString().length > 0) {
        html += "<td><input type='text' name='content' value='"+BX_range.toString()+"' /></td></tr>";
    } else {
        html += "<td><input type='text' name='content' value='#"+tag+"#' /></td></tr>";
    }
    for (var i = 0; i < attributes.length; i++) {
        html += "<tr ><td>" + attributes[i] + ": </td>\n";
        html += "<td><input type='text' name='"+attributes[i]+"' value='"+defaults[i]+"' /></td></tr>";
    }

    html += "<tr><td></td><td><input onclick='BX_popup_insertTagWithAttributes(\""+tag+"\");' type='button' value='insert' /></td></tr>";
    html += "</table>";
    html +="</form>";
    BX_popup_addHtml(html);

    document.removeEventListener("keypress",BX_keypress,false);
    document.removeEventListener("keyup",BX_onkeyup,false);


    BX_popup_show();
    BX_popup.style.top=BX_popup.offsetTop - 1 + "px";

}


function BX_popup_insertTagWithAttributes(tag) {
    //    window.defaultStatus="";
    if (BX_popup.style.visibility != 'visible') {
        return;
        document.addEventListener("keypress",BX_keypress,false);
        document.addEventListener("keyup",BX_onkeyup,false);

    }
    var attributes = new Array();

    for (var i = 0; i < document.forms.addtag.length; i++) {
        if (document.forms.addtag[i].name == "content") {
            var content = document.forms.addtag[i].value;
        } else if (document.forms.addtag[i].name) {
            attributes[document.forms.addtag[i].name] = document.forms.addtag[i].value;
        }
    }
    BX_add_tagWithAttributes(tag,content,attributes);
    BX_popup_hide();
    /*	BX_popup.innerHTML="<span></span>";*/
    document.addEventListener("keypress",BX_keypress,false);
    document.addEventListener("keyup",BX_onkeyup,false);

}

function BX_add_tagWithAttributes(tag,content,attributes) {
    var element = BX_xml.doc.createElement(tag);
    element.appendChild(BX_xml.doc.createTextNode(content));

    for (var attName in attributes) {
        element.setAttribute(attName,attributes[attName]);
    }
    element.setAttribute("id","BX_id_"+BX_id_counter);
    element.setAttribute("bxe_internalid",'yes');
    BX_id_counter++;

    BX_insertContent(element);
    BX_scrollToCursor(element);
    BX_selection.selectAllChildren(element);
    BX_update_buttons = true;

}

function BX_source_edit(id, selectNodeContents) {

    if (BX_range) {

        if (!BX_xslViewSource.doc) {

            if (!BX_xslViewSource.doc) {
                BX_xslViewSource.load(null);

            }
            //        	BX_xslViewSource.onload = null;  // set the callback when we are done loading

			window.setTimeout("BX_source_edit('"+id+"',"+selectNodeContents+")",50);
			
            return;
        }

        //		var edit_element = document.getElementById(id).cloneNode(true);
        var edit_element = BX_xml.doc.implementation.createDocument("","",null);
        edit_element.appendChild(BX_getElementById(id,document).cloneNode(true));
        if (edit_element) {
            var _new = BX_xml.doc.implementation.createDocument("","",null);
            BX_xsltProcessor.transformDocument( edit_element, BX_xslViewSource.doc, _new, null);
        } else {
            BX_popup_hide();
            return;
        }

        //	BX_clipboard = document.getElementById(id).cloneNode(1);
        //	document.getElementById(id).parentNode.removeChild(document.getElementById(id));
        //	BX_selection.selectAllChildren(document.getElementById(id));
        BX_popup_start("Edit Source ",500,0);

        var html = ' <center class="text"><form id="bx_form_clipboard" name="clipboard">';
        html += '<input class="buttonklein" type="button" value="Append CDATA" onclick=\'BX_clipboard_insertCDATA()\' />&#160;<br/>';

        html += '<textarea name="text" class="clipboardtext" style="margin: 10px;" wrap="virtual" cols="70" rows="28"></textarea>';
        html += '<br/><input class="text" type="button" value="update" onclick=\'BX_source_insert("'+id+'",'+selectNodeContents+')\' />';
        html += '</form></center>';
        BX_popup_addHtml(html);
        if(_new.documentElement) {
            if (selectNodeContents ) {
                childLength = _new.documentElement.childNodes.length
                              for (var i = 0; i < childLength; i++) {
                                  document.getElementById("bx_form_clipboard" ).text.value += calculateMarkup(_new.documentElement.childNodes[i],true);
                              }
                          } else {
                              document.getElementById("bx_form_clipboard" ).text.value += calculateMarkup(_new.documentElement,true);
                          }
                      }
                      BX_no_events = true;
        document.removeEventListener("keypress",BX_keypress,false);
        document.removeEventListener("keyup",BX_onkeyup,false);
        document.getElementById("bx_form_clipboard" ).text.focus();
        BX_popup_show();
        //fix for mozilla on mac and windows...
    }
}

function BX_source_insert(id,selectNodeContents) {
    var oldnode = BX_getElementById(id,document);
    BX_clipboard_copyToBX_clipboard();
    if (selectNodeContents) {
        var childLength = oldnode.childNodes.length;
        for(var i = 0; i < childLength; i++) {
            oldnode.removeChild(oldnode.childNodes[0]);
        }
        oldnode.appendChild(BX_clipboard);
    } else {
        oldnode.parentNode.replaceChild(BX_clipboard,oldnode);
    }

    BX_transform();

}




function BX_plain() {
    BX_get_selection();
    BX_undo_save();
    var tags = new Array("bold","emphasis","emphasize","subscript","superscript","ulink");
    var docFrag = BX_deleteTagsWithName(tags);

    return;
}

function BX_RangeCaptureOnContextMenu(event) {

    var target = event.target;

    try {


        if (typeof BX_range == "undefined") {
            BX_range = document.createRange( );
            BX_range.selectNode(target);
            BX_range.collapse(false);
        }
        if (BX_elements[target.nodeName] && BX_elements[target.nodeName]["altMenu"] && BX_find_bitfluxspanNode(target)) {

            eval(BX_elements[target.nodeName]["altMenu"]+"(target,event)");
			event.stopPropagation();
			event.preventDefault();
            return;
        }


        target = BX_range.startContainer;
        if (target.nodeName == "#text") {
            target = target.parentNode;
        }
        if (target.getAttribute("nodename")) {
            var current_nodename = target.getAttribute("nodename");
        } else {
            var current_nodename = target.nodeName;
        }
		var target_id = target.getAttribute("id");

        if (BX_elements[current_nodename]) {
			var target_fullname = BX_elements[current_nodename]["name"];
            BX_popup_start(target_fullname,0,0);
            if (BX_elements[current_nodename]["allowedElements"]) {

                var elements = BX_elements[current_nodename]["allowedElements"].split(" | ");
                for (var i = 0; i < elements.length; i++) {

                    if (elements[i] != "#PCDATA") {
                        if (BX_elements[elements[i]]) {
                            BX_popup_addLine(BX_elements[elements[i]]["name"],"javascript:BX_add_tag('"+elements[i]+"');");
                        }
                    }
                }

            }

            var targetEnd = BX_range.endContainer;


            if (targetEnd.nodeName == "#text") {
                targetEnd = targetEnd.parentNode;
            }


            if (current_nodename == targetEnd.nodeName && target != targetEnd && target.parentNode == targetEnd.parentNode) {
                BX_popup_addHr();
                BX_popup_addLine("Merge " + BX_elements[current_nodename]["name"],"javascript:BX_merge();BX_popup_hide();");
            }
			BX_popup_addHr();

            if (target.getAttribute("name") != "bitfluxspan") {

                do {
                    if (target.getAttribute("nodename")) {
                        var current_nodename = target.getAttribute("nodename");
                    } else {
                        var current_nodename = target.nodeName;
                    }

                    if (BX_elements[current_nodename] && BX_elements[current_nodename]["insertAfter"]) {
                        var elements = BX_elements[current_nodename]["insertAfter"].split(" | ");
                        for (var i = 0; i < elements.length; i++) {

                            if (BX_elements[elements[i]]) {
                                if (BX_elements[target.nodeName] && BX_elements[target.nodeName]["name"]) {
                                    var targetName =  BX_elements[target.nodeName]["name"];
                                } else {
                                    var targetName = target.nodeName;
                                }
                                var targetId = target.getAttribute("id");
                                BX_popup_addLine("Insert " +BX_elements[elements[i]]["name"] + " after " + targetName + "&#160;" ,"javascript:BX_add_tag('"+elements[i]+"','"+targetId+"') ", "onmouseover=\"BX_show_node('" +targetId+ "')\" onmouseout=\"BX_hide_node('" +targetId+ "')\"");
                            }
                        }

                    }
                    target = target.parentNode;

                } while ( target.getAttribute("name")!= "bitfluxspan");
			BX_popup_addHr();
            }

		    BX_popup_addLine("Copy","javascript:BX_copy_copy();BX_popup_hide()");

		    BX_popup_addLine("Cut/Delete","javascript:BX_copy_extract();BX_popup_hide()");
							
		    if (BX_clipboard)
    		{
    			BX_popup_addLine("Paste","javascript:BX_copy_paste();BX_popup_hide()");

				if (BX_clipboard.nodeType == 1 || ( BX_clipboard.firstChild && BX_clipboard.firstChild.nodeType == 1)) {
		    		BX_popup_addLine("Paste after " +target_fullname ,"javascript:BX_copy_pasteID('"+target_id+"');BX_popup_hide()");
				}
    		}

        } else {
            BX_popup_start("Element " + current_nodename + " not defined",0,0);
        }



        BX_popup_show();
    } catch(err) {

        BX_errorMessage(err);
    };
    BX_updateButtons();
    event.preventDefault();

}

function BX_errorMessage(e) {
    if (BX_debugMessage) {
        var mes = "ERROR:\n"+e.message +"\n";
        try {
            mes += "In File: " + e.filename +"\n";
        } catch (e) {
            mes += "In File: " + e.fileName +"\n";
        }
        try {
            mes += "Linenumber: " + e.lineNumber + "\n";
        } catch(e) {}
        try {
            mes += "BX_range.startContainer: " + BX_range.startContainer.nodeName + "\n";
        } catch(e) {}

        mes += "Type: " + e.name + "\n";
        mes += "Stack:" + e.stack + "\n";
        var confirm = "\nDo you want to open it in a window (for copy&paste) ?\n (press Cancel if No)";
        if (window.confirm(mes + confirm)) {

            var BX_error_window = window.open("","_blank","");
            BX_innerHTML(BX_error_window.document,"");
            BX_error_window.document.writeln("<pre>");
            mes += "UserAgent: "+navigator.userAgent +"\n";
            mes += "bitfluxeditor.js Info: $Revision: 1.3 $  $Name:  $  $Date: 2002/10/25 10:12:21 $ \n";
            BX_error_window.document.writeln(mes);
            mes = "\nError Object:\n\n";
            for (var b in e) {

                mes += b;
                try {

                    mes +=  ": "+e.eval(b) ;
                } catch(e) {
                    bla += ": NOT EVALED";
                };

                mes += "\n";
            }

            BX_error_window.document.writeln(mes);



        }
    } else {

        BX_innerHTML(BX_infoerror,"ERROR:\n"+e.message +"\n");
        if (BX_infoerror_timeout) {
			try {
            	BX_infoerror_timeout.clearTimeout();
			} catch(e) {};
        }
        BX_infoerror_timeout = window.setTimeout("BX_clearInfoError()",10000);
    }
}


function BX_find_bitfluxspanNode(node ) {

    while(node && node.nodeName == "#text" || (node.nodeName != "body" && node.getAttribute("name") != "bitfluxspan")) {
        node = node.parentNode;
    }
    if (node.getAttribute("name") == "bitfluxspan") {
        return node;
    } else {
        return false;
    }


}

function BX_show_node(id) {
    var node = BX_getElementByIdClean(id,document);

    node.setAttribute("bxe_mark","true");

    //	try{node.style.borderWidth="thin";}
    //	catch(e){};
}


function BX_hide_node(id) {
    var node = BX_getElementByIdClean(id,document);
    node.removeAttribute("bxe_mark");
    //	node.setAttribute("bxe_mark",null);
}

function BX_show_xml(node) {
    alert(calculateMarkup(node,true));
}


function BX_init_buttonBar() {
    BX_buttonbar = document.getElementById("BX_buttonbar");
    BX_buttonbar.style.width = window.innerWidth + "px";
    //	document.getElementsByTagName("body")[0].style.height = window.innerWidth + "px";
    var result = BX_config.doc.evaluate("/config/buttons//*", BX_config.doc, null, 0, null);
    var node;
    var resultArray = new Array();
    var i = 0;
    BX_buttonbar = BX_buttonbar.appendChild(document.createElementNS("http://www.w3.org/1999/xhtml","table"));
    BX_buttonbar.setAttribute("cellpadding",0);
    BX_buttonbar.setAttribute("cellspacing",0);
    BX_buttonbar.setAttribute("border",0);

    while (node = result.iterateNext()) {
        i++;
        if (node.nodeName == "button") {
            if (node.getAttribute("type") == "register") {
                BX_registerButton(node.getAttribute("tag"),node.getAttribute("name"),node.getAttribute("width"),node.getAttribute("height"),node.getAttribute("title"),eval(node.getAttribute("options")),node.getAttribute("callback"));
            } else if (node.getAttribute("type") == "graph") {
                BX_buttonbar.appendChild(BX_printButton(node.getAttribute("name"),node.getAttribute("width")));
            } else if (node.getAttribute("type") == "link") {
                BX_buttonbar.appendChild(document.createTextNode(" "));
                var ahref = document.createElementNS("http://www.w3.org/1999/xhtml","a");
                ahref.setAttribute("href",node.getAttribute("href"));

                ahref.appendChild(document.createTextNode(node.getAttribute("text")));
                BX_buttonbar.appendChild(ahref);
            }
        } else if (node.nodeName == "row") {
            if (BX_buttonbar.nodeName.toLowerCase() == "table") {
                BX_buttonbar = BX_buttonbar.appendChild(document.createElementNS("http://www.w3.org/1999/xhtml","tr"));
            } else //it's a td
            {
                BX_buttonbar = BX_buttonbar.parentNode.parentNode.appendChild(document.createElementNS("http://www.w3.org/1999/xhtml","tr"));
            }

        } else if (node.nodeName == "cell") {

            if (BX_buttonbar.nodeName.toLowerCase() == "tr") {
                BX_buttonbar = BX_buttonbar.appendChild(document.createElementNS("http://www.w3.org/1999/xhtml","td"));
            } else {
                BX_buttonbar = BX_buttonbar.parentNode.appendChild(document.createElementNS("http://www.w3.org/1999/xhtml","td"));
            }

            if (node.getAttribute("align")) {
                BX_buttonbar.setAttribute("align",node.getAttribute("align"));
            }
            if (node.getAttribute("colspan")) {
                BX_buttonbar.setAttribute("colspan",node.getAttribute("colspan"));
            }
            BX_buttonbar.setAttribute("valign","absmiddle");
        }
    }
    BX_buttonbar.addEventListener("mouseup", BX_event_buttonbarMouseUp, false);
}


function BX_init_page() {

    var html = '';
    html += '<div id="BX_buttonbar" ></div>';
    html += '<div id="transformlocation" ></div>'
            html += '<div id="BX_infobar" >';
    html += '<div id="BX_infotext" style="width: 95%; position: absolute; top: 0px; margin-left: 5px; ">';
    html += '<span id="BX_infoerror" style="float: right; z-index: 400; color: red; text-align: right; "></span>';
    html += '<div id="BX_infotext_tags"> </div>\n';
    html += '<div id="BX_infotext_attr"> </div>\n';
    html += '<div id="BX_infotext2" style="position: absolute; top: 0px; margin-left: 5px; visibility: hidden; "> </div>\n';
    html += '<div id="BX_popup" class="popup" style="visibility: hidden; top: 130px; height: 30px; width: 500px; position: fixed; background-color: #b4b4b4; z-index: 40;"></div></div></div>\n';
    html += '<form name="poster" action="/admin/wysiwyg_config/php/insertintodb.php" method="post" style="visibility: hidden;">\n';
    html += '<input name="content" type="hidden" />\n';
    html += '</form>';
    var bxe_area = document.getElementById("bxe_area");
    BX_innerHTML(bxe_area,html)

    //document.getElementById("bxe_area").innerHTML = html;

}


function BX_xml_removeWhiteSpaceNodes(node) {

    if (!node) {
        return false;
    }
    var l = node.childNodes.length -1 ;
    for (var i = l; i >= 0; i--) {

        switch(node.childNodes[i].nodeType) {

        case node.TEXT_NODE:
            if (node.childNodes[i].data.replace(/[\s\n\r]*/,"").length  == 0) {
                node.childNodes[i].parentNode.removeChild(node.childNodes[i]);
            }


            break;
        case node.COMMENT_NODE:
            node.childNodes[i].parentNode.removeChild(node.childNodes[i]);
            break;
        case node.ATTRIBUTE_NODE:
            break;
        case node.CDATA_SECTION_NODE:
        case node.ELEMENT_NODE:
        case node.PROCESSING_INSTRUCTION_NODE:
        case node.DOCUMENT_TYPE_NODE:
        case node.ENTITY_NODE:
        case node.NOTATION_NODE:
        case node.ENTITY_REFERENCE_NODE:
        case node.DOCUMENT_NODE:
            BX_xml_removeWhiteSpaceNodes(node.childNodes[i]);
        }

    }

}

function BX_window_onunload (e) {
    /* we should do something, if the users leaves the page without saving it. but onunload can't be canceled... */

    /*	if(confirm("There current document has been modified. \n Do you really want to leave?")) {
    		return true;
    	}
    	else {
    		return false;
    	}*/

}

function BX_noBackspace(e) {
    if (e.keyCode == e.DOM_VK_BACK_SPACE) { // backspace
        e.preventDefault();
    }
}


function BX_walker_findNextEditableNode(startnode) {

	if (startnode.nodeType == 1 && startnode.getAttribute("name") == "bitfluxspan" && startnode.childNodes.length == 0 ) {
		var newTextNode = startnode.appendChild(document.createTextNode(""));
		return newTextNode;
		}

    var nodeIt = document.createTreeWalker(document,
                                           NodeFilter.SHOW_TEXT,{
                                           acceptNode : function(node) {
										   	if (node.parentNode.getAttribute("name") == "bitfluxspan") {
												return NodeFilter.FILTER_ACCEPT;
											} else {
												return NodeFilter.FILTER_REJECT;
											}
												
											}
											},
                                           false);

	nodeIt.currentNode = startnode;

    var node;
	node = nodeIt.nextNode(  );
	if (node == null) {
		node = nodeIt.previousNode();
	}
	if (node != null && !(/[^\t\n\r\s]/.test(node.data))) {	
    var nodeIt2 = document.createTreeWalker(startnode,
                                           NodeFilter.SHOW_TEXT,{
                                           acceptNode : function(node) {
										   	if ((/[^\t\n\r\s]/.test(node.data))) {
												return NodeFilter.FILTER_ACCEPT;
											} else {
												return NodeFilter.FILTER_REJECT;
											}
												
											}
											},
                                           false);
		node = nodeIt2.nextNode(  );										   
	
	}	
	if (node) {
		return node;
	} else {
		return  null;
	}
}

function BX_cursor_moveToStartInNode(node, end) {
	
    if (!end) {
		if ( BX_selection.anchorOffset == 0) {
    		var nodeIt2 = document.createTreeWalker(document,
                                           NodeFilter.SHOW_TEXT,{
                                           acceptNode : function(node) {
										   	if ((/[^\t\n\r\s]/.test(node.data))) {
												return NodeFilter.FILTER_ACCEPT;
											} else {
												return NodeFilter.FILTER_REJECT;
											}
												
											}
											},
                                           false);
			nodeIt2.currentNode = node;										   
			node = nodeIt2.previousNode(  );
		} 
		if (BX_find_bitfluxspanNode(node)) {
	        BX_selection.collapse(node,0);
		}
		
    } else {

document.normalize();
			if (node.nodeType == 1 || BX_selection.anchorOffset == node.data.length) {
    		var nodeIt2 = document.createTreeWalker(node,
                                           NodeFilter.SHOW_TEXT,{
                                           acceptNode : function(node) {
										   	if ((/[^\t\n\r\s]/.test(node.data))) {
												return NodeFilter.FILTER_ACCEPT;
											} else {
												return NodeFilter.FILTER_REJECT;
											}
												
											}
											},
                                           false);
			node = nodeIt2.nextNode(  );
			
			
				
		
		} 
		if (BX_find_bitfluxspanNode(node)) {
	    
	        	BX_selection.collapse(node,node.data.length);
		}

    }


}

function BX_cursor_moveToNextEditable(firstnode,last) {
	if (! (editnode = BX_walker_findNextEditableNode(firstnode))) {
		return false;
	}
    BX_selection = window.getSelection();
    if (!last) {
        BX_selection.collapse(editnode,0);
    } else {
        BX_selection.collapse(editnode,editnode.data.length);
    }
    BX_range = BX_selection.getRangeAt(0);
    return editnode;
}

function BX_event_buttonbarMouseUp(e) {
    document.removeEventListener("keypress",BX_keypress,false);
    document.addEventListener("keypress",BX_onkeyup,false);

}

function BX_deleteTag(node) {
    var childsLen = node.childNodes.length;
    var child = node.firstChild;
    var newchild;
    do {
        newchild = child.nextSibling;
        try {
            var newNode = node.parentNode.insertBefore(child,node);
        } catch(e) { }
        ;
    } while (child = newchild )
        node.parentNode.removeChild(node);
    return newNode;
}

function BX_deleteTagsWithName(tag) {
    //anything here is also in BX_plain...
    var containsTag = false;
    var tags = "";
    var onetag = "";
    var selectNode;
    if (typeof (tag) == "object") {
        while (onetag = tag.pop()) {
            tags += "|"+onetag+"|";
        }
    } else {
        tags += "|" + tag + "|";
    }
    //delete the tag if it's exactly selected..
    if (BX_range.startContainer == BX_range.endContainer) {
        if (tags.indexOf("|"+BX_range.startContainer.nodeName+"|") > -1) {
            selectNode = BX_deleteTag(BX_range.startContainer);
            BX_range.selectNode(selectNode);
            containsTag = true;
        }
    }

    var docFrag = BX_range.cloneContents();
    var nodes2del = new Array();
    var nodeIt = document.createTreeWalker(docFrag, NodeFilter.SHOW_ELEMENT, null, false);
    while((node = nodeIt.nextNode(  )) != null) {
        if (tags.indexOf("|"+node.nodeName+"|") > -1 || tags == "|allElements|") {
            containsTag = true;
            nodes2del.push(node);
        }
    }

    var ancestor = BX_range.commonAncestorContainer;
    do {
        if (tags.indexOf("|"+ancestor.nodeName+"|") > -1 ) {
            //BX_deleteTag(ancestor);
            BX_range.extractContents();
            ;
            BX_splitNode(ancestor.getAttribute("id"));
            containsTag = true;
        }
    } while (ancestor = ancestor.parentNode)

        if (containsTag) {
            for (var j = 0; j < nodes2del.length; j++) {
                BX_deleteTag(nodes2del[j]);
            }
            BX_insertContent(docFrag,true);
            return true;
        } else {
            return false;
        }

}

function BX_merge() {
    BX_undo_save();
    var startNode = BX_range.startContainer.parentNode;
    var endNode = BX_range.endContainer.parentNode;

    var currentNode = startNode.nextSibling;

    while (currentNode ) {
        var child = currentNode.firstChild;
        var newchild;
        var nextNode = currentNode.nextSibling;
        if (child) {
            do {
                newchild = child.nextSibling;
                startNode.insertBefore(child,null);
            } while (child = newchild )
                currentNode.parentNode.removeChild(currentNode);
        } else {
            startNode.insertBefore(currentNode,null);
        }


        if (currentNode == endNode) {
            break;
        }

        currentNode = nextNode;
    }



}

function BX_config_getNodes(xpath) {
    var result = BX_config.doc.evaluate(xpath, BX_config.doc, null, 0, null);
    return result;
}


function BX_getComputedStyle(node,style) {

    return document.defaultView.getComputedStyle(node,"").getPropertyValue(style);
}

function BX_keypress_enter(e) {

    if (e.shiftKey ) {
        var newEle = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","br");
        newEle.setAttribute("type","softreturn");
        BX_node_insertID(newEle);
        BX_insertContent(newEle);
        BX_selection.collapse(newEle.nextSibling,0);

    } else {
        var selectNode = false;
        // if we have a special element after a return hit
        var current_nodename = BX_getCurrentNodeName(BX_range.startContainer);

        if (BX_elements[current_nodename] && BX_elements[current_nodename]["returnElement"]) {
            selectNode = true;

            // if there is content in the node, just add the returnElement
            if (BX_selection.anchorNode.childNodes.length > 0  || BX_selection.anchorNode.nodeName == "#text" ) {
                if (BX_elements[current_nodename]["returnElement"] == "none") {
                    return false;
                } else if (BX_elements[current_nodename]["returnElement"] != "#PCDATA") {
                    if (  BX_elements[current_nodename]["returnElement"].indexOf("<") >= 0) {
                        var newReturnElement = BX_parser.parseFromString("<?xml version='1.0'?><rooot>"+ BX_elements[current_nodename]["returnElement"]+"</rooot>","text/xml").firstChild.firstChild;
                    } else {

                        var _elements = BX_elements[current_nodename]["returnElement"].split(" + ");
                        var newReturnElement = BX_xml.doc.createElement(_elements[0]);

                        for (var i = 1 ; i < _elements.length; i++) {
                            newReturnElement.appendChild( BX_xml.doc.createElement(_elements[i]));
                        }
                    }

                } else {
                    var newReturnElement = false;
                }


            }
            // if there is no content in that line, we have different options
            else {
                //first remove this empty node (could be ed to remove empty parents as well..)
                var emptyNode = BX_range.startContainer;

                //if we want a new element after the emptyNode, we create that here,
                // otherwise we just add PCDATA after the node and do not create a new element
                if (BX_elements[current_nodename]["afterEmptyLineElement"] != "#PCDATA") {
                    var newReturnElement = BX_xml.doc.createElement(BX_defaultReturnElement);
                    bla = BX_range.createContextualFragment("&#160;");
                    newReturnElement.appendChild(bla);

                } else {
                    var newReturnElement = false;
                }

                // in the BX_elements-option "afterEmptyLineParent", you can assign after which parent element the
                //  pointer should be. for example in a <li> we want to type in more after <ol> or <ul> and not after the last li

                if (BX_elements[current_nodename]["afterEmptyLineParent"]) {
                    //                            BX_range.selectNodeContents(BX_getParentNode(BX_range.startContainer,BX_elements[current_nodename]["afterEmptyLineParent"]));
                    BX_selection.collapse(BX_getParentNode(BX_range.startContainer,BX_elements[current_nodename]["afterEmptyLineParent"]).nextSibling,0);


                }
                //this crashes at the moment (1.0RC1)... but it's not fatal, since empty nodes get removed anyway..
                emptyNode.parentNode.removeChild(emptyNode);

            }
        } else {
            var newReturnElement = BX_xml.doc.createElement(BX_defaultReturnElement);
            bla = BX_range.createContextualFragment("&#160;");
            newReturnElement.appendChild(bla);

            if (BX_defaultReturnElement != BX_range.startContainer.nodeName) {
                selectNode = true;
            }
        }

        if (selectNode) {
            /*                    BX_range.selectNode(BX_range.startContainer);*/
            //BX_selection.selectAllChildren(newReturnElement);
        }


        if (newReturnElement) {
            BX_node_insertID(newReturnElement);

            if (BX_getCurrentNodeName(BX_range.startContainer) == newReturnElement.nodeName) {
                if (BX_range.startContainer.nodeName == "#text" || ( BX_range.startContainer.nextSibling && BX_range.startContainer.nextSibling.nodeName == "#text")) {
                    if (BX_range.startContainer.nodeName != "#text" && BX_range.startContainer.nextSibling.nodeName == "#text") {
                        var parent = BX_range.startContainer;
                    } else {
                        var parent = BX_range.startContainer.parentNode;
                    }
                    BX_tmp_r1 = BX_range.cloneRange();
                    BX_tmp_r1.setEndAfter(parent);
                    BX_range.setStartBefore(parent);
                    var FirstNode = BX_range.extractContents();
                    var ThirdNode = BX_tmp_r1.extractContents();

                    parent.parentNode.insertBefore(FirstNode,parent);

                    //var SecondNode = parent.parentNode.insertBefore(newReturnElement,parent);
                    parent.parentNode.insertBefore(ThirdNode,parent);
                    BX_selection.collapse(parent.previousSibling,0);
                    BX_node_insertID(parent.previousSibling);
                    BX_node_insertID(parent.previousSibling.previousSibling);
                    parent.parentNode.removeChild(parent);
                } else {

                    var parent = BX_range.startContainer;
                    parent.parentNode.insertBefore(newReturnElement,parent.nextSibling);
					BX_selection.selectAllChildren(parent.nextSibling);
//                   BX_selection.collapse(parent.nextSibling,0);
                }


            } else {
                if (BX_range.startContainer.nodeName != "#text" ) {
                    var parent = BX_range.startContainer;
                } else {
                    var parent = BX_range.startContainer.parentNode;
                }


                bla = parent.parentNode.insertBefore(newReturnElement,parent.nextSibling);
                while (bla.firstChild )  {
                    bla = bla.firstChild;
                }
                //						BX_selection.collapse(bla,0);
                if (bla.nodeName=="#text") {
                    bla = bla.parentNode;
                }

                BX_selection.selectAllChildren(bla);

                /*						BX_range.collapse(false);
                						BX_insertContent(newReturnElement);*/

            }
        }
        BX_range.collapse(false);

        //                BX_cursor_update();
        // after e.g.listelements, it looks like, we need a new transformation to reflect the changes

        if (BX_elements[BX_range.startContainer.nodeName] && BX_elements[BX_range.startContainer.nodeName]["doTransform"]) {
            //                    BX_transform();
        }
    }

    BX_undo_save();

    BX_scrollToCursor();

    //			BX_selection.addRange(BX_range);
    BX_update_buttons = true;
    e.preventDefault();
    e.stopPropagation();
}

function BX_dump (text,level) {

	if (BX_debugging) {
	//	dump (" \n");
		dump(new Date());
		dump(": " + text + "\n");
/*		dump("    in: ");
		for(c = arguments.callee; c; c = c.caller){
			dump(c.name + " ");
		}*/
	}

}

// for whatever reason, jsdoc needs this line
