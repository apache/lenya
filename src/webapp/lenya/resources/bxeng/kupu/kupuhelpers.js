/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupuhelpers.js 778 2004-08-31 15:32:22Z silvan $

/*

Some notes about the scripts:

- Problem with bound event handlers:
    
    When a method on an object is used as an event handler, the method uses 
    its reference to the object it is defined on. The 'this' keyword no longer
    points to the class, but instead refers to the element on which the event
    is bound. To overcome this problem, you can wrap the method in a class that
    holds a reference to the object and have a method on the wrapper that calls
    the input method in the input object's context. This wrapped method can be
    used as the event handler. An example:

    class Foo() {
        this.foo = function() {
            // the method used as an event handler
            // using this here wouldn't work if the method
            // was passed to addEventListener directly
            this.baz();
        };
        this.baz = function() {
            // some method on the same object
        };
    };

    f = new Foo();

    // create the wrapper for the function, args are func, context
    wrapper = new ContextFixer(f.foo, f);

    // the wrapper can be passed to addEventListener, 'this' in the method
    // will be pointing to the right context.
    some_element.addEventListener("click", wrapper.execute, false);

- Problem with window.setTimeout:

    The window.setTimeout function has a couple of problems in usage, all 
    caused by the fact that it expects a *string* argument that will be
    evalled in the global namespace rather than a function reference with
    plain variables as arguments. This makes that the methods on 'this' can
    not be called (the 'this' variable doesn't exist in the global namespace)
    and references to variables in the argument list aren't allowed (since
    they don't exist in the global namespace). To overcome these problems, 
    there's now a singleton instance of a class called Timer, which has one 
    public method called registerFunction. This can be called with a function
    reference and a variable number of extra arguments to pass on to the 
    function.

    Usage:

        timer_instance.registerFunction(this, this.myFunc, 10, 'foo', bar);

        will call this.myFunc('foo', bar); in 10 milliseconds (with 'this'
        as its context).

*/

//----------------------------------------------------------------------------
// Helper classes and functions
//----------------------------------------------------------------------------

function addEventHandler(element, event, method, context) {
    /* method to add an event handler for both IE and Mozilla */
    var wrappedmethod = new ContextFixer(method, context);
    if (_SARISSA_IS_MOZ) {
        element.addEventListener(event, wrappedmethod.execute, false);
    } else if (_SARISSA_IS_IE) {
        element.attachEvent("on" + event, wrappedmethod.execute);
    } else {
        throw "Unsupported browser!";
    }
};

function removeEventHandler(element, event, method) {
    /* method to remove an event handler for both IE and Mozilla */
    if (_SARISSA_IS_MOZ) {
        window.removeEventListener('focus', method, false);
    } else if (_SARISSA_IS_IE) {
        element.detachEvent("on" + event, method);
    } else {
        throw "Unsupported browser!";
    };
};

function openPopup(url, width, height) {
    /* open and center a popup window */
    var sw = screen.width;
    var sh = screen.height;
    var left = sw / 2 - width / 2;
    var top = sh / 2 - height / 2;
    var win = window.open(url, 'someWindow', 
                'width=' + width + ',height=' + height + ',left=' + left + ',top=' + top);
    return win;
};

function selectSelectItem(select, item) {
    /* select a certain item from a select */
    for (var i=0; i < select.options.length; i++) {
        var option = select.options[i];
        if (option.value == item) {
            select.selectedIndex = i;
            return;
        }
    }
    select.selectedIndex = 0;
};

function StateButtonCheckFunction(tagnames, style, stylevalue) {
    /* small wrapper that provides a generic function to check if a button should look pressed in */
    this.execute = function(selNode, button, editor, event) {
        /* check if the button needs to look pressed in */
        var currnode = selNode;
        if (!currnode) {
            return;
        };
        if (currnode.nodeType == 3) {
            currnode = currnode.parentNode;
        };
        while (currnode && currnode.style) {
            for (var i=0; i < tagnames.length; i++) {
                if (currnode.nodeName.toLowerCase() == tagnames[i].toLowerCase()) {
                    return true;
                };
            };
            if (tagnames.contains(currnode.nodeName.toLowerCase()) && 
                    (style && currnode.style[style] == stylevalue)) {
                return true;
            };
            currnode = currnode.parentNode;
        };
        return false;
    };
};

