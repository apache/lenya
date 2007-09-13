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
// $Id: RelaxNG.js 1413 2005-12-06 13:13:45Z chregu $


const RELAXNGNS= "http://relaxng.org/ns/structure/1.0";

DocumentVDOM.prototype.parseRelaxNG = function () {
	
	
	this.xmldoc.documentElement.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:rng","http://relaxng.org/ns/structure/1.0");

	bxe_config.DocumentVDOM = this;
	//setup nsResolver
	rng_nsResolver = new bxe_RelaxNG_nsResolver(this.xmldoc.documentElement);
	//do includes
	dump ("Start parseIncludes " + (new Date()- startTimer)/1000 + " sec\n"); 
	this.parseIncludes();
	dump ("Start derefAttributes " + (new Date() - startTimer)/1000 + " sec\n"); 
	this.dereferenceAttributes();
	
	var endTimer = new Date();
	dump ("Start parsing RNG " + (endTimer - startTimer)/1000 + " sec\n"); 
	if (DebugOutput) {
		//dump(this.xmldoc.saveXML(this.xmldoc));
	}	

	//dump(this.xmldoc.saveXML(this.xmldoc));
	
	var rootChildren = this.xmldoc.documentElement.childNodes;
	
	var startExists = false;
	for (var i = 0; i < rootChildren.length; i++) {
		if (rootChildren[i].isRelaxNGElement("start")) {
			this.parseStart(rootChildren[i]);
			startExists = true;
			break;
		}
	}
	if (!startExists) {
		alert("No <start> element found.");
	}
	dump ("End parsing RNG " + (new Date() - startTimer)/1000 + " sec\n"); 
	return true;
}

DocumentVDOM.prototype.dereferenceAttributes = function() {
	
	
	//kill all interleave/optional/attribute ...
	var xp = "/rng:grammar//rng:interleave[rng:optional/rng:attribute]"
	var defRes= this.xmldoc.documentElement.getXPathResult(xp);
	var defNode = defRes.iterateNext();
	var defNodes = new Array();
	while (defNode) {
			defNodes.push(defNode);
			defNode = defRes.iterateNext();
	}
	
	for (j in defNodes) {
		var child = defNodes[j].firstChild;
			while (child) {
				var nextSib = child.nextSibling;
				defNodes[j].parentNode.insertBefore(child,defNodes[j]);
				child = nextSib;
			}
			defNodes[j].parentNode.removeChild(defNodes[j]);
	}
	// optional/attribute ...
	var xp = "/rng:grammar//rng:optional[rng:attribute]"
	var defRes= this.xmldoc.documentElement.getXPathResult(xp);
	var defNode = defRes.iterateNext();
	var defNodes = new Array();
	while (defNode) {
			defNodes.push(defNode);
			defNode = defRes.iterateNext();
	}
	
	for (j in defNodes) {
		var child = defNodes[j].firstChild;
			while (child) {
				var nextSib = child.nextSibling;
				if (child.nodeType == Node.ELEMENT_NODE) {
					child.setAttribute("type","optional");
				}
				
				defNodes[j].parentNode.insertBefore(child,defNodes[j]);
				//ugly hack..
				child = nextSib;
			}
			defNodes[j].parentNode.removeChild(defNodes[j]);
	}
	
	// group  is currently not supported.. removeit
	var xp = "/rng:grammar//rng:group";
	var defRes= this.xmldoc.documentElement.getXPathResult(xp);
	var defNode = defRes.iterateNext();
	var defNodes = new Array();
	while (defNode) {
			defNodes.push(defNode);
			defNode = defRes.iterateNext();
	}
	
	for (j in defNodes) {
		defNodes[j].parentNode.removeChild(defNodes[j]);
	}
	
	if (defNodes.length > 0) {
	    alert("Removed " + defNodes.length + " unsupported group nodes");
	}
	
	return true;
}

