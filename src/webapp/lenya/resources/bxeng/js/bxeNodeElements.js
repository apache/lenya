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
// $Id: bxeNodeElements.js 1414 2005-12-06 13:34:00Z chregu $

Node.prototype.insertIntoHTMLDocument = function(htmlnode,onlyChildren) {
	alert("Node.prototype.insertIntoHTMLDocument is deprecated");
	return;
}



Node.prototype.transformToDocumentFragment = function () {
	
	var docfrag = this.ownerDocument.createDocumentFragment();
	var child = this.firstChild;
	var oldchild = null;
	alert(this);
	do {
		oldchild = child;
		child = child.nextSibling
		docfrag.appendChild(oldchild);
	} while (child )
	return docfrag;
}

Node.prototype.convertToXMLDocFrag = function () {
	return  this.XMLNode.buildXML();
}

Node.prototype.getNamespaceDefinitions = function () {
	
	var node = this;
	var attr;
	var namespaces = new Array();
	while (node.nodeType == 1 ) {
		attr = node.attributes;
		for (var i = 0; i < attr.length; i++) {
			if (attr[i].namespaceURI == XMLNS && !(namespaces[attr[i].localName])) {
				namespaces[attr[i].localName] = attr[i].value;
			}
		}
		node = node.parentNode;
	}
	return namespaces;
}



Node.prototype.__defineGetter__(
"XMLNode",
function()
{
	if (this.InternalParentNode) {
		this._XMLNode = this.InternalParentNode.XMLNode;
	}
	else if (!this._XMLNode ) {
		if ( this.nodeType == 1) {
			this._XMLNode = new XMLNodeElement(this);
		} else {
			this._XMLNode = new XMLNode(this);
		}
	}
	
	return this._XMLNode;
}	
);

Node.prototype.__defineSetter__(
	"XMLNode",
	function(node)
	{
		this._XMLNode = node;
	}
);

Element.prototype.getCStyle = function(style) {
	return document.defaultView.getComputedStyle(this, null).getPropertyValue(style);
}

Element.prototype.changeContainer = function(namespace, localName) {
	var keep = false;
	if (namespace == XHTMLNS) {
		var removeClass = false;
		//if (lines[i].__container.getAttribute("class"));
		if (this.XMLNode) {
			if (this.XMLNode.nodeName == this.getAttribute("class")) {
				removeClass = true;
			}
		}
		var newNode = documentCreateXHTMLElement(localName)
		if (removeClass) {
			this.removeAttribute("class");
		}
	} else {
		var newNode = document.createElementNS(XHTMLNS,"div");
		this.setAttribute("class", containerName);
	}
	
	for(var i=0; i<this.attributes.length; i++)
	{
		var childAttribute = this.attributes.item(i);
		var childAttributeCopy = childAttribute.cloneNode(true);
		newNode.setAttributeNode(childAttributeCopy);
	}
	var childContents = document.createRange();
	childContents.selectNodeContents(this);

	newNode.appendChild(childContents.extractContents());
	childContents.detach();
	this.parentNode.replaceChild(newNode, this);

	
	newNode.setAttribute("__bxe_ns", namespace);
	return newNode;
	
}

Element.prototype.getBeforeAndAfterString = function (hasChildNodes, noParent) {
	
	var lastChild = this;
	while ( lastChild.firstChild) {
			lastChild = lastChild.firstChild;
	}
	var xmlstring;
	try {
		if (hasChildNodes == false) {
			xmlstring = new Array();
			if (noParent) {
				xmlstring[0] = this.ownerDocument.saveXML(this);
			} else {
				xmlstring[0] = this.ownerDocument.saveChildrenXML(this,true).str;
			}
			xmlstring[1] = null;
		} else {
			lastChild.appendChild(this.ownerDocument.createTextNode("::"));
			xmlstring = this.ownerDocument.saveChildrenXML(this,true).str.split("::");
		}
	} catch(e) {
		xmlstring = new Array();
		xmlstring[0] = this.ownerDocument.saveChildrenXML(this,true).str;
		xmlstring[1] = null;
	}
	xmlstring[2] = lastChild;

	return xmlstring;
	
}