function loadDictFromXML(document, islandid) {
    /* load configuration values from an XML chunk

        this is quite generic, it just reads data from a chunk of XML into
        an object, checking if the object is complete should be done in the
        calling context.
    */
    var dict = {};
    var confnode = document.getElementById(islandid);
    var root = null;
    for (var i=0; i < confnode.childNodes.length; i++) {
        if (confnode.childNodes[i].nodeType == 1) {
            root = confnode.childNodes[i];
            break;
        };
    };
    if (!root) {
        throw('No element found in the config island!');
    };
    for (var i=0; i < root.childNodes.length; i++) {
        var child = root.childNodes[i];
        if (child.nodeType == 1) {
            var value = '';
            for (var j=0; j < child.childNodes.length; j++) {
                value += child.childNodes[j].nodeValue;
            };
            if (!isNaN(parseInt(value)) && parseInt(value).toString().length == value.length) {
                value = parseInt(value);
            };
            dict[child.nodeName.toLowerCase()] = value;
        };
    };
    return dict;
};

/* selection classes, these are wrappers around the browser-specific
    selection objects to provide a generic abstraction layer
*/
function MozillaSelection(document) {
    this.document = document;
    this.selection = document.getWindow().getSelection();
    
    this.selectNodeContents = function(node) {
        /* select the contents of a node */
        this.selection.removeAllRanges();
        this.selection.selectAllChildren(node);
    };

    this.getSelectedNode = function() {
        /* return the selected node (or the node containing the selection) */
        var selectedNode = this.selection.anchorNode;
        if (this.selection.rangeCount == 0 || selectedNode.childNodes.length == 0) {
            return selectedNode;
        };
        for (var i=0; i < selectedNode.childNodes.length; i++) {
            var child = selectedNode.childNodes[i];
            if (this.selection.containsNode(child, true)) {
                selectedNode = child;
            };
        };

        return selectedNode;
    };

    this.collapse = function(collapseToEnd) {
        if (!collapseToEnd) {
            this.selection.collapseToStart();
        } else {
            this.selection.collapseToEnd();
        };
    };

    this.replaceWithNode = function(node, selectAfterPlace) {
        /* replaces the current selection with a new node
            returns a reference to the inserted node 

            newnode is the node to replace the content with, selectAfterPlace
            can either be a DOM node that should be selected after the new
            node was placed, or some value that resolves to true to select
            the placed node
        */
        // get the first range of the selection
        // (there's almost always only one range)
        var range = this.selection.getRangeAt(0);

        // deselect everything
        this.selection.removeAllRanges();

        // remove content of current selection from document
        range.deleteContents();

        // get location of current selection
        var container = range.startContainer;
        var pos = range.startOffset;

        // make a new range for the new selection
        var range = this.document.getDocument().createRange();

        if (container.nodeType == 3 && node.nodeType == 3) {
            // if we insert text in a textnode, do optimized insertion
            container.insertData(pos, node.nodeValue);

            // put cursor after inserted text
            range.setEnd(container, pos + node.length);
            range.setStart(container, pos + node.length);
        } else {
            var afterNode;
            if (container.nodeType == 3) {
                // when inserting into a textnode
                // we create 2 new textnodes
                // and put the node in between

                var textNode = container;
                var container = textNode.parentNode;
                var text = textNode.nodeValue;

                // text before the split
                var textBefore = text.substr(0,pos);
                // text after the split
                var textAfter = text.substr(pos);

                var beforeNode = this.document.getDocument().createTextNode(textBefore);
                var afterNode = this.document.getDocument().createTextNode(textAfter);

                // insert the 3 new nodes before the old one
                container.insertBefore(afterNode, textNode);
                container.insertBefore(node, afterNode);
                container.insertBefore(beforeNode, node);

                // remove the old node
                container.removeChild(textNode);
            } else {
                // else simply insert the node
                var afterNode = container.childNodes[pos];
                if (afterNode) {
                    container.insertBefore(node, afterNode);
                } else {
                    container.appendChild(node);
                };
            }

            range.setEnd(afterNode, 0);
            range.setStart(afterNode, 0);
        }

        if (selectAfterPlace) {
            // a bit implicit here, but I needed this to be backward 
            // compatible and also I didn't want yet another argument,
            // JavaScript isn't as nice as Python in that respect (kwargs)
            // if selectAfterPlace is a DOM node, select all of that node's
            // contents, else select the newly added node's
            this.selection = this.document.getWindow().getSelection();
            this.selection.addRange(range);
            if (selectAfterPlace.nodeType) {
                this.selection.selectAllChildren(selectAfterPlace);
            } else {
                this.selection.selectAllChildren(node);
            };
            this.document.getWindow().focus();
        };
        return node;
    };
};

