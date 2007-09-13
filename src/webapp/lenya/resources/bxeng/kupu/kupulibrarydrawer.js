/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupulibrarydrawer.js 810 2004-09-14 11:49:42Z silvan $

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
    };

    this.createContent = function() {
        // load the initial XML
        if(!this.xmldata) {
            this.loadLibraries();
        } else {
            this.updateDisplay();
        };

        // display the drawer div
        this.element.style.display = 'block';
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
        var doc = this._transformXml();
        var sourcenode = doc.selectSingleNode('//*[@id="'+id+'"]');
        var targetnode = document.getElementById(id);
        this._replaceNodeContents(document, targetnode, sourcenode);
    };

    this.deselectActiveCollection = function() {
        /* Deselect the currently active collection or library */
        var selected = this.xmldata.selectSingleNode('//*[@selected]');
        if (selected) {
            // deselect selected DOM node
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
          if (divs[i].className == 'selected') {
            divs[i].className = 'kupu-libsource';
          };
        };

        var libnode_path = '/libraries/library[@id="' + id + '"]';
        var libnode = this.xmldata.selectSingleNode(libnode_path);
        libnode.setAttribute('selected', '1');

        var items_xpath = "items";
        var items_node = libnode.selectSingleNode(items_xpath);
        if (items_node) {
            // The library has already been loaded before or was
            // already provided with an items list. No need to do
            // anything except for displaying the contents in the
            // middle pane.
            this.updateDisplay(this.resourcespanelid);
        } else {
            // We have to load the library from XML first.
            var uri_node = libnode.selectSingleNode('src/text()');
            var src_uri = uri_node.nodeValue;
            src_uri = src_uri.strip(); // needs kupuhelpers.js
            // Now load the library into the items pane. Since we have
            // to load the XML, do this via a call back
            var wrapped_callback = new ContextFixer(this._libraryContentCallback, this);
            this._loadXML(src_uri, wrapped_callback.execute, null);
        };
        // instead of running the full transformations again we get a 
        // reference to the element and set the classname...
        var newseldiv = document.getElementById(id);
        newseldiv.className = 'selected';
    };

    this._libraryContentCallback = function(dom, src_uri) {
        /* callback for when a library's contents (item list) is loaded

        XXX with slight modifications to the below xpath, this can
        also be as the handler for reloading a standard collection.
        */
        var libnode = this.xmldata.selectSingleNode('//*[@selected]');
        var itemsnode = libnode.selectSingleNode("items");
        var newitemsnode = dom.selectSingleNode("//items");

        // IE does not support importNode on XML documet nodes
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
    };

    /*** Load a collection ***/

    this.selectCollection = function(id) {
        this.deselectActiveCollection();

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
            return;
        };

        // Case 3: We've not loaded the data yet, so we need to load it
        // this is just so we can find the leafnode much easier in the
        // callback.
        leafnode.setAttribute('selected', '1');
        var uri_node = leafnode.selectSingleNode('src/text()');
        var src_uri = uri_node.nodeValue;
        src_uri = src_uri.strip(); // needs kupuhelpers.js
        var wrapped_callback = new ContextFixer(this._collectionContentCallback, this);
        this._loadXML(src_uri, wrapped_callback.execute, null);
    };

    this._collectionContentCallback = function(dom, src_uri) {
        // Unlike with libraries, we don't have to find a node to hook
        // our results into, UNLESS we've hit the reload button. Maybe
        // use a different callback for that
        // (--> _libraryContentCallback).

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
    };

    /*** Selecting a resource ***/

    this.selectItem = function(id) {
        /* select an item in the item pane, show the item's metadata 

            the selected item will have a specific CSS class (selected)
        */
        var selxpath = '//resource[@selected]';
        var selitem = this.xmldata.selectSingleNode(selxpath);
        if (selitem) {
            var seldiv = document.getElementById(selitem.getAttribute('id'));
            if (seldiv) {
                seldiv.className = 'kupu-libsource';
            }
            selitem.removeAttribute('selected');
        };

        var resnode_path = '//resource[@id="' + id + '"]';
        var resnode = this.xmldata.selectSingleNode(resnode_path);
        resnode.setAttribute('selected', '1');

        // instead of running the full transformations again we get a 
        // reference to the element and set the classname...
        var newseldiv = document.getElementById(id);
        newseldiv.className = 'selected';

        // now load the item's metadata into the metadata pane
        this.displayItemMetadata(resnode);
    };

    this.displayItemMetadata = function() {
        /* 'loads' the metadata XML for a single item from the main XML 
        
            when loaded it displays the metadata in the metadata pane
        */
        // get the transformed html
        var htmldoc = this._transformXml();
        // XXXX Xpath query on HTML
        var proppan = htmldoc.selectSingleNode('//*[@id="kupu-propertiespanel"]');

        // XXX html element reference hard-coded, do we mind?
        var panediv = document.getElementById('kupu-propertiespanel');
        //XXXX Xpath query on HTML
        var newpanediv = htmldoc.selectSingleNode('//*[@id="kupu-propertiespanel"]');
        this._replaceNodeContents(document, panediv, newpanediv);
    };

    this.search = function() {
        /* search */
        var searchvalue = document.getElementById('kupu-searchbox').value;
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
        this.xmldata.importNode(resultlib, true);
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
        this.xmldata.transformNodeToObject(this.xsl, doc);
        return doc;
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

function ImageDrawer(tool, xsluri, libsuri, searchuri) {
    /* a specific LibraryDrawer for images */

    this.init(tool, xsluri, libsuri, searchuri);

    this.save = function() {
        /* create an image in the iframe according to collected data from the drawer */
        var selxpath = '//resource[@selected]';
        var selnode = this.xmldata.selectSingleNode(selxpath);
        if (!selnode) {
            return;
        };

        var uri = selnode.selectSingleNode('uri/text()').nodeValue;
        uri = uri.strip();  // needs kupuhelpers.js
        var img = this.tool.createImage(uri);
        var alt = document.getElementById('image_alt').value;
        img.setAttribute('alt', alt);

        // XXX for some reason, image drawers aren't closed
        // automatically like Link Drawers. This is definitely a bug
        // and should be fixed. Until that happens, close the drawer
        // manually:
        this.drawertool.closeDrawer();
    };
};

ImageDrawer.prototype = new LibraryDrawer;

function LinkDrawer(tool, xsluri, libsuri, searchuri) {
    /* a specific LibraryDrawer for links */

    this.init(tool, xsluri, libsuri, searchuri);

    this.save = function() {
        /* create a link in the iframe according to collected data from the drawer */
        var selxpath = '//resource[@selected]';
        var selnode = this.xmldata.selectSingleNode(selxpath);
        if (!selnode) {
            return;
        };

        var uri = selnode.selectSingleNode('uri/text()').nodeValue;
        uri = uri.strip();  // needs kupuhelpers.js

        // XXX requiring the user to know what link type to enter is a
        // little too much I think. (philiKON)
        var type = null;
        var name = document.getElementById('link_name').value;
        var target = document.getElementById('link_target').value;
        this.tool.createLink(uri, type, name, target);
    };
};

LinkDrawer.prototype = new LibraryDrawer;
