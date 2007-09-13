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
// $Id: bxeXMLNode.js 1411 2005-10-06 18:52:18Z chregu $

function bxe_XMLNodeInit (nodein, localName, nodeType, autocreate) {
	if (nodein.nodeType == 1 || typeof nodein == "string") {
		return new XMLNodeElement(nodein, localName, nodeType, autocreate);
	} else {
		return new XMLNode(nodein, localName, nodeType, autocreate);
	}
}


function XMLNode  ( nodein, localName, nodeType, autocreate) {
	this.objectType ="XMLNode";
	this.init( nodein, localName, nodeType, autocreate);
}

XMLNode.prototype.copy  = function () {
	

	var cssr = document.createRange();
	cssr.selectNode(this._node);
	// data to save - render as text (temporary thing - move to html later)
	var clipboard = mozilla.getClipboard();

	// clipboard.setData(deletedFragment.saveXML(), "text/html"); // go back to this once, paste supports html paste!
	clipboard.setData(cssr,MozClipboard.TEXT_FLAVOR);
}

XMLNode.prototype.init = function ( nodein, localName, nodeType, autocreate) {
	if (typeof nodein != "undefined" && typeof nodein != "string") {
		this.nodeType = nodein.nodeType;
		this.localName = nodein.localName;
		this.namespaceURI = nodein.namespaceURI;
		this._node = nodein;
	} else {
		this.localName = localName;
		this.namespaceURI = nodein;
	}
	this.prefix = null;
	this.firstChild = null;
	this.lastChild = null;
	this.nextSibling = null;
	this.previousSibling = null;
	this.xmlBridge = false;
	
	if (nodeType && ! this.nodeType) {
		this.nodeType = nodeType;
	}
	if (autocreate) {
		this.createNS(nodein, localName);
	}
	if (this._node && this._node.ownerDocument == document) {
		if (this._node.nodeType == 1) {
			if (this._node.nodeName.toLowerCase() != "span" || this._node.getAttribute("__bxe_keep_span")  ) {
				if  (this.namespaceURI == XHTMLNS ) {
					this.localName = this._node.nodeName.toLowerCase();
				} 
			} else {
				var classe = this._node.getClass();
				if (classe) {
					this.localName = classe;
				} else {
					this.localName = this._node.localName;
				}
			}
			if (this._node.hasAttribute("__bxe_ns")) {
				this.namespaceURI = this._node.getAttribute("__bxe_ns");
			}
			attribs = this._node.attributes;
			for (var i = 0; i < attribs.length; i++) {
				dump(attribs[i].localName + "\n");
				this.setAttributeNS(attribs[i].namespaceURI,attribs[i].localName,attribs[i].value);
			}
			
		} else if (this._node.nodeType == 3){
			this.localName = "#text";
			this.nodeName = "#text";
		} else {
			this.localName = this._node.nodeName;
		}
	} 
	if (this._node) {
		this._node.XMLNode = this;
	}
	
}

XMLNode.prototype.insertAfter = function(newNode, oldNode) {

	this.insertBefore(newNode,oldNode.nextSibling);
}

XMLNode.prototype.insertBefore = function(newNode,oldNode) {
	var oldHtmlNode = newNode._node;
	var newNode = this.appendChild(newNode);
	if (oldHtmlNode) {
		oldHtmlNode.parentNode.removeChild(oldHtmlNode);
	}
	if (oldNode) {
		
		newNode._node = this._node.insertBefore(newNode._node,oldNode._node);
	} else {
		newNode._node=this._node.insertBefore(newNode._node,null);
	}
	
	this.insertBeforeIntern(newNode,oldNode);
	
	newNode._node.XMLNode = newNode;

}

XMLNode.prototype.insertBeforeIntern = function(newNode, oldNode) {
	try {
	newNode.unlink();
	newNode.parentNode = this;
	newNode.ownerDocument = this.ownerDocument;
	if (oldNode != null) {
		if (oldNode.previousSibling != null) {
			oldNode.previousSibling.nextSibling = newNode;
			newNode.previousSibling = oldNode.previousSibling;
		} else {
			this.firstChild = newNode;
			newNode.previousSibling = null;
		}
		oldNode.previousSibling = newNode;
		newNode.nextSibling = oldNode;
	} else  {
		if (this.lastChild) { 
			newNode.previousSibling = this.lastChild;
			this.lastChild.nextSibling = newNode;
		} else {
			this.firstChild = newNode;
			newNode.previousSibling = null;
		}
		newNode.nextSibling = null;
		this.lastChild = newNode;
		
	}
	
	} catch(e) {alert(e);}
}

