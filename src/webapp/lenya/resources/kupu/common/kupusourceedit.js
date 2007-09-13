/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id$


function SourceEditTool(sourcebuttonid, sourceareaid) {
    /* Source edit tool to edit document's html source */
    this.sourceButton = document.getElementById(sourcebuttonid);
    this.sourcemode = false;
    this._currently_editing = null;

    this.getSourceArea = function() {
        return document.getElementById(sourceareaid);
    }

    this.cancelSourceMode = function() {
        if (this._currently_editing) {
            this.switchSourceEdit(null, true);
        }
    }
    this.updateState = this.cancelSourceMode;

    this.initialize = function(editor) {
        /* attach the event handlers */
        this.editor = editor;
        this._fixTabIndex(this.sourceButton);
        addEventHandler(this.sourceButton, "click", this.switchSourceEdit, this);
        this.editor.logMessage('Source edit tool initialized');
    };
 
    this.switchSourceEdit = function(event, nograb) {
        var kupu = this.editor;
        var docobj = this._currently_editing||kupu.getDocument();
        var editorframe = docobj.getEditable();
        var sourcearea = this.getSourceArea();
        var kupudoc = docobj.getDocument();
        var sourceClass = 'kupu-sourcemode';
    
        if (!this.sourcemode) {
            if (window.drawertool) {
                window.drawertool.closeDrawer();
            }
            if (/on/i.test(kupudoc.designMode)) {
                kupudoc.designMode = 'Off';
            };
            kupu._initialized = false;

            var data='';
            if(kupu.config.filtersourceedit) {
                window.status = 'Cleaning up HTML...';
                var transform = kupu._filterContent(kupu.getInnerDocument().documentElement);
                data = kupu.getXMLBody(transform);
                data = kupu._fixupSingletons(data).replace(/<\/?body[^>]*>/g, "");
                window.status = '';
            } else {
                data = kupu.getHTMLBody();
            }
            sourcearea.value = data;
            kupu.setClass(sourceClass);
            editorframe.style.display = 'none';
            sourcearea.style.display = 'block';
            if (!nograb) {
                sourcearea.focus();
            };
            this._currently_editing = docobj;
          } else {
            kupu.setHTMLBody(sourcearea.value);
            kupu.clearClass(sourceClass);
            sourcearea.style.display = 'none';
            editorframe.style.display = 'block';
            if (/off/i.test(kupudoc.designMode)) {
                kupudoc.designMode = 'On';
            };
            if (!nograb) {
                docobj.getWindow().focus();
                var selection = this.editor.getSelection();
                selection.collapse();
            };

            kupu._initialized = true;
            this._currently_editing = null;
        };
        this.sourcemode = !this.sourcemode;
     };
};

SourceEditTool.prototype = new KupuTool;

function MultiSourceEditTool(sourcebuttonid, textareaprefix) {
    /* Source edit tool to edit document's html source */
    this.sourceButton = document.getElementById(sourcebuttonid);
    this.textareaprefix = textareaprefix;

    this.getSourceArea = function() {
        var docobj = this._currently_editing||kupu.getDocument();
        var sourceareaid = this.textareaprefix + docobj.getEditable().id;
        return document.getElementById(sourceareaid);
    }

    this._currently_editing = null;

};

MultiSourceEditTool.prototype = new SourceEditTool;
