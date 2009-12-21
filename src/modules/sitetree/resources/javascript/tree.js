/*****************************************************************************
 *
 * Copyright (c) 2004 Guido Wesdorp. All rights reserved.
 *
 * This software is distributed under the terms of the tree.js
 * License. See LICENSE.txt for license text.
 *
 *  E-mail: johnny@debris.demon.nl
 *
 *****************************************************************************/

/*
Copyright (c) 2004, Guido Wesdorp
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:


    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.

    * Neither the name of tree.js nor the names of its contributors may
      be used to endorse or promote products derived from this
      software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

function Node(id, parent) {
    this.id = id;
    this.parent = parent;
    // this is just here for show, will be overwritten in init()
    this.items = {};
};

Node.prototype.init = function() {
    this.depth = this.parent.depth + 1;
    this.tree = this.parent.tree;
    this.reopen = false;
    this.items = {};
};

Node.prototype.open = function(reload, endhandler) {
    if (!this.isCollection()) {
        if (reload) {
            this.reloadSingle();
        };
        return;
    };
    if (!reload && this.opened) {
        return;
    };
    this.opened = true;
    var newopensign = this.tree.getOpenSign(this);
    this.opensign.parentNode.replaceChild(newopensign, this.opensign);
    this.opensign = newopensign;
    addEventHandler(this.opensign, 'click', this.tree.handleItemSignClick, 
                    this.tree, this);
    if (!this.itemids || this.itemids.length==0) {
        // get the items, getItems() should be defined on the subclass
        this.tree.doc.getElementsByTagName('body')[0].style.cursor = 'wait';
        this.element.className = this.element.className;
        this.tree.getItems(this, this._continueOpen, !reload, endhandler);
    } else {
      
        for (var itemid in this.items) {
            var item = this.items[itemid];
            item.render();
            if (item.reopen) {
                item.open();
            };
        };
        if (endhandler) {
            endhandler();
        };
    };
};

Node.prototype._continueOpen = function(items, endhandler) {
    this.items = {};
    this.itemids = [];
    for (var i = items.length - 1; i >= 0; i--) {
        var item = items[i];
        this.items[item.id] = item;
        this.itemids.unshift(item.id);
        item.render();
        if (item.reopen) {
            item.open();
        };
    };

    this.tree.doc.getElementsByTagName('body')[0].style.cursor = 'default';
    if (endhandler) {
        endhandler();
    };
};

Node.prototype.reloadSingle = function() {
    /* called on opening a non-collection item with a reload arg

        can be overridden in subclasses
    */
};

Node.prototype.close = function(reopen_when_opening_parent) {
    if (!this.opened || !this.isCollection()) {
        return;
    };
    this.reopen = reopen_when_opening_parent;
    var newopensign = this.tree.getCloseSign(this);
    this.opensign.parentNode.replaceChild(newopensign, this.opensign);
    this.opensign = newopensign;
    this.opened = false;
    addEventHandler(this.opensign, 'click', this.tree.handleItemSignClick, 
                    this.tree, this);
    for (var itemid in this.items) {
        var item = this.items[itemid];
        item.close(true);
        this.tree.unrenderItem(item);
    };
};

Node.prototype.reload = function() {
    this.itemids = null;
    var currel = this.element;
    this.element = this.tree.createItemHtml(this);
    currel.parentNode.replaceChild(this.element, currel);
    if (this.opened) {
        this.close();
        this.open(true);
    };
};

Node.prototype.render = function() {
    this.tree.renderItem(this);
};

Node.prototype.isCollection = function() {
    return true;
};

Node.prototype.getPath = function() {
    var parentpath;
    if (this.parent) {
        parentpath = this.parent.getPath();
    } else {
        parentpath =  '';
    }
    return parentpath + '/' + this.id;
};

Node.prototype.setSelectClass = function() {
    if (this.element) {
        this.element.className = 'selected_node';
    };
};

Node.prototype.unsetSelectClass = function() {
    if (this.element) {
        this.element.className = 'unselected_node';
    };
};

Node.prototype.getItemByPath = function(path) {
    /* return an item by its path 
    
        path should be relative from the current item, excluding 
        the current item's id
    */
    var item = null;
    for (var itemid in this.items) {
        // see if the path starts with the next item id
        if (path.indexOf(itemid + '/') == 0 || path == itemid) {
            item = this.items[itemid];
            break;
        };
    };
    if (!item) {
        return false;
    };
    if (path == item.id) {
        return item;
    } else {
        return item.getItemByPath(path.substr(item.id.length+1));
    };
    return false;
};

function Tree(doc, treeElement) {
    this.doc = doc;
    this.treeElement = treeElement;
    this.selected = null;
};

