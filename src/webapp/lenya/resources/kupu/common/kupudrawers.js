/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 * 
 *****************************************************************************/

// $Id: kupudrawers.js 7887 2004-12-16 16:15:29Z duncan $

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
        if (this.editor.getBrowserName() == 'IE') {
            this.editor._saveSelection();
        }
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
        if (this.editor.getBrowserName() == 'IE') {
            this.element.focus();
        }
    };

    this.hide = function() {
        this.element.style.display = 'none';
    };
};

function LinkDrawer(elementid, tool) {
    /* Link drawer */
    this.element = document.getElementById(elementid);
    this.tool = tool;

    this.createContent = function() {
        /* display the drawer */
        var currnode = this.editor.getSelectedNode();
        var linkel = this.editor.getNearestParentOfType(currnode, 'a');
        var input = document.getElementById('kupu-linkdrawer-input');
        input.value = "";
        this.preview();
        if (linkel) {
            input.value = linkel.getAttribute('href');
        } else {
            input.value = 'http://';
        };
        this.element.style.display = 'block';
        if (this.editor.getBrowserName() == 'IE') {
            this.element.focus();
        }
    };

    this.save = function() {
        /* add or modify a link */
        var input = document.getElementById('kupu-linkdrawer-input');
        var url = input.value;
        var target = '_self';
        if (this.target) target = this.target;
        this.tool.createLink(url, null, null, target);
        input.value = '';

        // XXX when reediting a link, the drawer does not close for
        // some weird reason. BUG! Close the drawer manually until we
        // find a fix:
        this.drawertool.closeDrawer();
    };
    
    this.preview = function() {
        var input = document.getElementById('kupu-linkdrawer-input');
        var preview = document.getElementById('kupu-linkdrawer-preview');
        preview.src = input.value;
        if (this.editor.getBrowserName() == 'IE') {
            preview.width = "800";
            preview.height = "365";
            preview.style.zoom = "60%";
        };
    }
    this.preview_loaded = function() {
        var input = document.getElementById('kupu-linkdrawer-input');
        var preview = document.getElementById('kupu-linkdrawer-preview');
        if (input.value  != preview.src) {
            input.value = preview.src;
        }
    }
};

LinkDrawer.prototype = new Drawer;

