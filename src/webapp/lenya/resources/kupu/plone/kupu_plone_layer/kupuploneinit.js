/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupuploneinit.js 7833 2004-12-13 10:42:11Z duncan $

function initPloneKupu(iframe, fieldname) {
    var l = new DummyLogger();

    // XXX this should be fixed in stylesheets, but I don't know how to do 
    // that without applying this change to the outter document. Damn iframes.
    var ibody = iframe.contentWindow.document.body;
    var textarea = document.getElementById(fieldname);
    var form = textarea.form;
    ibody.style.margin = "12px";
    ibody.className = "kupu"; // This is set in emptypage but gets lost for some reason.
    ibody.innerHTML = textarea.value || '<p class=""><br></p>';

    // now some config values
    var conf = loadDictFromXML(document, 'kupuconfig');

    // the we create the document, hand it over the id of the iframe
    var doc = new KupuDocument(iframe);

    // now we can create the controller
    var kupu = new KupuEditor(doc, conf, l);

    // add the contextmenu
    var cm = new ContextMenu();
    kupu.setContextMenu(cm);

    // now we can create a UI object which we can use from the UI
    var ui = new PloneKupuUI('kupu-tb-styles');
    kupu.registerTool('ui', ui);

    // function that returns a function to execute a button command
    var execCommand = function(cmd) {
        return function(button, editor) {
            editor.execCommand(cmd);
        };
    };

    var boldchecker = ParentWithStyleChecker(new Array('b', 'strong'),
					     'font-weight', 'bold');
    var boldbutton = new KupuStateButton('kupu-bold-button', 
                                         execCommand('bold'),
                                         boldchecker,
                                         'kupu-bold',
                                         'kupu-bold-pressed');
    kupu.registerTool('boldbutton', boldbutton);

    var italicschecker = ParentWithStyleChecker(new Array('i', 'em'),
						'font-style', 'italic');
    var italicsbutton = new KupuStateButton('kupu-italic-button', 
                                           execCommand('italic'),
                                           italicschecker, 
                                           'kupu-italic', 
                                           'kupu-italic-pressed');
    kupu.registerTool('italicsbutton', italicsbutton);

    var underlinechecker = ParentWithStyleChecker(new Array('u'));
    var underlinebutton = new KupuStateButton('kupu-underline-button', 
                                              execCommand('underline'),
                                              underlinechecker,
                                              'kupu-underline', 
                                              'kupu-underline-pressed');
    kupu.registerTool('underlinebutton', underlinebutton);

    var subscriptchecker = ParentWithStyleChecker(new Array('sub'));
    var subscriptbutton = new KupuStateButton('kupu-subscript-button',
                                              execCommand('subscript'),
                                              subscriptchecker,
                                              'kupu-subscript',
                                              'kupu-subscript-pressed');
    kupu.registerTool('subscriptbutton', subscriptbutton);

    var superscriptchecker = ParentWithStyleChecker(new Array('super', 'sup'));
    var superscriptbutton = new KupuStateButton('kupu-superscript-button', 
                                                execCommand('superscript'),
                                                superscriptchecker,
                                                'kupu-superscript', 
                                                'kupu-superscript-pressed');
    kupu.registerTool('superscriptbutton', superscriptbutton);

    var justifyleftbutton = new KupuButton('kupu-justifyleft-button',
                                           execCommand('justifyleft'));
    kupu.registerTool('justifyleftbutton', justifyleftbutton);

    var justifycenterbutton = new KupuButton('kupu-justifycenter-button',
                                             execCommand('justifycenter'));
    kupu.registerTool('justifycenterbutton', justifycenterbutton);

    var justifyrightbutton = new KupuButton('kupu-justifyright-button',
                                            execCommand('justifyright'));
    kupu.registerTool('justifyrightbutton', justifyrightbutton);

    var outdentbutton = new KupuButton('kupu-outdent-button', execCommand('outdent'));
    kupu.registerTool('outdentbutton', outdentbutton);

    var indentbutton = new KupuButton('kupu-indent-button', execCommand('indent'));
    kupu.registerTool('indentbutton', indentbutton);

    var undobutton = new KupuButton('kupu-undo-button', execCommand('undo'));
    kupu.registerTool('undobutton', undobutton);

    var redobutton = new KupuButton('kupu-redo-button', execCommand('redo'));
    kupu.registerTool('redobutton', redobutton);

    var removeimagebutton = new KupuRemoveElementButton('kupu-removeimage-button',
							'img',
							'kupu-removeimage');
    kupu.registerTool('removeimagebutton', removeimagebutton);
    var removelinkbutton = new KupuRemoveElementButton('kupu-removelink-button',
						       'a',
						       'kupu-removelink');
    kupu.registerTool('removelinkbutton', removelinkbutton);

    // add some tools

    var listtool = new ListTool('kupu-list-ul-addbutton',
                                'kupu-list-ol-addbutton',
                                'kupu-ulstyles',
                                'kupu-olstyles');
    kupu.registerTool('listtool', listtool);

    var definitionlisttool = new DefinitionListTool('kupu-list-dl-addbutton');
    kupu.registerTool('definitionlisttool', definitionlisttool);
    
    var tabletool = new TableTool();
    kupu.registerTool('tabletool', tabletool);

    var showpathtool = new ShowPathTool('kupu-showpath-field');
    kupu.registerTool('showpathtool', showpathtool);

    var sourceedittool = new SourceEditTool('kupu-source-button',
                                            'kupu-editor-textarea');
    kupu.registerTool('sourceedittool', sourceedittool);

    var imagetool = NoContextMenu(new ImageTool());
    kupu.registerTool('imagetool', imagetool);

    var linktool = NoContextMenu(new LinkTool());
    kupu.registerTool('linktool', linktool);

    var zoom = new KupuZoomTool('kupu-zoom-button');
    kupu.registerTool('zoomtool', zoom);

    // Use the generic beforeUnload handler if we have it:
    var beforeunloadTool = window.onbeforeunload && window.onbeforeunload.tool;
    if (beforeunloadTool) {
        var initialBody = ibody.innerHTML;
        beforeunloadTool.addHandler(function() {
            return ibody.innerHTML != initialBody;
        });
        beforeunloadTool.chkId['kupu-tb-styles'] = beforeunloadTool.chkId[fieldname] = function() { return false; }
        beforeunloadTool.addForm(form);
    }
    // Patch for bad AT format pulldown.
    var fmtname = textarea.name+'_text_format';
    var pulldown = form[fmtname];
    if (pulldown && pulldown.type=='select-one') {
        for (var i=0 ; i < pulldown.length; i++) {
            var opt = pulldown.options[i];
            opt.selected = opt.defaultSelected = (opt.value=='text/html');
        }
        pulldown.disabled = true;
        var hidden = document.createElement('input');
        hidden.type = 'hidden';
        hidden.name = fmtname;
        hidden.value = 'text/html';
        pulldown.parentNode.appendChild(hidden);
    };

    // Drawers...

    // Function that returns function to open a drawer
    var opendrawer = function(drawerid) {
        return function(button, editor) {
            drawertool.openDrawer(drawerid);
        };
    };

    var imagelibdrawerbutton = new KupuButton('kupu-imagelibdrawer-button',
                                              opendrawer('imagelibdrawer'));
    kupu.registerTool('imagelibdrawerbutton', imagelibdrawerbutton);

    var linklibdrawerbutton = new KupuButton('kupu-linklibdrawer-button',
                                             opendrawer('linklibdrawer'));
    kupu.registerTool('linklibdrawerbutton', linklibdrawerbutton);

    var linkdrawerbutton = new KupuButton('kupu-linkdrawer-button',
                                          opendrawer('linkdrawer'));
    kupu.registerTool('linkdrawerbutton', linkdrawerbutton);

    var tabledrawerbutton = new KupuButton('kupu-tabledrawer-button',
                                           opendrawer('tabledrawer'));
    kupu.registerTool('tabledrawerbutton', tabledrawerbutton);

    // create some drawers, drawers are some sort of popups that appear when a 
    // toolbar button is clicked
    var drawertool = new DrawerTool();
    kupu.registerTool('drawertool', drawertool);

    var linklibdrawer = new LinkLibraryDrawer(linktool, conf['link_xsl_uri'],
                                              conf['link_libraries_uri'],
                                              conf['search_links_uri']);
    drawertool.registerDrawer('linklibdrawer', linklibdrawer);

    var imagelibdrawer = new ImageLibraryDrawer(imagetool, conf['image_xsl_uri'],
                                                conf['image_libraries_uri'],
                                                conf['search_images_uri']);
    drawertool.registerDrawer('imagelibdrawer', imagelibdrawer);

    var linkdrawer = new LinkDrawer('kupu-linkdrawer', linktool);
    drawertool.registerDrawer('linkdrawer', linkdrawer);

    var tabledrawer = new TableDrawer('kupu-tabledrawer', tabletool);
    drawertool.registerDrawer('tabledrawer', tabledrawer);

    // register form submit handler, remove the drawer's contents before submitting 
    // the form since it seems to crash IE if we leave them alone
    function prepareForm(event) {
        kupu.saveDataToField(this.form, field);
        var drawer = document.getElementById('kupu-librarydrawer');
        drawer.parentNode.removeChild(drawer);
    };
    var field = document.getElementById(fieldname);
    addEventHandler(field.form, 'submit', prepareForm, field);
                
    return kupu;
};

// modify LinkDrawer so all links have a target
// defaults to _self, override here if reqd.
//LinkDrawer.prototype.target = '_blank';