Tree.prototype.init = function(id) {
    this.root = new Node(id);
    this.root.tree = this;
    this.root.depth = 0;
    this.root.reopen = false;
    this._currentId = 0;
    // addEventHandler(this.doc, 'contextmenu', 
    //        this.createContextMenu, this);
    //addEventHandler(this.doc, 'mouseup', this.removeContextMenu, this);
};

Tree.prototype.getItems = function(item, handler, reload, endhandler) {
    /* this should be overridden in subclasses */
    var items = [];
    var ids = ['foo', 'bar', 'baz'];
    for (var i=0; i < ids.length; i++) {
        var child = new Node(ids[i], item);
        child.init();
        items.push(child);
    };
    handler.call(item, items, endhandler);
};

Tree.prototype.render = function() {
    var element = this.createItemHtml(this.root);
    this.root.element = element;
    element.tree_item = this.root;
    this.doc.getElementsByTagName('body')[0].style.cursor = 'default';
    this.treeElement.appendChild(element);
};

Tree.prototype.renderItem = function(item) {
    var element = this.createItemHtml(item);
    this.doc.getElementsByTagName('body')[0].style.cursor = 'default';
    item.element = element;
    element.tree_item = item;
    if (item.parent.element.nextSibling) {
        item.parent.element.parentNode.insertBefore(item.element, 
                                    item.parent.element.nextSibling);
    } else {
        item.parent.element.parentNode.appendChild(item.element);
    };
};

Tree.prototype.createItemHtml = function(item) {
    /* you may want to override this */
    var div = this.doc.createElement('div');
    div.style.whiteSpace = 'nowrap';
    div.className = 'treenode';
    
    // place a reference to the item on the div
    div.treeitem = item;

    var space = '';
    for (var i=0; i < item.depth; i++) {
        space += '\xa0\xa0\xa0';
    };
    var text = this.doc.createTextNode(space);
    div.appendChild(text);
    
    item.opensign = this.getCloseSign(item);
    item.opensign.className = 'treenode_opensign';
    div.appendChild(item.opensign);

    // the actual icon and name are generated from another method
    // to improve extending
    var line = this.createItemLine(item);
    div.appendChild(line);
    
    addEventHandler(line, 'click', this.handleItemClick, this, item);
    addEventHandler(item.opensign, 'click', this.handleItemSignClick, 
                    this, item);

    if (this.selected == item.getPath()) {
        this.unselect();
        this.select(item);
        div.className = 'selected_node';
    };

    return div;
};

Tree.prototype.createItemLine = function(item) {
    var div = this.doc.createElement('div');
    /* creates the item name and any icons and such */
    var span = this.doc.createElement('span');
    var icon = this.getIcon(item);
    if (icon.nodeType == 1) {
        icon.className = 'treenode_icon';
    };
    div.appendChild(icon);
    
    var text = this.doc.createTextNode(item.name ? item.name : item.id);
    span.className = 'treenode_label';
    span.appendChild(text);
    div.appendChild(span);
    
    return div;
};

Tree.prototype.unrenderItem = function(item) {
    if (item.element) {
        item.element.parentNode.removeChild(item.element);
        delete item.element;
    };
};

Tree.prototype.handleItemClick = function(item, event) {
    if (item.skip_next_click) {
        item.skip_next_click = false;
        return;
    };
    this.select(item);
    if (!item.opened) {
        item.open();
    } else {
        item.close();
    };
    if (item.element && item.element.scrollIntoView) {
        item.element.scrollIntoView();
    };
};

Tree.prototype.select = function(item) {
    this.unselect();
    this.selected = item.getPath();
    item.setSelectClass();
};

Tree.prototype.unselect = function() {
    if (typeof(this.selected) == 'string') {
        var item = this.getItemByPath(this.selected);
        item.unsetSelectClass();
    };
};

Tree.prototype.handleItemSignClick = function(item, event) {
    if (!item.opened) {
        item.open();
    } else {
        item.close();
    };
    if (event.preventDefault) {
        event.preventDefault();
    } else {
        event.returnValue = false;
    };
};

Tree.prototype.createUniqueId = function() {
    this._currentId++;
    return this._currentId.toString();
};

Tree.prototype.createContextMenu = function(event) {
    var clickel = event.target;
    var item = clickel.tree_item;
    if (!item) {
        return;
    };
    if (!this.contextmenu) {
        var contextels = this.getContextMenuElements();
        this.renderContextMenu(item, contextels, event);
    };
    if (event.preventDefault) {
        event.preventDefault();
    } else {
        event.returnValue = false;
    };
    return false;
};

Tree.prototype.getContextMenuElements = function() {
    /* you will probably want to override this

        this should return a mapping (object) from context menu
        entry (string) to the name of a method on the Node object
        (also a string), the method will be called when the element
        is clicked
    */
    return {'open': 'open',
            'close': 'close'};
};