DocumentVDOM.prototype.parseIncludes = function() {
	var rootChild = this.xmldoc.documentElement.firstChild;
	var alreadyNext;
	this.replaceIncludePaths(this.xmldoc,this.directory);
	var loadedPaths = new Array();
	while (rootChild) {
		alreadyNext = false;
		if (rootChild.nodeType == Node.TEXT_NODE && rootChild.isWhitespaceOnly) {
			var rootChildOld = rootChild;
			var rootChild = rootChild.nextSibling;
			alreadyNext = true;
			rootChildOld.parentNode.removeChild(rootChildOld);
		}
			
		else if (rootChild.isRelaxNGElement("include")) {
			var insertionNode = rootChild.nextSibling;
			var td = new mozileTransportDriver("http");
			var href = rootChild.getAttribute("href");
			bxe_about_box.addText(rootChild.getAttribute("href") + " " );
			if (loadedPaths[href]) {
				debug (href + " was already loaded...");
			}
			else { 
				var req =  td.load(href, null, false);
				if (req.isError) {
					debug("!!!RelaxNG file " + rootChild.getAttribute("href") + " could not be loaded!!!")
				} else {
					
					if (td.document.documentElement.isRelaxNGElement("grammar")) {
						
						this.replaceIncludePaths(td.document,href);
						this.replacePrefixes(td.document);
						// check childs of include path;
						var includeChild = rootChild.firstChild;
						while (includeChild) {
							var _newChild =  includeChild.nextSibling;
							if (includeChild.isRelaxNGElement("define") || includeChild.isRelaxNGElement("start")) {
								includeChild.setAttribute("__bxe_includeChild","true");
								insertionNode.parentNode.insertBefore(includeChild,insertionNode);
							}
							includeChild = _newChild;
						}

						var child = td.document.documentElement.firstChild;
						while (child) {
							if (child.isRelaxNGElement("define") || child.isRelaxNGElement("start")) {
								if (child.localName == "define") {
									var xp = "/rng:grammar/rng:define[@name = '" + child.getAttribute("name") + "']";
								} else {
									var xp = "/rng:grammar/rng:start";
								}
								var firstDefine = this.xmldoc.documentElement.getXPathFirst(xp); 
								if (child.hasAttribute("combine") || (firstDefine && firstDefine.hasAttribute("combine"))) {
									var comb = child.getAttribute("combine");
									if (!comb) {
										comb = firstDefine.getAttribute("combine");
									}
									if(firstDefine && !firstDefine.hasAttribute("__bxe_includeChild")) {
										debug("firstDefine " + firstDefine.getAttribute("name") + comb);
										var firstElement = firstDefine.getXPathFirst("*[position() = 1]")
										if (firstElement.nodeName == comb) {
											debug("append to firstElement " + firstElement.nodeName);
											var newChild = this.xmldoc.importNode(child,true);
											var firstIncludeDefElement = newChild.getXPathFirst("*[position() = 1]");
											if (firstIncludeDefElement.localName == comb) {
												firstElement.appendAllChildren(firstIncludeDefElement);
											} else {
												firstElement.appendAllChildren(newChild);
											}
											
										} else {
											debug ("make new..." + comb);
											var newChild = this.xmldoc.createElementNS(RELAXNGNS,comb);
											newChild.appendAllChildren(firstDefine);
											firstDefine.appendChild(newChild);
											var importedDefine = this.xmldoc.importNode(child,true)
											newChild.appendAllChildren(importedDefine);
											
										}
									} else if (firstDefine && firstDefine.hasAttribute("__bxe_includeChild")) {
										debug ("!!!overriden by include directive");	
									} else {
										//debug ("not already defined");
										var newChild = this.xmldoc.importNode(child,true);
										rootChild.parentNode.insertBefore(newChild,insertionNode);
									}
								} else {
									if (firstDefine) {
										debug("!!! " + child.getAttribute("name") + " already defined !!!!");
										if (firstDefine.hasAttribute("__bxe_includeChild")) {
											debug ("!!!overriden by include directive");	
										}
									} else {
										var newChild = this.xmldoc.importNode(child,true);
										rootChild.parentNode.insertBefore(newChild,insertionNode);
									}
								}
								
							} else {
								var newChild = this.xmldoc.importNode(child,true);
								rootChild.parentNode.insertBefore(newChild,insertionNode);
							}
							child = child.nextSibling;
						}
					} else {
						debug("!!!!" +href + " is not a Relax NG Document\n" +  td.document.saveXML(td.document), E_FATAL);
					}
				}
			}
			var rootChildOld = rootChild;
			var rootChild = rootChild.nextSibling;
			alreadyNext = true;
			
			rootChildOld.parentNode.removeChild(rootChildOld);
		}
		if (!alreadyNext) {
			rootChild = rootChild.nextSibling;
		}
	}

}
DocumentVDOM.prototype.replacePrefixes = function (doc) {
	doc.documentElement.setAttributeNS("http://www.w3.org/2000/xmlns/","xmlns:rng","http://relaxng.org/ns/structure/1.0");
	var _rng_nsResolver = new bxe_RelaxNG_nsResolver(doc.documentElement);
	var res = doc.getElementsByTagNameNS(RELAXNGNS,"element");
	var prefix;
	for (var i = 0; i < res.length; i++) {
		var ns = _rng_nsResolver.parseNodeNameOnElement(res[i]);
		prefix = rng_nsResolver.lookupPrefix(ns.namespaceURI);
		if (typeof prefix == "string" && prefix != "") {
			
			res[i].setAttribute("name", prefix + ":" + ns.localName); 
		} else if (prefix == "") {
			res[i].setAttribute("name", ns.localName);
		}
	}
}