XMLNode.prototype.unlink = function () {
	
	if (this.nextSibling == null) {
		if ( this.parentNode != null) {
			this.parentNode.lastChild = this.previousSibling;
		}
	} else {
		this.nextSibling.previousSibling = this.previousSibling;
	}
	if (this.previousSibling == null) {
		if (this.parentNode != null) {
			this.parentNode.firstChild = this.nextSibling;
		}
	} else {
		this.previousSibling.nextSibling = this.nextSibling;
	}
	this.parentNode = null;
}

XMLNode.prototype.unlinkChildren = function () {
	
	var child = this.firstChild;
	
	while (child) {
		child.parentNode = null;
		child = child.nextSibling;
	}
	this.firstChild = null;
	this.lastChild = null;
}


XMLNode.prototype.appendChild = function(newNode) {
	if (newNode._node) {
		var child = newNode._node.firstChild;
		while (child) {
			var nextchild = child.nextSibling;
			if ( child.nodeType == 1 && child.getAttribute("_edom_internal_node")) {
				child.parentNode.removeChild(child);
			}
			child = nextchild;
			
		}
	}
	
	if (this._node.ownerDocument == document && this.nodeType == 1 ) {
		newNode.createNS(newNode.namespaceURI, newNode.localName, newNode.attributes);
	}
	
	var child = newNode.firstChild;
	while (child) {
		newNode._node.appendChild(child._node);
		child = child.nextSibling;
	}
	
	newNode_node = this._node.appendChild(newNode._node);
	newNode._node = newNode_node;
	this.appendChildIntern(newNode);
	if (!newNode.hasChildNodes()) {
		createTagNameAttributes(newNode._node,true);
	}
	return newNode;
}

XMLNode.prototype.appendChildIntern = function (newNode) {
	
	if (newNode._node.nodeType == 11) {
		var child = newNode._node.firstChild;
		while (child) {
			this.appendChildIntern(child.XMLNode);
			child = child.nextSibling;
			
		}
	}
	if (newNode.parentNode) {
		if (newNode.parentNode.firstChild == newNode) {
			newNode.parentNode.firstChild = newNode.nextSibling;
		}
		if (newNode.parentNode.lastChild == newNode) {
			newNode.parentNode.lastChild = newNode.previousSibling;
		}
	}
	if (newNode.previousSibling) {
		newNode.previousSibling.nextSibling = newNode.nextSibling;
	}
	if (newNode.nextSibling) {
		newNode.nextSibling.previousSibling = newNode.previousSibling;
	}
	
	newNode.parentNode = this;

	if (this.firstChild == null) {

		this.firstChild = newNode;

		this.lastChild = newNode;
		newNode.nextSibling = null;
		newNode.previousSibling = null;
	} else {
		newNode.previousSibling = this.lastChild;
		this.lastChild.nextSibling = newNode;
		this.lastChild = newNode;
		newNode.nextSibling = null;
	}
	
	newNode.ownerDocument = this.ownerDocument;
}


XMLNode.prototype.setContent = function (text, autocreate) {
	this.removeAllChildren();
	var mmmh = new XMLNode(text, null, 3, autocreate);
	this.appendChild(mmmh);
}

XMLNode.prototype.removeChild = function (child) {
	if (child._node.parentNode == this._node) {
		this._node.removeChild(child._node);
	}
	child.unlink();
	return child = null;
}

XMLNode.prototype.removeAllChildren = function() {
	var child = this.firstChild;
	while (child) {
		var oldchild = child;
		child = child.nextSibling;
		this.removeChild(oldchild);
	}
}


XMLNode.prototype.setNode = function(xmlnode) {
	this._xmlnode = xmlnode;
	this.namespaceURI = xmlnode.namespaceURI;
	this.localName = xmlnode.localName;
	this.prefix = xmlnode.prefix;
	this.nodeType = xmlnode.nodeType;
	/*if (xmlnode.vdom) {
		this._vdom = xmlnode.vdom;
	}*/
}

XMLNode.prototype.setAttribute = function(name, value) {
	this._node.setAttribute(name,value);
	if (this._sernode) {
		this._sernode.setAttribute(name,value);
	}
}


