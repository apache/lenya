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
// $Id: bitfluxcms.js,v 1.1 2002/09/13 20:26:49 michicms Exp $
var BX_id_counter = 0;
var BX_wysiwyg_loaded = 0;

function alertXML()
{
	BX_xml_source= BX_getResultXML();
	var BX_source_window = window.open(BX_root_dir+"showsource/index.html","_blank","");
}


function BX_preview()
{
	if (BX_doc_changed)
    {
	    document.forms.poster.content.value = calculateMarkup(BX_getResultXML(),true);
    	document.forms.poster.submit();
        BX_doc_changed = false;
	}
    var windowName = "BX_preview"
	if (window.name == "BX_preview")
    {
    	windowName = "BX_preview2";
	}
    BX_win = window.open("/?Section=" + BX_SectionID + "&Document=" +BX_DocumentID,windowName,"");
}

function BX_reset()
{
    //	BX_debug(window);
    location.reload();
}

function BX_index()
{
	window.location = "/admin/editor/";
}

function BX_popup_titel()
{

    if (!BX_getParentNode(BX_range.startContainer,"note"))
    {
        BX_popup_start("Headline",0,0);
        BX_popup_addLine("Headline 1","javascript:BX_add_tag('hl1',false,true);BX_popup_hide();");
        BX_popup_addLine("Headline 2","javascript:BX_add_tag('hl2',false,true);BX_popup_hide();");
        BX_popup_addLine("Headline 3","javascript:BX_add_tag('hl3',false,true);BX_popup_hide();");
        BX_popup_show();
    }
    else
    {
        BX_add_tag('titel2',false,true);
        BX_popup_hide();
    }


}

function BX_image_popup ()
{

    window.open("./"+BX_root_dir+"/insertpicture/html/insertimage.php?ID="+BX_DocumentID,"sc",'toolbar=no,width=550,height=550,scrollbars=no,resizable=yes','articleinfo_keywordset');
}