Node.prototype.initXMLNode = function () {
	if (this.nodeType == 1 ) {
		this.XMLNode = new XMLNodeElement(this) ;
	} else {
		this.XMLNode = new XMLNode(this);
	}
	return this.XMLNode;
	
}

Node.prototype.__defineGetter__ ( 
	"previousNotInternalSibling",
	function () {
		var prev = this.previousSibling;
		while (prev) {
			if(prev.nodeType != 1 || ! prev.hasAttribute("_edom_internal_node")) {
				return prev;
			}
			prev = prev.previousSibling;
		}
		return null;
	}
)

Node.prototype.__defineGetter__ ( 
	"nextNotInternalSibling",
	function () {
		var next = this.nextSibling;
		while (next) {
			if(next.nodeType != 1  || ! next.hasAttribute("_edom_internal_node")) {
				return next;
			}
			next = next.nextSibling;
		}
		return null;
	}
)
Node.prototype.__defineGetter__ ( 
	"firstNotInternalChild",
	function () {
		var first = this.firstChild;
		while (first) {
			if(first.nodeType != 1  || ! first.hasAttribute("_edom_internal_node")) {
				return first;
			}
			first = first.nextSibling;
		}
		return null;
	}
)

Node.prototype.updateXMLNode = function (force) {
	//dump("updateXMLNode " + this + "\n");
	if (this.nodeType == 1 && !this.userModifiable && this.hasChildren) {
		return;
	}
	if (this._XMLNode && this.XMLNode.xmlBridge) {
		var firstChild = this.firstNotInternalChild;
		if (firstChild) {
			this.XMLNode.firstChild = firstChild.XMLNode;
			return firstChild.updateXMLNode(force);}
		else { 
			this.XMLNode.firstChild = null;
			return ;
		}
	}

		
	if (this.parentNode && !this.parentNode._XMLNode ) {
		return this.parentNode.updateXMLNode(force);
	}
	if (this.nodeType == 3) {
		this.normalize();
	}
	
	/*if (this.nodeType== 1 && this.hasAttribute("class")) {
		this.SplitClasses();
	}*/
	var prev = this.previousNotInternalSibling;
	if (prev ) {
		if (!prev._XMLNode ) {
			prev.updateXMLNode(force);
		}
		this.XMLNode.previousSibling = prev.XMLNode;
		prev.XMLNode.nextSibling = this.XMLNode;
	} else {
		this.XMLNode.previousSibling = null;
	}
	var next = this.nextNotInternalSibling;
	if (next ) {
		if (!next._XMLNode || force || next.nodeType == 3) {
			next.updateXMLNode(force);
		}
		this.XMLNode.nextSibling = next.XMLNode;
		next.XMLNode.previousSibling = this.XMLNode;
	} else {
		this.XMLNode.nextSibling = null;
	}
	if (this.parentNode  && this.parentNode.XMLNode) {
		
		this.XMLNode.parentNode = this.parentNode.XMLNode;
	}
	if (!this.XMLNode.nextSibling && this.XMLNode.parentNode) {
		this.XMLNode.parentNode.lastChild = this.XMLNode;
	}
	if (!this.XMLNode.previousSibling && this.XMLNode.parentNode) {
		this.XMLNode.parentNode.firstChild = this.XMLNode;
	}
	this.XMLNode._node = this

	if (this.nodeType == 1 && this.hasAttribute("__bxe_ns")) {
		this.XMLNode.namespaceURI = this.getAttribute("__bxe_ns");
	}
	var child = this.firstNotInternalChild;
	if (child) {
		
		while (child) {
			child.updateXMLNode(force);
			child = child.nextNotInternalSibling;
		}
	}

}

Node.prototype.getXPathResult = function(xpath) {
	
	var nsRes = this.ownerDocument.createNSResolver(this.ownerDocument.documentElement);
	return this.ownerDocument.evaluate(xpath, this,nsRes, 0, null);
}


Node.prototype.getXPathFirst = function(xpath) {
	
	var res = this.getXPathResult(xpath);
	return res.iterateNext();
}


