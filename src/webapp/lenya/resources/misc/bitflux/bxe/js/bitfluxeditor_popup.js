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
// $Id: bitfluxeditor_popup.js,v 1.4 2002/11/23 11:47:33 felixcms Exp $
/**
 * @file
 * Implements some Popup functions
 *
 * The functions here will go into some Widget Classes.
 * It's not decided yet, how exactly we do that.
 */


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

	var prevnode = node;
	while (prevnode = prevnode.previousSibling) {
		if (prevnode.nodeType == 1 || (prevnode.nodeType == 3 && /[^\t\n\r\s]/.test(prevnode.data))) { break;}
	}
    if (prevnode && ("nodeType" in prevnode) ) {
        BX_popup_addLine("Move up","javascript:BX_node_move_up('"+id+"')");
    }

	var prevnode = node;
	while (prevnode = prevnode.nextSibling) {
		if (prevnode.nodeType == 1 || (prevnode.nodeType == 3 && /[^\t\n\r\s]/.test(prevnode.data))) { break;}
	}
    if (prevnode && ("nodeType" in prevnode) ) {
        BX_popup_addLine("Move down","javascript:BX_node_move_down('"+id+"')");
    }
	
    BX_popup_addLine("Cut/Delete","javascript:BX_copy_extractID('"+id+"');BX_popup_hide()");
    BX_popup_addLine("Copy","javascript:BX_copy_copyID('"+id+"');BX_popup_hide()");

    if (BX_clipboard)
    	{
    		BX_popup_addLine("Paste","javascript:BX_copy_paste();BX_popup_hide()");
    		BX_popup_addLine("Paste after","javascript:BX_copy_pasteID('"+id+"');BX_popup_hide()");
    	}
    
    BX_popup_addLine("Edit Source","javascript:BX_source_edit('"+id+"');");
    if (node.attributes.length > 4) {
        BX_popup_addLine("Edit Attributes","javascript:BX_infobar_printAttributes(BX_getElementByIdClean('"+id+"',document));BX_up();BX_popup_hide();");
    }

	var parent_nodeName = BX_getCurrentNodeName(node.parentNode);
    if (BX_elements[parent_nodeName] && BX_elements[parent_nodeName]["allowedElements"].indexOf("#PCDATA") > -1) 
	{
	    BX_popup_addLine("Make Plain","javascript:BX_popup_makePlain(BX_getElementByIdClean('"+id+"',document))");
		if (node.childNodes.length > 1 || (node.childNodes.length == 1 &&  node.childNodes[0].nodeType == 1 )) {
			BX_popup_addLine("Make all Plain","javascript:BX_popup_makePlain(BX_getElementByIdClean('"+id+"',document),true)");
		}
	}
    
	
    

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
    var node = BX_getElementById(id,document);

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


/**************************
* popupcreation Functions *
***************************/

function BX_popup_start (title,width,height)
{
    BX_popup.style.visibility = "hidden";
    BX_popup.style.top = mouseY - window.pageYOffset + "px";
    BX_popup.style.left = mouseX - window.pageXOffset + "px";
    if (height == 0)
    {
        BX_popup.style.height = "";
    }
    else
    {
        BX_popup.style.height = height + "px";
    }
    if (width == 0)
    {
        BX_popup.style.width = "";
    }
    else
    {
        BX_popup.style.width = width;
    }
    BX_innerHTML(BX_popup,'');

    var TitelEle = BX_popup.appendChild(document.createElementNS("http://www.w3.org/1999/xhtml","div"));
    TitelEle.setAttribute("class","popupTitle");
    TitelEle.parentNode.addEventListener("mousedown",BX_beginDrag, false);
    TitelEle.setAttribute("dragable","yes");
    BX_innerHTML(TitelEle,'&#160;<a href="javascript:BX_addEvents();BX_popup_hide();" style="color: #ffffff;" class="usual">x</a>&#160;<i dragable="yes">'+title+'</i>&#160;');

}

function BX_popup_addHtml(html)

{

    BX_innerHTML(BX_popup,html+"\n",true);
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
	if (! addHrefTags)
	{
		addHrefTags = "";
	}
    BX_innerHTML(BX_popup,'<a href="'+link+'" '+ addHrefTags +' class="popupline">&#160;&#160;&#160;&#160;'+text+'</a>',true);
}

function BX_popup_addHr ()
{
    //	BX_popup.innerHTML += '<hr width="10%"/>';
    BX_innerHTML(BX_popup,'<center>----</center>',true);
}

function BX_popup_makePlain(node,all)
{
	BX_range.selectNode(node);
	if (all) {
		BX_deleteTagsWithName("allElements");
	}
	else {
		BX_deleteTagsWithName(node.nodeName);
	}
	BX_popup_hide();
}

// for whatever reason, jsdoc needs this line
