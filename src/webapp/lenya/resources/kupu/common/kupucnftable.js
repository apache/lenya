/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupubasetools.js 6120 2004-08-22 23:23:42Z roku $

TableTool.prototype.setTableRowRepeat = function() {
    var selNode = this.editor.getSelectedNode();
    var row = this.editor.getNearestParentOfType(selNode, 'tr');
    if (!row) {
        this.editor.logMessage('Not inside a row!', 1);
        return;
    };
    row.setAttribute('repeatable', 'repeatable');
    row.className = 'repeatable';
    this.editor.logMessage('Row repeated');
    this.updateState(selNode);
};

TableTool.prototype.delTableRowRepeat = function() {
    var selNode = this.editor.getSelectedNode();
    var row = this.editor.getNearestParentOfType(selNode, 'tr');
    if (!row) {
        this.editor.logMessage('Not inside a row!', 1);
        return;
    };
    row.removeAttribute('repeatable');
    row.className = '';
    row.removeAttribute('class');
    this.editor.logMessage('Row repeat turned off');
    this.updateState(selNode);
};

function CNFTableToolBox(addtabledivid, edittabledivid, newrowsinputid, 
                    newcolsinputid, makeheaderinputid, classselectid, alignselectid, addtablebuttonid,
                    addrowbuttonid, delrowbuttonid, setrowrepeatbuttonid, delrowrepeatbuttonid,
                    addcolbuttonid, delcolbuttonid, fixbuttonid,
                    fixallbuttonid, toolboxid, plainclass, activeclass) {

    this.addtablediv = document.getElementById(addtabledivid);
    this.edittablediv = document.getElementById(edittabledivid);
    this.newrowsinput = document.getElementById(newrowsinputid);
    this.newcolsinput = document.getElementById(newcolsinputid);
    this.makeheaderinput = document.getElementById(makeheaderinputid);
    this.classselect = document.getElementById(classselectid);
    this.alignselect = document.getElementById(alignselectid);
    this.addtablebutton = document.getElementById(addtablebuttonid);
    this.addrowbutton = document.getElementById(addrowbuttonid);
    this.delrowbutton = document.getElementById(delrowbuttonid);
    this.setrowrepeatbutton = document.getElementById(setrowrepeatbuttonid);
    this.delrowrepeatbutton = document.getElementById(delrowrepeatbuttonid);
    this.addcolbutton = document.getElementById(addcolbuttonid);
    this.delcolbutton = document.getElementById(delcolbuttonid);
    this.fixbutton = document.getElementById(fixbuttonid);
    this.fixallbutton = document.getElementById(fixallbuttonid);
    this.toolboxel = document.getElementById(toolboxid);
    this.plainclass = plainclass;
    this.activeclass = activeclass;

    this.initialize = function(tool, editor) {
        /* attach the event handlers */
        this.tool = tool;
        this.editor = editor;
        // build the select list of table classes if configured
        if (this.editor.config.table_classes) {
            var classes = this.editor.config.table_classes['class'];
            while (this.classselect.hasChildNodes()) {
                this.classselect.removeChild(this.classselect.firstChild);
            };
            for (var i=0; i < classes.length; i++) {
                var classname = classes[i];
                var option = document.createElement('option');
                var content = document.createTextNode(classname);
                option.appendChild(content);
                option.setAttribute('value', classname);
                this.classselect.appendChild(option);
            };
        };
        addEventHandler(this.addtablebutton, "click", this.addTable, this);
        addEventHandler(this.addrowbutton, "click", this.tool.addTableRow, this.tool);
        addEventHandler(this.delrowbutton, "click", this.tool.delTableRow, this.tool);
        addEventHandler(this.setrowrepeatbutton, "click", this.tool.setTableRowRepeat, this.tool);
        addEventHandler(this.delrowrepeatbutton, "click", this.tool.delTableRowRepeat, this.tool);
        addEventHandler(this.addcolbutton, "click", this.tool.addTableColumn, this.tool);
        addEventHandler(this.delcolbutton, "click", this.tool.delTableColumn, this.tool);
        addEventHandler(this.alignselect, "change", this.setColumnAlign, this);
        addEventHandler(this.classselect, "change", this.setTableClass, this);
        addEventHandler(this.fixbutton, "click", this.tool.fixTable, this.tool);
        addEventHandler(this.fixallbutton, "click", this.tool.fixAllTables, this.tool);
        this.addtablediv.style.display = "block";
        this.edittablediv.style.display = "none";
        this.editor.logMessage('Table tool initialized');
    };

    this.updateState = function(selNode) {
        /* update the state (add/edit) and update the pulldowns (if required) */
        var table = this.editor.getNearestParentOfType(selNode, 'table');
        if (table) {
            this.addtablediv.style.display = "none";
            this.edittablediv.style.display = "block";

            var align = this.tool._getColumnAlign(selNode);
            selectSelectItem(this.alignselect, align);
            selectSelectItem(this.classselect, table.className);
            if (this.toolboxel) {
                this.toolboxel.className = this.activeclass;
            };
            var row = this.editor.getNearestParentOfType(selNode, 'tr');
            var isRepeatable = row.getAttribute('repeatable');
            if (isRepeatable) {
                this.setrowrepeatbutton.style.display = 'none';
                this.delrowrepeatbutton.style.display = 'inline';
            } else {
                this.setrowrepeatbutton.style.display = 'inline';
                this.delrowrepeatbutton.style.display = 'none';
            };
        } else {
            this.edittablediv.style.display = "none";
            this.addtablediv.style.display = "block";
            this.alignselect.selectedIndex = 0;
            this.classselect.selectedIndex = 0;
            if (this.toolboxel) {
                this.toolboxel.className = this.plainclass;
            };
        };
    };
};

CNFTableToolBox.prototype = new TableToolBox;