XMLNode.prototype.__defineGetter__( 
	"allowedChildren",
	function()
	{
		var ac = this.vdom.allowedChildren;
		if (ac) {
			return ac;
		} else {
			return new Array();
		}
	}
);

XMLNode.prototype.__defineGetter__( 
	"allowedNextSiblings",
	function()
	{
		var ac = this.vdom.allowedNextSiblings;
		
		if (ac) {
			return ac;
		} else {
			return new Array();
		}
	}
);


XMLNode.prototype.__defineSetter__( 
	"namespaceURI",
	function(value)
	{
		if (value == null) {
			value = "";
		}
		this._namespaceURI = value;
		if (this._node && this.isInHTMLDocument() && this.nodeType == 1 ) {
			this._node.setAttribute("__bxe_ns",value);
		}
	}
);
// FIXME: slow part for buildXML!!
//anonymous: 279-284, 573 call(s), 225.34ms total, 0.18ms min, 20.36ms max, 0.39ms avg
XMLNode.prototype.__defineGetter__( 
	"namespaceURI",
	function()
	{
		if (this._node && this._node.nodeType == 1 &&   this._node.hasAttribute("__bxe_ns")) {
			return this._node.getAttribute("__bxe_ns");
		}
		return this._namespaceURI;
	}
);


XMLNode.prototype.__defineGetter__( 
	"data",
	function()
	{
		if (this.nodeType == 3) {
			return this._node.data;
		} else {
			return false;
		}
	}
);



XMLNode.prototype.__defineGetter__( 
	"attributes",
	function()
	{
			return Array();
			
	}
);



XMLNode.prototype.__defineGetter__( 
	"nodeName",
	function()
	{
		if (this._nodeName) {
			return this._nodeName;
		} else {
			return this.localName;
		}
	}
);

XMLNode.prototype.__defineSetter__( 
	"nodeName",
	function(value)
	{
		 this._nodeName = value;
	}
);

XMLNode.prototype.setAttributeNS = function (namespaceURI, localName, value) {
	this._node.setAttributeNS(namespaceURI,localName, value);
	if (this._sernode) {
		this._sernode.setAttributeNS(namespaceURI,localName, value);
	}
}

XMLNode.prototype.insertIntoHTMLDocument = function(htmlnode,onlyChildren) {
	
	var walker = this.createTreeWalker();
	var node;
	if(onlyChildren) {
		node = walker.nextNode();
	} else {
		node = this;
		
	}
	if (node.parentNode) {
		htmlnode.XMLNode = node.parentNode;
		node.parentNode._node = htmlnode;
	}
	var firstChild = false;
	do  {
			var newNode;
			if (node.nodeType == 1 ) {
				newNode = node.makeHTMLNode()
				if (! node.hasChildNodes() && !(node.namespaceURI == XHTMLNS && ( node.localName == "img" || node.localName == "object")) ) {
						var xmlstring = node.getBeforeAndAfterString(false,true);
						
						newNode.setAttribute("_edom_tagnameopen",xmlstring[0]);
				}
			} else {
				newNode = node.makeHTMLNode();
			}
			if (this.nodeType == 3) {
				return;
			}
			if (!firstChild) {
				firstChild = newNode;
			}

			node = walker.nextNode();
			
	}  while(node );
	return firstChild;
}


XMLNode.prototype.getBeforeAndAfterString = function () {
	var nodeName = "";
	if (this.prefix) {
		nodeName = this.prefix + ":";
	}
	var before = "";
	var after = "";
	
	nodeName = nodeName + this.localName;
	before = "<"+ nodeName;
	var attribs = this.attributes;
	for (var i = 0; i < attribs.length; i++) {
		before = before + " " + attribs[i].localName + '="'+attribs[i].value+'"';
	}
	if (this.hasChildNodes() ){
		before = before + ">";
		after = "</"+ nodeName +">";
	} else {
		before = before + "/>";
	}
	return new Array(before,after);
	
}
XMLNode.prototype.createNS = function (namespaceURI, localName, attribs) {
	
	var htmlelementname;
	if (this.nodeType == 1) {
		this.localName = localName;
		this.namespaceURI = namespaceURI;
	}
	this._node = bxe_Node_createNS(this.nodeType, namespaceURI, localName, attribs);
	
	if (this._node) {
		this._node.XMLNode = this;
	} 
}

