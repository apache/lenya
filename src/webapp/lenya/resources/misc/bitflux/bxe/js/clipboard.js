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
// $Id: clipboard.js,v 1.4 2002/11/23 11:47:33 felixcms Exp $
/**
 * @file
 * Implements some Clipboard functions
 *
 * The functions here will go into some classes 
 *  and uses some Widget classes.
 * It's not decided yet, how exactly we do that.
 */
function BX_clipboard_insertCDATA()
{

    document.forms.clipboard.text.value = document.forms.clipboard.text.value + "<![CDATA[\n\n]]>";
    document.forms.clipboard.addparas.checked = false;

}


function BX_clipboard_open()
{
    if (BX_range)
    {
        BX_popup_start("Clipboard",400,0);

        var html = ' <center class="text"><form id="bx_form_clipboard" name="clipboard">';
        html += '<input class="buttonklein" type="button" value="Append CDATA" onclick=\'BX_clipboard_insertCDATA()\' />&#160;<br/>';
        html += '<textarea name="text" class="clipboardtext" style="margin: 10px;" wrap="virtual" cols="50" rows="20"></textarea><br/>\n';
        html += '<input xmlns="http://www.w3.org/1999/xhtml" class="text" type="button" value="Insert" onclick="BX_clipboard_copyToBX_clipboard();BX_copy_paste();BX_transform();" />\n';
        //		html += '<input class="text" type="button" value="only copy" onClick="BX_clipboard_copyToBX_clipboard();">';
        var current_node_name  = BX_getCurrentNodeName(BX_range.startContainer);
        if (BX_elements[current_node_name] && ! BX_elements[current_node_name]["noAddParas"])
        {
            html += '<input class="text" name="addparas" checked="checked" type="checkbox" />Add Paras';
        }
        html += '</form></center>';
        BX_popup_addHtml(html);

        if(BX_clipboard )
        {
            document.getElementById("bx_form_clipboard").text.value = calculateMarkup(BX_clipboard,true);
        }

        document.removeEventListener("keypress",BX_keypress,false);
        document.removeEventListener("keyup",BX_onkeyup,false);
		BX_no_events = true;
        document.getElementById("bx_form_clipboard").text.focus();
        BX_popup_show();
        //fix for mozilla on mac and windows...
        BX_popup.style.top=BX_popup.offsetTop - 1 + "px";
    }
}

function BX_clipboard_copyToBX_clipboard ()
{
    BX_range.extractContents();
    var toBeInserted = document.getElementById("bx_form_clipboard").text.value.replace(/< /g,"&lt; ");
    toBeInserted = toBeInserted.replace(/\& /g,"&amp; ");
    if (document.getElementById("bx_form_clipboard").addparas && document.getElementById("bx_form_clipboard").addparas.checked)
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
            toBeInserted.childNodes[i].setAttribute("bxe_internalid",'yes');
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

// for whatever reason, jsdoc needs this line