Node.prototype.init = function() {
	var walker = this.ownerDocument.createTreeWalker(
		this,NodeFilter.SHOW_ALL,
	{
		acceptNode : function(node) {			
			return NodeFilter.FILTER_ACCEPT;
		}
	}
	, true);

	var node = this;
	var firstChild = false;

	do  {
		if (node.nodeType == 1) {
			node.XMLNode = new XMLNodeElement(node);
		} else {
			node.XMLNode = new XMLNode(node);
		}
		node = walker.nextNode();
	}  while(node );
	
	walker.currentNode = this;
	if (this == this.ownerDocument.documentElement) {
		this.ownerDocument.XMLNode.documentElement = this.ownerDocument.documentElement.XMLNode;
        this.ownerDocument.documentElement.XMLNode.parentNode = this.ownerDocument.XMLNode;
	} else {
	}
	
	node = walker.currentNode;
	do  {

		x = node.XMLNode;
		x.ownerDocument = this.ownerDocument.XMLNode;
		if (node.parentNode) {
			x.parentNode = node.parentNode.XMLNode;
		}
		if (node.previousSibling) {
			x.previousSibling = node.previousSibling.XMLNode;
		}
		if (node.nextSibling) {
			x.nextSibling = node.nextSibling.XMLNode;
		}
		if (node.firstChild) {
			x.firstChild = node.firstChild.XMLNode;
		}
		if (node.lastChild) {
			x.lastChild = node.lastChild.XMLNode;
		}
		x.nodeType = node.nodeType;
		x.prefix = node.prefix;
		node = walker.nextNode();
	}  while(node );
	return this.XMLNode;
}

/* mmmh, the same as in insertIntoHTML methods of XMLNode
    not that smart to have both ways...
	but    
	 node.prepareForInsert();
	 node.updateXML();
	seems to work quite well for this here.
	
	Maybe some stuff from XMLNode, could use this here..
*/

Node.prototype.prepareForInsert = function(onlyChildren) {
	var walker = document.createTreeWalker(
	 this,NodeFilter.SHOW_ALL,
	{
		acceptNode : function(node) {
			
			return NodeFilter.FILTER_ACCEPT;
		}
	}
	, true);
	var node;
	if(onlyChildren) {
		node = walker.nextNode();
	} else {
		node = this;
		
	}
	var firstChild = false;
	do  {
			var newNode;
	

			newNode = node.makeHTMLNode();

			if (!firstChild) {
				firstChild = newNode;
			}
			if (node.parentNode && node.parentNode.newNode) {
				node.parentNode.newNode.appendChild(newNode);
			}
			node.newNode = newNode;

			node = walker.nextNode();
			
	}  while(node );
	return firstChild;
}

Node.prototype.makeHTMLNode = function () {
	var _node;
	if (this.nodeType == 1) {
		_node = this.createNS(this.namespaceURI, this.attributes);
	} else if (this.nodeType == 3 ) {
		_node = this.createNS(this.data);
	}
	else if (this.nodeType == 9 ) { // Node.XMLDOCUMENT) {
			_node = this.documentElement;
	}
	return _node;	
}

Node.prototype.createNS = function ( namespaceURI, attribs ) {
	return bxe_Node_createNS(this.nodeType, namespaceURI, this.localName, attribs);
}


function bxe_Node_createNS(nodeType, namespaceURI, localName, attribs) {
	var htmlelementname;
	var _node;
	if (nodeType == 1) {
		
		if (namespaceURI != XHTMLNS) {
			if (namespaceURI == MATHMLNS) {
				_node = document.createElementNS(MATHMLNS,localName);
			} else {
			htmlelementname = "span"
			_node = document.createElement(htmlelementname);
			_node.setAttribute("class", localName);
			_node.setAttribute("__bxe_ns",namespaceURI);
			}
		}
		else {
			_node = documentCreateXHTMLElement(localName.toLowerCase(), attribs);
		}
		if (attribs) {
			for (var i = 0; i< attribs.length; i++) {
				if (attribs[i].namespaceURI != XMLNS) {
					_node.setAttributeNS(attribs[i].namespaceURI, attribs[i].localName,attribs[i].value);
				} 
			}
		}
	}
	else if (nodeType == 3) {
		_node = document.createTextNode(namespaceURI);
	}
	return _node;
	
}