XMLNode.prototype.getXPathString = function() {
	var prevSibling = this;
	var position = 1;
	var xpathstring = "";
	if (this.parentNode && this.parentNode.nodeType == 1) {
		xpathstring = this.parentNode.getXPathString() ;
	}
	if (this.nodeType == 3 ) {
		xpathstring += "/text()";
	}
	else {
		prevSibling = prevSibling.previousSibling
		while (prevSibling ) {
			if (prevSibling.nodeName == this.nodeName) {
				position++;
			}
			prevSibling = prevSibling.previousSibling
		}
		xpathstring += "/" + this.localName +"[" + position + "]";
	}
	return xpathstring;
	
}

XMLNode.prototype.createTreeWalker= function() {
	return new XMLNodeWalker(this);
}
	
function XMLNodeWalker (startnode,afunction) {
	
	this.currentNode = startnode;
	this.startNode = startnode;

}
XMLNodeWalker.prototype.nextNode = function() {
	if (this.currentNode.firstChild) {
		this.currentNode = this.currentNode.firstChild;
		return this.currentNode;
	}
	else if (this.currentNode.nextSibling) {
		if (this.currentNode == this.startNode) {
			return null;
		}
		this.currentNode = this.currentNode.nextSibling;
		return this.currentNode;
	}
	else if(this.currentNode.parentNode && this.currentNode != this.startNode) {
		this.currentNode = this.currentNode.parentNode;
		while ( this.currentNode && this.currentNode != this.startNode && !this.currentNode.nextSibling ) { 
			this.currentNode = this.currentNode.parentNode;
		}
		if (this.currentNode && this.currentNode != this.startNode ) {
			this.currentNode = this.currentNode.nextSibling;
			return this.currentNode;
		}
		else { 
			return null
		};
	}
	return null;
	
}
// FIXME: slow part for buildXML!!
//anonymous: 504-506, 573 call(s), 62.68ms total, 0.05ms min, 9.81ms max, 0.11ms avg
XMLNode.prototype.isInHTMLDocument= function() {
	return (this._node.ownerDocument == document)
}

XMLNode.prototype.isAllowedNextSibling = function (namespaceURI, localName) {
	var aNS = this.allowedNextSiblings;
	
	for (i = 0; i < aNS.length; i++) {
		if (aNS[i].namespaceURI == namespaceURI && aNS[i].localName == localName) {
			return true;
		}
	}
	return false;
	
}

XMLNode.prototype.makeHTMLNode = function () {
	if (this.nodeType == 1) {
		this.createNS(this.namespaceURI, this.localName, this._node.attributes);
	} else if (this.nodeType == 3 ) {
		this.createNS(this.data);
	}
	
	else if (this._node.nodeType == 9 ) { // Node.XMLDOCUMENT) {
			this._node = this._node.documentElement;
	}
	if (this.parentNode && this.parentNode.isInHTMLDocument()) {
		this._node = this.parentNode._node.appendChild(this._node);
	} else {
	}
		
	this._node.XMLNode = this;
	return this._node;	
}

XMLNode.prototype.__defineGetter__ (
	"childNodes",
	function() {
		chN = new Array();
		var node = chN.firstChild;
		while (node) { 
			chN.push(node);
			node= node. nextSibling;
		}
		return chN;
	}
);
XMLNode.prototype.hasChildNodes = function() {
	if (typeof this.firstChild != "undefined" && this.firstChild != null && this.firstChild._node.nodeValue != '') {
		return true;
	} else {
		return false;
	}
}
/* not yet implemented */
XMLNode.prototype.hasAttributes = function() {
	return false;
}

/**
 * Removes all children of an Element
 */
XMLNode.prototype.appendAllChildren = function(node) {
	var child = node.firstChild;
	while (child) {
		var oldchild = child;
		child = child.nextSibling;
		this.appendChild(oldchild);
	}
}


XMLNode.prototype.info = function() {
	var str = "";
	str = "nodeType: " +this.nodeType + "\n";
	str += "namespaceURI: " + this.namespaceURI + "\n";
	str += "localName: " + this.localName + "\n";
	str += "data: "+ this.data + "\n";
	
	return  (str);
}
	

