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
// $Id: bitfluxeditor_core.js,v 1.1 2002/09/13 20:26:49 michicms Exp $

function BX_config_getNodes(xpath)
{
    var result = BX_config.evaluate(xpath, BX_config, null, 0, null);
    return result;

}

/**
* Initializationfunction.
*
* Starts loading of all documents and initiatizes some 
*  global variables
*/

function BX_init()
{
    BX_schema_init();

    BX_xmltransformfile = BX_config_getContent("/config/files/transform/file[@name='BX_xmltransformfile']");
    var BX_xmltransformfile_method = BX_config_getContent("/config/files/transform/file[@name='BX_xmltransformfile']/@method");

    BX_xsltransformfile = BX_config_getContent("/config/files/transform/file[@name='BX_xsltransformfile']");
    var BX_xsltransformfile_method = BX_config_getContent("/config/files/transform/file[@name='BX_xsltransformfile']/@method");
	
    BX_xmltransformbackfile = BX_config_getContent("/config/files/transform/file[@name='BX_xmltransformbackfile']");
    var BX_xmltransformbackfile_method = BX_config_getContent("/config/files/transform/file[@name='BX_xmltransformbackfile']/@method");

    BX_xslViewSourceFile = BX_config_getContent("/config/files/transform/file[@name='BX_xslViewSourceFile']");
/* not implemented yet */
//    var BX_xslViewSourceFile_method = BX_config_getContent("/config/files/transform/file[@name='BX_xslViewSourceFile']/@method");

    BX_xmlfile = BX_config_getContent("/config/files/input/file[@name='BX_xmlfile']");
    var BX_xmlfile_method = BX_config_getContent("/config/files/input/file[@name='BX_xmlfile']/@method");

    BX_xslfile = BX_config_getContent("/config/files/input/file[@name='BX_xslfile']");
    var BX_xslfile_method = BX_config_getContent("/config/files/input/file[@name='BX_xslfile']/@method");

    BX_posturl = BX_config_getContent("/config/files/output/file[@name='BX_posturl']");
	// there is no var here as we need this info later
    BX_posturl_method = BX_config_getContent("/config/files/output/file[@name='BX_posturl']/@method");

    var node;
    var options = BX_config_getNodes("/config/options/option");
    while (node = options.iterateNext())
    {
        if (node.firstChild)
        {
            eval(node.getAttribute("name") + " = " + node.firstChild.data + "");
        }
        else
        {
            eval(node.getAttribute("name") + " = ''");
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

    BX_xml = BX_load_document(BX_xmlfile,BX_xmlfile_method);
    BX_xsl = BX_load_document(BX_xslfile,BX_xslfile_method);

    BX_xmlTR = BX_load_document(BX_xmltransformfile,BX_xmltransformfile_method);

    BX_xslTR = BX_load_document(BX_xsltransformfile,BX_xsltransformfile_method);

    BX_xmlTRBack = BX_load_document(BX_xmltransformbackfile,BX_xmltransformbackfile_method);

    BX_transformLocation =  document.getElementById("transformLocation");
    BX_xsltProcessor = new XSLTProcessor();

    document.getElementsByNameAndAttribute = getElementsByNameAndAttribute;

    BX_xml.getElementById = BX_getElementById;

    //temporary ranges. to be used in some functions
    /* it is used in BX_insertContent(), so instead of creating it
    * everytime we insert content, just do it once here 
    */
    BX_tmp_r1 = document.createRange();
    BX_tmp_r2 = document.createRange();

    BX_popup = document.getElementById("BX_popup");

}

function BX_schema_init() {
	    BX_schemafile = BX_config_getContent("/config/files/input/file[@name='BX_schemafile']");
        BX_schema = document.implementation.createDocument("","",null);
 		BX_schema.onload = BX_schema_loaded;  // set the callback when we are done loading
        BX_schema.load(BX_schemafile);
	

}

function BX_schema_loaded() {

	BX_xml_removeWhiteSpaceNodes(BX_schema.documentElement);

	if (BX_schema.documentElement.localName != "schema" || 
	BX_schema.documentElement.namespaceURI != "http://www.w3.org/2001/XMLSchema" )
	{
		alert ("Schema file "+BX_schemafile +" seems not to be a valid schema document. Check for example your namespaces.\n localName is " + BX_schema.documentElement.localName + "\n namespaceURI is " + BX_schema.documentElement.namespaceURI);
		return false;
	}
	var nsResolver = BX_schema.createNSResolver(BX_schema.documentElement);

	var result = BX_xml_getChildNodesByXpath("/xs:schema/xs:element","/*[name() = 'xs:schema']/*[name() = 'xs:element']", BX_schema.documentElement,nsResolver);
	while (node = result.iterateNext()) {
		var name = node.getAttribute("name");
		BX_elements[name] = new Array();
		
		// do appinfo stuff	
		var appinfo_result = BX_xml_getChildNodesByXpath("xs:annotation/xs:appinfo/*","*[name() = 'xs:annotation']/*[name() = 'xs:appinfo']/*",node,nsResolver);
		while (appinfo_node = appinfo_result.iterateNext()) {
			if (appinfo_node.namespaceURI == "http://bitfluxeditor.org/schema/1.0")
			{
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

					case "insertafter":
						insertafter_result = BX_xml_getChildNodesByTagName("bxe:element",appinfo_node,nsResolver);
						if ( insertafter_node = insertafter_result.iterateNext())
						{
							var inserttext = insertafter_node.firstChild.data;
							
							while (insertafter_node = insertafter_result.iterateNext())
							{
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
		if (complexType)
		{
			if (complexType.getAttribute("mixed") == "true")
			{
				BX_elements[name]["allowedElements"] = "#PCDATA | ";
			}
			var elements_result = BX_xml_getChildNodesByXpath("xs:choice/xs:element","*[name() = 'xs:choice']/*[name() = 'xs:element']",complexType,nsResolver);

			while (element = elements_result.iterateNext()) {
					BX_elements[name]["allowedElements"] +=  element.getAttribute("ref") + " | "; 
			}
			BX_elements[name]["allowedElements"] = BX_elements[name]["allowedElements"].replace(/ \| $/,"");
		}
		else
		{
		}

	}
		return;



/*
		var name = element_nodes[i].getAttribute("name");
		BX_elements[name] = new Array();
	}
	var first_nodes = element_nodes[i].childNodes;
	
	for (var j = 0; j < first_nodes.length; j++ )
	{
	    switch(first_nodes[j].nodeName) {
		
		case "xs:annotation":
			if(BX_mozilla_version >= 1.1) {

				var result = BX_schema.evaluate("xs:documentation", first_nodes[j], nsResolver, 0, null);
			}
			else {
				var result = BX_schema.evaluate("*[name() = 'xs:documentation']", first_nodes[j], null, 0, null);
			}
			node = result.iterateNext();
			alert (node.nodeName);
			break;
		}
		
		

	}
return;
}
*/
}

function BX_xml_getChildNodesByTagName(name, node, nsResolver)
{
	if(BX_mozilla_version >= 1.1) {
		var result = node.ownerDocument.evaluate(name, node, nsResolver, 0, null);

	}
	else {
		var result = node.ownerDocument.evaluate("*[name() = '"+name+"']", node, null, 0, null);
	}
	return result;
}

function BX_xml_getChildNodesByXpath(xpath11, xpath10 ,node, nsResolver)
{
	if(BX_mozilla_version >= 1.1) {
		var result = node.ownerDocument.evaluate(xpath11, node, nsResolver, 0, null);
	}
	else {
		var result = node.ownerDocument.evaluate(xpath10, node, null, 0, null);
	}

	return result;
}



function BX_init_adjustButtonbar() {

    BX_infobar.style.width = window.innerWidth;
    BX_infobar.style.top = window.innerHeight - 30;

}
function BX_transformDoc()
{

    if (BX_xmldone > 4)
    {
        var ad = new Date();
        var a = ad.getMinutes()*60  + ad.getSeconds() + (ad.getMilliseconds()/1000);

        window.defaultStatus = "Rendering ...";
        if (!BX_xml_done)
        {
            var xsltransformed = BX_xml.implementation.createDocument("","",null);
            var xmltransformed = BX_xml.implementation.createDocument("","",null);

            if (typeof(BX_xsl_params) != "undefined")
            {
                /* the following is to set parameters in the xsl-stylesheet according to url querystrings
                   there seems to be no other way to do that ... */
                /* mozilla does not work with namespaces on xpath as of RC2... maybe this will change...
                		see http://bugzilla.mozilla.org/show_bug.cgi?id=113611 for details (it will be in 1.1)
                until then we need this more complicated xpath string..
                var nsResolver = BX_xsl.createNSResolver(BX_xsl);
                */

                var result = BX_xsl.evaluate("/*/*[name() = 'xsl:param']", BX_xsl, null, 0, null);
                var node;
                while ((node = result.iterateNext()))
                {
                    if (typeof(BX_xsl_params[node.getAttribute("name")]) !="undefined")
                    {
                        if (typeof(node.childNodes[0]) != "undefined")
                        {

                            node.childNodes[0].nodeValue = BX_xsl_params[node.getAttribute("name")];

                        }
                    }
                }

            }
            /* end param replacement */

            BX_xsltProcessor.transformDocument( BX_xml, BX_xmlTR, xmltransformed, null);

            BX_xsltProcessor.transformDocument( BX_xsl, BX_xslTR, xsltransformed, null);
            BX_xsl = xsltransformed;
            BX_xml = xmltransformed;
            BX_xml_done = 1;
            // free them.. not used anymore
            //			xmltransformed = null;
            xsltransformed = null;
            BX_xslTR = null;
            //        BX_xmlTR = null;
        }
        var out = BX_xml.implementation.createDocument("","",null);
        BX_xsltProcessor.transformDocument( BX_xml, BX_xsl, out, null);

        try {
            var x = out.getElementsByTagName('body');
            var r = BX_xml.createRange();
            r.selectNodeContents(BX_transformLocation);
            r.extractContents();
            r.insertNode(x[0]);

            var bd = new Date();
            var b = bd.getMinutes()*60 + bd.getSeconds() + (bd.getMilliseconds()/1000);

            window.defaultStatus = "Rendering took "+parseInt((b-a)*1000)/1000+" sec";

            BX_dotFocus = BX_transformLocation;

            BX_addEvents();

            BX_undo_save();

        }
        catch (e)
        {
            alert(e);
        }

    }
    else
    {
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
* @param gif    name of the gif-button, without wt_ and .gif
* @param width  width of the button
* @param id     name of the id
* @return void
*/
function BX_printButton (gif,width,height,id,title)
{
    var button = document.createElement("img");
    button.setAttribute("border","0");
    if (id)
    {
        button.setAttribute("id","but_"+id);
    }
    if (title)
    {
        button.setAttribute("title",title);
    }
    else
    {
        button.setAttribute("title",gif);
    }
    button.setAttribute("height",height);
    button.setAttribute("width",width);
    button.setAttribute("src","./"+BX_root_dir+"/img/wt_"+gif+".gif");
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
* @param gif    name of the gif-button, without wt_ and .gif
* @param width  width of the button
* @param id     name of the id
* @param tagoptions see above
* @param tagoptions2 see above
* @return void
*/
function BX_registerButton(tag,gif,width,height,title,tagoptions,tagoptions2)
{

    if (!tagoptions)
    {
        tagoptions = 0;
    }

    if (!tagoptions2)
    {
        tagoptions2 = new Array();
    }

    /* if options is inactive, print the inactive button (ending with _p) */
    if (tagoptions & optInactive)
    {
        if (tagoptions & optCallback)
        {
            BX_printButton(gif+"_p",width,height,false, title);
        }
    }
    /* if options is nonclickable, print the inactive button (ending with _p)
    but add it to the buttons-system as well;
    */
    else if (tagoptions & optNonclickable)
    {

        BX_buttonbar.appendChild(BX_printButton(gif+"_p",width,height,gif,title));
        BX_buttons[tag] = new Array();
        BX_buttons[tag]["gif"] = gif;
        BX_buttons[tag]["options"] = tagoptions;
    }
    else if (tagoptions & optCallback)
    {

        var ahref = document.createElement("a");
        ahref.setAttribute("href","javascript:"+tagoptions2);
        ahref.appendChild(BX_printButton(gif+"_n",width,height,gif,title));
        if (optElementSensitive & tagoptions)
        {
            BX_buttons[tag] = new Array();
            BX_buttons[tag]["gif"] = gif;
            BX_buttons[tag]["options"] = tagoptions;

        }
        BX_buttonbar.appendChild(ahref);


    }

    /* otherwise it's the default behaviour */
    else
    {
        /* if we want a normal javascript popup, we use a special function... */
        if (tagoptions2["popUp"] == true)
        {
            var ahref = document.createElement("a");
            ahref.setAttribute("href","javascript:BX_surroundTagWithPopup('"+tagoptions2["popUpBefore"]+"','"+tagoptions2["popUpAfter"]+"')");
        }

        else
        {
            var ahref = document.createElement("a");

            if (tagoptions & optSplitNode)
            {
                ahref.setAttribute("href","javascript:BX_add_tag('"+tag+"',false,true);");
            }
            else
            {
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
        if (!idAttr) continue;
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

    if ( !(BX_xml_nodelist) )
    {

        if (xml)
        {
            BX_xml_nodelist = xml.getElementsByTagName("*");
        }
        else
        {
            BX_xml_nodelist = this.getElementsByTagName("*");
        }
    }

    for (var i=0; i < BX_xml_nodelist.length; i++)
    {
        var idAttr = BX_xml_nodelist[i].getAttribute("id");
        if (!idAttr) continue;
        if (idAttr == id) {
            return BX_xml_nodelist[i];
        }
    }
    return null;
}

function BX_focusSpan (w_div )
{
    if (!(BX_dotFocus) || BX_dotFocus != w_div)
    {
        w_div.style.borderColor='#000000';

        BX_dotFocus.style.borderColor='#cccccc';
        BX_dotFocus=w_div;
    }

}


/**************************
* Undo Stuff              *
***************************/

function BX_undo_save()
{
    if (BX_undo_counter > 0)
    {
        BX_doc_changed = true;
    }

    BX_undo_counter++;
    BX_undo_max = BX_undo_counter;
    BX_undo_buffer[BX_undo_counter] = BX_transformLocation.cloneNode(true);
    //	window.defaultStatus = "undo save: " + BX_undo_counter;
    if (BX_undo_buffer.length > 10)
    {
        BX_undo_buffer[BX_undo_counter - 10] = null;
    }

    //    window.defaultStatus = "undo saved: " + BX_undo_counter;
    BX_undo_updateButtons();
}

function BX_undo_undo()
{
    if (BX_undo_buffer[BX_undo_counter - 1])
    {

        BX_undo_counter--;

        //        window.defaultStatus = "undo undo: " + BX_undo_counter;
        var newNode = BX_undo_buffer[BX_undo_counter].cloneNode(true);
        BX_transformLocation.parentNode.replaceChild(newNode,BX_transformLocation);
        BX_transformLocation =  document.getElementById("transformLocation");
        BX_addEvents();
        BX_cursor = document.getElementById("bx_cursor");
        if (BX_cursor)
        {
            BX_range.selectNode(BX_cursor);
            BX_range.collapse(true);
        }
    }

    BX_undo_updateButtons();
}

function BX_undo_redo()
{

    if (BX_undo_buffer[BX_undo_counter + 1] && (BX_undo_counter + 1) <= BX_undo_max )
    {


        BX_undo_counter++;
        //        window.defaultStatus = "undo undo: " + BX_undo_counter;
        var newNode = BX_undo_buffer[BX_undo_counter].cloneNode(true);
        BX_transformLocation.parentNode.replaceChild(newNode,BX_transformLocation);

        BX_transformLocation =  document.getElementById("transformLocation");

        BX_addEvents();
        BX_cursor = document.getElementById("bx_cursor");
        if (BX_cursor)
        {
            BX_range.selectNode(BX_cursor);
            BX_range.collapse(true);
        }

    }
    BX_undo_updateButtons();
}

function BX_undo_updateButtons()
{
    if (BX_undo_counter == BX_undo_max)
    {
        document.getElementById("but_redo").src="./"+BX_root_dir+"/img/wt_redo_p.gif";
    }
    else
    {
        document.getElementById("but_redo").src="./"+BX_root_dir+"/img/wt_redo_n.gif";
    }
    if (! BX_undo_buffer[BX_undo_counter - 1])
    {
        document.getElementById("but_undo").src="./"+BX_root_dir+"/img/wt_undo_p.gif";
    }
    else
    {
        document.getElementById("but_undo").src="./"+BX_root_dir+"/img/wt_undo_n.gif";
    }
}


function BX_addEvents()
{

    document.addEventListener("keypress",BX_keypress,false);
    document.addEventListener("keyup",BX_onkeyup,false);

    //  var allSpans = document.getElementsByName("bitfluxspan");
    /*    for (i = 0; i < allSpans.length; i ++)
        {
            allSpans[i].addEventListener("mouseup", BX_RangeCaptureOnMouseUp, false);
        }*/
    document.getElementById("transformLocation").addEventListener("mouseup", BX_RangeCaptureOnMouseUp, false);

}


function BX_keypress(e)
{
    BX_selection = window.getSelection();
    try {BX_range = BX_selection.getRangeAt(0);}
    catch(e) { return false;}

    if (e.ctrlKey || e.metaKey)
    {

        switch(String.fromCharCode(e.charCode))
        {
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

    }
    else
    {

        switch (e.keyCode)
        {

        case e.DOM_VK_BACK_SPACE: // backspace
            BX_undo_save();
            if (BX_selection.anchorOffset ==  BX_selection.focusOffset)
            {
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
        case e.DOM_VK_RETURN:

            if (e.shiftKey && true)
            {
                //does not work yet...
                var newEle = BX_xml.createElementNS("http://www.w3.org/1999/xhtml","br");
                newEle.setAttribute("type","softreturn");
                BX_node_insertID(newEle);
                BX_insertContent(newEle);

                BX_selection.collapse(newEle.nextSibling,0);

            }
            else
            {
                var selectNode = false;
                // if we have a special element after a return hit
                var current_nodename = BX_getCurrentNodeName(BX_range.startContainer);

                if (BX_elements[current_nodename] && BX_elements[current_nodename]["returnElement"])
                {
                    selectNode = true;

                    // if there is content in the node, just add the returnElement
                    if (BX_selection.anchorNode.childNodes.length > 0  || BX_selection.anchorNode.nodeName == "#text" )
                    {
                        if (BX_elements[current_nodename]["returnElement"] == "none")
                        {
                            return false;
                        }

                        else if (BX_elements[current_nodename]["returnElement"] != "#PCDATA")
                        {
                            if (  BX_elements[current_nodename]["returnElement"].indexOf("<") >= 0)
                            {
                                var newReturnElement = BX_parser.parseFromString("<?xml version='1.0'?><rooot xmlns='http://www.w3.org/1999/xhtml'>"+ BX_elements[current_nodename]["returnElement"]+"</rooot>","text/xml").firstChild.firstChild;
                            }
                            else
                            {

                                var _elements = BX_elements[current_nodename]["returnElement"].split(" + ");
                                var newReturnElement = BX_xml.createElementNS("http://www.w3.org/1999/xhtml",_elements[0]);

                                for (var i = 1 ; i < _elements.length; i++) {
                                    newReturnElement.appendChild( BX_xml.createElementNS("http://www.w3.org/1999/xhtml",_elements[i]));
                                }
                            }

                        }
                        else
                        {
                            var newReturnElement = false;
                        }


                    }
                    // if there is no content in that line, we have different options
                    else
                    {
                        //first remove this empty node (could be ed to remove empty parents as well..)
                        var emptyNode = BX_range.startContainer;

                        //if we want a new element after the emptyNode, we create that here,
                        // otherwise we just add PCDATA after the node and do not create a new element
                        if (BX_elements[current_nodename]["afterEmptyLineNewElement"] != "#PCDATA")
                        {
                            var newReturnElement = BX_xml.createElementNS("http://www.w3.org/1999/xhtml",BX_defaultReturnElement);
                            bla = BX_range.createContextualFragment("&#160;");
                            newReturnElement.appendChild(bla);

                        }
                        else
                        {
                            var newReturnElement = false;
                        }

                        // in the BX_elements-option "afterEmptyLineParent", you can assign after which parent element the
                        //  pointer should be. for example in a <li> we want to type in more after <ol> or <ul> and not after the last li

                        if (BX_elements[current_nodename]["afterEmptyLineParent"])
                        {
                            //                            BX_range.selectNodeContents(BX_getParentNode(BX_range.startContainer,BX_elements[current_nodename]["afterEmptyLineParent"]));
                            BX_selection.collapse(BX_getParentNode(BX_range.startContainer,BX_elements[current_nodename]["afterEmptyLineParent"]).nextSibling,0);


                        }
                        //this crashes at the moment (1.0RC1)... but it's not fatal, since empty nodes get removed anyway..
                        emptyNode.parentNode.removeChild(emptyNode);

                    }
                }
                else
                {
                    var newReturnElement = BX_xml.createElementNS("http://www.w3.org/1999/xhtml",BX_defaultReturnElement);
                    bla = BX_range.createContextualFragment("&#160;");
                    newReturnElement.appendChild(bla);

                    if (BX_defaultReturnElement != BX_range.startContainer.nodeName)
                    {
                        selectNode = true;
                    }
                }

                if (selectNode)
                {
                    /*                    BX_range.selectNode(BX_range.startContainer);*/
                    //		BX_selection.selectAllChildren(newReturnElement);
                }


                if (newReturnElement)
                {
                    BX_node_insertID(newReturnElement);

                    if (BX_getCurrentNodeName(BX_range.startContainer) == newReturnElement.nodeName)
                    {

                        if (BX_range.startContainer.nodeName == "#text" || ( BX_range.startContainer.nextSibling && BX_range.startContainer.nextSibling.nodeName == "#text"))
                        {
                            if (BX_range.startContainer.nodeName != "#text" && BX_range.startContainer.nextSibling.nodeName == "#text")
                            {
                                var parent = BX_range.startContainer;
                            }
                            else
                            {
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
                        }
                        else
                        {

                            var parent = BX_range.startContainer;
                            parent.parentNode.insertBefore(newReturnElement,parent.nextSibling);
                            BX_selection.collapse(parent.nextSibling,0);
                        }


                    }
                    else
                    {
                        if (BX_range.startContainer.nodeName != "#text" )
                        {
                            var parent = BX_range.startContainer;
                        }
                        else
                        {
                            var parent = BX_range.startContainer.parentNode;
                        }


                        bla = parent.parentNode.insertBefore(newReturnElement,parent.nextSibling);
                        while (bla.firstChild )  { bla = bla.firstChild;}
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

                if (BX_elements[BX_range.startContainer.nodeName] && BX_elements[BX_range.startContainer.nodeName]["doTransform"])
                {
                    //                    BX_transform();
                }
            }

            BX_undo_save();

            BX_scrollToCursor();

            //			BX_selection.addRange(BX_range);
            BX_update_buttons = true;
            e.preventDefault();
            e.stopPropagation();

            return false;

            break;
        default:

            if (e.which!=0) {
                if (e.charCode == e.DOM_VK_SPACE)
                {
                    BX_undo_save();
                }

                BX_insertContent(String.fromCharCode(e.charCode));
                e.preventDefault();
                e.stopPropagation();
                BX_selection.collapse(BX_selection.focusNode, BX_selection.focusOffset );

            }
        }
    }

    if (BX_update_buttons)
    {
        window.setTimeout("BX_updateButtonsDelayed()",10);

    }
}

function BX_onkeyup(e)
{

    switch (e.keyCode)
    {

    case e.DOM_VK_UP:
    case e.DOM_VK_DOWN:
    case e.DOM_VK_LEFT:
    case e.DOM_VK_RIGHT:
    case e.DOM_VK_DELETE:
    case e.DOM_VK_BACK_SPACE:
        BX_update_buttons = true;
        window.setTimeout("BX_updateButtonsDelayed()",10);
    }
}
function BX_updateButtonsDelayed()
{
    if (BX_update_buttons)
    {
        BX_get_selection();
        BX_updateButtons();
        BX_update_buttons = false;

    }
}

function BX_insertContent(content, do_undo_save)
{
    /*    BX_selection.removeAllRanges();*/
    try {BX_range.deleteContents();}
    catch(e){};

    var StartContainer = BX_range.startContainer;
    var StartPosition = BX_range.startOffset;

    if (typeof(content) == "string")
    {

        if (content.length == 1)
        {
            content = document.createTextNode(content);
        }
        else if (content.indexOf("<") >= 0)
        {

            content = BX_parser.parseFromString("<?xml version='1.0'?><rooot>"+content+"</rooot>","text/xml");
            content = content.childNodes[0];

            BX_tmp_r1 = document.createRange();

            BX_tmp_r1.selectNodeContents(content);
            content = BX_tmp_r1.extractContents();

        }
        else {
            content = document.createTextNode(content);
        }
        //		BX_range.createContextualFragment(content);
    }

    var startOffBefore = BX_range.startOffset;

    if (StartContainer.nodeType==StartContainer.TEXT_NODE && content.nodeType==content.TEXT_NODE)
    {
        StartContainer.insertData(StartPosition, content.nodeValue);
        if (startOffBefore == BX_range.startOffset)
        {
            BX_range.setEnd(BX_range.endContainer ,BX_range.endOffset +1);
            BX_range.collapse(false);
        }
    }

    else // if (StartContainer.nodeType == StartContainer.TEXT_NODE)
    {
        var startOffBefore = BX_range.startOffset;

        BX_range.insertNodeBX = InsertNodeAtStartOfRange;
        try{		BX_range.insertNodeBX(content);}
        catch(e) {};

        if (startOffBefore == BX_range.startOffset)
        {
            BX_range.setEnd(BX_range.endContainer ,BX_range.endOffset +1);
        }
        BX_range.collapse(false);

    }

    BX_selection.addRange(BX_range);
    return content;
}


function BX_key_delete()
{
    BX_get_selection();
    BX_selection.removeAllRanges();
    if (BX_range.toString().length == 0)
    {
        if (BX_range.endOffset == BX_range.endContainer.data.length)
        {
            if (BX_range.endContainer.nextSibling)
            {
                BX_range.selectNodeContents(BX_range.endContainer.nextSibling.childNodes[0]);
            }
            else if (BX_range.endContainer.parentNode)
            {
                BX_range.selectNodeContents(BX_range.endContainer.parentNode.nextSibling);
            }
            BX_range.collapse(1);
        }
        BX_range.setEnd(BX_range.endContainer, BX_range.endOffset+1);
    }

    BX_range.extractContents();
    BX_selection.removeAllRanges();
    BX_range.collapse(1);
    BX_selection.addRange(BX_range);
    //        BX_range = BX_selection.getRangeAt( BX_selection.rangeCount-1 ).cloneRange();
}

function BX_cursor_findTextLeft(node)
{
    var z= 0;
    BX_cursor_overElement = false;
    window.defaultStatus = node.nodeName;

    if (node.nodeName == "#text" && BX_selection.anchorOffset > 0 )
    {
        return node;
    }
    else if (node.previousSibling)
    {
        node = node.previousSibling;
    }
    else if (node.parentNode)
    {
        node = node.parentNode.previousSibling;
    }
    else
    {
        return false;
    }

    BX_cursor_overElement = false;
    while (( node.nodeName != "#text" || node.data.length==0) && z < 50)
    {
        if (node.hasChildNodes())
        {
            node = node.lastChild;
        }
        else if (node.previousSibling )
        {
            node = node.previousSibling;
            BX_cursor_overElement = true;
        }
        else if (node.parentNode)
        {
            node = node.parentNode;
            BX_cursor_overElement = true;
        }
        else {
            return false;
        }

        z++;
    }
    return node;
}

function BX_cursor_moveLeft ()
{
    BX_selection = window.getSelection();
    if (BX_selection)
    {
        BX_selection.collapseToStart();
    }

    if (BX_selection.anchorOffset == 0 )
    {
        var nextNode = false;
        nextNode = BX_cursor_findTextLeft(BX_selection.anchorNode);
        //no whitespace stuff...

        if (nextNode != false)
        {
            var stripWS = nextNode.data.replace(/[\n ]{2,}$/,"");
            if (stripWS.length == 0 || BX_cursor_overElement)
            {
                BX_selection.collapse(nextNode,stripWS.length)
            }
            else
            {
                BX_selection.collapse(nextNode,stripWS.length-1);
            }
        }
    }
    else
    {
        BX_selection.collapse(BX_selection.anchorNode, BX_selection.anchorOffset - 1 );
    }

    /*    BX_range = BX_selection.getRangeAt( BX_selection.rangeCount-1 ).cloneRange();
        BX_range.collapse(true);*/

}


function BX_add_tag(tag, afterNodeId,splitNode)
{
    if (BX_notEditable) { return;}

    BX_get_selection();
    if (! BX_find_bitfluxspanNode(BX_range.startContainer)) { return;}
    var replaceChildrenByAddAlso_done = false;
    BX_undo_save();
    var element = BX_xml.createElementNS("http://www.w3.org/1999/xhtml",tag);
    if (BX_elements[tag]["requiredAttributes"]) {
        var attributes = BX_elements[tag]["requiredAttributes"].split(" | ");
        for (var i = 0 ; i < attributes.length; i++)
        {
            element.setAttribute(attributes[i],"#"+attributes[i]);
        }
    }

    element.setAttribute("id",'BX_id_'+BX_id_counter);
    element.id = 'BX_id_'+BX_id_counter;
    element.setAttribute("internalid",'yes');
    BX_id_counter++;
    if (BX_elements[tag]["originalName"]){
        element.setAttribute("bx_originalname",BX_elements[tag]["originalName"]);
    }

    var selectNode= false;
    if (BX_range.toString().length == 0)
    {
        selectNode = true;
        if (BX_elements[tag]["insertContent"])
        {
            var frag  = BX_xml.createTextNode(BX_elements[tag]["insertContent"]);
        }
        else
        {
            var frag  = BX_xml.createTextNode("#"+tag);
        }

    }
    else
    {
        var frag = BX_range.extractContents();
        // if we want for example replace para by listitem if we add an itemized list
        // then this code does this. replaceChildrenByAddAlso replace all nodes with that
        // nodename with the addAlso nodename

        if (BX_elements[tag]["replaceChildrenByAddAlso"])
        {
            for(var i = 0; i < frag.childNodes.length; i++)
            {
                if (frag.childNodes[i].nodeName == BX_elements[tag]["replaceChildrenByAddAlso"])
                {
                    replaceChildrenByAddAlso_done = true;
                    var newNode = BX_xml.createElementNS("http://www.w3.org/1999/xhtml",BX_elements[tag]["addAlso"]);
                    for (var j = 0; j < frag.childNodes[i].childNodes.length; j++)
                    {
                        newNode.appendChild(frag.childNodes[i].childNodes[j].cloneNode(1));
                    }
                    newNode.setAttribute("id","BX_id_"+BX_id_counter);
                    BX_id_counter++;
                    newNode.setAttribute("internalid",'yes');

                    frag.childNodes[i].parentNode.replaceChild(newNode,frag.childNodes[i]);

                }

            }
        }
    }
    if (splitNode)
    {
        BX_splitNode();
    }

    if (BX_elements[tag]["addAlso"] && !replaceChildrenByAddAlso_done) {
        var elements = BX_elements[tag]["addAlso"].split(" | ");
        for (var i = 0; i < elements.length; i++)
        {
            var noContent = false;
            if (  elements[i].indexOf("<") >= 0)
            {
                var alsoElement = BX_parser.parseFromString("<?xml version='1.0'?><rooot xmlns='http://www.w3.org/1999/xhtml'>"+ elements[i]+"</rooot>","text/xml").firstChild.firstChild;
                noContent = true;
            }
            else
            {
                var alsoElement = BX_xml.createElementNS("http://www.w3.org/1999/xhtml",elements[i]);

            }


            if (i > 0 || selectNode)
            {
                if ( (BX_elements[tag]["insertContent"]) && i == 0 && ! noContent)
                {
                    frag  = BX_xml.createTextNode(BX_elements[tag]["insertContent"]);
                    alsoElement.appendChild(frag);
                }
                else if (! noContent)
                {
                    frag = BX_xml.createTextNode("#"+elements[i]);
                    alsoElement.appendChild(frag);
                }
            }
            else
            {
                alsoElement.appendChild(frag);
            }
            alsoElement.setAttribute("id",'BX_id_'+BX_id_counter);
            alsoElement.setAttribute("internalid",'yes');
            alsoElement.id = 'BX_id_'+BX_id_counter;

            BX_id_counter++;
            element.appendChild(alsoElement);

        }

    }
    else
    {
        element.appendChild(frag);
    }
    if (afterNodeId)
    {
        //    	document.getElementById = BX_getElementById;

        var node = document.getElementById(afterNodeId);
        if (!(node))
        {
            var node = BX_getElementByIdClean(afterNodeId,document,1);
        }
        var next = node.nextSibling;
        while (next != null && next.nodeName == "#text" ){
            next = next.nextSibling;
        };
        var parentNode = node.parentNode;

        parentNode.insertBefore(element,next);
        var newNode = element;
        //             BX_debug(newNode);

    }
    else
    {
        var newNode = BX_insertContent(element);
    }
    //    BX_updateOtherFields ();

    //    BX_range.selectNodeContents(newNode);

    if (BX_elements[tag]["addAlso"])
    {
        newNode = newNode.childNodes[0];
    }

    if (!selectNode)
    {
        BX_range.collapse(false);
    }

    //    BX_cursor_update();



    /*    BX_range_length = BX_range.toString().length;*/
    if (BX_elements[tag]["doTransform"])
    {
        //        BX_transform(selectNode);
    }

    BX_popup.style.top = window.innerHeight + "px";

    BX_popup.style.visibility = "hidden";
    BX_addEvents();
    BX_scrollToCursor(node);
    BX_selection.selectAllChildren(newNode);
    BX_update_buttons = true;


    /*	BX_range.selectNodeContents(newNode);*/
    /*	BX_selection.collapse(BX_range.startContainer, BX_range.startOffset );
        BX_selection.extend(BX_range.endContainer,BX_range.endOffset);*/
    /*	BX_selection.addRange(BX_range);*/

    /*    BX_selection.collapse(BX_range.startContainer.childNodes[0],BX_range.startOffset);
        BX_selection.extend(BX_range.endContainer,BX_range.endOffset);*/
    //  BX_updateButtons();

    //    BX_scrollToCursor();
}

function BX_get_selection()
{
    BX_selection = window.getSelection();
    BX_range = BX_selection.getRangeAt(0);
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


function InsertNodeAtStartOfRange( newNode ){
    //Range.prototype.insertNode = InsertNodeAtStartOfRange;
    //Mozilla has not implemented this method yet.  This code will simulate the
    //required functionality

    try{
        var test_INVALID_STATE_ERR = this.endContainer;
        var test_INVALID_STATE_ERR = this.startContainer;
    }catch(e){throw new Error("INVALID_STATE_ERR")}

    var node=newNode;
    //check for invalid node type
    switch( node.nodeType ){
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
            this.startContainer.nodeType == Node.TEXT_NODE){
        //The start of the range is within a Text or CDATASection node.  The node
        //must be split so that the new node can be inserted.
        this.startContainer.parentNode.insertBefore(
            node, this.startContainer.splitText( this.startOffset));
    } else if( this.startOffset == this.startContainer.childNodes.length){
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
    if( node.nodeType == Node.DOCUMENT_FRAGMENT_NODE) node = firstChild;
    try{
        this.setStart( node, 0 );
    }catch(err){}
}

function BX_getCurrentNodeName(node)
{

    if (!node)
    {
        return false;
    }
    if (node.nodeName =="#text")
    {
        node = node.parentNode;
    }
    if (node.hasAttributes() && node.getAttribute("nodename")) {
        return node.getAttribute("nodename");
    }
    else
    {
        return node.nodeName;
    }
}

function BX_transform(selectNode)
{
    BX_get_selection();
    var BX_cursor = document.createElementNS("http://www.w3.org/1999/xhtml","span");
    //	BX_cursor.appendChild(document.createTextNode("|"));
    BX_cursor.setAttribute("id","bx_cursor");
    BX_insertContent(BX_cursor);
    if (BX_mixedCaseAttributes)
    {
        var xmltransformedback = BX_xml.implementation.createDocument("","",null);
        BX_xsltProcessor.transformDocument( BX_getResultXML(), BX_xmlTR, xmltransformedback, null);
        BX_xml = xmltransformedback;
    }
    else
    {
        BX_updateXML();
    }
    BX_transformDoc();

    BX_cursor = document.getElementById("bx_cursor");


    if (BX_cursor && BX_cursor.nextSibling)
    {
        BX_selection.collapse(BX_cursor.nextSibling,0);
    }
    else
    {
        try{		BX_selection.collapse(BX_cursor,0);}
        catch(e) { return;}
    }
    BX_cursor.parentNode.removeChild(BX_cursor);
    if (selectNode)
    {
        BX_range.selectNodeContents(BX_range.startContainer);
        BX_range.insertNode(BX_cursor);

    }


    BX_range_length = BX_range.toString().length;
    //	BX_range.collapse(true);
}

/********************
* XML Update Stuff  *
*********************/

function BX_updateXML()
{

    var allDivs = document.getElementsByName("bitfluxspan");
    BX_clean_nodelist(BX_xml);
    BX_xml.getElementById = BX_getElementById;
    BX_tmp_r1 = BX_xml.createRange();
    BX_tmp_r2 = document.createRange();
    for (var i = 0; i < allDivs.length; i ++)
    {
        if (allDivs[i].getAttribute("id"))
        {

            var myNodeAttr = BX_xml.getElementById(allDivs[i].getAttribute("id"));

            //get all children of this <span> tag and add them to a new element with <formfieldid>
            // later we replace that in the BX_xml.
            if (!(myNodeAttr))
            {
                continue;
            }

            BX_tmp_r1.selectNodeContents(myNodeAttr);

            BX_tmp_r1.extractContents();

            BX_tmp_r2.selectNodeContents(document.getElementById(allDivs[i].getAttribute("id")));
            if (BX_tmp_r2.toString().length > 0)
            {
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


function BX_RangeCaptureOnMouseUp(e){

    try {


        if (e.target && BX_elements[e.target.nodeName] && BX_elements[e.target.nodeName]["altMenu"] && BX_find_bitfluxspanNode(e.target))
        {

            eval(BX_elements[e.target.nodeName]["altMenu"]+"(e.target)");
            return;
        }

        BX_get_selection();
        if (e.target.nodeName != "#text" && e.target.nodeName != BX_selection.anchorNode.parentNode.nodeName)
        {
            BX_selection.collapse( e.target,0);
            BX_range = BX_selection.getRangeAt(0);

        }
        if (e.which == 1)
        {
            BX_popup.style.top = window.innerHeight + "px";
            BX_popup.style.visibility = "hidden";
        }
    }
    catch(err)
    {
        BX_errorMessage(err);
    };
    BX_updateButtons();
    BX_range_length = 0;
    BX_range_length = BX_range.toString().length;


}

function BX_updateButtons()
{
    //    window.defaultStatus = "";
    BX_notEditable = false;
    var startNode =  BX_range.startContainer;

    var sectionDepth= 0;
    var thisNodeName ;
    var BX_infotext_tags = document.getElementById("BX_infotext_tags");
    BX_node_clean_bg();

    if (startNode.nodeName=="#text")
    {
        var firstnode = startNode.parentNode;
    }
    else
    {
        var firstnode = startNode;
        startNode= startNode.childNodes[0];

    }
    var firstNodeName = BX_getCurrentNodeName(firstnode);
    var parentNodeName = BX_getCurrentNodeName(firstnode.parentNode);
    //    window.defaultStatus="";
    for(var button in BX_buttons)
    {

        if (document.getElementById("but_"+BX_buttons[button]["gif"]))
        {

            BX_buttons[button]["highlight"] = false;
            //make it nonClickable, if the element is not allowed in this element
            // or if the element is a splitnode element, but the parent also does not allow it...
            if (BX_buttons[button]["options"] & optNonclickable  ||
                    (BX_elements[firstNodeName] && BX_elements[firstNodeName]["allowedElements"].indexOf(button) < 0)
                    && !(((BX_buttons[button]["options"] & optSplitNode)  && parentNodeName && BX_elements[parentNodeName] && BX_elements[parentNodeName]["allowedElements"].indexOf(button) >= 0))
               )
            {

                document.getElementById("but_"+BX_buttons[button]["gif"]).src="./"+BX_root_dir+"/img/wt_"+BX_buttons[button]["gif"]+"_p.gif";
            }
            else
            {
                document.getElementById("but_"+BX_buttons[button]["gif"]).src="./"+BX_root_dir+"/img/wt_"+BX_buttons[button]["gif"]+"_n.gif";
            }
        }
    }
    var infotext = "</b> /";

    var first = 1;
    while(startNode && startNode.parentNode && startNode.parentNode.nodeName.toLowerCase() != "body" && startNode.parentNode.getAttribute("name") != "bitfluxspan")
    {
        startNode = startNode.parentNode;

        var startNodeId = startNode.getAttribute("id");
        if (startNodeId == null)
        {
            BX_node_insertID(startNode);152
            var startNodeId = startNode.getAttribute("id");

        }
        if (BX_elements[startNode.nodeName])
        {
            infotext =  "<a onmouseout=\"BX_hide_node('"+startNodeId+"')\"  onmouseover=\"BX_show_node('"+startNodeId+"')\" href=\"javascript:BX_popup_node('"+startNodeId+"');\" >" + BX_elements[startNode.nodeName]["name"] + "</a>"+ infotext;
        }
        else
        {
            infotext = "<a href=\"javascript:BX_popup_node('"+startNode.getAttribute("id")+"');\" >" + startNode.nodeName + "</a>" + infotext;
        }

        if (first == 1)
        {
            infotext = "<b>" + infotext;
            first = 0;
        }
        infotext =  " / " + infotext ;
        thisNodeName = startNode.nodeName;
        if (thisNodeName == "section")
        {
            sectionDepth++;
        }
        if (BX_buttons[thisNodeName])
        {
            document.getElementById("but_"+BX_buttons[thisNodeName]["gif"]).src="./"+BX_root_dir+"/img/wt_"+BX_buttons[thisNodeName]["gif"]+"_a.gif";
            BX_buttons[thisNodeName]["highlight"] = true;
            //            window.defaultStatus += thisNodeName + " " + BX_buttons[thisNodeName]["gif"];
        }


    }

    startNode = startNode.parentNode;
    var startNodeId = startNode.getAttribute("id");
    if  (startNode && startNode.nodeName.toLowerCase() == "body")
    {
        infotext = "Not Editable";
        document.removeEventListener("keypress",BX_keypress,false);
        BX_notEditable = true;
    }

    else if (BX_elements[startNode.nodeName])
    {
        infotext =  "<a onmouseout=\"BX_hide_node('"+startNodeId+"')\"  onmouseover=\"BX_show_node('"+startNodeId+"')\" href=\"javascript:BX_popup_node_bitfluxspan('"+startNodeId+"');\" >" + BX_elements[startNode.nodeName]["name"] + "</a>"+ infotext;

        //		infotext = "<span class=\"tagBelow\" onmouseout=\"BX_hide_node('"+startNodeId+"')\"  onmouseover=\"BX_show_node('"+startNodeId+"')\">" + BX_elements[startNode.nodeName]["name"] +  "</span> " + infotext;
        document.addEventListener("keypress",BX_keypress,false);
    }
    else
    {
        infotext =  "<span class=\"tagBelow\" onmouseout=\"BX_hide_node('"+startNodeId+"')\"  onmouseover=\"BX_show_node('"+startNodeId+"')\">" + startNode.nodeName + "</span> " + infotext;
        document.addEventListener("keypress",BX_keypress,false);
    }

    BX_infotext_tags.innerHTML = infotext + "<br/>";
    if (startNode.nodeName.toLowerCase() != "body")
    {
        BX_infobar_printAttributes(firstnode);
    }
    BX_infobar.style.visibility = "visible";
    BX_clearInfoError();
    if (BX_doSections && sectionDepth > 0)
    {
        document.getElementById("but_ebene_"+sectionDepth).src="./"+BX_root_dir+"/img/wt_ebene_"+sectionDepth+"_a.gif";
    }

}

function BX_infobar_printAttributes(node)
{
    var infotext_attr = document.getElementById("BX_infotext_attr");
    infotext_attr.innerHTML = "";

    if (node.getAttribute("name") != "bitfluxspan")
    {
        var element_id = node.getAttribute("id");
        var infotext2 = "<b>"+BX_elements[node.nodeName]+"</b> <a href=\"javascript:BX_infobar_printAttributes(BX_getElementByIdClean('"+node.getAttribute("id")+"',document));BX_down()\">down</a><br/>";
        infotext2 += "<table class=\"usualBlackTd\">\n";

        for (var i=0; i < node.attributes.length; i++ )
        {
            if (node.attributes[i].nodeName != "bx_originalname" && node.attributes[i].nodeName != "internalid" && node.attributes[i].nodeName != "id" && node.attributes[i].nodeName != "style"  )
            {
                infotext_attr.innerHTML = "<a href=\"javascript:BX_infobar_printAttributes(BX_getElementByIdClean('"+element_id+"',document));BX_up();\">"+ node.attributes[i].nodeName + "=" + node.attributes[i].nodeValue+"</a> ";
                infotext2 += "<tr ><td>" + node.attributes[i].nodeName + ": </td>\n";
                infotext2 += "<td><input onchange=\"BX_getElementByIdClean('"+element_id+"',document).setAttribute('"+node.attributes[i].nodeName+"',this.value); \" onfocus=\"javascript: BX_range= null;\" size=\"40\" value=\""+node.attributes[i].nodeValue+"\"></td></tr>\n";
            }
        }
        /**
        * adding of new attributes. not needed in most of the cases
        infotext2 += "<tr><td ><input id=\"BX_newattr\">: </td>\n";
                                     infotext2 += "<td><input onchange=\"getElementById'"+element_id+"',BX_xml).setAttribute(document.getElementById('BX_newattr').value,this.value); \" onfocus=\"javascript: BX_range= null;\" size=\"20\" ></td></tr>\n";
                                                 */
        infotext2 += "<tr><td colspan='2'><input type='button' value = ' ok '  onclick=\"BX_infobar_printAttributes(BX_getElementByIdClean('"+node.getAttribute("id")+"',document));BX_down()\"/></td></tr>";
        BX_infotext2.innerHTML = infotext2 + "</table>";

    }

}

function BX_clearInfoError()
{
    BX_infoerror.innerHTML = "";
    BX_infoerror_timeout = false;
}

function BX_getResultXML()
{
    BX_updateXML();

    var xmltransformedback = document.implementation.createDocument("","",null);
    BX_xsltProcessor.transformDocument( BX_xml, BX_xmlTRBack, xmltransformedback, null);
    return xmltransformedback;
}


function calculateMarkup( node, isXML, inRange ){
    if( arguments.length < 2 ) isXML = (node.document + "" == "[object Document]");
    var calcRange = false;
    if( inRange ) calcRange = true;

    //calculate only those nodes that are within the range;
    if( calcRange && !inRange.intersectsNode(node) ) return "";

    //this function manually calculates the outerHTML of the supplied node.
    switch(node.nodeType){
        //the following node types are supported for the recursion nature
        //of this function.
    case Node.CDATA_SECTION_NODE: //CDATASections are not parsed
        var retVal = node.data;
        if(  calcRange && inRange.compareNode(node) != Range.NODE_INSIDE ){
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
        if(  calcRange && inRange.compareNode(node) != Range.NODE_INSIDE ){
            if( node != inRange.endContainer && node != inRange.startContainer){
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
        if(  calcRange && inRange.compareNode(node) != Range.NODE_INSIDE ){
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
        while( ptr ){
            retVal += calculateMarkup( ptr , isXML, inRange);
            ptr = ptr.nextSibling;
        }
        return retVal;

        //These nodes corrispond to tags.  Calculate the tags value.
    case Node.ELEMENT_NODE:
        var name = node.nodeName;
        var empty = (node.childNodes.length == 0);
        var attr, attrs = node.attributes;				var len = attrs.length;				var retVal= "<" + name;
        //get each of the attributes
        for (var i = 0; i < len; i++) {					attr = attrs.item(i);
            //if it has not been specified than it assumes it default
            //value and does not need to be included.					if( attr.specified )
            retVal += " " + calculateMarkup( attr, isXML, null );				}				if( isXML && empty ) return retVal + "/>";				retVal += ">";
        if( !isXML && !node.canHaveChildren ) return retVal;
        var ptr = node.firstChild;
        while( ptr ){
            retVal += calculateMarkup( ptr , isXML, inRange);
            ptr = ptr.nextSibling;
        }
        return retVal + "</" + name + ">";
    case Node.PROCESSING_INSTRUCTION_NODE:
        return "<?" + node.target + " " + node.data + "?>";

    case Node.DOCUMENT_TYPE_NODE:
        var retVal = "<!DOCTYPE " + node.nodeName;
        if( node.publicId ){
            retVal += ' PUBLIC "' + node.publicId + '"';
            if( node.systemId ) retVal += ' "' + node.systemId + '"';
        } else if( node.systemId ) retVal += ' SYSTEM "' + node.systemId + '"';
        if( node.internalSubset ) retVal += " " + node.internalSubset;
        return retVal + ">\n";

    case Node.ENTITY_NODE:		//cannot test this code due to Mozilla Bug #15118
        var retVal = "<!ENTITY " + node.nodeName;
        if( node.publicId ){
            retVal += ' PUBLIC "' + node.publicId + '"';
            if( node.systemId ) retVal += ' "' + node.systemId + '"';
            if( node.notationName ) retVal += " NDATA " + node.notationName;
        } else if( node.systemId ) {
            retVal += ' SYSTEM "' + node.systemId + '"';
            if( node.notationName ) retVal += " NDATA " + node.notationName;
        } else {
            retVal += '"';
            var ptr = node.firstChild;
            while( ptr ){
                retVal += calculateMarkup( ptr , isXML, inRange);
                ptr = ptr.nextSibling;
            }
            retVal += '"';
        }
        return retVal + ">";

    case Node.NOTATION_NODE:	//cannot test this code due to Mozilla Bug #15118
        var retVal = "<!NOTATION " + node.nodeName;
        if( node.publicId ){
            retVal += ' PUBLIC "' + node.publicId + '"';
            if( node.systemId ) retVal += ' "' + node.systemId + '"';
        } else if( node.systemId ) retVal += ' SYSTEM "' + node.systemId + '"';
        return retVal + ">";

    case Node.ENTITY_REFERENCE_NODE:
        return "&" + node.nodeName + ";";

    case Node.ATTRIBUTE_NODE:
        if( node.specified )
            return node.nodeName + '="' + node.value + '"';				return "";

    case Node.DOCUMENT_NODE:
        //there is a bug in XML documents.  The doctype tag is NOT in the document
        //sub-tree, but is in an HTML document.  Detect it and surpress it.
        var retVal = "";
        var foundDocType = false;
        var ptr = node.firstChild;
        while( ptr ){
            if(ptr.nodeType != Node.DOCUMENT_TYPE_NODE )
                foundDocType = true;
            retVal += calculateMarkup( ptr, isXML, inRange );
            ptr = ptr.nextSibling;
        }
        if( !foundDocType && node.doctype ) retVal = calculateMarkup(node.doctype, isXML, inRange) + retVal;
        if(isXML && ( !calcRange || (node == inRange.startContainer && inRange.startOffset == 0)))
            retVal = '<?xml version="1.0" encoding="'+ node.characterSet + '"?>\n' + retVal;
        return retVal;
    }
}


function BX_scrollToCursor(node)
{

    BX_get_selection();

    if (!node)
    {
        var anchorNode = BX_selection.anchorNode;
        var anchorOffset = BX_selection.anchorOffset;

        if (BX_selection.isCollapsed)	{
            var focusNode = false;
        }
        else {
            var focusNode = BX_selection.focusNode;
            var focusOffset = BX_selection.focusOffset;
        }
    }
    else
    {
        var anchorNode = node;
        var anchorOffset = 0;
        var focusNode =false;
    }

    //	var BX_cursor = document.createElement("span");
    //	BX_cursor.appendChild(document.createTextNode("|"));
    /*    BX_cursor.setAttribute("id","bx_cursor");
    	
    	BX_insertContent(BX_cursor);
    */
    if (anchorNode.nodeName == "#text")
    {
        var cursorNode = anchorNode.parentNode;
    }
    else
    {
        var cursorNode = anchorNode;
    }

    try {var cursorPos = cursorNode.offsetTop + cursorNode.offsetHeight;}
    catch(e) { return;}

    if (cursorPos == 0)
    {
        return ;
    }
    if (cursorPos > (window.innerHeight + window.pageYOffset - 230))
    {
        window.scrollTo(0,cursorPos - window.innerHeight + 270);
    }
    /*	else if (cursorPos < window.pageYOffset - 15)
    	{
    		window.scrollTo(0,cursorPos - 80);

    	}*/
    //	BX_cursor.parentNode.removeChild(BX_cursor);
    BX_selection.collapse(anchorNode,anchorOffset);
    if(focusNode && ( anchorNode != focusNode || focusOffset != anchorOffset))
    {
        BX_selection.extend(focusNode,focusOffset);
    }
}

function BX_range_surroundContents(element)
{

    var frag = BX_range.extractContents();
    element.appendChild(frag);
    BX_range.insertNode(element);
    BX_range.collapse(false);

}

function BX_splitNode(id)
{
    if (id)
    {
        var parent = BX_getElementByIdClean(id,document);
    }
    else
    {
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

function BX_getParentNode(startNode,nodename)
{

    var thisNodeName = "";
    while(startNode && startNode.parentNode && startNode.parentNode.nodeName != "body" && startNode.parentNode.getAttribute("name") != "bitfluxspan")
    {
        startNode = startNode.parentNode;
        thisNodeName = startNode.nodeName;
        var tags = nodename.split(" | ");
        for (var i = 0; i < tags.length; i++)
        {
            if (thisNodeName == tags[i])
            {
                return startNode;
            }
        }
    }
    return false;
}



function BX_node_insertID(node)

{

    if (node == "[object NodeList]")
    {
        node=node.item(0);
    }
    node.setAttribute("id",'BX_id_'+BX_id_counter);
    node.setAttribute("internalid",'yes');
    BX_id_counter++;
    if (BX_elements[node.nodeName] && BX_elements[node.nodeName]["originalName"]){
        node.setAttribute("bx_originalname",BX_elements[node.nodeName]["originalName"]);
    }

}

function BX_popup_node(id)
{
    var node = BX_getElementByIdClean(id,document);

    if (BX_elements[node.nodeName])
    {
        BX_popup_start("Element " +BX_elements[node.nodeName]["name"],0,0);
    }
    else
    {
        BX_popup_start("Element " +node.nodeName,0,0);
    }


    if (node.previousSibling)
    {
        BX_popup_addLine("Move up","javascript:BX_node_move_up('"+id+"')");
    }
    if (node.nextSibling)
    {

        BX_popup_addLine("Move down","javascript:BX_node_move_down('"+id+"')");
    }
    BX_popup_addLine("Cut/Delete","javascript:BX_copy_extractID('"+id+"');BX_popup_hide()");
    //	BX_popup_addLine("Copy","javascript:BX_copy_copyID('"+id+"');BX_popup_hide()");

    /*	if (BX_clipboard)
    	{
    		BX_popup_addLine("Paste","javascript:BX_copy_paste();BX_popup_hide()");
    		BX_popup_addLine("Paste after","javascript:BX_copy_pasteID('"+id+"');BX_popup_hide()");
    	}
    */
    BX_popup_addLine("Edit Source","javascript:BX_source_edit('"+id+"');");

    if (node.attributes.length > 4)
    {
        BX_popup_addLine("Edit Attributes","javascript:BX_infobar_printAttributes(BX_getElementByIdClean('"+id+"',document));BX_up();BX_popup_hide();");
    }

    var parent_nodeName = BX_getCurrentNodeName(node.parentNode);

    if (BX_elements[parent_nodeName] && BX_elements[parent_nodeName]["allowedElements"]) {
        var elements= BX_elements[parent_nodeName]["allowedElements"].split(" | ");
        for (var i = 0 ; i < elements.length; i++)
        {
            if (elements[i] != node.nodeName)
            {
                if (BX_elements[elements[i]])
                {
                    BX_popup_addLine("Change to " + BX_elements[elements[i]]["name"],"javascript:BX_node_changeID('"+id+"','"+elements[i]+"');BX_popup_hide();");
                }
            }
        }
    }

    BX_popup_show();

}

function BX_popup_node_bitfluxspan(id)
{
    var node = document.getElementById(id);

    if (BX_elements[node.nodeName])
    {
        BX_popup_start("Element " +BX_elements[node.nodeName]["name"],0,0);
    }
    else
    {
        BX_popup_start("Element " +node.nodeName,0,0);
    }

    BX_popup_addLine("Edit Source","javascript:BX_source_edit('"+id+"',1);");

    BX_popup_show();

}


function BX_node_move_up (id)
{
    //	var node = BX_getElementByIdClean(id,document);
    var node = document.getElementById(id);

    var anchorNode = BX_selection.anchorNode;
    var anchorOffset = BX_selection.anchorOffset;

    var next = node.previousSibling;

    BX_popup_hide();
    BX_node_clean_bg()	;

    BX_opa_node=node.getAttribute("ID");

    try{node.style.background="#dddddd";}
    catch(e) {}

    while (next != null && (next.nodeName == "#text" || next.childNodes.length == 0))
    {
        next = next.previousSibling;
    }
    if (next != null)
    {
        node.parentNode.insertBefore(node,next);
    }
    //  BX_range_updateToCursor();
    BX_selection.collapse(anchorNode,anchorOffset);
    BX_undo_save();
    BX_scrollToCursor();
    BX_update_buttons = true;
}

function BX_node_move_down (id)
{
    //	var node = BX_getElementByIdClean(id,document);
    var node = document.getElementById(id);

    var anchorNode = BX_selection.anchorNode;
    var anchorOffset = BX_selection.anchorOffset;

    var next = node.nextSibling;
    BX_popup_hide();

    BX_node_clean_bg();

    BX_opa_node=node.getAttribute("ID");
    try{node.style.background="#dddddd";}
    //	try{node.style.borderWidth="thin";}

    catch(e) {}

    while (next != null && next.nodeName == "#text")
    {
        next = next.nextSibling;
    }

    if (next != null)
    {
        node.parentNode.insertBefore(node,next.nextSibling);
    }

    //    BX_range_updateToCursor();
    BX_selection.collapse(anchorNode,anchorOffset);
    BX_undo_save();
    BX_scrollToCursor();
    BX_update_buttons = true;

}

function BX_node_change(node,newNodeName)
{
    var nodeChildren = node.childNodes;

    var newNode = BX_xml.createElementNS("http://www.w3.org/1999/xhtml",newNodeName);
    for (i = 0; i < nodeChildren.length; i++)
    {
        newNode.appendChild(nodeChildren[i].cloneNode(1));
    }

    newNode.setAttribute("id","BX_id_"+BX_id_counter);
    BX_id_counter++;
    newNode.setAttribute("internalid",'yes');

    node.parentNode.replaceChild(newNode,node);

    //	BX_range_updateToCursor();
    BX_undo_save();
    if (BX_elements[newNodeName]["doTransform"])
    {
        BX_transform();
    }
    BX_selection.collapse(newNode,0);
    BX_updateButtons();
    BX_scrollToCursor();

}
function BX_node_changeID(id,newNodeName)
{

    var node = BX_getElementByIdClean(id,document);
    BX_node_change(node,newNodeName);

}

// mozilla can't use getElementById, since it doesn't know, which one is the id...
// this is a hack for that. (i hope it's not to slow)
function BX_getElementById(id,xml) {


    if ( !(BX_xml_nodelist) )
    {

        if (xml)
        {
            BX_xml_nodelist = xml.getElementsByTagName("*");
        }
        else
        {
            BX_xml_nodelist = this.getElementsByTagName("*");
        }
    }

    for (var i=0; i < BX_xml_nodelist.length; i++)
    {
        var idAttr = BX_xml_nodelist[i].getAttribute("id");
        if (!idAttr) continue;
        if (idAttr == id) {
            return BX_xml_nodelist[i];
        }
    }
    return null;
}

function BX_clean_nodelist(xml)
{
    if (xml)
    {
        BX_xml_nodelist = xml.getElementsByTagName("*");
    }
    else
    {
        BX_xml_nodelist = this.getElementsByTagName("*");
    }
}

function BX_getElementByIdClean(id,xml) {

    if (xml)
    {
        BX_xml_nodelist = xml.getElementsByTagName("*");
    }
    else
    {
        BX_xml_nodelist = this.getElementsByTagName("*");
    }


    for (var i=0; i < BX_xml_nodelist.length; i++)
    {
        var idAttr = BX_xml_nodelist[i].getAttribute("id");
        if (!idAttr) continue;
        if (idAttr == id) {
            return BX_xml_nodelist[i];
        }
    }
    return null;
}

/**************************
* popupcreation Functions *
***************************/

function BX_popup_start (title,width,height)
{
    BX_popup.style.visibility = "hidden";
    BX_popup.style.top = mouseY - window.pageYOffset;
    BX_popup.style.left = mouseX - window.pageXOffset;
    if (height == 0)
    {
        BX_popup.style.height = "";
    }
    else
    {
        BX_popup.style.height = height;
    }
    if (width == 0)
    {
        BX_popup.style.width = "";
    }
    else
    {
        BX_popup.style.width = width;
    }
    BX_popup.innerHTML = '';

    var TitelEle = BX_popup.appendChild(document.createElementNS("http://www.w3.org/1999/xhtml","div"));
    TitelEle.setAttribute("style","background-color: #999999; color: #000000;");
    TitelEle.parentNode.addEventListener("mousedown",BX_beginDrag, false);
    TitelEle.setAttribute("dragable","yes");
    TitelEle.innerHTML= '<img src="./'+BX_root_dir+'/img/space.gif" width="2" height="10" border="0" /><a href="javascript:BX_addEvents();BX_popup_hide();" style="color: #ffffff;" class="usualBlackTd">x</a><img src="'+BX_root_dir+'img/space.gif" width="10" height="10" border="0" /><i dragable="yes">'+title+'</i>';

}

function BX_popup_addHtml(html)

{

    BX_popup.innerHTML += html+"\n";
}

function BX_popup_show ()
{
    if ((BX_popup.offsetTop + BX_popup.offsetHeight) > window.innerHeight)
    {
        BX_popup.style.top = window.innerHeight - BX_popup.offsetHeight- 15 +"px";
    }
    BX_popup.style.visibility = "visible";
    BX_popup.style.top=BX_popup.offsetTop - 1 + "px";

}

function BX_popup_hide()
{
    BX_popup.style.top = window.innerHeight + "px";
    BX_popup.style.visibility = "hidden";

    //    BX_popup.parentNode.removeChild(BX_popup);


}


function BX_popup_addLine (text,link,addHrefTags)
{

    //	BX_popup.innerHTML += '<a href="'+link+'" '+ addHrefTags +' class="popupline"><div class="popupline"><img src="img/space.gif" width="16" height="10" border="0">'+text+'</div></a>';
    BX_popup.innerHTML += '<a href="'+link+'" '+ addHrefTags +' class="popupline"><div class="popupline">&#160;&#160;&#160;&#160;'+text+'</div></a>';
}

function BX_popup_addHr ()
{
    //	BX_popup.innerHTML += '<hr width="10%"/>';
    BX_popup.innerHTML += '<center>----</center>';
}
/******************************
* Copy/Paste Stuff            *
*******************************/
function BX_copy_copy()

{
    //    window.defaultStatus += "copy";
    BX_clipboard = BX_range.cloneContents();
    var childNodes = BX_clipboard.childNodes;
    for (var i = 0; i < childNodes.length; i++)
    {
        if (childNodes[i].nodeName == "SPAN")
        {
            childNodes[i].parentNode.removeChild(childNodes[i]);
            i = childNodes.length;
        }
    }


}

function BX_copy_copyID(id)

{
    //    window.defaultStatus += "copy";

    BX_clipboard = document.getElementById(id).cloneNode(1);
    var oldCursor = BX_clipboard.getElementsByTagName('span');
    if (oldCursor.item(0))
    {
        oldCursor.item(0).parentNode.removeChild(oldCursor.item(0));
    }
}

function BX_copy_extractID(id)

{
    //    window.defaultStatus += "copy";
    var node = BX_getElementByIdClean(id,document);
    BX_clipboard = node.cloneNode(1);
    node.parentNode.removeChild(node);

    var oldCursor = BX_clipboard.getElementsByTagName('span');
    if (oldCursor.item(0))
    {
        oldCursor.item(0).parentNode.removeChild(oldCursor.item(0));
    }
    BX_undo_save();

}


function BX_copy_extract()
{
    BX_clipboard = BX_range.cloneContents();
    BX_range.extractContents();
    BX_undo_save();
}

function BX_copy_pasteID(id,before)
{
    var thisNode = document.getElementById(id);
    if (before)
    {
        var newNode = thisNode.parentNode.insertBefore(BX_clipboard.cloneNode(true),thisNode);
    }
    else
    {
        var newNode = thisNode.parentNode.insertBefore(BX_clipboard.cloneNode(true),thisNode.nextSibling);
    }
    newNode.setAttribute("id","BX_id_"+BX_id_counter);
    BX_id_counter++;
    BX_undo_save();

    BX_range_updateToCursor();
    BX_updateButtons();

}

function BX_copy_paste()
{
    //    window.defaultStatus += "paste";
    BX_range.extractContents();
    /*var end = BX_range.endContainer;
    var start = BX_range.startContainer;
    var endO = BX_range.endOffset;
    var startO = BX_range.startOffset;
    */
    var cb = BX_clipboard.cloneNode(true);
    /**
    * there's a bug in mozilla (http://bugzilla.mozilla.org/show_bug.cgi?id=76895)
    * which prevents from cloning documentFragments
    * if the build has this bug, we just insert BX_clipboard, but then we can't insert more
    * than once .(
    *
    * as of 2001-04-15 it works under linux, but not under mac and windows (0.9.9)
    */
    if(cb.xml)
    {
        BX_insertContent(cb);
    }
    else
    {
        BX_insertContent(BX_clipboard);
    }

    /*	BX_range.setStart(start,startO);
    	BX_range.setEnd(end,endO+1);
        BX_selection.addRange(BX_range);*/

    BX_undo_save();
    if (BX_range.startContainer.nodeName == "section")
    {
        BX_range.selectNodeContents(BX_range.startContainer.childNodes[0]);
    }
    //    BX_cursor_update();
    BX_updateButtons();

}

function BX_node_clean_bg()
{
    if (BX_opa_node != null)
    {
        var Opa_node = BX_getElementByIdClean(BX_opa_node,document);
        Opa_node.style.background = "transparent";
        Opa_node.style.removeProperty("background",false);
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
    if (	navigator.platform.indexOf("Linux") >= 0)
    {
        var newX = parseInt(window.BX_popupLeft) +difX/2;
        var newY = parseInt(window.BX_popupTop) +difY/2;
    }
    else
    {
        var newX = parseInt(window.BX_popupLeft) +difX;
        var newY = parseInt(window.BX_popupTop) +difY;

    }
    // Sets the new position for the editcanvas div element.
    // Note: the table was created inside editcanvas div element;
    // this way, all editcanvas's child elements are affected by
    // positioning.
    //

    if (newY < 0) {newY = 0};
    if (newX < 0) {newX = 0};
    if (newY > window.innerHeight - 10) { newY = window.innerHeight - 10;}
    if (newX > window.innerWidth - 100) { newX = window.innerWidth - 100;}

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
        if (! ((e.target.parentNode.nodeType == e.target.parentNode.ELEMENT_NODE && e.target.parentNode.getAttribute("dragable") == "yes") || (e.target.nodeType == e.target.ELEMENT_NODE &&   e.target.getAttribute("dragable") == "yes")))
        {
            return false;
        }
    }
    catch(e) { return false;}

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


function BX_onContextMenuImg(e){

    var xref = e.parentNode;
    BX_popup_start("Image",0,0);
    BX_popup_addLine("Copy Image","javascript:BX_copy_copyID('"+xref.id+"');BX_popup_hide()");
    BX_popup_addLine("Cut Image","javascript:BX_copy_extractID('"+xref.id+"');BX_popup_hide()");
//    BX_popup_addLine("Edit Image","javascript:BX_open_ImageEdit(BX_getElementByIdClean('"+xref.id+"',document).getAttribute('linkend').replace(/Mediaobject/,''))");
    if (BX_clipboard && BX_clipboard.nodeName == "xref")
    {
        BX_popup_addLine("Paste Clipboard Image After","javascript:BX_copy_pasteID('"+xref.id+"');BX_popup_hide()");
        BX_popup_addLine("Paste Clipboard Image Before","javascript:BX_copy_pasteID('"+xref.id+"',1);BX_popup_hide()");
    }

    if (xref.parentNode.nodeName == "mediagroup")
    {
        BX_popup_addLine("Copy Mediagroup","javascript:BX_copy_copyID('"+xref.parentNode.id+"');BX_popup_hide()");
        BX_popup_addLine("Cut Mediagroup","javascript:BX_copy_extractID('"+xref.parentNode.id+"');BX_popup_hide()");
    }

    BX_popup_show();

	BX_range.selectNodeContents(xref);
	BX_updateButtons();

}


function BX_up()
{

    BX_removeEvents();
    BX_infotext.style.visibility = "hidden";
    BX_infotext2.style.visibility = "visible";
    BX_infobar.style.top = window.innerHeight - 200;
    BX_infobar.style.height = 200;
    BX_range= null;
}

function BX_down()
{

    BX_infotext2.style.visibility = "hidden";
    BX_infotext.style.visibility = "visible";
    BX_infobar.style.top = window.innerHeight - 30;
    BX_infobar.style.height = 30;
    BX_addEvents();
}

function BX_popup_link()

{

    BX_popup_start("Add Link",0,0);
    BX_popup_addLine("External Link","javascript:BX_popup_addTagWithAttributes('ulink','url','http://');");
    BX_popup_addLine("Email","javascript:BX_add_tag('email'),BX_popup_hide();");
    BX_popup_show();

}

function BX_removeEvents()
{
    document.removeEventListener("keypress",BX_keypress,false);
    document.removeEventListener("keyup",BX_onkeyup,false);

    //    var allSpans = document.getElementsByName("bitfluxspan");
    /*    for (i = 0; i < allSpans.length; i ++)
        {
            allSpans[i].removeEventListener("mouseup", BX_RangeCaptureOnMouseUp, false );

        }
    */
    BX_transformLocation.removeEventListener("mouseup", BX_RangeCaptureOnMouseUp, false);
}


function BX_popup_addTagWithAttributes(tag,attributes,defaults)

{
    BX_popup_hide();
    BX_popup_start( tag,0,0);
    attributes = attributes.replace(/\s+/g,"").split("|");
    defaults = defaults.replace(/\s+\|/g,"|").replace(/\|\s+/g,"|").split("|");

    var html = "<form name='addtag'>";
    html += "<table class=\"usualBlackTd\">\n";
    html += "<tr ><td>Content: </td>\n";
    if (BX_range.toString().length > 0)
    {
        html += "<td><input type='text' name='content' value='"+BX_range.toString()+"'></td>";
    }
    else
    {
        html += "<td><input type='text' name='content' value='#"+tag+"#'></td>";
    }
    for (var i = 0; i < attributes.length; i++)
    {
        html += "<tr ><td>" + attributes[i] + ": </td>\n";
        html += "<td><input type='text' name='"+attributes[i]+"' value='"+defaults[i]+"'></td>";
    }

    html += "<tr><td></td><td><input onclick='BX_popup_insertTagWithAttributes(\""+tag+"\");' type='button' value='insert'/></td></tr>";
    html += "</table>";
    html +="</form>";
    BX_popup_addHtml(html);

    document.removeEventListener("keypress",BX_keypress,false);
    document.removeEventListener("keyup",BX_onkeyup,false);


    BX_popup_show();
    BX_popup.style.top=BX_popup.offsetTop - 1 + "px";

}


function BX_popup_insertTagWithAttributes(tag)
{
    //    window.defaultStatus="";
    if (BX_popup.style.visibility != 'visible')
    {
        return;
        document.addEventListener("keypress",BX_keypress,false);
        document.addEventListener("keyup",BX_onkeyup,false);

    }
    var attributes = new Array();

    for (var i = 0; i < document.forms.addtag.length; i++)
    {
        if (document.forms.addtag[i].name == "content")
        {
            var content = document.forms.addtag[i].value;
        }
        else if (document.forms.addtag[i].name)
        {
            attributes[document.forms.addtag[i].name] = document.forms.addtag[i].value;
        }
    }
    BX_add_tagWithAttributes(tag,content,attributes);
    BX_popup_hide();
    /*	BX_popup.innerHTML="<span></span>";*/
    document.addEventListener("keypress",BX_keypress,false);
    document.addEventListener("keyup",BX_onkeyup,false);

}

function BX_add_tagWithAttributes(tag,content,attributes)
{
    var element = BX_xml.createElementNS("http://www.w3.org/1999/xhtml",tag);
    element.appendChild(BX_xml.createTextNode(content));

    for (var attName in attributes)
    {
        element.setAttribute(attName,attributes[attName]);
    }
    element.setAttribute("id","BX_id_"+BX_id_counter);
    element.setAttribute("internalid",'yes');
    BX_id_counter++;

    BX_insertContent(element);
    BX_scrollToCursor(element);
    BX_selection.selectAllChildren(element);
    BX_update_buttons = true;

}

function BX_source_edit(id, selectNodeContents)
{

    if (BX_range)
    {

        if (!BX_xslViewSource || !BX_xslViewSource.documentElement)
        {

            if (!BX_xslViewSource) {
                BX_xslViewSource = document.implementation.createDocument("","",null);
                BX_xslViewSource.load(BX_xslViewSourceFile);

            }
            //        	BX_xslViewSource.onload = null;  // set the callback when we are done loading
            window.setTimeout("BX_source_edit('"+id+"',"+selectNodeContents+")",50);
            return;
        }

        //		var edit_element = document.getElementById(id).cloneNode(true);
        var edit_element = BX_xml.implementation.createDocument("","",null);
        edit_element.appendChild(document.getElementById(id).cloneNode(true));
        if (edit_element) {
            var _new = BX_xml.implementation.createDocument("","",null);
            BX_xsltProcessor.transformDocument( edit_element, BX_xslViewSource, _new, null);
        }
        else { BX_popup_hide(); return; }

        //	BX_clipboard = document.getElementById(id).cloneNode(1);
        //	document.getElementById(id).parentNode.removeChild(document.getElementById(id));
        //	BX_selection.selectAllChildren(document.getElementById(id));
        BX_popup_start("Edit Source ",500,0);

        var html = ' <center class="text"><form name="clipboard">';
        html += '<input class="buttonklein" type="button" value="Append CDATA" onClick=\'BX_clipboard_insertCDATA()\'>&#160;';

        html += '<textarea name="text" class="clipboardtext" style="margin: 10px;" wrap="virtual" cols="70" rows="28"></textarea>';
        html += '<input class="text" type="button" value="update" onClick=\'BX_source_insert("'+id+'",'+selectNodeContents+')\'>';
        html += '</form></center>';
        BX_popup_addHtml(html);
        if(_new.documentElement)
        {
            if (selectNodeContents )
            {
                childLength = _new.documentElement.childNodes.length
                              for (var i = 0; i < childLength; i++)
                              {
                                  document.forms.clipboard.text.value += calculateMarkup(_new.documentElement.childNodes[i],true);
                              }
                          }
                          else
                          {
                              document.forms.clipboard.text.value += calculateMarkup(_new.documentElement,true);
                          }
                      }

                      document.removeEventListener("keypress",BX_keypress,false);
        document.removeEventListener("keyup",BX_onkeyup,false);

        document.forms.clipboard.text.focus();
        BX_popup_show();
        //fix for mozilla on mac and windows...
        BX_popup.style.top=BX_popup.offsetTop - 1 + "px";
    }
}
function BX_clipboard_insertCDATA()
{

    document.forms.clipboard.text.value = document.forms.clipboard.text.value + "<![CDATA[\n\n]]>";
    document.forms.clipboard.addparas.checked = false;

}

function BX_source_insert(id,selectNodeContents)
{
    var oldnode = document.getElementById(id);
    BX_clipboard_copyToBX_clipboard();
    if (selectNodeContents)
    {
        var childLength = oldnode.childNodes.length;
        for(var i = 0; i < childLength; i++)
        {
            window.defaultStatus += " " +i;
            oldnode.removeChild(oldnode.childNodes[0]);
        }
        oldnode.appendChild(BX_clipboard);
    }
    else
    {
        oldnode.parentNode.replaceChild(BX_clipboard,oldnode);
    }

    BX_transform();

}


function BX_clipboard_open()
{
    if (BX_range)
    {
        BX_popup_start("Clipboard",400,0);

        var html = ' <center class="text"><form name="clipboard">';
        html += '<input class="buttonklein" type="button" value="Append CDATA" onClick=\'BX_clipboard_insertCDATA()\'>&#160;';
        html += '<textarea name="text" class="clipboardtext" style="margin: 10px;" wrap="virtual" cols="50" rows="20"></textarea><br>';
        html += '<input class="text" type="button" value="Insert" onClick="BX_clipboard_copyToBX_clipboard();BX_copy_paste();BX_transform();">';
        //		html += '<input class="text" type="button" value="only copy" onClick="BX_clipboard_copyToBX_clipboard();">';
        var current_node_name  = BX_getCurrentNodeName(BX_range.startContainer);
        if (BX_elements[current_node_name] && ! BX_elements[current_node_name]["noAddParas"])
        {
            html += '<input class="text" name="addparas" checked="checked" type="checkbox" >Add Paras';
        }
        html += '</form></center>';
        BX_popup_addHtml(html);

        if(BX_clipboard )
        {
            document.forms.clipboard.text.value = calculateMarkup(BX_clipboard,true);
        }

        document.removeEventListener("keypress",BX_keypress,false);
        document.removeEventListener("keyup",BX_onkeyup,false);

        document.forms.clipboard.text.focus();
        BX_popup_show();
        //fix for mozilla on mac and windows...
        BX_popup.style.top=BX_popup.offsetTop - 1 + "px";
    }
}

function BX_clipboard_copyToBX_clipboard ()
{
    BX_range.extractContents();
    var toBeInserted = document.forms.clipboard.text.value.replace(/< /g,"&lt; ");
    toBeInserted = toBeInserted.replace(/\& /g,"&amp; ");
    if (document.forms.clipboard.addparas && document.forms.clipboard.addparas.checked)
    {
        toBeInserted = "<"+BX_para_element+">" + toBeInserted.replace(/\n/g,"</"+BX_para_element+"><"+BX_para_element+">")+"</"+BX_para_element+">";
        BX_splitNode();
    }

    toBeInserted = BX_parser.parseFromString("<?xml version='1.0'?><rooot>"+toBeInserted+"</rooot>","text/xml");
    if(toBeInserted.documentElement.nodeName=="parsererror")
    {
        var alerttext = "Parse Error: \n \n";
        alerttext += toBeInserted.documentElement.firstChild.data +"\n\n";
        alerttext += "Sourcetext:\n\n";
        alerttext += toBeInserted.documentElement.childNodes[1].firstChild.data;

        alert(alerttext);
        return false;
    }
    toBeInserted = toBeInserted.childNodes[0];
    for (var i = 0; i < toBeInserted.childNodes.length; i++)
    {
        if (toBeInserted.childNodes[i].nodeName != "#text" && toBeInserted.childNodes[i].nodeName != "#cdata-section")
        {
            toBeInserted.childNodes[i].setAttribute("id","BX_id_"+BX_id_counter);
            toBeInserted.childNodes[i].setAttribute("internalid",'yes');
            BX_id_counter++;
        }
    }
    BX_tmp_r1 = document.createRange();
    BX_tmp_r1.selectNodeContents(toBeInserted);
    BX_clipboard = BX_tmp_r1.extractContents();
    BX_popup_hide();
    document.addEventListener("keypress",BX_keypress,false);
    document.addEventListener("keyup",BX_onkeyup,false);

    BX_tmp_r1.detach();
}


function BX_plain()
{
    BX_get_selection();
    BX_undo_save();
    var content = BX_range.toString();
    BX_range.extractContents();
    //create a temporary elemen
    var element = BX_xml.createElementNS("http://www.w3.org/1999/xhtml","span");
    var frag = BX_range.extractContents();
    element.appendChild(frag);
    var new_node = BX_insertContent(element);
    BX_range.selectNode(new_node);

    while (BX_range.startContainer.nodeName=="bold" || BX_range.startContainer.nodeName=="emphasis" ||
            BX_range.startContainer.nodeName=="emphasize" ||
            BX_range.startContainer.nodeName=="ulink" ||
            BX_range.startContainer.nodeName=="subscript" ||
            BX_range.startContainer.nodeName=="superscript"
          )
    {
        //split the stuff
        BX_splitNode();
    }
    var bla = 	BX_range.createContextualFragment(content);
    BX_insertContent(bla);
    BX_selection.collapse(BX_selection.focusNode, BX_selection.focusOffset );
    BX_transform();

}

function BX_RangeCaptureOnContextMenu(target) {


    try {


        if (!BX_range)
        {
            BX_range = document.createRange( );
            BX_range.selectNode(target);
            BX_range.collapse(false);
        }
        if (BX_elements[target.nodeName] && BX_elements[target.nodeName]["altMenu"] && BX_find_bitfluxspanNode(target))
        {

            eval(BX_elements[target.nodeName]["altMenu"]+"(target)");
            return;
        }


        target = BX_range.startContainer;
        if (target.nodeName == "#text") { target = target.parentNode;}
        if (target.getAttribute("nodename")) {
            var current_nodename = target.getAttribute("nodename");
        }
        else
        {
            var current_nodename = target.nodeName;
        }

        if (BX_elements[current_nodename])
        {

            BX_popup_start(BX_elements[current_nodename]["name"],0,0);
            if (BX_elements[current_nodename])
            {

                var elements = BX_elements[current_nodename]["allowedElements"].split(" | ");
                for (var i = 0; i < elements.length; i++)
                {

                    if (elements[i] != "#PCDATA")
                    {
                        if (BX_elements[elements[i]])
                        {
                            BX_popup_addLine(BX_elements[elements[i]]["name"],"javascript:BX_add_tag('"+elements[i]+"');");
                        }
                    }
                }

            }
            if (target.getAttribute("name") != "bitfluxspan")
            {
                BX_popup_addHr();

                do
                {
                    if (target.getAttribute("nodename")) {
                        var current_nodename = target.getAttribute("nodename");
                    }
                    else
                    {
                        var current_nodename = target.nodeName;
                    }

                    if (BX_elements[current_nodename] && BX_elements[current_nodename]["insertAfter"])
                    {
                        var elements = BX_elements[current_nodename]["insertAfter"].split(" | ");
                        for (var i = 0; i < elements.length; i++)
                        {

                            if (BX_elements[elements[i]])
                            {
                                if (BX_elements[target.nodeName] && BX_elements[target.nodeName]["name"])
                                {
                                    var targetName =  BX_elements[target.nodeName]["name"];
                                }
                                else
                                {
                                    var targetName = target.nodeName;
                                }
                                var targetId = target.getAttribute("id");
                                BX_popup_addLine("Insert " +BX_elements[elements[i]]["name"] + " after " + targetName + "&#160;" ,"javascript:BX_add_tag('"+elements[i]+"','"+targetId+"') ", "onmouseover=\"BX_show_node('" +targetId+ "')\" onmouseout=\"BX_hide_node('" +targetId+ "')\"");
                            }
                        }

                    }
                    target = target.parentNode;

                }
                while ( target.getAttribute("name")!= "bitfluxspan");
            }
        }
        else
        {
            BX_popup_start("Element " + current_nodename + " not defined",0,0);
        }

        BX_popup_show();
    }
    catch(err)
    {
        BX_errorMessage(err);
    };
    BX_updateButtons();


}

function BX_errorMessage(e)
{
    if (BX_debugMessage)
    {
        var mes = "ERROR:\n"+e.message +"\n";
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
        try
        {
            mes += "BX_range.startContainer: " + BX_range.startContainer.nodeName + "\n";
        }
        catch(e) {}

        mes += "Type: " + e.name + "\n";
        mes += "Stack:" + e.stack + "\n";
        var confirm = "\nDo you want to open it in a window (for copy&paste) ?\n (press Cancel if No)";
        if (window.confirm(mes + confirm))
        {

            var BX_error_window = window.open("","_blank","");
            BX_error_window.document.innerHTML = "";
            BX_error_window.document.writeln("<pre>");
            mes += "UserAgent: "+navigator.userAgent +"\n";
            mes += "bitfluxeditor.js Info: $Revision: 1.1 $  $Name:  $  $Date: 2002/09/13 20:26:49 $ \n";
            BX_error_window.document.writeln(mes);
            mes = "\nError Object:\n\n";
            for (var b in e)
            {

                mes += b;
                try {

                    mes +=  ": "+e.eval(b) ;
                }
                catch(e)
                {
                    bla += ": NOT EVALED";
                };

                mes += "\n";        }

            BX_error_window.document.writeln(mes);



        }
    }

    else {

        BX_infoerror.innerHTML = 	"ERROR:\n"+e.message +"\n";
        if (BX_infoerror_timeout)
        {
            BX_infoerror_timeout.clearTimeout();
        }
        BX_infoerror_timeout = window.setTimeout("BX_clearInfoError()",10000);
    }
}


function BX_find_bitfluxspanNode(node )
{
    while(node.nodeName == "#text" || (node.nodeName != "body" && node.getAttribute("name") != "bitfluxspan"))
    {
        node = node.parentNode;
    }
    if (node.getAttribute("name") == "bitfluxspan")
    {
        return node;
    }
    else
    {
        return false;
    }


}

function BX_show_node(id)
{
    var node = BX_getElementByIdClean(id,document);
    node.style.background = "#aaaaaa";
    //	try{node.style.borderWidth="thin";}
    //	catch(e){};
}


function BX_hide_node(id)
{
    var node = BX_getElementByIdClean(id,document);
    node.style.background = "transparent";
}

function BX_show_xml(node)
{
    alert(calculateMarkup(node,true));
}


function BX_init_buttonBar()
{
    BX_buttonbar = document.getElementById("BX_buttonbar");
    BX_buttonbar.style.width = window.innerWidth;

    var result = BX_config.evaluate("/config/buttons//*", BX_config, null, 0, null);
    var node;
    var resultArray = new Array();
    var i = 0;
    BX_buttonbar = BX_buttonbar.appendChild(document.createElement("table"));
    BX_buttonbar.setAttribute("cellpadding",0);
    BX_buttonbar.setAttribute("cellspacing",0);
    BX_buttonbar.setAttribute("border",0);
    while (node = result.iterateNext())
    {
        i++;
        if (node.nodeName == "button")
        {
            if (node.getAttribute("type") == "register")
            {
                BX_registerButton(node.getAttribute("tag"),node.getAttribute("name"),node.getAttribute("width"),node.getAttribute("height"),node.getAttribute("title"),eval(node.getAttribute("options")),node.getAttribute("callback"));
            }
            else if (node.getAttribute("type") == "graph")
            {
                BX_buttonbar.appendChild(BX_printButton(node.getAttribute("name"),node.getAttribute("width")));
            }
            else if (node.getAttribute("type") == "link")
            {
                BX_buttonbar.appendChild(document.createTextNode(" "));
                var ahref = document.createElement("a");
                ahref.setAttribute("href",node.getAttribute("href"));

                ahref.appendChild(document.createTextNode(node.getAttribute("text")));
                BX_buttonbar.appendChild(ahref);
            }
        }
        else if (node.nodeName == "row")
        {
            if (BX_buttonbar.nodeName.toLowerCase() == "table")
            {
                BX_buttonbar = BX_buttonbar.appendChild(document.createElement("tr"));
            }
            else //it's a td
            {
                BX_buttonbar = BX_buttonbar.parentNode.parentNode.appendChild(document.createElement("tr"));
            }

        }

        else if (node.nodeName == "cell")
        {

            if (BX_buttonbar.nodeName.toLowerCase() == "tr")
            {
                BX_buttonbar = BX_buttonbar.appendChild(document.createElement("td"));
            }
            else
            {
                BX_buttonbar = BX_buttonbar.parentNode.appendChild(document.createElement("td"));
            }
			
            if (node.getAttribute("align"))
            {
                BX_buttonbar.setAttribute("align",node.getAttribute("align"));
            }
            if (node.getAttribute("colspan"))
            {
                BX_buttonbar.setAttribute("colspan",node.getAttribute("colspan"));
            }
            BX_buttonbar.setAttribute("valign","absmiddle");
        }
    }
}


function BX_init_page()
{

    var html = '<div id="BX_buttonbar" style="height: 65px; position: fixed; background-color: #b4b4b4; z-index: 5; margin-left: -7px;"></div>';
    html += '<div  style="position:absolute; top:70; left: 5; z-index: 3;" id="transformLocation" ></div>'
            html += '<div id="BX_infobar" style="visibility: hidden; top: 530px; height: 45px; width: 500px; position: fixed; background-color: #b4b4b4; z-index: 5; margin-left: -10px;">';
    html += '<div id="BX_infotext" style="width: 95%; position: absolute; top: 0px; margin-left: 5px; ">';
    html += '<span id="BX_infoerror" style="float: right; z-index: 400; color: red; text-align: right; "></span>';
    html += '<div id="BX_infotext_tags"> </div>';
    html += '<div id="BX_infotext_attr"> </div>';
    html += '<div id="BX_infotext2" style="position: absolute; top: 0px; margin-left: 5px; visibility: hidden; "> </div>';
    html += '<div id="BX_popup" class="popup" style="visibility: hidden; top: 130px; height: 30px; width: 500px; position: fixed; background-color: #b4b4b4; z-index: 40;"></div>';
    html += '<form name="poster" action="/admin/wysiwyg_config/php/insertintodb.php" method="post" style="visibility: hidden;">';
    html += '<input name="content" type="hidden">';
    html += '</form>';
    document.getElementsByTagName("body")[0].innerHTML = html;

}


function BX_xml_removeWhiteSpaceNodes(node)
{	
	var l = node.childNodes.length -1 ;
	for (var i = l; i >= 0; i--)
	{

	   switch(node.childNodes[i].nodeType)
	   {

			case node.TEXT_NODE:
			if (node.childNodes[i].data.replace(/[\s\n\r]*/,"").length  == 0)
			{
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
