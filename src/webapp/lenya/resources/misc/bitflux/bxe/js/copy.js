/******************************
* Copy/Paste Stuff            *
*******************************/
/**
 * @file
 * Implements some copy/paste functions
 *
 * The functions here will go into some classes 
 * To be decided..
 */

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

//    BX_range_updateToCursor();
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
	if (! BX_clipboard)
	{ return ; }
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
	/* to be fixed, we should not empty the clipboard on paste... */
	BX_clipboard = null;
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

// for whatever reason, jsdoc needs this line
