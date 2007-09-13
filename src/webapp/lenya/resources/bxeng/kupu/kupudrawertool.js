/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupudrawertool.js 778 2004-08-31 15:32:22Z silvan $

function DrawerTool() {
    /* a tool to open and fill drawers

        this tool has to (and should!) only be instantiated once
    */
    this.drawers = {};
    this.current_drawer = null;
    
    this.initialize = function(editor) {
        this.editor = editor;
        // this essentially makes the drawertool a singleton
        window.drawertool = this;
    };

    this.registerDrawer = function(id, drawer) {
        
        this.drawers[id] = drawer;
        drawer.initialize(this.editor, this);
    };
    
    this.openDrawer = function(id) {
        /* open a drawer */
        if (this.current_drawer) {
            this.closeDrawer();
        };
        var drawer = this.drawers[id];
        drawer.createContent();
        this.current_drawer = drawer;
    };
    
    this.updateState = function(selNode) {
        if (this.current_drawer) {
            this.closeDrawer();
        };
    };

    this.closeDrawer = function() {
        if (!this.current_drawer) {
            return;
        };
        
        this.current_drawer.hide();
        this.current_drawer = null;
    };

    this.getDrawerEnv = function(iframe_win) {
        var drawer = null;
        for (var id in this.drawers) {
            var ldrawer = this.drawers[id];
            // Note that we require drawers to provide us with an
            // element property!
            if (ldrawer.element.contentWindow == iframe_win) {
                drawer = ldrawer;
            };
        };
        if (!drawer) {
            this.editor.logMessage("Drawer not found", 1);
            return;
        };
        return {
            'drawer': drawer,
            'drawertool': this,
            'tool': drawer.tool
        };
    };
};

DrawerTool.prototype = new KupuTool;

function Drawer(elementid, tool) {
    /* base prototype for drawers */

    this.element = document.getElementById(elementid);
    this.tool = tool;
    
    this.initialize = function(editor, drawertool) {
        this.editor = editor;
        this.drawertool = drawertool;
    };
    
    this.createContent = function() {
        /* fill the drawer with some content */
        // here's where any intelligence and XSLT transformation and such 
        // is done
        this.element.style.display = 'block';
    };

    this.hide = function() {
        this.element.style.display = 'none';
    };
};

function TableDrawer(elementid, tool) {
    this.element = document.getElementById(elementid);
    this.tool = tool;

    this.addpanelid = 'kupu-tabledrawer-addtable';
    this.editpanelid = 'kupu-tabledrawer-edittable';

    this.addpanel = document.getElementById(this.addpanelid);
    this.editpanel = document.getElementById(this.editpanelid);

    this.createContent = function() {
        var selNode = this.editor.getSelectedNode();
        var table = this.editor.getNearestParentOfType(selNode, 'table');

        if (!table) {
            // show add table drawer
            show = this.addpanel;
            hide = this.editpanel;
        } else {
            // show edit table drawer
            show = this.editpanel;
            hide = this.addpanel;
        };
        hide.style.display = 'none';
        show.style.display = 'block';
        this.element.style.display = 'block';
    };

    this.createTable = function() {
      var rows = document.getElementById('kupu-tabledrawer-newrows').value;
      var cols = document.getElementById('kupu-tabledrawer-newcols').value;
      var style = document.getElementById('kupu-tabledrawer-classchooser').value;
      var add_header = document.getElementById('kupu-tabledrawer-makeheader').checked;
      this.tool.createTable(parseInt(rows), parseInt(cols), add_header, style);
      this.drawertool.closeDrawer();
    };

};

TableDrawer.prototype = new Drawer;