DocumentVDOM.prototype.replaceIncludePaths = function(doc, currentFile) {
	var includes = doc.documentElement.getElementsByTagNameNS(RELAXNGNS,"include");
	var workingdir = bxe_getDirPart(currentFile);
	//replace includes with fulluri
	var href;
	for (var i = 0; i < includes.length; i++) {
		href = includes[i].getAttribute("href");
		if (href.indexOf("/") != 0  && href.indexOf("://") < 0) {
			href = workingdir + href;
		}
		includes[i].setAttribute("href", href);
	}
}
var rng_nsResolver;
DocumentVDOM.prototype.parseStart = function(node) {
	var startChildren = node.childNodes;
	var startNode = null;
	Ende:
	for (var i = 0; i < startChildren.length; i++) {
		// debug ("i" + i + " " +startChildren[i].nodeName);
		if (startChildren[i].isRelaxNGElement("element")) {
			startNode = startChildren[i];
			break Ende;
		} 
		if (startChildren[i].isRelaxNGElement("ref")) {
			var xp = "/rng:grammar/rng:define[@name = '" + startChildren[i].getAttribute("name") + "']/rng:element"
			startNode = this.xmldoc.documentElement.getXPathFirst(xp); 
			break Ende;
		}
	}
	if (startNode) {
		var startElement = new ElementVDOM(startNode);
		this.firstChild = startElement;
		startElement.parentNode = this;
		var nsParts = rng_nsResolver.parseNodeNameOnElement(startNode);
		startElement.nodeName = nsParts.nodeName;
		startElement.localName = nsParts.localName;
		startElement.namespaceURI = nsParts.namespaceURI;
		startElement.prefix = nsParts.prefix;
		startElement.canBeRoot = true;
		startElement.nextSibling = null;
		startElement.previousSibling = null;
	}
	// debug(startNode );
	//dump("before parseChildren");
	startElement.parseChildren();
	//dump("RelaxNG is parsed\n");

}