function XMLNodeElement ( nodein, localName, nodeType, autocreate) {
this.objectType ="XMLNodeElement";
	this.init( nodein, localName, nodeType, autocreate);

}

XMLNodeElement.prototype = new XMLNode();




XMLNodeElement.prototype.hasAttributes = function() {
	if (this._node) {
		var attribs = this._node.attributes;
		for (var i = 0; i < attribs.length; i++) {
			if (attribs[i].localName.substr(0,5) != "_edom" && attribs[i].localName.substr(0,5) != "__bxe") {
				return true;
			}
		}
	} 
	return false;
}
// FIXME: slow part for buildXML!!
//anonymous: 603-619, 156 call(s), 185.67ms total, 0.37ms min, 10.46ms max, 1.19ms avg

XMLNodeElement.prototype.__defineGetter__( 
	"attributes",
	function()
	{
		var attribs;
		if (this.xmlBridge) {
			attribs = this.xmlBridge.attributes;
		} else if (this._node) {
			attribs = this._node.attributes;
		} else {
			attribs = new Array;
		}
		var attributes = new Array();
		for (var i = 0; i < attribs.length; i++) {
			var prefix = attribs[i].localName.substr(0,5);
			if (prefix != "_edom" && prefix != "__bxe" && attribs[i].namespaceURI != "http://www.w3.org/2000/xmlns/" && !(this.namespaceURI != XHTMLNS && attribs[i].localName == "class") )  {
				if (! (attribs[i].localName == "class" && attribs[i].value == this.localName)) {
					attributes.push(attribs[i]);
				}
			}
		}
		return attributes;
	}
);

XMLNodeElement.prototype.setAttribute = function(name,value) {
	try {
	if (this._sernode) {
		this._sernode.setAttribute(name,value);
	}
	return this._node.setAttribute(name, value);
	
	} catch(e) {
		alert(e + "\n" + name + " = " + value + " could not be inserted");
	}
}

XMLNodeElement.prototype.setAttributeNode = function(node) {
	return this._node.setAttributeNS(node.namespaceURI,node.localName, node.value);
	
}


XMLNodeElement.prototype.setAttributeNS = function(namespace,name,value) {
	
	if (this._sernode) {
		this._sernode.setAttribute(namespace,name,value);
	}
	if(name != "xmlns" && namespace != "http://www.w3.org/2000/xmlns/") { 
		return this._node.setAttributeNS(namespace,name, value);
	}
}

XMLNodeElement.prototype.getAttribute = function(name) {
	try {
		return this._node.getAttribute(name);
	} catch (e) {
		return null;
	}
}

XMLNodeElement.prototype.getAttributeNS = function(namespace,name) {
	return this._node.getAttributeNS(namespace,name);
}

XMLNodeElement.prototype.removeAttribute = function(name) {
	return this._node.removeAttribute(name);
}

XMLNodeElement.prototype.isAllowedAttribute = function(name) {
	var attr = this.vdom.attributes;
	if (attr[name]) {
		return true;
	} else {
		return false;
	}
}


XMLNodeElement.prototype.makeDefaultNodes = function(noPlaceholderText) {
	
	
	var cHT  =  this.canHaveText;
	if (cHT ) {
		if (!noPlaceholderText) {
			this.setContent("#" + this.localName + " ");
			
			this._node.removeAttribute("_edom_tagnameopen");
		}
		this.parentNode.isNodeValid(true,2,false,true);

	} else {
		var ac = this.allowedChildren;
		if (ac.length == 1)  {
			eDOMEventCall("appendChildNode",document,{"appendToNode": this, "localName":ac[0].localName,"namespaceURI":ac[0].namespaceURI});
		} else if (ac.length > 1) {
			var _hasMust = false;
			for ( var i in ac) {
				if (!(ac[i].optional ) ) { 
					eDOMEventCall("appendChildNode",document,{ "appendToNode": this, "localName":ac[i].localName,"namespaceURI":ac[i].namespaceURI});
					_hasMust =true;
				}
				
			}
			if (!_hasMust) {
				bxe_context_menu.buildElementChooserPopup(this,ac);
			} else {
				ret = this;
				
			}
			
		}
		else {
			var xmlstring = this.getBeforeAndAfterString(false,true);
			this.setAttribute("_edom_tagnameopen",xmlstring[0]);
			this.parentNode.isNodeValid(true,2,false,true);
		}
	}
}

	
	
	
