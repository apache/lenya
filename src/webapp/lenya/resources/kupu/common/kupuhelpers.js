/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupuhelpers.js 9585 2005-03-02 15:15:08Z guido $

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
    try {
        if (_SARISSA_IS_MOZ) {
            element.addEventListener(event, wrappedmethod.execute, false);
        } else if (_SARISSA_IS_IE) {
            element.attachEvent("on" + event, wrappedmethod.execute);
        } else {
            throw "Unsupported browser!";
        };
    } catch(e) {
        alert('exception ' + e.message + ' while registering an event handler for element ' + element + ', event ' + event + ', method ' + method);
    };
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

function ParentWithStyleChecker(tagnames, style, stylevalue) {
    /* small wrapper that provides a generic function to check if a
       button should look pressed in */
    return function(selNode, button, editor, event) {
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

function _load_dict_helper(element) {
    /* walks through a set of XML nodes and builds a nested tree of objects */
    var dict = {};
    for (var i=0; i < element.childNodes.length; i++) {
        var child = element.childNodes[i];
        if (child.nodeType == 1) {
            var value = '';
            for (var j=0; j < child.childNodes.length; j++) {
                // test if we can recurse, if so ditch the string (probably
                // ignorable whitespace) and dive into the node
                if (child.childNodes[j].nodeType == 1) {
                    value = _load_dict_helper(child);
                    break;
                } else if (typeof(value) == typeof('')) {
                    value += child.childNodes[j].nodeValue;
                };
            };
            if (typeof(value) == typeof('') && !isNaN(parseInt(value)) && 
                    parseInt(value).toString().length == value.length) {
                value = parseInt(value);
            } else if (typeof(value) != typeof('')) {
                if (value.length == 1) {
                    value = value[0];
                };
            };
            var name = child.nodeName.toLowerCase();
            if (dict[name] != undefined) {
                if (!dict[name].push) {
                    dict[name] = new Array(dict[name], value);
                } else {
                    dict[name].push(value);
                };
            } else {
                dict[name] = value;
            };
        };
    };
    return dict;
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
    dict = _load_dict_helper(root);
    return dict;
};

function NodeIterator(node, continueatnextsibling) {
    /* simple node iterator

        can be used to recursively walk through all children of a node,
        the next() method will return the next node until either the next
        sibling of the startnode is reached (when continueatnextsibling is 
        false, the default) or when there's no node left (when 
        continueatnextsibling is true)

        returns false if no nodes are left
    */
    
    this.node = node;
    this.current = node;
    this.terminator = continueatnextsibling ? null : node;
    
    this.next = function() {
        /* return the next node */
        if (this.current === false) {
            // restart
            this.current = this.node;
        };
        var current = this.current;
        if (current.firstChild) {
            this.current = current.firstChild;
        } else {
            // walk up parents until we finish or find one with a nextSibling
            while (current != this.terminator && !current.nextSibling) {
                current = current.parentNode;
            };
            if (current == this.terminator) {
                this.current = false;
            } else {
                this.current = current.nextSibling;
            };
        };
        return this.current;
    };

    this.reset = function() {
        /* reset the iterator so it starts at the first node */
        this.current = this.node;
    };

    this.setCurrent = function(node) {
        /* change the current node
            
            can be really useful for specific hacks, the user must take
            care that the node is inside the iterator's scope or it will
            go wild
        */
        this.current = node;
    };
};

/* selection classes, these are wrappers around the browser-specific
    selection objects to provide a generic abstraction layer
*/
function BaseSelection() {
    /* superclass for the Selection objects
    
        this will contain higher level methods that don't contain 
        browser-specific code
    */
    this.splitNodeAtSelection = function(node) {
        /* split the node at the current selection

            remove any selected text, then split the node on the location
            of the selection, thus creating a new node, this is attached to
            the node's parent after the node

            this will fail if the selection is not inside the node
        */
        if (!this.selectionInsideNode(node)) {
            throw('Selection not inside the node!');
        };
        // a bit sneaky: what we'll do is insert a new br node to replace
        // the current selection, then we'll walk up to that node in both
        // the original and the cloned node, in the original we'll remove
        // the br node and everything that's behind it, on the cloned one
        // we'll remove the br and everything before it
        // anyway, we'll end up with 2 nodes, the first already in the 
        // document (the original node) and the second we can just attach
        // to the doc after the first one
        var doc = this.document.getDocument();
        var br = doc.createElement('br');
        br.setAttribute('node_splitter', 'indeed');
        this.replaceWithNode(br);
        
        var clone = node.cloneNode(true);

        // now walk through the original node
        var iterator = new NodeIterator(node);
        var currnode = iterator.next();
        var remove = false;
        while (currnode) {
            if (currnode.nodeName.toLowerCase() == 'br' && currnode.getAttribute('node_splitter') == 'indeed') {
                // here's the point where we should start removing
                remove = true;
            };
            // we should fetch the next node before we remove the current one, else the iterator
            // will fail (since the current node is removed)
            var lastnode = currnode;
            currnode = iterator.next();
            // XXX this will leave nodes that *became* empty in place, since it doesn't visit it again,
            // perhaps we should do a second pass that removes the rest(?)
            if (remove && (lastnode.nodeType == 3 || !lastnode.hasChildNodes())) {
                lastnode.parentNode.removeChild(lastnode);
            };
        };

        // and through the clone
        var iterator = new NodeIterator(clone);
        var currnode = iterator.next();
        var remove = true;
        while (currnode) {
            var lastnode = currnode;
            currnode = iterator.next();
            if (lastnode.nodeName.toLowerCase() == 'br' && lastnode.getAttribute('node_splitter') == 'indeed') {
                // here's the point where we should stop removing
                lastnode.parentNode.removeChild(lastnode);
                remove = false;
            };
            if (remove && (lastnode.nodeType == 3 || !lastnode.hasChildNodes())) {
                lastnode.parentNode.removeChild(lastnode);
            };
        };

        // next we need to attach the node to the document
        if (node.nextSibling) {
            node.parentNode.insertBefore(clone, node.nextSibling);
        } else {
            node.parentNode.appendChild(clone);
        };

        // this will change the selection, so reselect
        this.reset();

        // return a reference to the clone
        return clone;
    };

    this.selectionInsideNode = function(node) {
        /* returns a Boolean to indicate if the selection is resided
            inside the node
        */
        var currnode = self.getSelectedNode();
        while (currnode) {
            if (currnode == node) {
                return true;
            };
            currnode = currnode.parentNode;
        };
        return false;
    };
};

function MozillaSelection(document) {
    this.document = document;
    this.selection = document.getWindow().getSelection();
    
    this.selectNodeContents = function(node) {
        /* select the contents of a node */
        this.selection.removeAllRanges();
        this.selection.selectAllChildren(node);
    };

    this.collapse = function(collapseToEnd) {
        try {
            if (!collapseToEnd) {
                this.selection.collapseToStart();
            } else {
                this.selection.collapseToEnd();
            };
        } catch(e) {};
    };

    this.replaceWithNode = function(node, selectAfterPlace) {
        // XXX this should be on a range object
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
                afterNode = this.document.getDocument().createTextNode(textAfter);

                // insert the 3 new nodes before the old one
                container.insertBefore(afterNode, textNode);
                container.insertBefore(node, afterNode);
                container.insertBefore(beforeNode, node);

                // remove the old node
                container.removeChild(textNode);
            } else {
                // else simply insert the node
                afterNode = container.childNodes[pos];
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
            if (selectAfterPlace.nodeType == 1) {
                this.selection.selectAllChildren(selectAfterPlace);
            } else {
                if (node.hasChildNodes()) {
                    this.selection.selectAllChildren(node);
                } else {
                    var range = this.selection.getRangeAt(0).cloneRange();
                    this.selection.removeAllRanges();
                    range.selectNode(node);
                    this.selection.addRange(range);
                };
            };
            this.document.getWindow().focus();
        };
        return node;
    };

    this.startOffset = function() {
        // XXX this should be on a range object
        var startnode = this.startNode();
        var startnodeoffset = 0;
        if (startnode == this.selection.anchorNode) {
            startnodeoffset = this.selection.anchorOffset;
        } else {
            startnodeoffset = this.selection.focusOffset;
        };
        var parentnode = this.parentElement();
        if (startnode == parentnode) {
            return startnodeoffset;
        };
        var currnode = parentnode.firstChild;
        var offset = 0;
        if (!currnode) {
            // 'Control range', range consists of a single element, so startOffset is 0
            if (startnodeoffset != 0) {
                // just an assertion to see if my assumption about this case is right
                throw('Start node offset detected in a node without children!');
            };
            return 0;
        };
        while (currnode != startnode) {
            if (currnode.nodeType == 3) {
                offset += currnode.nodeValue.length;
            };
            currnode = currnode.nextSibling;
        };
        return offset + startnodeoffset;
    };

    this.startNode = function() {
        // XXX this should be on a range object
        var anode = this.selection.anchorNode;
        var aoffset = this.selection.anchorOffset;
        var onode = this.selection.focusNode;
        var ooffset = this.selection.focusOffset;
        var arange = this.document.getDocument().createRange();
        arange.setStart(anode, aoffset);
        var orange = this.document.getDocument().createRange();
        orange.setStart(onode, ooffset);
        return arange.compareBoundaryPoints('START_TO_START', orange) <= 0 ? anode : onode;
    };

    this.endOffset = function() {
        // XXX this should be on a range object
        var endnode = this.endNode();
        var endnodeoffset = 0;
        if (endnode = this.selection.focusNode) {
            endnodeoffset = this.selection.focusOffset;
        } else {
            endnodeoffset = this.selection.anchorOffset;
        };
        var parentnode = this.parentElement();
        var currnode = parentnode.firstChild;
        var offset = 0;
        if (parentnode == endnode) {
            for (var i=0; i < parentnode.childNodes.length; i++) {
                var child = parentnode.childNodes[i];
                if (i == endnodeoffset) {
                    return offset;
                };
                if (child.nodeType == 3) {
                    offset += child.nodeValue.length;
                };
            };
        };
        if (!currnode) {
            // node doesn't have any content, so offset is always 0
            if (endnodeoffset != 0) {
                // just an assertion to see if my assumption about this case is right
                alert('End node offset detected in a node without children!');
                throw('End node offset detected in a node without children!');
            };
            return 0;
        };
        while (currnode != endnode) {
            if (currnode.nodeType == 3) { // should account for CDATA nodes as well
                offset += currnode.nodeValue.length;
            };
            currnode = currnode.nextSibling;
        };
        return offset + endnodeoffset;
    };

    this.endNode = function() {
        // XXX this should be on a range object
        var anode = this.selection.anchorNode;
        var aoffset = this.selection.anchorOffset;
        var onode = this.selection.focusNode;
        var ooffset = this.selection.focusOffset;
        var arange = this.document.getDocument().createRange();
        arange.setStart(anode, aoffset);
        var orange = this.document.getDocument().createRange();
        orange.setStart(onode, ooffset);
        return arange.compareBoundaryPoints('START_TO_START', orange) > 0 ? anode : onode;
    };

    this.getContentLength = function() {
        // XXX this should be on a range object
        return this.selection.toString().length;
    };

    this.cutChunk = function(startOffset, endOffset) {
        // XXX this should be on a range object
        var range = this.selection.getRangeAt(0);
        
        // set start point
        var offsetParent = this.parentElement();
        var currnode = offsetParent.firstChild;
        var curroffset = 0;

        var startparent = null;
        var startparentoffset = 0;
        
        while (currnode) {
            if (currnode.nodeType == 3) { // XXX need to add CDATA support
                var nodelength = currnode.nodeValue.length;
                if (curroffset + nodelength < startOffset) {
                    curroffset += nodelength;
                } else {
                    startparent = currnode;
                    startparentoffset = startOffset - curroffset;
                    break;
                };
            };
            currnode = currnode.nextSibling;
        };
        // set end point
        var currnode = offsetParent.firstChild;
        var curroffset = 0;

        var endparent = null;
        var endoffset = 0;
        
        while (currnode) {
            if (currnode.nodeType == 3) { // XXX need to add CDATA support
                var nodelength = currnode.nodeValue.length;
                if (curroffset + nodelength < endOffset) {
                    curroffset += nodelength;
                } else {
                    endparent = currnode;
                    endparentoffset = endOffset - curroffset;
                    break;
                };
            };
            currnode = currnode.nextSibling;
        };
        
        // now cut the chunk
        if (!startparent) {
            throw('Start offset out of range!');
        };
        if (!endparent) {
            throw('End offset out of range!');
        };

        var newrange = range.cloneRange();
        newrange.setStart(startparent, startparentoffset);
        newrange.setEnd(endparent, endparentoffset);
        return newrange.extractContents();
    };

    this.getElementLength = function(element) {
        // XXX this should be a helper function
        var length = 0;
        var currnode = element.firstChild;
        while (currnode) {
            if (currnode.nodeType == 3) { // XXX should support CDATA as well
                length += currnode.nodeValue.length;
            };
            currnode = currnode.nextSibling;
        };
        return length;
    };

    this.parentElement = function() {
        /* return the selected node (or the node containing the selection) */
        // XXX this should be on a range object
        if (this.selection.rangeCount == 0) {
            var parent = this.document.getDocument().body;
            while (parent.firstChild) {
                parent = parent.firstChild;
            };
        } else {
            var range = this.selection.getRangeAt(0);
            var parent = range.commonAncestorContainer;

            // the following deals with cases where only a single child is
            // selected, e.g. after a click on an image
            var inv = range.compareBoundaryPoints(Range.START_TO_END, range) < 0;
            var startNode = inv ? range.endContainer : range.startContainer;
            var startOffset = inv ? range.endOffset : range.startOffset;
            var endNode = inv ? range.startContainer : range.endContainer;
            var endOffset = inv ? range.startOffset : range.endOffset;

            var selectedChild = null;
            var child = parent.firstChild;
            while (child) {
                // XXX the additional conditions catch some invisible
                // intersections, but still not all of them
                if (range.intersectsNode(child) &&
                    !(child == startNode && startOffset == child.length) &&
                    !(child == endNode && endOffset == 0)) {
                    if (selectedChild) {
                        // current child is the second selected child found
                        selectedChild = null;
                        break;
                    } else {
                        // current child is the first selected child found
                        selectedChild = child;
                    };
                } else if (selectedChild) {
                    // current child is after the selection
                    break;
                };
                child = child.nextSibling;
            };
            if (selectedChild) {
                parent = selectedChild;
            };
        };
        if (parent.nodeType == Node.TEXT_NODE) {
            parent = parent.parentNode;
        };
        return parent;
    };

    // deprecated alias of parentElement
    this.getSelectedNode = this.parentElement;

    this.moveStart = function(offset) {
        // XXX this should be on a range object
        var offsetparent = this.parentElement();
        // the offset within the offsetparent
        var startoffset = this.startOffset();
        var realoffset = offset + startoffset;
        if (realoffset >= 0) {
            var currnode = offsetparent.firstChild;
            var curroffset = 0;
            var startparent = null;
            var startoffset = 0;
            while (currnode) {
                if (currnode.nodeType == 3) { // XXX need to support CDATA sections
                    var nodelength = currnode.nodeValue.length;
                    if (curroffset + nodelength >= realoffset) {
                        var range = this.selection.getRangeAt(0);
                        //range.setEnd(this.endNode(), this.endOffset());
                        range.setStart(currnode, realoffset - curroffset);
                        return;
                        //this.selection.removeAllRanges();
                        //this.selection.addRange(range);
                    };
                };
                currnode = currnode.nextSibling;
            };
            // if we still haven't found the startparent we should walk to 
            // all nodes following offsetparent as well
            var currnode = offsetparent.nextSibling;
            while (currnode) {
                if (currnode.nodeType == 3) {
                    var nodelength = currnode.nodeValue.length;
                    if (curroffset + nodelength >= realoffset) {
                        var range = this.selection.getRangeAt(0);
                        // XXX does IE switch the begin and end nodes here as well?
                        var endnode = this.endNode();
                        var endoffset = this.endOffset();
                        range.setEnd(currnode, realoffset - curroffset);
                        range.setStart(endnode, endoffset);
                        return;
                    };
                    curroffset += nodelength;
                };
                currnode = currnode.nextSibling;
            };
            throw('Offset out of document range');
        } else if (realoffset < 0) {
            var currnode = offsetparent.prevSibling;
            var curroffset = 0;
            while (currnode) {
                if (currnode.nodeType == 3) { // XXX need to support CDATA sections
                    var currlength = currnode.nodeValue.length;
                    if (curroffset - currlength < realoffset) {
                        var range = this.selection.getRangeAt(0);
                        range.setStart(currnode, realoffset - curroffset);
                    };
                    curroffset -= currlength;
                };
                currnode = currnode.prevSibling;
            };
        } else {
            var range = this.selection.getRangeAt(0);
            range.setStart(offsetparent, 0);
            //this.selection.removeAllRanges();
            //this.selection.addRange(range);
        };
    };

    this.moveEnd = function(offset) {
        // XXX this should be on a range object
    };

    this.reset = function() {
        this.selection = this.document.getWindow().getSelection();
    };

    this.cloneContents = function() {
        /* returns a document fragment with a copy of the contents */
        var range = this.selection.getRangeAt(0);
        return range.cloneContents();
    };

    this.containsNode = function(node) {
        return this.selection.containsNode(node, true);
    }

    this.toString = function() {
        return this.selection.toString();
    };
};

MozillaSelection.prototype = new BaseSelection;

function IESelection(document) {
    this.document = document;
    this.selection = document.getDocument().selection;

    /* If no selection in editable document, IE returns selection from
     * main page, so force an inner selection. */
    var doc = document.getDocument();

    var range = this.selection.createRange()
    var parent = this.selection.type=="Text" ?
        range.parentElement() :
        this.selection.type=="Control" ?  range.parentElement : null;

    if(parent && parent.ownerDocument != doc) {
            var range = doc.body.createTextRange();
            range.collapse();
            range.select();
    }

    this.selectNodeContents = function(node) {
        /* select the contents of a node */
        // a bit nasty, when moveToElementText is called it will move the selection start
        // to just before the element instead of inside it, and since IE doesn't reserve
        // an index for the element itself as well the way to get it inside the element is
        // by moving the start one pos and then moving it back (yuck!)
        var range = this.selection.createRange().duplicate();
        range.moveToElementText(node);
        range.moveStart('character', 1);
        range.moveStart('character', -1);
        range.moveEnd('character', -1);
        range.moveEnd('character', 1);
        range.select();
        this.selection = this.document.getDocument().selection;
    };

    this.collapse = function(collapseToEnd) {
        var range = this.selection.createRange();
        range.collapse(!collapseToEnd);
        range.select();
        this.selection = document.getDocument().selection;
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
            if (parent.tagName=='IMG') parent=parent.parentElement;

            var elrange = selrange.duplicate();
            elrange.moveToElementText(parent);
            
            // now find the start parent and offset
            var startoffset = this.startOffset();
            var endoffset = this.endOffset();
            
            // copy parent to contain new nodes, don't copy its children (false arg)
            var newparent = this.document.getDocument().createElement('span');
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
                // using htmlText here should fix markup problems (opening tags without closing ones etc.)
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
                if (selectAfterPlace.nodeType == 1) {
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

    this.startOffset = function() {
        var startoffset = 0;
        var selrange = this.selection.createRange();
        var parent = selrange.parentElement();
        var elrange = selrange.duplicate();
        elrange.moveToElementText(parent);
        var tempstart = selrange.duplicate();
        while (elrange.compareEndPoints('StartToStart', tempstart) < 0) {
            startoffset++;
            tempstart.moveStart('character', -1);
        };

        return startoffset;
    };

    this.endOffset = function() {
        var endoffset = 0;
        var selrange = this.selection.createRange();
        var parent = selrange.parentElement();
        var elrange = selrange.duplicate();
        elrange.moveToElementText(parent);
        var tempend = selrange.duplicate();
        while (elrange.compareEndPoints('EndToEnd', tempend) > 0) {
            endoffset++;
            tempend.moveEnd('character', 1);
        };

        return endoffset;
    };

    this.getContentLength = function() {
        var contentlength = 0;
        var range = this.selection.createRange().duplicate();
        var startpoint = range.duplicate();
        startpoint.collapse();
        var endpoint = range.duplicate();
        endpoint.collapse(false);
        while (!startpoint.isEqual(endpoint)) {
            startpoint.moveEnd('character', 1);
            startpoint.moveStart('character', 1);
            contentlength++;
        };
        return contentlength;
    };

    this.cutChunk = function(startOffset, endOffset) {
        /* cut a chunk of HTML from the selection

            this *should* return the chunk of HTML but doesn't yet
        */
        var range = this.selection.createRange().duplicate();
        range.moveStart('character', startOffset);
        range.moveEnd('character', -endOffset);
        range.pasteHTML('');
        // XXX here it should return the chunk
    };

    this.getElementLength = function(element) {
        /* returns the length of an element *including* 1 char for each child element

            this is defined on the selection since it returns results that can be used
            to work with selection offsets
        */
        var length = 0;
        var range = this.selection.createRange().duplicate();
        range.moveToElementText(element);
        range.moveStart('character', 1);
        range.moveEnd('character', -1);
        var endpoint = range.duplicate();
        endpoint.collapse(false);
        range.collapse();
        while (!range.isEqual(endpoint)) {
            range.moveEnd('character', 1);
            range.moveStart('character', 1);
            length++;
        };
        return length;
    };

    this.parentElement = function() {
        /* return the selected node (or the node containing the selection) */
        // XXX this should be on a range object
        if (this.selection.type == 'Control') {
            return this.selection.createRange().item(0);
        } else {
            return this.selection.createRange().parentElement();
        };
    };

    // deprecated alias of parentElement
    this.getSelectedNode = this.parentElement;

    this.moveStart = function(offset) {
        /* move the start of the selection */
        var range = this.selection.createRange();
        range.moveStart('character', offset);
        range.select();
    };

    this.moveEnd = function(offset) {
        /* moves the end of the selection */
        var range = this.selection.createRange();
        range.moveEnd('character', offset);
        range.select();
    };

    this.reset = function() {
       this.selection = this.document.getDocument().selection;
    };

    this.cloneContents = function() {
        /* returns a document fragment with a copy of the contents */
        var contents = this.selection.createRange().htmlText;
        var doc = this.document.getDocument();
        var docfrag = doc.createElement('span');
        docfrag.innerHTML = contents;
        return docfrag;
    };

    this.containsNode = function(node) {
        var selected = this.selection.createRange();
        
        if (this.selection.type.toLowerCase()=='text') {
            var range = doc.body.createTextRange();
            range.moveToElementText(node);

            if (selected.compareEndPoints('StartToEnd', range) >= 0 ||
                selected.compareEndPoints('EndToStart', range) <= 0) {
                return false;
            }
            return true;
        } else {
            for (var i = 0; i < selected.length; i++) {
                if (selected.item(i).contains(node)) {
                    return true;
                }
            }
            return false;
        }
    };
    
    this.toString = function() {
        return this.selection.createRange().htmlText;
    };
};

IESelection.prototype = new BaseSelection;

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
Array.prototype.contains = function(element, objectequality) {
    /* see if some value is in this */
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
};

// JavaScript has a friggin' blink() function, but not for string stripping...
String.prototype.strip = function() {
    var stripspace = /^\s*([\s\S]*?)\s*$/;
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