function bxe_RelaxNG_nsResolver(node) {
	var rootAttr = node.attributes;
	this.defaultNamespace = "";
	this.namespaces = new Array();
	this.prefixes = new Array();

	for(var i = 0; i < rootAttr.length; i++) {
		if (rootAttr[i].namespaceURI == "http://www.w3.org/2000/xmlns/") {
			this.namespaces[rootAttr[i].localName] = rootAttr[i].value;
			this.prefixes[rootAttr[i].value] = rootAttr[i].localName;
		} else if (rootAttr[i].localName == "ns") {
			this.defaultNamespace = rootAttr[i].value;
			this.prefixes[rootAttr[i].value] = "";
		}
	}
}
bxe_RelaxNG_nsResolver.prototype.lookupNamespaceURI = function(prefix) {
	if (this.namespaces[prefix]) {
		return this.namespaces[prefix];
	}
	return null;
}

bxe_RelaxNG_nsResolver.prototype.lookupPrefix = function (namespaceURI) {
	
	if (this.prefixes[namespaceURI]) {
		return this.prefixes[namespaceURI];
	}
	return null;
}
bxe_RelaxNG_nsResolver.prototype.parseNodeNameOnElement = function(node) {
	var nodename = node.getAttribute("name");
	if (nodename) {
		return this.parseNodeName(nodename);
	}
	// no attribute name, search for childnode with name attribute
	var child = node.firstChild;
	var ret = new Object();
	while (child) {
		if (child.nodeType == Node.ELEMENT_NODE && child.localName == "name") {
			child.getAttribute("ns");
			ret.namespaceURI = child.getAttribute("ns");
			ret.localName = child.firstChild.data;
			ret.prefix = "";
			return ret;
		}
		child = child.nextSibling;
	}
	
}

bxe_RelaxNG_nsResolver.prototype.parseNodeName = function(nodename) {
	var spli = nodename.split(":");
	var ret = new Object;
	ret.nodeName = nodename;
	
	if (spli.length > 1) {
		ret.localName = spli[1];
		ret.namespaceURI = this.lookupNamespaceURI(spli[0]);
		ret.prefix = spli[0];
	} else {
		ret.localName = spli[0];
		ret.namespaceURI = this.defaultNamespace;
		ret.prefix = "";
	}
	return ret;
	
}
	
Node.prototype.__defineGetter__ ("hasRelaxNGNamespace", function() {
	
	if (this.namespaceURI == RELAXNGNS) {
		return true;
	} else {
		return false;
	}
}
)
Node.prototype.isRelaxNGElement = function(nodename) {
	
//	dump ("isRelaxNGElement" + this.nodeType  +  " " + this.nodeName + " " + this.hasRelaxNGNamespace + "\n");
	if (this.nodeType == Node.ELEMENT_NODE && this.nodeName == nodename && this.hasRelaxNGNamespace) {
		return true;
	} else {
		return false;
	}
}
	