Tree.prototype.renderContextMenu = function(item, elements, event) {
    /* you may want to override this 
        
        item is the item clicked on
        elements should be a mapping from menu element (string) to 
        the name of a method located on the clicked Node
    
    */
    var menu = this.doc.createElement('div');
    menu.style.position = 'absolute';
    menu.className = 'contextmenu';
    addEventHandler(menu, 'click', this.removeContextMenu, this);
    for (var el in elements) {
        var div = this.doc.createElement('div');
        var text = this.doc.createTextNode(el);
        div.appendChild(text);
        addEventHandler(div, 'click', item[elements[el]], item);
        menu.appendChild(div);
    };
    this._positionMenu(event, menu);
    this.doc.getElementsByTagName('body')[0].appendChild(menu);
    this.contextmenu = menu;
};

Tree.prototype.removeContextMenu = function() {
    if (!this.contextmenu) {
        return;
    };
    this.contextmenu.parentNode.removeChild(this.contextmenu);
    delete this.contextmenu;
};

Tree.prototype.getCloseSign = function(item) {
    /* get the open sign for a collection or resource */
    var opensign = this.doc.createElement('img');
    if (item.isCollection()) {
        opensign.setAttribute('src', 'images/closed-collection.png');
    } else {
        opensign.setAttribute('src', 'images/non-collection.png');
    };
    return opensign;
};

Tree.prototype.getOpenSign = function(item) {
    /* get the close sign for a collection */
    var opensign = this.doc.createElement('img');
    opensign.setAttribute('src', 'images/opened-collection.png');
    return opensign;
};

Tree.prototype.getIcon = function(item) {
    /* return an img object that represents the file type */
    return this.doc.createTextNode('\xa0');
};

//Root.prototype.getPath = function() {
//    return '/' + this.id;  // FIXME
//};

Tree.prototype._positionMenu = function(event, menu) {
    var left = event.layerX;
    var top = event.layerY;
    menu.style.left = left + 'px';
    menu.style.top = top + 'px';
};

Tree.prototype.getItemByPath = function(path) {
    /* return an item by its path 
    
        path should be relative from the current item, excluding 
        the current item's id
    */
    // if the path starts with a slash, remove it
    if (path.charAt(0) == '/') {
        path = path.substr(1);
    };
    if (path == this.root.id) {
        return this.root;
    }
    if (path.indexOf(this.root.id) == 0) {
        path = path.substr(this.root.id.length+1);
        return this.root.getItemByPath(path);
    } else {
        return false;
    }
};

// Copied from helpers.js to not have a dependency
function ContextFixer(func, context) {
    /* Make sure 'this' inside a method points to its class */
    this.func = func;
    this.context = context;
    this.args = arguments;
    var self = this;
    
    this.execute = function() {
        /* execute the method */
        var args = new Array();
        // the first arguments will be the extra ones of the class
        for (var i=0; i < self.args.length - 2; i++) {
            args.push(self.args[i + 2]);
        };
        // the last are the ones passed on to the execute method
        for (var i=0; i < arguments.length; i++) {
            args.push(arguments[i]);
        };
        return self.func.apply(self.context, args);
    };
};

// this is used to make sure that all registered event handlers get a unique
// id, this id will be returned by addEventHandler and can be used to remove
// the event handler later on
// of course you *must never* change this id!
LAST_HANDLER_ID = 0;

// a mapping from id to handler (required because the handlers are wrapped
// before they're registered, and the mapped version is not available to the
// client, while it is required for de-registering the event later on
// needless to say this should never be touched...
ID_TO_HANDLER = {};

function addEventHandler(element, event, method, context) {
    /* method to add an event handler for both IE and Mozilla */
    var wrappedmethod = new ContextFixer(method, context);
    
    // store it, return the id, that can be used to de-register the
    // handler later on
    var id = LAST_HANDLER_ID++;
    ID_TO_HANDLER[id] = wrappedmethod;
    
    var args = new Array(null, null);
    for (var i=4; i < arguments.length; i++) {
        args.push(arguments[i]);
    };
    wrappedmethod.args = args;
    try {
        if (element.addEventListener) {
            element.addEventListener(event, wrappedmethod.execute, false);
        } else if (element.attachEvent) {
            element.attachEvent("on" + event, wrappedmethod.execute);
        } else {
            throw "Unsupported browser!";
        };
    } catch(e) {
        alert('exception ' + e.message + ' while registering an event handler for element ' + element + ', event ' + event + ', method ' + method);
    };

    return id;
};

function removeEventHandler(element, event, id) {
    /* method to remove an event handler for both IE and Mozilla */
    var method = ID_TO_HANDLER[id].execute;
    if (element.removeEventListener) {
        element.removeEventListener(event, method, false);
    } else if (element.detachEvent) {
        element.detachEvent("on" + event, method);
    } else {
        throw "Unsupported browser!";
    };
};

String.prototype.strip = function() {
    var stripspace = /^\s*([\s\S]*?)\s*$/;
    return stripspace.exec(this)[1];
};

