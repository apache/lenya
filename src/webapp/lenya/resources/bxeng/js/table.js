// +--------------------------------------------------------------------------+
// | BXE                                                                      |
// +--------------------------------------------------------------------------+
// | Copyright (c) 2003,2004 Bitflux GmbH                                     |
// +--------------------------------------------------------------------------+
// | Licensed under the Apache License, Version 2.0 (the "License");          |
// | you may not use this file except in compliance with the License.         |
// | You may obtain a copy of the License at                                  |
// |     http://www.apache.org/licenses/LICENSE-2.0                           |
// | Unless required by applicable law or agreed to in writing, software      |
// | distributed under the License is distributed on an "AS IS" BASIS,        |
// | WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
// | See the License for the specific language governing permissions and      |
// | limitations under the License.                                           |
// +--------------------------------------------------------------------------+
// | Author: Christian Stocker <chregu@bitflux.ch>                            |
// +--------------------------------------------------------------------------+
//
// $Id: table.js 1369 2005-09-02 14:53:45Z chregu $


Element.prototype.TableRemoveRow = function() {
	bxe_history_snapshot();
	return bxe_table_delete_row(this);
}

Element.prototype.TableRemoveCol = function() {
	bxe_history_snapshot();
	return bxe_table_delete_col(this);
}

Element.prototype.TableAppendRow = function () {
	bxe_history_snapshot();
	return bxe_table_insert_row(this);
}

Element.prototype.TableAppendCol = function () {
	bxe_history_snapshot();
	return bxe_table_insert_col(this);
	
}

Element.prototype.findPosition = function () {
	// find position
	var prevSibling = this.previousSibling;
	var pos = 1;
	while(prevSibling) {
		if (prevSibling.nodeType == 1 && (prevSibling.localName == "td" || cell.localName == "th")) {
			var _attr = prevSibling.getAttribute("colspan");
			if (_attr > 0) {
				pos += parseInt( _attr);
			} else {
				pos++;
			}
		}
		prevSibling = prevSibling.previousSibling;
	}
	return pos;
}

Element.prototype.TableCellMergeRight = function () {
	var positions = bxe_table_getRowAndColPosition(this);
	var matrix = positions['matrix'];
	var rowPos = positions['row'];
	var colPos = positions['col'];
	var thisColspan = bxe_table_getSpanCount(this.getAttribute("colspan"));
	var rightCol = matrix[rowPos][colPos+thisColspan][1];
	if (!rightCol) {
		alert("There's no right cell to be merged");
		return;
	}
	var rightRowSpanCount = bxe_table_getSpanCount(rightCol.getAttribute("rowspan"));
	var thisRowSpanCount  = bxe_table_getSpanCount(this.getAttribute("rowspan"));
	if (rightRowSpanCount != thisRowSpanCount) {
			alert("Right cell's rowspan is different to this cell's rowspan, merging not possible");
			return;
		
	}
	/*
	var nextSibling = this.nextSibling;
	while (nextSibling && nextSibling.nodeType != 1) {
		nextSibling = nextSibling.nextSibling;
	}*/
	var child = rightCol.firstChild;
	while (child) {
		var nextchild = child.nextSibling;
		this.appendChild(child);
		child = nextchild;
	}
	this.normalize();
	rightCol.parentNode.removeChild(rightCol);
	
	this.setAttribute("colspan", thisColspan+1);
	return this.parentNode.parentNode;
}



Element.prototype.TableCellMergeDown = function () {
	
	var positions = bxe_table_getRowAndColPosition(this);
	var matrix = positions['matrix'];
	var rowPos = positions['row'];
	var colPos = positions['col'];
	
	var thisRowspan = bxe_table_getSpanCount(this.getAttribute("rowspan"));
	if (!matrix[rowPos + thisRowspan]) {
		alert("There's no cell below to be merged");
		return;
	}
	var downCol = matrix[rowPos + thisRowspan][colPos][1];
	if (!downCol) {
		alert("There's no cell below to be merged");
		return;
	}
	
	var downColSpanCount = bxe_table_getSpanCount(downCol.getAttribute("colspan"));
	var thisColSpanCount = bxe_table_getSpanCount(this.getAttribute("colspan"));
	
	if (downColSpanCount != thisColSpanCount) {
			alert("Down cell's colspan is different to this cell's colspan, merging not possible");
			return;
		
	}
	
	this.setAttribute("rowspan",thisRowspan+1);	
	
	var child = downCol.firstChild;
	while (child) {
		var nextchild = child.nextSibling;
		this.appendChild(child);
		child = nextchild;
	}
	this.normalize();
	downCol.parentNode.removeChild(downCol);
	return this.parentNode.parentNode;
}