function IESelection(document) {
    this.document = document;
    this.selection = document.getDocument().selection;
    
    this.selectNodeContents = function(node) {
        /* select the contents of a node */
        var range = this.selection.createRange().duplicate();
        range.moveToElementText(node);
        range.select();
    };

    this.getSelectedNode = function() {
        /* return the selected node (or the node containing the selection) */
        range = null;
        switch (this.selection.type) {
            case "None":
            case "Text":
                range = this.selection.createRange();
                selectedNode = range.parentElement();
                break;
            case "Control":
                // return img itself instead of its parent
                selectedNode = this.selection.createRange().item(0);
                break;
        };
        return selectedNode;
    };

    this.collapse = function(collapseToEnd) {
        var range = this.selection.createRange();
        range.collapse(!collapseToEnd);
        range.select();
    };

    this.replaceWithNode = function(newnode, selectAfterPlace) {
        /* replaces the current selection with a new node
            returns a reference to the inserted node 

            newnode is the node to replace the content with, selectAfterPlace
            can either be a DOM node that should be selected after the new
            node was placed, or some value that resolves to true to select
            the placed node
        */
        // XXX one big hack!!
        
        // XXX this method hasn't been optimized *at all* but can probably 
        // be made a hell of a lot faster, however for now it's complicated
        // enough the way it is and I want to have it stable first
        
        if (this.selection.type == 'Control') {
            var range = this.selection.createRange();
            range.item(0).parentNode.replaceChild(newnode, range.item(0));
            for (var i=1; i < range.length; i++) {
                range.item(i).parentNode.removeChild(range[i]);
            };
            if (selectAfterPlace) {
                var range = this.document.getDocument().body.createTextRange();
                range.moveToElementText(newnode);
                range.select();
            };
        } else {
            var selrange = this.selection.createRange();
            var startpoint = selrange.duplicate();
            startpoint.collapse();
            var endpoint = selrange.duplicate();
            endpoint.collapse(false);
            var parent = selrange.parentElement();
            
            // now find the start parent and offset
            var startnode = parent;
            var startoffset = 0;
            var endnode = parent;
            var endoffset = 0;
            // first see if the endpoint and startpoint can be found directly in the parent
            var elrange = selrange.duplicate();
            elrange.moveToElementText(parent);
            var tempstart = startpoint.duplicate();
            while (elrange.compareEndPoints('StartToStart', tempstart) < 0) {
                startoffset++;
                tempstart.moveStart('character', -1);
            };
            var tempend = endpoint.duplicate();
            while (elrange.compareEndPoints('EndToEnd', tempend) > 0) {
                endoffset++;
                tempend.moveEnd('character', 1);
            };

            // copy parent to contain new nodes, don't copy its children (false arg)
            var newparent = this.document.getDocument().createElement('span'); // parent.cloneNode(false);
            // also make a temp node to copy some temp nodes into later
            var tempparent = newparent.cloneNode(false);

            // this is awful, it is a hybrid DOM/copy'n'paste solution
            // first it gets the chunk of data before the selection and
            // pastes that (as a string) into the new parent, then it appendChilds
            // the new node and then it pastes the stuff behind the selection 
            // to a temp node (there's no string paste method to append) and
            // copies the contents of that to the new node using appendChild...

            // first the first bit, straightforward string pasting
            var temprange = elrange.duplicate();
            temprange.moveToElementText(parent);
            temprange.collapse();
            temprange.moveEnd('character', startoffset);
            if (temprange.isEqual(elrange)) {
                // cursor was on the last position in the parent
                while (parent.hasChildNodes()) {
                    newparent.appendChild(parent.firstChild);
                };
            } else {
                newparent.insertAdjacentHTML('afterBegin', temprange.htmlText);
            };

            // now some straightforward appendChilding the new node
            newparent.appendChild(newnode);
            
            // getting the rest of the elements behind the selection can only be 
            // done using htmlText (afaik) so we end up with a string, which we
            // can not just use to attach to the new node (innerHTML would 
            // overwrite the content) so we use set it as the innerHTML of the 
            // temp node and after that's done appendChild all the child elements
            // of the temp node to the new parent
            temprange.moveToElementText(parent);
            temprange.collapse(false);
            temprange.moveStart('character', -endoffset);
            if (temprange.isEqual(elrange)) {
                // cursor was on position 0 of the parent
                while (parent.hasChildNodes()) {
                    tempparent.appendChild(parent.firstChild);
                };
            } else if (endoffset > 0) {
                tempparent.insertAdjacentHTML('afterBegin', temprange.htmlText);
            };
            while (tempparent.hasChildNodes()) {
                newparent.appendChild(tempparent.firstChild);
            };

            // so now we have the result in newparent, replace the old parent in 
            // the document and we're done
            //parent.parentNode.replaceChild(newparent, parent);
            while (parent.hasChildNodes()) {
                parent.removeChild(parent.firstChild);
            };
            while (newparent.hasChildNodes()) {
                var child = newparent.firstChild;
                parent.appendChild(newparent.firstChild);
            };

            if (selectAfterPlace) {
                // see MozillaSelection.replaceWithNode() for some comments about
                // selectAfterPlace
                var temprange = this.document.getDocument().body.createTextRange();
                if (selectAfterPlace.nodeType) {
                    temprange.moveToElementText(selectAfterPlace);
                } else {
                    temprange.moveToElementText(newnode);
                };
                //temprange.moveEnd('character', -1);
                temprange.select();
            };
        };

        this.selection = this.document.getDocument().selection;

        return newnode;
    };

    // XXX this isn't used, is it? if not, we should remove it...
    this._getTextLength = function(node) {
        /* recursively walks through a node to get the total length of all
            contained text nodes
        */
        var length = 0;
        for (var i=0; i < node.childNodes.length; i++) {
            var child = node.childNodes[i];
            if (child.nodeType == 3) {
                length += child.nodeValue.length;
            } else {
                length += this._getTextLength(child);
            };
        };
        return length;
    };
};