NodeVDOM.prototype.parseChildren = function(node) {
	var childNodes;

	if (node) {
		childNodes = node.childNodes;
	} else {
		childNodes = this.node.childNodes;
	}
	var newChoice;
	var newOneOrMore;
	
	
	for (var i = 0; i < childNodes.length; i++) {
		
		if (!(childNodes[i].nodeType == 1 && childNodes[i].hasRelaxNGNamespace)) {continue;}
		switch (childNodes[i].localName) {
			case "element": 
				var newElement = new ElementVDOM(childNodes[i]);
				var nsParts = rng_nsResolver.parseNodeNameOnElement(childNodes[i]);
				newElement.nodeName = nsParts.nodeName;
				newElement.localName = nsParts.localName;
				newElement.namespaceURI = nsParts.namespaceURI;
				newElement.prefix = nsParts.prefix;
				this.appendChild(newElement);
				newElement.parseChildren();
				this._hasEmpty = false;
				break;
			
		//} else if (childNodes[i].isRelaxNGElement("ref")  && !childNodes[i].getAttribute("name").match(/\.attlist/)) {
			case "ref":
			
				//FIXME this can be done smarter... cache the defines.
				var xp = "/rng:grammar/rng:define[@name = '" + childNodes[i].getAttribute("name") + "']"
				var defineChild = this.node.ownerDocument.documentElement.getXPathFirst(xp);
				//debug("ref " + xp + " " + defineChild); 
				
				if (defineChild) {
					//FIXME:...
					if (!defineChild.isParsed) {
						var newDefine = new DefineVDOM(defineChild);
						defineChild.isParsed = true;
						defineChild.vdom = newDefine;
						newDefine.parseChildren(defineChild);
						/* if (newDefine.lastChild) {
							newDefine.lastChild.nextSibling = newDefine;
						} */
						
					} 
					var newRef = new RefVDOM(childNodes[i]);
					newRef.DefineVDOM = defineChild.vdom;
					this.appendChild(newRef);
				}
				break;
			case "oneOrMore":
				newOneOrMore = new OneOrMoreVDOM(childNodes[i]);
				this.appendChild(newOneOrMore);
				this._hasEmpty = false;
				newOneOrMore.parseChildren(childNodes[i]);
				break;
			case "text":
				this.appendChild(new TextVDOM(childNodes[i]));
				this._hasEmpty = false;
				break;
			case "zeroOrMore":
				newZeroOrMore = new ZeroOrMoreVDOM(childNodes[i]);
				this.appendChild(newZeroOrMore);
				this._hasEmpty = true;
				newZeroOrMore.parseChildren(childNodes[i]);
				break;
			case "attribute":
				this.addAttributeNode( new AttributeVDOM(childNodes[i]));
				break;
			case "optional":
				newChoice = new ChoiceVDOM(childNodes[i]);
				newChoice.optional = true;
				this.appendChild(newChoice);
				newChoice.appendChild(new EmptyVDOM());
				this._hasEmpty = false;
				newChoice.parseChildren();
				break;
			case "choice":
				newChoice = new ChoiceVDOM(childNodes[i]);
				newChoice.optional = true;
				this.appendChild(newChoice);
				newChoice.parseChildren();
				this._hasEmpty = false;
				break;
			case "interleave":
				var newInterleave = new InterleaveVDOM(childNodes[i]);
				this.appendChild(newInterleave);
				newInterleave.parseChildren();
				this._hasEmpty = false;
				break;
			case "empty":
				this.appendChild(new EmptyVDOM());
				this._hasEmpty = true;
				break;
			case "data":
				//donothing
				break;
			default:
				alert("Unknown/not-implemented RelaxNG element: " + childNodes[i].localName);
		}
	}
}

RefVDOM.prototype = new NodeVDOM();

function RefVDOM(node) {
	this.node = node;
	this.type = "RELAXNG_REF";
	this.nodeName = "RELAXNG_REF";
	this.name = node.getAttribute("name");
}

RefVDOM.prototype.isValid = function(ctxt) {
	var c = this.getFirstChild(ctxt);
	var b;
	if (c) {
		b = c.isValid(ctxt);
	}
//	debug (b);
	return b;
	/*if (!this.DefineVDOM.firstChild) {  
		ctxt.vdom = this;
		var ret = ctxt.nextVDOM(); 
		if (ret) {
			return ctxt.vdom.isValid(ctxt);
		} else {
			return false;
		}
	}*/
}	

NodeVDOM.prototype.getFirstChild = function (ctxt) {
	var firstChild = this.firstChild;
	if (firstChild && firstChild.nodeName == "RELAXNG_REF") {
		return firstChild.getFirstChild(ctxt);
	} 
	return firstChild;
}