Element.prototype.TableCellSplitRight = function () {
	

    var first = bxe_splitAtSelection(this);	
	var colspan = parseInt(first.getAttribute("colspan"));
	if (colspan > 2) {
		first.setAttribute("colspan", colspan-1);
	} else {
		first.removeAttribute("colspan");
	}
	var nextSibling = first.nextSibling;
	while (nextSibling && nextSibling.nodeType != 1) {
		nextSibling = nextSibling.nextSibling;
	}
	if (nextSibling) {
		nextSibling.removeAttribute("colspan");
	}
	return first.parentNode.parentNode;
}


Element.prototype.TableCellSplitDown = function() {
	var pos = this.findPosition();
	
	
	var positions = bxe_table_getRowAndColPosition(this);
	var matrix = positions['matrix'];
	var rowPos = positions['row'];
	var colPos = positions['col'];
	
	
	var tr = this.parentNode;
	var first = bxe_splitAtSelection(this);	
	
	var rowspan = bxe_table_getSpanCount(first.getAttribute("rowspan"));
	if (rowspan > 2) {
		first.setAttribute("rowspan", rowspan-1);
	} else {
		first.removeAttribute("rowspan");
	}
	
	if (first.nextSibling) {
		var nextSibling = first.nextSibling;
	} 
	while (nextSibling && nextSibling.nodeType != 1) {
		nextSibling = nextSibling.nextSibling;
	}
	if (nextSibling) {
		nextSibling.removeAttribute("rowspan");
	}
	var cell = matrix[rowPos + rowspan - 1][colPos + 1];
	if (cell) {
		cell = cell[1];
	}
	if (cell) {
		cell.parentNode.insertBefore(nextSibling,cell);
	} else {
		//get next tr from first td cell of next row
		var nexttr = matrix[rowPos + rowspan - 1][1][1].parentNode;
		nexttr.appendChild(nextSibling);
	}
	return first.parentNode.parentNode;
}


Element.prototype.findCellPosition = function(pos) {
	var cell = this.firstChild;
	var nextpos = 1;
	
	while (cell) {
		if (cell.nodeType == 1 && (cell.localName == "td" || cell.localName == "th")) {
			if (nextpos >= pos) {
				return cell;
			}
			var _attr = parseInt(cell.getAttribute("colspan"));
			if (_attr >  0 ) {
				nextpos += parseInt(_attr);
			} else {
				nextpos++;
			}
		}
		cell = cell.nextSibling;
	}
	
}



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
// $Id: table.js 1369 2005-09-02 14:53:45Z chregu $
/**
* @file
* Implements the table plugin
*
* The functions here will go into some table Class.
* we need also some kind of plugin-interface. to be defined yet
*/

var mouseX = 0;
var mouseY = 0;

//document.addEventListener("mousemove",BX_mousetrack,true);

