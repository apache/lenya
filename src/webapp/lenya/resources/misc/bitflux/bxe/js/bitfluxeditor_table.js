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
// $Id: bitfluxeditor_table.js,v 1.3 2002/10/25 10:12:22 felixcms Exp $
/**
 * @file
 * Implements the table plugin
 *
 * The functions here will go into some table Class.
 * we need also some kind of plugin-interface. to be defined yet
 */

var mouseX =0;
var mouseY = 0;

document.addEventListener("mousemove",BX_mousetrack,true);

function BX_table_insert(e)
{
    var output = "";
		BX_popup_start("Create Table",0,90);

    if (BX_range)
    {

        output = "<form action='javascript:BX_table_newtable();' id='bxe_tabelle' name='tabelle'><table class=\"usual\"><tr>";
        output += "<td >Columns</td><td ><input value='2' size=\"3\" name=\"cols\" /></td>\n";
        output += "</tr><tr>\n";
        output += "<td>Rows</td><td ><input value='2' size=\"3\" name=\"rows\" /></td>\n";
        output += "</tr><tr>\n";
        output += "<td colspan='2' align='right'>\n";
        output += "<input type='submit' class=\"usual\" value='create' /> </td>";
        output += "</tr></table></form>";
		BX_popup_addHtml(output);

    }
    else
    {

        output = "<span class='usual'>Nothing selected, please select the point, where you want the table inserted</span>";
		BX_popup_addHtml(output);

    }
	BX_removeEvents();

    BX_popup_show();


}

function BX_table_newtable()
{
	if (BX_popup.style.visibility != 'visible') 
	{
		return;
	}
	
	var TRows = document.getElementById("bxe_tabelle").rows.value;
	var TCols = document.getElementById("bxe_tabelle").cols.value;
	BX_popup_hide();
	var table = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","informaltable");
	BX_node_insertID(table);
    var tgroup = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","tgroup");
	BX_node_insertID(tgroup);
    var tbody = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","tbody");
	BX_node_insertID(tbody);
    tgroup.appendChild(tbody);
    table.appendChild(tgroup);
    for (var i  = 0; i < TRows; i++)
    {
        var trElement = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","row");
		BX_node_insertID(trElement);
        for (var j  = 0; j < TCols; j++)
        {
            var tdElement = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","entry");
			 BX_node_insertID(tdElement);
            var textNode = BX_xml.doc.createTextNode("#");
            tdElement.appendChild(textNode);
            trElement.appendChild(tdElement)
        }
        var tr = tbody.appendChild(trElement);
    }

    BX_range.extractContents();
    BX_insertContent(table);

	BX_selection.collapse(table.firstChild.firstChild.firstChild.firstChild.firstChild,0);
	BX_update_buttons = true;
    BX_addEvents();


}

function BX_table_insert_row_or_col(roworcol)
{
	if(BX_popup.style.visibility== 'visible')
    {
		if (document.getElementById("bxe_tabelle").ch[0].checked)
        {
        	BX_table_insert_row();
		}
        else
        {
        	BX_table_insert_col();
        }
		BX_popup.style.visibility= 'hidden';
        BX_addEvents();
        return;
    }


    if (!(BX_range))
    {
        alert("Nothing selected, please select a table cell");
        return;
    }
    if (BX_range.startContainer.parentNode.nodeName.toLowerCase() != "entry")
    {
        alert("No table-cell (but " + BX_range.startContainer.parentNode.nodeName +") selected, please choose one");
        return ;
    }
    BX_popup_start("Add Row or Col",110,90);
    var output = "";
    output += "<form action='javascript:BX_table_insert_row_or_col();' id='bxe_tabelle' name='tabelle'><table class=\"usual\"><tr>";
    output += "<td ><input name='ch' type='radio' value='row' checked='checked' /></td><td >add row</td>\n";
    output += "</tr><tr><td ><input name='ch' type='radio' value='col' /></td><td >add col</td>\n";

    output += "</tr><tr><td colspan='2'><input type='submit' class=\"usual\" value='add' /> </td>";
    output += "</tr></table></form>";

	BX_popup_addHtml(output);
    BX_popup_show();
        BX_popup.style.top=BX_popup.offsetTop - 1 + "px";

}
function BX_table_insert_row()
{
    var cell = BX_range.startContainer.parentNode;
    var row = cell.parentNode;
    var tbody = row.parentNode;

    var newRow = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","row");
    BX_node_insertID(newRow);
    for (var i = 0; i < row.childNodes.length; i++)
    {
        var newCell = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","entry");
	BX_node_insertID(newCell);
        var textNode = BX_xml.doc.createTextNode("#");
        newCell.appendChild(textNode);
        newRow.appendChild(newCell);
    }

    tbody.replaceChild(newRow,row);
    bla = tbody.insertBefore(row,newRow);
    BX_range.setEnd(bla.nextSibling.childNodes[0].childNodes[0],0);
    BX_range.setStart(bla.nextSibling.childNodes[0].childNodes[0],0);

}

function BX_table_insert_col()
{
    if (!(BX_range))
    {
        alert("Nothing selected, please select a table cell");
        return;
    }
    var cell = BX_range.startContainer.parentNode;
    if (cell.nodeName.toLowerCase() != "entry")
    {
        alert("No table-cell selected, please choose one");
        return ;
    }
    var row = cell.parentNode;
    var tbody = row.parentNode;
    var cellPos;
    // find on which position we are
    for (var i = 0; i < row.childNodes.length; i++)
    {
        if (row.childNodes[i] == cell)
        {
            cellPos = i;
            break;
        }
    }
    //for each row, add a entry
    for (var i = 0; i < tbody.childNodes.length; i++)
    {
        if (tbody.childNodes[i].nodeName.toLowerCase() == "row")
        {
            var newCell = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","entry");
			BX_node_insertID(newCell);
            var textNode = BX_xml.doc.createTextNode("#");
            newCell.appendChild(textNode);
            var oldCell = tbody.childNodes[i].childNodes[cellPos];
            tbody.childNodes[i].replaceChild(newCell,oldCell);
            bla = tbody.childNodes[i].insertBefore(oldCell,newCell);
        }
    }
    BX_range.setEnd(newCell.childNodes[0],0);
    BX_range.setStart(newCell.childNodes[0],0);
}


function BX_mousetrack(e) {

    if (e)
    {
        mouseX = e.pageX;
        mouseY = e.pageY;
    }
}