RefVDOM.prototype.getFirstChild = function (ctxt) {
	var firstChild = this.DefineVDOM;
	if (firstChild && firstChild.firstChild) {
		ctxt.refs.push(this);
		return firstChild.getFirstChild(ctxt);
	} else {
		return this.getNextSibling(ctxt);
	}
}
NodeVDOM.prototype.getNextSibling = function(ctxt) {
	var nextSib = this.nextSibling;
	/*
	for (var i = 0; i < ctxt.refs.length; i++) {
		dump (".");
	}
	dump ("NodeName: " + this.nodeName + " " + this.name +  " Node: " + ctxt.node.nodeName);
	dump ("\n");*/
	if (!nextSib && this.parentNode && this.parentNode.nodeName == "RELAXNG_DEFINE") {
		return this.parentNode.getNextSibling(ctxt);
	} 
	
	if (nextSib) {
		if( nextSib.nodeName == "RELAXNG_REF") {
			nextSib = nextSib.getFirstChild(ctxt);
		} /*else if (nextSib.type == "RELAXNG_ATTRIBUTE") {
			nextSib = nextSib.getNextSibling(ctxt);
		}*/
		
	}
	return nextSib;
}


NodeVDOM.prototype.getParentNode = function(ctxt) {
	if (this.parentNode &&  this.parentNode.nodeName == "RELAXNG_DEFINE") {
		debug ("getParentNode" + this.parentNode.name);
		return this.parentNode.getParentNode(ctxt);
	}
	return this.parentNode;
}

RefVDOM.prototype.allowedElements = function(ctxt) {
	
	return this.DefineVDOM.allowedElements(ctxt);
}


DefineVDOM.prototype = new NodeVDOM();

DefineVDOM.prototype.allowedElements = function(ctxt) {
	var child = this.getFirstChild(ctxt);
	var ac = new Array();
	
	while (child) {
		var subac = child.allowedElements(ctxt);
		if (subac) {
			if (subac.nodeName) {
				ac.push(subac);
			} else if (subac) {
				for (var i = 0; i < subac.length; i++) {
					ac.push(subac[i]);
				}
			}
		}
		child = child.getNextSibling(ctxt);
	}
	
	return ac;
	
}

DefineVDOM.prototype.getNextSibling = function(ctxt) {


	if (ctxt.refs.length == 0) {
		debug ("	: " + ctxt.nr + "... 0");
		return null;
		
	} 
	var ref = ctxt.refs.pop();

	return ref.getNextSibling(ctxt);
}

DefineVDOM.prototype.getParentNode = function(ctxt) {
	return ctxt.refs.pop();
}

function DefineVDOM(node) {
	this.node = node;
	this.type = "RELAXNG_DEFINE";
	this.nodeName = "RELAXNG_DEFINE";
	this._attributes = new Array();
	this.name = node.getAttribute("name");
}

DefineVDOM.prototype.isValid = function(ctxt, RefVDOM) {
}

/*
DefineVDOM.prototype.__defineGetter__("nextSibling", 
	function() {
		debug("DefineVDOM.nextSibling "); 
		return null;
	}
)*/

ZeroOrMoreVDOM.prototype = new NodeVDOM();

function ZeroOrMoreVDOM(node) {
	this.node = node;
	this.type = "RELAXNG_ZEROORMORE";
	this.nodeName = "RELAXNG_ZEROORMORE";
}

ZeroOrMoreVDOM.prototype.isValid = function(ctxt) {
	var refsPosition = ctxt.refs.length;
	var child = this.getFirstChild(ctxt);

	while (child) {
		if (child.isValid(ctxt)) {
			ctxt.setVDOM(this, refsPosition);
			return true;
		}
		child = child.getNextSibling(ctxt);
	}
	
	ctxt.setVDOM(this, refsPosition);
	var vdom = ctxt.nextVDOM();
	if (vdom) {
		return vdom.isValid(ctxt); 
	} else { 
		return false;
	}
	
}