// <cope>
// </cope>
function bxe_table_insert(id)
{
	var output = "";
	BX_popup_start("Create Table",0,90);
	
	if (BX_range)
	{
		
		output = "<form action=\"javascript:bxe_table_newtable('"+id+"');\" id='bxe_tabelle' name='tabelle'><table class=\"usual\"><tr>";
		output += "<td >Columns</td><td ><input value='5' size=\"3\" name=\"cols\" /></td>\n";
		output += "</tr><tr>\n";
		output += "<td>Rows</td><td ><input value='5' size=\"3\" name=\"rows\" /></td>\n";
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

function bxe_table_newtable(id)
{
	alert("bxe_table_newtable");
	
	if (BX_popup.style.visibility != 'visible')
	{
		return;
	}
	
	var TRows = document.getElementById("bxe_tabelle").rows.value;
	var TCols = document.getElementById("bxe_tabelle").cols.value;
	BX_popup_hide();
	var table = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","informaltable");
	//<cope>
	/*table.setAttribute("cols", TCols);
	table.setAttribute("rows", TRows);
	*/
	table.setAttribute("borderlines", "yes");
	table.setAttribute("colheaders", "yes");
	
	//</cope>
	BX_node_insertID(table);
	var tgroup = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","tgroup");
	BX_node_insertID(tgroup);
	var tbody = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","tbody");
	BX_node_insertID(tbody);
	tgroup.appendChild(tbody);
	table.appendChild(tgroup);
	for (var i  = 0; i < TRows; i++)
	{
		var trElement = BX_xml.doc.createElementNS("http://www.w3.org/1999/xhtml","tr");
		BX_node_insertID(trElement);
		for (var j  = 0; j < TCols; j++)
		{
			var tdElement = bxe_createNewTableCell();
			trElement.appendChild(tdElement)
		}
		var tr = tbody.appendChild(trElement);
	}
	// <cope>
	if (id) {
		var node = BX_getElementByIdClean(id,document);
		BX_range.collapse(true);
		BX_range.setEndAfter(node);
		BX_range.setStartAfter(node);
	}
	// </cope>
	BX_range.extractContents();
	
	BX_insertContent(table);
	BX_selection.collapse(table.firstChild.firstChild.firstChild.firstChild.firstChild,0);
	BX_transform();
	BX_update_buttons = true;
	BX_addEvents();
	
	
}

//no longer needed
function bxe_table_insert_row_or_col(roworcol)
{
	if(BX_popup.style.visibility== 'visible')
	{
		if (document.getElementById("bxe_tabelle").ch[0].checked)
		{
			bxe_table_insert_row();
		}
		else
		{
			bxe_table_insert_col();
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
	// <cope> changed != "entry" to "xhtml:td" in order to get the fucking col/rowspan work
	if (BX_range.startContainer.parentNode.nodeName.toLowerCase() != "xhtml:td")
	{
		alert("No table-cell (but " + BX_range.startContainer.parentNode.nodeName +") selected, please choose one");
		return ;
	}
	BX_popup_start("Add Row or Col",110,90);
	var output = "";
	output += "<form action='javascript:bxe_table_insert_row_or_col();' id='bxe_tabelle' name='tabelle'><table class=\"usual\"><tr>";
	output += "<td ><input name='ch' type='radio' value='row' checked='checked' /></td><td >add row</td>\n";
	output += "</tr><tr><td ><input name='ch' type='radio' value='col' /></td><td >add col</td>\n";
	
	output += "</tr><tr><td colspan='2'><input type='submit' class=\"usual\" value='add' /> </td>";
	output += "</tr></table></form>";
	
	BX_popup_addHtml(output);
	BX_popup_show();
	BX_popup.style.top=BX_popup.offsetTop - 1 + "px";
	
}
/*
function bxe_table_find_current_cell()
{
	
	var cell = BX_range.startContainer.parentNode;
	//current range points not to td-node... try to dive up
	while(cell.nodeName.toLowerCase() != "xhtml:td")
	{
		cell = cell.parentNode;
		if(cell.nodeName.toLowerCase() == "page")
		{
			return false;
		}
	}
	
	return cell;
	
}*/

function bxe_table_insert_row(cell)
{
	

	var positions = bxe_table_getRowAndColPosition(cell);
	var matrix = positions['matrix'];
	var rowPos = positions['row'];
	var colPos = positions['col'];
	//rowPos now contains the row and we can now search for the cell within the matrix
	//matrix works 1...n
	//first of all we create a new empty matrix with one more col
	var newMatrix = bxe_createInitialTableMatrix(matrix[0][0][0]-0+1, matrix[0][0][1]);
	//now we fill the new one by traversing the current
	var nx = 0;
	var ny = 0;
	var rowspan = 1;
	var colspan = 1;
	for(var y = 1; y <= matrix[0][0][0]; y++) {
		ny++;
		nx = 0;
		for(var x = 1; x <= matrix[0][0][1]; x++) {
			nx++;
			//we insert new cols after colPos!
			//we have to increase all colspans that spans the colPos!
			rowspan = 0;
			if(matrix[y][x][0] > 0 && matrix[y][x][0] < 3) {
				if((matrix[y][x][1].getAttribute("rowspan")-1 + y) > rowPos && y <= rowPos) {
					rowspan = bxe_table_getSpanCount(matrix[y][x][1].getAttribute("rowspan"));
				}
			}
			newMatrix[ny][nx][0] = matrix[y][x][0];
			newMatrix[ny][nx][1] = matrix[y][x][1];
			newMatrix[ny][nx][2] = matrix[y][x][2];
			newMatrix[ny][nx][3] = matrix[y][x][3];
			if(rowspan) {
				//in this case, colspan spans the colPos
				newMatrix[ny][nx][0] = 2;
				newMatrix[ny][nx][1].setAttribute("rowspan", rowspan+1)
			}
			//do we have reached the position to add the new cell?
			if(y == rowPos) {
				newMatrix[ny+1][nx][0] = 1;
				newMatrix[ny+1][nx][1] = bxe_createNewTableCell();
				if(rowspan) {
					newMatrix[ny+1][nx][0] = 3;
					newMatrix[ny+1][nx][1] = false;
				}
				if(y+1 <= matrix[0][0][0]) {
					if(matrix[y][x][0] == 3 && matrix[y+1][x][0] == 3) {
						//we are inbetween a span! new cell is a span!
						newMatrix[ny+1][nx][0] = 3;
						newMatrix[ny+1][nx][1] = false;
					}
				}
			}
		}
		if(y == rowPos) ny++;
	}
	return bxe_rebuildTableByTableMatrix(cell, newMatrix);
	
/*	BX_range.setEnd(cell,0);
	BX_range.setStart(cell,0);
	
	BX_transform();
	*/
	// </cope>
	
}

function bxe_table_delete_row(cell)
{
	
	var positions = bxe_table_getRowAndColPosition(cell);
	var matrix = positions['matrix'];
	var rowPos = positions['row'];
	var colPos = positions['col'];
	//rowPos now contains the row and we can now search for the cell within the matrix
	//matrix works 1...n
	//first of all we create a new empty matrix with one more col
	var newMatrix = bxe_createInitialTableMatrix(matrix[0][0][0]-1, matrix[0][0][1]);
	//now we fill the new one by traversing the current
	var nx = 0;
	var ny = 0;
	var rowspan = 1;
	var colspan = 1;
	for(var y = 1; y <= matrix[0][0][0]; y++) {
		ny++;
		nx = 0;
		for(var x = 1; x <= matrix[0][0][1]; x++) {
			nx++;
			rowspan = 0;
			if(matrix[y][x][0] > 0 && matrix[y][x][0] < 3) {
				if((bxe_table_getSpanCount(matrix[y][x][1].getAttribute("rowspan"))-1 + y) >= rowPos && y < rowPos) {
					rowspan = bxe_table_getSpanCount(matrix[y][x][1].getAttribute("rowspan"));
				}
			}
			if(y == rowPos) {
				//we have to delete this row
				//but have to take care of deleting a cell which spans over
				var rs = 0;
				var cs = 0;
				if(matrix[y][x][0] > 0 && matrix[y][x][0] < 3) {
					rs = bxe_table_getSpanCount(matrix[y][x][1].getAttribute("rowspan"));
					cs = bxe_table_getSpanCount(matrix[y][x][1].getAttribute("colspan"));
				}
				if(rs > 1 || cs > 1) {
					//because we will delete this cell, we have to rebuild
					//the spaned cells!
					for(var rs_y = 0; rs_y < rs; rs_y++) {
						for(var cs_x = 0; cs_x < cs; cs_x++) {
							matrix[ny+rs_y][nx+cs_x][0] = 1;
							matrix[ny+rs_y][nx+cs_x][1] = bxe_createNewTableCell();
						}
					}
				}
				if(y < matrix[0][0][0]) {
					newMatrix[ny][nx][0] = matrix[y+1][x][0];
					newMatrix[ny][nx][1] = matrix[y+1][x][1];
					newMatrix[ny][nx][2] = matrix[y+1][x][2];
					newMatrix[ny][nx][3] = matrix[y+1][x][3];
				}
			} else {
				newMatrix[ny][nx][0] = matrix[y][x][0];
				newMatrix[ny][nx][1] = matrix[y][x][1];
				newMatrix[ny][nx][2] = matrix[y][x][2];
				newMatrix[ny][nx][3] = matrix[y][x][3];
				if(rowspan) {
					newMatrix[ny][nx][0] = 2;
					newMatrix[ny][nx][1].setAttribute("rowspan", rowspan-1)
				}
			}
		}
		if(y == rowPos) y++;
	}
	return bxe_rebuildTableByTableMatrix(cell, newMatrix);
	/*
	BX_range.setEnd(cell,0);
	BX_range.setStart(cell,0);
	BX_transform();
	*/
	// </cope>
	
}


function bxe_table_insert_col(cell)
{
	var positions = bxe_table_getRowAndColPosition(cell);
	var matrix = positions['matrix'];
	var rowPos = positions['row'];
	var colPos = positions['col'];
	
	//first of all we create a new empty matrix with one more col
	var newMatrix = bxe_createInitialTableMatrix(matrix[0][0][0], matrix[0][0][1]-0+1);
	//now we fill the new one by traversing the current
	var nx = 0;
	var ny = 0;
	var rowspan = 1;
	var colspan = 1;
	for(var y = 1; y <= matrix[0][0][0]; y++) {
		ny++;
		nx = 0;
		for(var x = 1; x <= matrix[0][0][1]; x++) {
			nx++;
			//we insert new cols after colPos!
			//we have to increase all colspans that spans the colPos!
			colspan = 0;
			if(matrix[y][x][0] > 0 && matrix[y][x][0] < 3) {
				if((matrix[y][x][1].getAttribute("colspan")-1 + x) > colPos && x <= colPos) {
					colspan = bxe_table_getSpanCount(matrix[y][x][1].getAttribute("colspan"));
				}
			}
			newMatrix[ny][nx][0] = matrix[y][x][0];
			newMatrix[ny][nx][1] = matrix[y][x][1];
			newMatrix[ny][nx][2] = matrix[y][x][2];
			newMatrix[ny][nx][3] = matrix[y][x][3];
			if(colspan) {
				//in this case, colspan spans the colPos
				newMatrix[ny][nx][0] = 2;
				newMatrix[ny][nx][1].setAttribute("colspan", colspan+1)
			}
			//do we have reached the position to add the new cell?
			if(x == colPos) {
				nx++;
				newMatrix[ny][nx][0] = 1;
				newMatrix[ny][nx][1] = bxe_createNewTableCell();
				if(colspan) {
					newMatrix[ny][nx][0] = 3;
					newMatrix[ny][nx][1] = false;
				}
				if(x+1 <= matrix[0][0][1]) {
					if(matrix[y][x][0] == 3 && matrix[y][x+1][0] == 3) {
						//we are inbetween a span! new cell is a span!
						newMatrix[ny][nx][0] = 3;
						newMatrix[ny][nx][1] = false;
					}
				}
			}
		}
	}
	return bxe_rebuildTableByTableMatrix(cell, newMatrix);
	/*
	BX_range.setEnd(cell,0);
	BX_range.setStart(cell,0);
	BX_transform();*/
	// </cope>
}


function bxe_table_delete_col(cell)
{
	var positions = bxe_table_getRowAndColPosition(cell);
	var matrix = positions['matrix'];
	var rowPos = positions['row'];
	var colPos = positions['col'];
	//first of all we create a new empty matrix with one more col
	var newMatrix = bxe_createInitialTableMatrix(matrix[0][0][0], matrix[0][0][1]-0-1);
	//now we fill the new one by traversing the current
	var nx = 0;
	var ny = 0;
	var rowspan = 1;
	var colspan = 1;
	for(var y = 1; y <= matrix[0][0][0]; y++) {
		ny++;
		nx = 0;
		for(var x = 1; x <= matrix[0][0][1]; x++) {
			nx++;
			//we insert new cols after colPos!
			//we have to increase all colspans that spans the colPos!
			colspan = 0;
			if(matrix[y][x][0] > 0 && matrix[y][x][0] < 3) {
				if((bxe_table_getSpanCount(matrix[y][x][1].getAttribute("colspan"))-1 + x) >= colPos && x < colPos) {
					colspan = bxe_table_getSpanCount(matrix[y][x][1].getAttribute("colspan"));
				}
			}
			//do we have reached the position to add the new cell?
			if(x == colPos) {
				//we have to delete this col
				//but have to take care of deleting a cell which spans over
				var rs = 0;
				var cs = 0;
				if(matrix[y][x][0] > 0 && matrix[y][x][0] < 3) {
					rs = bxe_table_getSpanCount(matrix[y][x][1].getAttribute("rowspan"));
					cs = bxe_table_getSpanCount(matrix[y][x][1].getAttribute("colspan"));
				}
				if(rs > 1 || cs > 1) {
					//because we will delete this cell, we have to rebuild
					//the spaned cells!
					for(var rs_y = 0; rs_y < rs; rs_y++) {
						for(var cs_x = 0; cs_x < cs; cs_x++) {
							matrix[ny+rs_y][nx+cs_x][0] = 1;
							matrix[ny+rs_y][nx+cs_x][1] = bxe_createNewTableCell();
						}
					}
				}
				if(x < matrix[0][0][1]) {
					x++;
					newMatrix[ny][nx][0] = matrix[y][x][0];
					newMatrix[ny][nx][1] = matrix[y][x][1];
					newMatrix[ny][nx][2] = matrix[y][x][2];
					newMatrix[ny][nx][3] = matrix[y][x][3];
				}
			} else {
				newMatrix[ny][nx][0] = matrix[y][x][0];
				newMatrix[ny][nx][1] = matrix[y][x][1];
				newMatrix[ny][nx][2] = matrix[y][x][2];
				newMatrix[ny][nx][3] = matrix[y][x][3];
				if(colspan) {
					//in this case, colspan spans the colPos
					newMatrix[ny][nx][0] = 2;
					newMatrix[ny][nx][1].setAttribute("colspan", colspan-1)
				}
			}
		}
	}
	return bxe_rebuildTableByTableMatrix(cell, newMatrix);
	/*
	BX_range.setEnd(cell,0);
	BX_range.setStart(cell,0);
	BX_transform();*/
	// </cope>
}

function bxe_table_getRowAndColPosition(cell) {
	var row = cell.parentNode;
	var tbody = row.parentNode;
	var table = tbody;
	var matrix = bxe_createTableMatrix(cell);
	var colPos = 0;
	var rowPos = 0;
	var ii = 0;
	// find on which row position we are
	for (var i = 0; i < tbody.childNodes.length; i++)
	{
		if (tbody.childNodes[i].nodeName.toLowerCase() != "tr") continue;
		ii++;
		if (tbody.childNodes[i] == row)
		{
			rowPos = ii;
			break;
		}
	}
	//rowPos now contains the row and we can now search for the cell within the matrix
	//matrix works 1...n
	var matrixRowVector = matrix[rowPos];
	for (var i = 1; i < matrixRowVector.length; i++) {
		if(cell == matrixRowVector[i][1]) {
			//hui, we found the cell!
			colPos = i;
			break;
		}
	}
	if(colPos == 0) {
		alert("ERROR: could not find the cell in matrix!");
		alert("rowPos: "+rowPos);
		return false;
	}
	var pos = new Array();
	pos['row'] = rowPos;
	pos['col'] = colPos;
	pos['matrix'] = matrix;
	return pos;
}
//<cope>
// needed???
/*
function BX_tableCellAttributesChanged(node, attr_name, attr_value) {
	
	var cell = node;
	var row = cell.parentNode;
	var tbody = row.parentNode;
	var table = tbody.parentNode.parentNode;
	var matrix = bxe_createTableMatrix(cell);
	var colpos = 0;
	var colpos = 0;
	var ii = 0;
	//lets find out in which col we are
	for (var i = 0; i < tbody.childNodes.length; i++) {
		if(tbody.childNodes[i].nodeName.toLowerCase() != "tr") continue;
		ii++;
		if (tbody.childNodes[i] == row) {
			rowPos = ii;
			break;
		}
	}
	var matrixRowVector = matrix[rowPos];
	for (var i = 1; i < matrixRowVector.length; i++) {
		if(cell == matrixRowVector[i][1]) {
			//hui, we found the cell!
			colPos = i;
			break;
		}
	}
	if(colPos == 0) {
		alert("ERROR: could not find the cell in matrix!");
		alert("rowPos: "+rowPos);
		return;
	}
	var rowspan = bxe_table_getSpanCount(node.getAttribute("rowspan"));
	var colspan = bxe_table_getSpanCount(node.getAttribute("colspan"));
	var old_rowspan = rowspan;
	var old_colspan = colspan;
	
	var old_span = node.getAttribute(attr_name)-0;
	var new_span = attr_value -0;
	
	if(new_span < 1) {
		alert("Sorry, a span must be at least 1!");
		BX_down();
		return;
		
	}
	
	if(new_span > old_span) {
		if(attr_name == "colspan") {
			old_rowspan = 1;
			colspan = new_span;
		} else {
			old_colspan = 1;
			rowspan = new_span;
		}
		//we have to delete table cell by collecting their content to the current cell
		//lets check to boundary
		//which could be table boundary are overlaping other spans!
		for(var y = old_rowspan-1; y < rowspan; y++) {
			if(rowPos + y > matrix[0][0][0]) {
				alert("Sorry, your rowspan overlaps the table boundary!");
				BX_down();
				return;
			}
			for(var x = old_colspan-1; x < colspan; x++) {
				if(colPos + x > matrix[0][0][1]) {
					alert("Sorry, your colspan overlaps the table boundary!");
					BX_down();
					return;
				}
				if(matrix[rowPos+y][colPos+x][0] > 1) {
					if(
						matrix[rowPos+y][colPos+x][2] != matrix[rowPos][colPos][2] ||
					matrix[rowPos+y][colPos+x][3] != matrix[rowPos][colPos][3]
					) {
						//ups, we touched a boundary
						alert("Sorry, your span area overlaps with an other spaned area!");
						BX_down();
						return;
					}
				}
			}
		}
		//if we reached this point, the new spaned area is valid!
		for(var y = old_rowspan-1; y < rowspan; y++) {
			for(var x = old_colspan-1; x < colspan; x++) {
				if(x > 0 || y > 0) {
					if(matrix[rowPos+y][colPos+x][0] > 0 && matrix[rowPos+y][colPos+x][0] < 3) {
						for(var i=0; i < matrix[rowPos+y][colPos+x][1].childNodes.length; i++) {
							matrix[rowPos][colPos][1].appendChild(matrix[rowPos+y][colPos+x][1].childNodes[i]);
						}
					}
					matrix[rowPos+y][colPos+x][0] = 3;
					matrix[rowPos+y][colPos+x][1] = false;
					matrix[rowPos+y][colPos+x][2] = rowPos;
					matrix[rowPos+y][colPos+x][3] = colPos;
				}
			}
		}
		matrix[rowPos][colPos][2] = rowPos;
		matrix[rowPos][colPos][3] = colPos;
		if(attr_name == "colspan") {
			matrix[rowPos][colPos][1].setAttribute("colspan", new_span);
		} else {
			matrix[rowPos][colPos][1].setAttribute("rowspan", new_span);
		}
		
		
	} else {
		//we create new cells because the old span is reduced
		if(new_span < old_span) {
			for(var y = 0; y < old_rowspan; y++) {
				for(var x = 0; x < old_colspan; x++) {
					if( (attr_name == "rowspan" && y >= new_span) || (attr_name == "colspan" && x >= new_span)) {
						matrix[rowPos+y][colPos+x][0] = 1;
						matrix[rowPos+y][colPos+x][1] = bxe_createNewTableCell();
					}
				}
			}
			if(attr_name == "colspan") {
				matrix[rowPos][colPos][1].setAttribute("colspan", new_span);
			} else {
				matrix[rowPos][colPos][1].setAttribute("rowspan", new_span);
			}
			
		} else {
			//nothing to do -- hui fine, code seams somewhat struwelig.
			//its because of the hefeweize bei 30 grad and empty stomage!
		}
	}
	bxe_rebuildTableByTableMatrix(cell, matrix);
	
	//BX_range.setEnd(cell,0);
	//BX_range.setStart(cell,0);
	//BX_transform();
	return;
	
}
*/

function bxe_createInitialTableMatrix(rows, cols) {
	
	var table = new Array(rows+1);
	//array will be 1... n not 0... n-1!
	for(var r = 0; r <= rows; r++) {
		table[r] = new Array(cols+1);
		for(var c = 0; c <= cols; c++) {
			table[r][c] = new Array(4);
			table[r][c][0] = 0; //0=not in use, 1=td without span, 2=spaning td, 3=span area
			table[r][c][1] = false; //will contain DOM node of td-cell
			table[r][c][2] = 0; //if [0]=2, then it contains y of spaning cell
			table[r][c][3] = 0; //if [0]=2, then it contains x of spaning cell
		}
	}
	// now we store cols and rows
	table[0][0] = new Array(2);
	table[0][0][0] = rows;
	table[0][0][1] = cols;
	return table;
}

function bxe_rebuildTableByTableMatrix(td, matrix) {
	var tbody = td.parentNode.parentNode;
	var table =tbody;
	/*table.setAttribute("rows", matrix[0][0][0]);
	table.setAttribute("cols", matrix[0][0][1]);
	*/
	//remove all old rows
	while(tbody.childNodes.length) {
		tbody.removeChild(tbody.childNodes[0]);
	}
	//rebuild all rows
	for(var r = 1; r <= matrix[0][0][0]; r++) {
		var rowNode = document.createElementNS(XHTMLNS,"tr");
		//BX_node_insertID(rowNode);
		tbody.appendChild(rowNode);
		for(var c = 1; c <= matrix[0][0][1]; c++) {
			//only if matrix gives a cell: [0] == 1
			if(matrix[r][c][0] == 1 || matrix[r][c][0] == 2) {
				if(matrix[r][c][1] == false) {
					alert("no cell node at r="+r+" c="+c);
				} else {
					//a cell without a span or a spaning one
					rowNode.appendChild(matrix[r][c][1]);
				}
			}
		}
	}
	bxe_history_snapshot_async()
	return table;
}

function bxe_table_getDimensions(table_node) {
	//get rows
	var rows = 0;
	var cols = 0;
	var firstRow = null;
	for(var r = 0; r < table_node.childNodes.length; r++) {
		row = table_node.childNodes[r];
		if(row.nodeName.toLowerCase() != "tr") { continue; }
		if (!firstRow) { firstRow = row;}
		rows += bxe_table_getSpanCount(row.getAttribute("rowspan"));
	}
	var nodeName = "";
	for(var r = 0; r < firstRow.childNodes.length; r++) {
		cell = firstRow.childNodes[r];
		nodeName = cell.nodeName.toLowerCase();
		if(!(nodeName == "td" || nodeName == "th")) { continue; }
		cols += bxe_table_getSpanCount(cell.getAttribute("colspan"));
	}
	
	
	var dim = new Array();
	dim['rows'] = rows;
	dim['cols'] = cols;
/*	dump("c: " + cols + "\n");
	dump("r: " + rows + "\n");
	*/
	return dim;
}

function bxe_createTableMatrix(td) {
	
	var tbody = td.parentNode.parentNode;
	
	if(tbody.nodeName.toLowerCase() != "table") { alert("got no table body!"); }
	var table_node = tbody;
	//var table_node = tbody.parentNode.parentNode;
	
	var dim =  bxe_table_getDimensions(table_node);
	
	var cols = dim['cols'];
	var rows = dim['rows'];
	//the matrix:
	var table = bxe_createInitialTableMatrix(rows, cols);
	//as we have initialized the homogenious matrix,
	//fill it by traversing the table DOM-node:
	var tr = 0;	//matrix coordinates
	var tc = 0;
	//loop all real existing table rows
	for(var r = 0; r < tbody.childNodes.length; r++) {
		row = tbody.childNodes[r];
		if(row.nodeName.toLowerCase() != "tr") continue; //caution: may be there are text nodes (CRLF or whitespace)
		tr++;
		tc = 0;
		//loop all real existing table cells
		for(var c = 0; c < row.childNodes.length; c++) {
			cell = row.childNodes[c];
			if(cell.nodeName.toLowerCase() != "td" && cell.nodeName.toLowerCase() != "th")  continue; //caution: may be there are text nodes (CRLF or whitespace)
			tc++;
			//find the homogenious matrix pos
			//alert("tr="+tr+" tc="+tc);
			while(table[tr][tc][0] > 0) {
				//matrix cell already in use. obviously spaned!
				tc++;
			}
			//2do here: check whether node is a table cell. dont know what BXE makes for fucking stuff!
			colspan = bxe_table_getSpanCount(cell.getAttribute("colspan")); //get integer!
			rowspan = bxe_table_getSpanCount(cell.getAttribute("rowspan")); //get integer!
			//fill out the spaning area
			for(var x = 0; x < colspan; x++) {
				for(var y = 0; y < rowspan; y++) {
					//x=0 y=0 will be overridden later *)
					table[tr+y][tc+x][0] = 3;
					table[tr+y][tc+x][1] = false;
					table[tr+y][tc+x][2] = tr;
					table[tr+y][tc+x][3] = tc;
				}
			}
			if(table[tr][tc][0] == 3 && (colspan > 1 || rowspan > 1)) {
				//the spaning cell itself!
				table[tr][tc][0] = 2;
			} else {
				table[tr][tc][0] = 1;
			}
			// *) rigth here by the cell itself
			//alert(tr+" "+tc);
			table[tr][tc][1] = cell;
			table[tr][tc][2] = tr;
			table[tr][tc][3] = tc;
		}
	}
	return table;
}

function bxe_table_getSpanCount (value) {

	value = parseInt(value);
    if ( !value && value != 0) {
		value = 1;
	}
	
	return value;
		
}

function bxe_createNewTableCell() {
	var newCell = document.createElementNS(XHTMLNS,"td");
	/*newCell.setAttribute("align", "left");
	newCell.setAttribute("valign", "top");
	newCell.setAttribute("colspan", "1");
	newCell.setAttribute("rowspan", "1");*/
	//BX_node_insertID(newCell);
	var textNode = document.createTextNode(STRING_NBSP);
	newCell.appendChild(textNode);
	
	return newCell;
}

//</cope>