function TableDrawer(elementid, tool) {
    /* Table drawer */
    this.element = document.getElementById(elementid);
    this.tool = tool;

    this.addpanelid = 'kupu-tabledrawer-addtable';
    this.editpanelid = 'kupu-tabledrawer-edittable';

    this.addpanel = document.getElementById(this.addpanelid);
    this.editpanel = document.getElementById(this.editpanelid);

    this.createContent = function() {
        var selNode = this.editor.getSelectedNode();
        if (this.editor.config.table_classes) {
            var classselect = document.getElementById('kupu-tabledrawer-classchooser');
            var classes = this.editor.config.table_classes['class'];
            while (classselect.hasChildNodes()) {
                classselect.removeChild(classselect.firstChild);
            };
            for (var i=0; i < classes.length; i++) {
                var classname = classes[i];
                var option = document.createElement('option');
                var content = document.createTextNode(classname);
                option.appendChild(content);
                option.setAttribute('value', classname);
                classselect.appendChild(option);
            };
        };
        
        var table = this.editor.getNearestParentOfType(selNode, 'table');

        if (!table) {
            // show add table drawer
            show = this.addpanel;
            hide = this.editpanel;
        } else {
            // show edit table drawer
            show = this.editpanel;
            hide = this.addpanel;
            var align = this.tool._getColumnAlign(selNode);
            var alignselect = document.getElementById('kupu-tabledrawer-alignchooser');
            selectSelectItem(alignselect, align);
            var classselect = document.getElementById('kupu-tabledrawer-classchooser');
            selectSelectItem(classselect, table.className);
        };
        hide.style.display = 'none';
        show.style.display = 'block';
        this.element.style.display = 'block';
        if (this.editor.getBrowserName() == 'IE') {
            this.element.focus();
        }
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

function LibraryDrawer(tool, xsluri, libsuri, searchuri) {
    /* a drawer that loads XSLT and XML from the server 
       and converts the XML to XHTML for the drawer using the XSLT

       there are 2 types of XML file loaded from the server: the first
       contains a list of 'libraries', partitions for the data items, 
       and the second a list of data items for a certain library

       all XML loading is done async, since sync loading can freeze Mozilla
    */

    this.init = function(tool, xsluri, libsuri, searchuri) {
        /* This method is there to thin out the constructor and to be
           able to inherit it in sub-prototypes. Don't confuse this
           method with the component initializer (initialize()).
        */
        // these are used in the XSLT. Maybe they should be
        // parameterized or something, but we depend on so many other
        // things implicitly anyway...
        this.drawerid = 'kupu-librarydrawer';
        this.librariespanelid = 'kupu-librariespanel';
        this.resourcespanelid = 'kupu-resourcespanel';
        this.propertiespanelid = 'kupu-propertiespanel';

        this.tool = tool;
        this.element = document.getElementById(this.drawerid);
        this.xsluri = xsluri;
        this.libsuri = libsuri;
        this.searchuri = searchuri;
        
        // marker that gets set when a new image has been uploaded
        this.newimages = null;

        // the following vars will be available after this.initialize()
        // has been called
    
        // this will be filled by this._libXslCallback()
        this.xsl = null;
        // this will be filled by this.loadLibraries(), which is called 
        // somewhere further down the chain starting with 
        // this._libsXslCallback()
        this.xmldata = null;

    };
    this.init(tool, xsluri, libsuri, searchuri);

    this.initialize = function(editor, drawertool) {
        this.editor = editor;
        this.drawertool = drawertool;

        // load the xsl and the initial xml
        var wrapped_callback = new ContextFixer(this._libsXslCallback, this);
        this._loadXML(this.xsluri, wrapped_callback.execute);
    };

    /*** bootstrapping ***/

    this._libsXslCallback = function(dom) {
        /* callback for when the xsl for the libs is loaded
        
            this is called on init and since the initial libs need
            to be loaded as well (and everything is async with callbacks
            so there's no way to wait until the XSL is loaded) this
            will also make the first loadLibraries call
        */
        this.xsl = dom;

        // Change by Paul to have cached xslt transformers for reuse of 
        // multiple transforms and also xslt params
        try {
            this.xsltproc = new XSLTProcessor();
            this.xsltproc.importStylesheet(dom);
            this.xsltproc.setParameter("", "drawertype", this.drawertype);
            this.xsltproc.setParameter("", "drawertitle", this.drawertitle);
            this.xsltproc.setParameter("", "showupload", this.showupload);
            if (this.editor.config.captions) {
                this.xsltproc.setParameter("", "usecaptions", 'yes');
            }
        } catch(e) {
            return; // No XSLT Processor, maybe IE 5.5?
        }
    };

    this.createContent = function() {
        // load the initial XML
        if(!this.xmldata) {
            // Do a meaningful test to see if this is IE5.5 or some other 
            // editor-enabled version whose XML support isn't good enough 
            // for the drawers
            if (!Sarissa.IS_ENABLED_XSLTPROC) {
               alert("This function requires better XML support in your browser.");
               return;
            }
            this.loadLibraries();
        } else {
            if (this.newimages) {
                this.reloadCurrent();
                this.newimages = null;
            };
            this.updateDisplay();
        };

        // display the drawer div
        this.element.style.display = 'block';
        if (this.editor.getBrowserName() == 'IE') {
            this.element.focus();
        }
    };

    this._singleLibsXslCallback = function(dom) {
        /* callback for then the xsl for single libs (items) is loaded

            nothing special needs to be called here, since initially the
            items pane will be empty
        */
        this.singlelibxsl = dom;
    };

    this.loadLibraries = function() {
        /* load the libraries and display them in a redrawn drawer */
        var wrapped_callback = new ContextFixer(this._libsContentCallback, this);
        this._loadXML(this.libsuri, wrapped_callback.execute);
    };

    this._libsContentCallback = function(dom) {
        /* this is called when the libs xml is loaded

            does the xslt transformation to set up or renew the drawer's full
            content and adds the content to the drawer
        */
        this.xmldata = dom;
        this.xmldata.setProperty("SelectionLanguage", "XPath");

        // replace whatever is in there with our stuff
        this.updateDisplay(this.drawerid);
    };

    this.updateDisplay = function(id) {
      /* (re-)transform XML and (re-)display the necessary part
       */
        if(!id) {
            id = this.drawerid;
        };
        try {
            this.xsltproc.setParameter("", "showupload", this.showupload);
        } catch(e) {};
        var doc = this._transformXml();
        var sourcenode = doc.selectSingleNode('//*[@id="'+id+'"]');
        var targetnode = document.getElementById(id);
        this._replaceNodeContents(document, targetnode, sourcenode);

        if (this.editor.getBrowserName() == 'IE' && id == this.resourcespanelid) {
            this.updateDisplay(this.drawerid);
        };
    };

    this.deselectActiveCollection = function() {
        /* Deselect the currently active collection or library */
        while (1) {
            // deselect selected DOM node
            var selected = this.xmldata.selectSingleNode('//*[@selected]');
            if (!selected) {
                return;
            };
            selected.removeAttribute('selected');
        };
    };

    /*** Load a library ***/

    this.selectLibrary = function(id) {
        /* unselect the currently selected lib and select a new one

            the selected lib (libraries pane) will have a specific CSS class 
            (selected)
        */
        // remove selection in the DOM
        this.deselectActiveCollection();
        // as well as visual selection in CSS
        // XXX this is slow, but we can't do XPath, unfortunately
        var divs = this.element.getElementsByTagName('div');
        for (var i=0; i<divs.length; i++ ) {
          if (divs[i].className == 'kupu-libsource-selected') {
            divs[i].className = 'kupu-libsource';
          };
        };

        var libnode_path = '/libraries/library[@id="' + id + '"]';
        var libnode = this.xmldata.selectSingleNode(libnode_path);
        libnode.setAttribute('selected', '1');

        var items_xpath = "items";
        var items_node = libnode.selectSingleNode(items_xpath);
        
        if (items_node && !this.newimages) {
            // The library has already been loaded before or was
            // already provided with an items list. No need to do
            // anything except for displaying the contents in the
            // middle pane. Newimages is set if we've lately
            // added an image.
            this.updateDisplay(this.resourcespanelid);
            this.updateDisplay(this.propertiespanelid);
        } else {
            // We have to load the library from XML first.
            var src_uri = libnode.selectSingleNode('src/text()').nodeValue;
            src_uri = src_uri.strip(); // needs kupuhelpers.js
            // Now load the library into the items pane. Since we have
            // to load the XML, do this via a call back
            var wrapped_callback = new ContextFixer(this._libraryContentCallback, this);
            this._loadXML(src_uri, wrapped_callback.execute, null);
            this.newimages = null;
        };
        // instead of running the full transformations again we get a 
        // reference to the element and set the classname...
        var newseldiv = document.getElementById(id);
        newseldiv.className = 'kupu-libsource-selected';
    };

    this._libraryContentCallback = function(dom, src_uri) {
        /* callback for when a library's contents (item list) is loaded

        This is also used as he handler for reloading a standard
        collection.
        */
        var libnode = this.xmldata.selectSingleNode('//*[@selected]');
        var itemsnode = libnode.selectSingleNode("items");
        var newitemsnode = dom.selectSingleNode("//items");

        // IE does not support importNode on XML document nodes. As an
        // evil hack, clonde the node instead.

        if (this.editor.getBrowserName() == 'IE') {
            newitemsnode = newitemsnode.cloneNode(true);
        } else {
            newitemsnode = this.xmldata.importNode(newitemsnode, true);
        }
        if (!itemsnode) {
            // We're loading this for the first time
            libnode.appendChild(newitemsnode);
        } else {
            // User has clicked reload
            libnode.replaceChild(newitemsnode, itemsnode);
        };
        this.updateDisplay(this.resourcespanelid);
        this.updateDisplay(this.propertiespanelid);
    };

    /*** Load a collection ***/

    this.selectCollection = function(id) {
        this.deselectActiveCollection();

        // First turn off current selection, if any
        this.removeSelection();
        
        var leafnode_path = "//collection[@id='" + id + "']";
        var leafnode = this.xmldata.selectSingleNode(leafnode_path);

        // Case 1: We've already loaded the data, so we just need to
        // refer to the data by id.
        var loadedInNode = leafnode.getAttribute('loadedInNode');
        if (loadedInNode) {
            var collnode_path = "/libraries/collection[@id='" + loadedInNode + "']";
            var collnode = this.xmldata.selectSingleNode(collnode_path);
            if (collnode) {
                collnode.setAttribute('selected', '1');
                this.updateDisplay(this.resourcespanelid);
                this.updateDisplay(this.propertiespanelid);
                return;
            };
        };

        // Case 2: We've already loaded the data, but there hasn't
        // been a reference made yet. So, make one :)
        uri = leafnode.selectSingleNode('uri/text()').nodeValue;
        uri = (new String(uri)).strip(); // needs kupuhelpers.js
        var collnode_path = "/libraries/collection/uri[text()='" + uri + "']/..";
        var collnode = this.xmldata.selectSingleNode(collnode_path);
        if (collnode) {
            id = collnode.getAttribute('id');
            leafnode.setAttribute('loadedInNode', id);
            collnode.setAttribute('selected', '1');
            this.updateDisplay(this.resourcespanelid);
            this.updateDisplay(this.propertiespanelid);
            return;
        };

        // Case 3: We've not loaded the data yet, so we need to load it
        // this is just so we can find the leafnode much easier in the
        // callback.
        leafnode.setAttribute('selected', '1');
        var src_uri = leafnode.selectSingleNode('src/text()').nodeValue;
        src_uri = src_uri.strip(); // needs kupuhelpers.js
        var wrapped_callback = new ContextFixer(this._collectionContentCallback, this);
        this._loadXML(src_uri, wrapped_callback.execute, null);
    };

    this._collectionContentCallback = function(dom, src_uri) {
        // Unlike with libraries, we don't have to find a node to hook
        // our results into (UNLESS we've hit the reload button, but
        // that is handled in _libraryContentCallback anyway).
        // We need to give the newly retrieved data a unique ID, we
        // just use the time.
        date = new Date();
        time = date.getTime();

        // attach 'loadedInNode' attribute to leaf node so Case 1
        // applies next time.
        var leafnode = this.xmldata.selectSingleNode('//*[@selected]');
        leafnode.setAttribute('loadedInNode', time);
        this.deselectActiveCollection()

        var collnode = dom.selectSingleNode('/collection');
        collnode.setAttribute('id', time);
        collnode.setAttribute('selected', '1');

        var libraries = this.xmldata.selectSingleNode('/libraries');

        // IE does not support importNode on XML documet nodes
        if (this.editor.getBrowserName() == 'IE') {
            collnode = collnode.cloneNode(true);
        } else {
            collnode = this.xmldata.importNode(collnode, true);
        }
        libraries.appendChild(collnode);
        this.updateDisplay(this.resourcespanelid);
        this.updateDisplay(this.propertiespanelid);
    };

    /*** Reloading a collection or library ***/

    this.reloadCurrent = function() {
        // Reload current collection or library
        this.showupload = '';
        var current = this.xmldata.selectSingleNode('//*[@selected]');
        // make sure we're dealing with a collection even though a
        // resource might be selected
        if (current.tagName == "resource") {
            current.removeAttribute("selected");
            current = current.parentNode;
            current.setAttribute("selected", "1");
        };
        var src_node = current.selectSingleNode('src');
        if (!src_node) {
            // simply do nothing if the library cannot be reloaded. This
            // is currently the case w/ search result libraries.
            return;
        };

        var src_uri = src_node.selectSingleNode('text()').nodeValue;
        
        src_uri = src_uri.strip(); // needs kupuhelpers.js

        var wrapped_callback = new ContextFixer(this._libraryContentCallback, this);
        this._loadXML(src_uri, wrapped_callback.execute);
    };

    this.removeSelection = function() {
        // turn off current selection, if any
        var oldselxpath = '/libraries/*[@selected]//resource[@selected]';
        var oldselitem = this.xmldata.selectSingleNode(oldselxpath);
        if (oldselitem) {
            oldselitem.removeAttribute("selected");
        };
        this.showupload = '';
    }

    this.selectUpload = function() {
        this.removeSelection();
        this.showupload = 'yes';
        this.updateDisplay(this.resourcespanelid);
        this.updateDisplay(this.propertiespanelid);
    }
    /*** Selecting a resource ***/

    this.selectItem = function (id) {
        /* select an item in the item pane, show the item's metadata */

        // First turn off current selection, if any
        this.removeSelection();
        
        // Grab XML DOM node for clicked "resource" and mark it selected
        var newselxpath = '/libraries/*[@selected]//resource[@id="' + id + '"]';
        var newselitem = this.xmldata.selectSingleNode(newselxpath);
        newselitem.setAttribute("selected", "1");

        this.updateDisplay(this.resourcespanelid);
        this.updateDisplay(this.propertiespanelid);
        return;
    }


    this.search = function() {
        /* search */
        var searchvalue = document.getElementById('kupu-searchbox-input').value;
        //XXX make search variable configurable
        var body = 'SearchableText=' + escape(searchvalue);

        // the search uri might contain query parameters in HTTP GET
        // style. We want to do a POST though, so find any possible
        // parameters, trim them from the URI and append them to the
        // POST body instead.
        var chunks = this.searchuri.split('?');
        var searchuri = chunks[0];
        if (chunks[1]) {
            body += "&" + chunks[1];
        };
        var wrapped_callback = new ContextFixer(this._searchCallback, this);
        this._loadXML(searchuri, wrapped_callback.execute, body);
    };

    this._searchCallback = function(dom) {
        var resultlib = dom.selectSingleNode("/library");

        var items = resultlib.selectNodes("items/*");
        if (!items.length) {
            alert("No results found.");
            return;
        };

        // we need to give the newly retrieved data a unique ID, we
        // just use the time.
        date = new Date();
        time = date.getTime();
        resultlib.setAttribute("id", time);

        // deselect the previous collection and mark the result
        // library as selected
        this.deselectActiveCollection();
        resultlib.setAttribute("selected", "1");

        // now hook the result library into our DOM
        if (this.editor.getBrowserName() == 'IE') {
            resultlib = resultlib.cloneNode(true);
        } else {
            this.xmldata.importNode(resultlib, true);
        }
        var libraries = this.xmldata.selectSingleNode("/libraries");
        libraries.appendChild(resultlib);

        this.updateDisplay(this.drawerid);
        var newseldiv = document.getElementById(time);
        newseldiv.className = 'selected';
    };

    this.save = function() {
        /* save the element, should be implemented on subclasses */
        throw "Not yet implemented";
    };

    /*** Auxiliary methods ***/

    this._transformXml = function() {
        /* transform this.xmldata to HTML using this.xsl and return it */
        var doc = Sarissa.getDomDocument();

	//var xsltproc = new XSLTProcessor();
	var result = this.xsltproc.transformToDocument(this.xmldata);

	// this.xmldata.transformNodeToObject(this.xsl, doc);
        return result;
    };

    this._loadXML = function(uri, callback, body) {
        /* load the XML from a uri
        
            calls callback with one arg (the XML DOM) when done
            the (optional) body arg should contain the body for the request
*/
        var xmlhttp = Sarissa.getXmlHttpRequest();
        var method = 'GET';
        if (body) {
          method = 'POST';
        } else {
          // be sure that body is null and not an empty string or
          // something
          body = null;
        };
        xmlhttp.open(method, uri, true);
        // use ContextFixer to wrap the Sarissa callback, both for isolating 
        // the 'this' problem and to be able to pass in an extra argument 
        // (callback)
        var wrapped_callback = new ContextFixer(this._sarissaCallback, xmlhttp,
                                                callback, uri);
        xmlhttp.onreadystatechange = wrapped_callback.execute;
        if (method == "POST") {
            // by default, we would send a 'text/xml' request, which
            // is a dirty lie; explicitly set the content type to what
            // a web server expects from a POST.
            xmlhttp.setRequestHeader('content-type', 'application/x-www-form-urlencoded');
        };
        xmlhttp.send(body);
    };

    this._replaceNodeContents = function(doc, target, container) {
        /* replace all childnodes in target with all childnodes in container */
        var importedContainer = doc.importNode(container, true);
        // XXX it seems that IE doesn't allow hacks like these
        // no need to worry anyway, since the transformed HTML seems
        // to have the right JS context variables anyway.

        if (this.editor.getBrowserName() != 'IE') {
            container.ownerDocument.contentWindow = doc.contentWindow;
        };
        while (target.hasChildNodes()) {
            target.removeChild(target.firstChild);
        };
        // XXX don't know if this works since i'm not sure whether 
        // appendChild actually removes a child from a previous
        // location (although i think it does)
        while (importedContainer.hasChildNodes()) {
            target.appendChild(importedContainer.firstChild);
        };
    };

    this._sarissaCallback = function(user_callback, uri) {
        /* callback for Sarissa
            when the callback is called because the data's ready it
            will get the responseXML DOM and call user_callback
            with the DOM as the first argument and the uri loaded
            as the second
            
            note that this method should be called in the context of an 
            xmlhttp object
        */
        var errmessage = 'Error loading XML: ';
        if (uri) {
            errmessage = 'Error loading ' + uri + ':';
        };
        if (this.readyState == 4) {
            if (this.status && this.status != 200) {
                alert(errmessage + this.status);
                throw "Error loading XML";
            };
            var dom = this.responseXML;
            user_callback(dom, uri);
        };
    };
};

LibraryDrawer.prototype = new Drawer;

function ImageLibraryDrawer(tool, xsluri, libsuri, searchuri) {
    /* a specific LibraryDrawer for images */

    this.drawertitle = "Image Library";
    this.drawertype = "image";
    this.showupload = '';
    this.init(tool, xsluri, libsuri, searchuri);    
 
    
    // upload, on submit/insert press
    this.uploadImage = function() {
        var form = document.kupu_upload_form;
        if (!form || form.node_prop_image.value=='') return;

        if (form.node_prop_caption.value == "") {
            alert("Please enter a title for the image you are uploading");
            return;        
        };
        
        var targeturi =  this.xmldata.selectSingleNode('/libraries/*[@selected]/uri/text()').nodeValue
        document.kupu_upload_form.action =  targeturi + "/kupuUploadImage";
        document.kupu_upload_form.submit();
    };
    
    // called for example when no permission to upload for some reason
    this.cancelUpload = function(msg) {
        var s = this.xmldata.selectSingleNode('/libraries/*[@selected]');     
        s.removeAttribute("selected");
        this.updateDisplay();
        if (msg != '') {
            alert(msg);
        };
    };
    
    // called by onLoad within document sent by server
    this.finishUpload = function(url) {
        var img = this.tool.createImage(url);
        if (this.editor.config.captions) {
            img.className = img.className + " captioned";
        }
        this.newimages = 1;
        this.drawertool.closeDrawer();
    };
    

    this.save = function() {
        /* create an image in the iframe according to collected data
           from the drawer */
        var selxpath = '//resource[@selected]';
        var selnode = this.xmldata.selectSingleNode(selxpath);
        
        // If no image resource is selected, check for upload
        if (!selnode) {
            var uploadbutton = this.xmldata.selectSingleNode("/libraries/*[@selected]//uploadbutton");
            if (uploadbutton) {
                this.uploadImage();
            };
            return;
        };

        var uri = selnode.selectSingleNode('uri/text()').nodeValue;
        uri = uri.strip();  // needs kupuhelpers.js
        var img = this.tool.createImage(uri);
        var alt = document.getElementById('image_alt').value;
        img.setAttribute('alt', alt);

        // Set image class from the alignment radio buttons
        var radios = document.getElementsByName('image-align');
        for (var i = 0; i < radios.length; i++) {
            if (radios[i].checked) {
                img.className = radios[i].value;
            }
        }

        var caption = document.getElementsByName('image-caption');
        if (caption && caption.length>0 && caption[0].checked) {
            img.className = img.className + " captioned";
        }

        this.drawertool.closeDrawer();
    };
};

ImageLibraryDrawer.prototype = new LibraryDrawer;

function LinkLibraryDrawer(tool, xsluri, libsuri, searchuri) {
    /* a specific LibraryDrawer for links */

    this.drawertitle = "Link Drawer";
    this.drawertype = "link";
    this.showupload = '';
    this.init(tool, xsluri, libsuri, searchuri);

    this.save = function() {
        /* create a link in the iframe according to collected data
           from the drawer */
        var selxpath = '//resource[@selected]';
        var selnode = this.xmldata.selectSingleNode(selxpath);
        if (!selnode) {
            return;
        };

        var uri = selnode.selectSingleNode('uri/text()').nodeValue;
        uri = uri.strip();  // needs kupuhelpers.js
        var title = '';
        title = selnode.selectSingleNode('title/text()').nodeValue;
        title = title.strip();

        // XXX requiring the user to know what link type to enter is a
        // little too much I think. (philiKON)
        var type = null;
        var name = document.getElementById('link_name').value;
        var target = null;
        if (document.getElementById('link_target') && document.getElementById('link_target').value != '')
            target = document.getElementById('link_target').value;
        
        this.tool.createLink(uri, type, name, target, title);
    };
};

LinkLibraryDrawer.prototype = new LibraryDrawer;