ZeroOrMoreVDOM.prototype.allowedElements = function(ctxt) {
	var child = this.getFirstChild(ctxt);
	var ac = new Array();
	try{
		while (child) {
			var subac = child.allowedElements(ctxt);
			if (subac) {
				if (subac.nodeName) {
					subac.optional = true;
					ac.push(subac);
				} else {
					for (var i = 0; i < subac.length; i++) {
						subac[i].optional = true;
						ac.push(subac[i]);
					}
				}
			}
			child = child.getNextSibling(ctxt);
		}
	} catch(e) { bxe_catch_alert(e); alert(child.nodeName + " " + subac); }
	return ac;
	
}

ChoiceVDOM.prototype = new NodeVDOM();

ChoiceVDOM.prototype.isValid = function(ctxt) {
	var refsPosition = ctxt.refs.length;
	var child = this.getFirstChild(ctxt);

	var hasEmpty = false;
	while (child) {
		if (child.type == "RELAXNG_EMPTY") {
			hasEmpty = true;
		}
		if (child.isValid(ctxt)) {
			//ctxt.setVDOM(child,refsPosition);
			return true;
		}
		child = child.getNextSibling(ctxt);
	}
	//this._attributes is like having an <empty/> element, meaning, the choice can be empty
	// as we check attributes in a different way, this is a safe assumption
	if (hasEmpty || this._attributes) {
		var vdom = ctxt.nextVDOM();
		if (vdom) {
			var v =  vdom.isValid(ctxt);
			if (v) {
				ctxt.setVDOM(this, refsPosition);
			}
			return v;
		}
	}
	return false;
}

function ChoiceVDOM(node) {
	this.node = node;
	this.type = "RELAXNG_CHOICE";
	this.nodeName = "RELAXNG_CHOICE";
}

InterleaveVDOM.prototype = new NodeVDOM();

InterleaveVDOM.prototype.isValid = function(ctxt) {
	var refsPosition = ctxt.refs.length;
	var child = this.getFirstChild(ctxt);
	var hasEmpty = false;
	while (child) {
		debug("Interleave.child: " + child.nodeName);
		if (child.isValid(ctxt)) {
			var ret = ctxt.next();
			if (ret == null) {
				return true;
			}
			ctxt.setVDOM(this, refsPosition);
			child = this.getFirstChild(ctxt);
			this.hit = true;
		}
		child = child.getNextSibling(ctxt);
	}
	ctxt.setVDOM(this, refsPosition);
	ctxt.nextVDOM();
	if (this.hit) {
		return true;
	} else {
		return false;
	}
}

InterleaveVDOM.prototype.allowedElements = function(ctxt) {
	try {
	var child = this.getFirstChild(ctxt);
	var ac = new Array();
	
	while (child) {
		var subac = child.allowedElements(ctxt);
		if (subac) {
			if (subac.nodeName) {
				ac.push(subac);
			} else {
				for (var i = 0; i < subac.length; i++) {
					ac.push(subac[i]);
				}
			}
		}
		child = child.getNextSibling(ctxt);
	}
	return ac;
	} catch(e) { bxe_catch_alert(e);}
}

function InterleaveVDOM(node) {
	this.node = node;
	this.type = "RELAXNG_INTERLEAVE";
	this.nodeName = "RELAXNG_INTERLEAVE";
}


EmptyVDOM.prototype = new NodeVDOM();

function EmptyVDOM(node) {
	this.node = node;
	this.type = "RELAXNG_EMPTY";
	this.nodeName = "RELAXNG_EMPTY";
}

EmptyVDOM.prototype.isValid  = function() {
	return false;
}

EmptyVDOM.prototype.allowedElements = function() {
	
	return null;
}
TextVDOM.prototype = new NodeVDOM();

function TextVDOM(node ) {
	this.node = node;
	this.type = "RELAXNG_TEXT";
	this.nodeName = "RELAXNG_TEXT";
	this.localName = "#text";
}

TextVDOM.prototype.isValid = function(ctxt) {
	//dump("TextVDOM.isValid :" + ctxt.node.data + ":\n");
	if (ctxt.node.nodeType == Node.TEXT_NODE) {
		return true;
	} else {
		return false;
	}
	
}