/* ContextFixer, fixes a problem with the prototype based model

    When a method is called in certain particular ways, for instance
    when it is used as an event handler, the context for the method
    is changed, so 'this' inside the method doesn't refer to the object
    on which the method is defined (or to which it is attached), but for
    instance to the element on which the method was bound to as an event
    handler. This class can be used to wrap such a method, the wrapper 
    has one method that can be used as the event handler instead. The
    constructor expects at least 2 arguments, first is a reference to the
    method, second the context (a reference to the object) and optionally
    it can cope with extra arguments, they will be passed to the method
    as arguments when it is called (which is a nice bonus of using 
    this wrapper).
*/

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
        self.func.apply(self.context, args);
    };
};

/* Alternative implementation of window.setTimeout

    This is a singleton class, the name of the single instance of the
    object is 'timer_instance', which has one public method called
    registerFunction. This method takes at least 2 arguments: a
    reference to the function (or method) to be called and the timeout.
    Arguments to the function are optional arguments to the 
    registerFunction method. Example:

    timer_instance.registerMethod(foo, 100, 'bar', 'baz');

    will call the function 'foo' with the arguments 'bar' and 'baz' with
    a timeout of 100 milliseconds.

    Since the method doesn't expect a string but a reference to a function
    and since it can handle arguments that are resolved within the current
    namespace rather then in the global namespace, the method can be used
    to call methods on objects from within the object (so this.foo calls
    this.foo instead of failing to find this inside the global namespace)
    and since the arguments aren't strings which are resolved in the global
    namespace the arguments work as expected even inside objects.

*/

function Timer() {
    /* class that has a method to replace window.setTimeout */
    this.lastid = 0;
    this.functions = {};
    
    this.registerFunction = function(object, func, timeout) {
        /* register a function to be called with a timeout

            args: 
                func - the function
                timeout - timeout in millisecs
                
            all other args will be passed 1:1 to the function when called
        */
        var args = new Array();
        for (var i=0; i < arguments.length - 3; i++) {
            args.push(arguments[i + 3]);
        }
        var id = this._createUniqueId();
        this.functions[id] = new Array(object, func, args);
        setTimeout("timer_instance._handleFunction(" + id + ")", timeout);
    };

    this._handleFunction = function(id) {
        /* private method that does the actual function call */
        var obj = this.functions[id][0];
        var func = this.functions[id][1];
        var args = this.functions[id][2];
        this.functions[id] = null;
        func.apply(obj, args);
    };

    this._createUniqueId = function() {
        /* create a unique id to store the function by */
        while (this.lastid in this.functions && this.functions[this.lastid]) {
            this.lastid++;
            if (this.lastid > 100000) {
                this.lastid = 0;
            }
        }
        return this.lastid;
    };
};

// create a timer instance in the global namespace, obviously this does some
// polluting but I guess it's impossible to avoid...

// OBVIOUSLY THIS VARIABLE SHOULD NEVER BE OVERWRITTEN!!!
timer_instance = new Timer();

// helper function on the Array object to test for containment
/*Array.prototype.contains = function(element, objectequality) {
  
    for (var i=0; i < this.length; i++) {
        if (objectequality) {
            if (element === this[i]) {
                return true;
            };
        } else {
            if (element == this[i]) {
                return true;
            };
        };
    };
    return false;
};*/

// JavaScript has a friggin' blink() function, but not for string stripping...
String.prototype.strip = function() {
    var stripspace = /^\s*((\S+\s*\S+)*)\s*$/;
    return stripspace.exec(this)[1];
};

//----------------------------------------------------------------------------
// Exceptions
//----------------------------------------------------------------------------

// XXX don't know if this is the regular way to define exceptions in JavaScript?
function Exception() {
    return;
};

// throw this as an exception inside an updateState handler to restart the
// update, may be required in situations where updateState changes the structure
// of the document (e.g. does a cleanup or so)
UpdateStateCancelBubble = new Exception();
