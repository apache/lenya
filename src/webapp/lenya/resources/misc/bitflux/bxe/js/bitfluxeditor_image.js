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
// $Id: bitfluxeditor_image.js,v 1.1 2002/10/24 14:44:31 felixcms Exp $



function BX_onContextMenuImg(e){

    var xref = e;
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

function BX_image_popup ()
{

    window.open("./"+BX_root_dir+"/insertpicture/index.html","image",'toolbar=no,width=550,height=350,scrollbars=no,resizable=yes','');
}

// for whatever reason, jsdoc needs this line