TextVDOM.prototype.allowedElements = function (ctxt){
	return {"nodeName": "#text", "namespaceURI": null, "localName": "#text", "nodeType": 3};
}

OneOrMoreVDOM.prototype = new NodeVDOM();

function OneOrMoreVDOM(node) {
	this.type = "RELAXNG_ONEORMORE";
	this.nodeName = "RELAXNG_ONEORMORE";
	this.node = node;
	this.hit = false;
}

OneOrMoreVDOM.prototype.isValid = function(ctxt) {
	var refsPosition = ctxt.refs.length;
	var child = this.getFirstChild(ctxt);
	var empty = false;
	while (child) {
		if (child.isValid(ctxt)) {
			this.hit = true;
			ctxt.setVDOM(this, refsPosition);
			return true;
		} 
		if (child.nodeName == "RELAXNG_EMPTY") {
			empty = true;
		}
		child = child.getNextSibling(ctxt);
	}
	ctxt.setVDOM(this, refsPosition);
	if (this.hit) {
		var vdom = ctxt.nextVDOM();
		if (vdom) {
			return vdom.isValid(ctxt); 
		} else { 
			return false;
		}
	}
	if (empty) {
		this.hit = true;
		return true;
	}
	return false;
}

ChoiceVDOM.prototype.allowedElements = function(ctxt) {
	var child = this.getFirstChild(ctxt);
	var ac = new Array();
	try{
		while (child) {
			var subac = child.allowedElements(ctxt);
			if (subac) {
				if (subac.nodeName) {
					if (this.optional) {
						subac.optional = true;
					}
					ac.push(subac);
				} else {
					for (var i = 0; i < subac.length; i++) {
						if (this.optional) {
							subac[i].optional = true;
						}
						ac.push(subac[i]);
					}
				}
			}
			child = child.getNextSibling(ctxt);
		}
	} catch(e) { bxe_catch_alert(e); alert(child.nodeName + " " + subac); }
	return ac;
	
}

OneOrMoreVDOM.prototype.allowedElements = function(ctxt) {
	var child = this.getFirstChild(ctxt);
	var ac = new Array();
	
	while (child) {
		var subac = child.allowedElements(ctxt);
		if (subac) {
			if (subac.nodeName) {
				if (this.optional) {
					subac.optional = true;
				}
				ac.push(subac);
			} else {
				for (var i = 0; i < subac.length; i++) {
					if (this.optional) {
						subac[i].optional = true;
					}
					ac.push(subac[i]);
				}
			}
		}
		child = child.getNextSibling(ctxt);
	}
	return ac;
	
}



ElementVDOM.prototype.allowedElements = function(ctxt) {
	var nodeName = "" ;
	if (this.prefix) {
		nodeName = this.prefix +":";
	}
	return {"nodeName":nodeName + this.localName, "namespaceURI": this.namespaceURI, "localName": this.localName, "vdom": this};
}


ElementVDOM.prototype.__defineSetter__("nodeName", function(name) {
	var html = true;
	if (html) {
		this._xmlnodeName = name;
	}
}
)

ElementVDOM.prototype.__defineGetter__("nodeName", function(name) {
	return this._xmlnodeName;
}
)
	
ElementVDOM.prototype.__defineGetter__("canHaveChildren", 
	function() {
		if (this._hasEmpty) {
		return !(this._hasEmpty);
		}
		return true;
	}
)
		


DocumentVDOM.prototype.getStructure = function() {
	
	
	 return "\n"+ this.getFirstChild(ctxt).getStructure();
}

XMLNode.prototype.__defineGetter__(
"isWhitespaceOnly",
function()
{
	if (this.nodeType == Node.TEXT_NODE) {
		if(/\S+/.test(this._node.nodeValue)) // any non white space visible characters
			return false;
		
		return true;
	} else {
		return false;
	}
}
);


