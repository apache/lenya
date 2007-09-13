/* ***** BEGIN LICENSE BLOCK *****
 * Licensed under Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * Full Terms at http://mozile.mozdev.org/license.html
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Playsophy code.
 *
 * The Initial Developer of the Original Code is Playsophy
 * Portions created by the Initial Developer are Copyright (C) 2002-2003
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK ***** */

// $Id: eDOM.js 1027 2004-12-05 20:57:20Z chregu $

/**********************************************************************************
 * eDOM.js V0.5: editor or extended DOM
 *
 * The basic w3c DOM is not honed for interactive editing. eDOM extends the standard DOM
 * to make it easy to build CSS-enabled XML and XHTML editing applications that work
 * within a browser. Initially it should support editing of XHTML in XHTML and XML pages 
 * within any W3c DOM supporting browser. Geiko-based browser like Mozilla are the initial 
 * target.
 *
 * When complete, eDOM will be a DOM module that facilities a wide variety of domain specific
 * and general purpose editors. It itself is not an editor: issues like Selection models
 * and toolbars are beyond its scope. It represents the routines needed in editors built
 * over the w3c DOM. First releases tackle text/object manipulation (editing and styling); 
 * later releases will address layout editing.
 * 
 * The initial target is basic XHTML editing. This requires some new methods for w3c DOM
 * objects and three new Objects:
 * - CSSRange: range content may be manipulated in various ways; CSS is one
 * way to restrict how it should be processed.
 * - InsertionPoint: allow you to iterate, examine and manipulate the visible characters 
 * of a document
 * - CSSLine: captures the set of elements that are rendered as a line on a screen
 *
 * POST05:
 * - all XHTML stuff out of here and into eDOMXHTML
 * - redo tighten Range methods to use lines and insertion points properly
 * - new restoration methods for insertion point and range (TextPointer is wasteful)
 * - ability to inline style text based on an inline element like EM or STRONG as opposed
 * to explicitly applying inline styles.
 * 
 **********************************************************************************/

/*********************************** eDOM version *********************************/

const eDOM_VERSION = "0.5";

/**
 * Is there support for the eDOM either as a whole or for a particular version.
 *
 * Use: if(document.supportsEDOM) or if(document.supportsEDOM && document.supportsEDOM("0.3"))
 *
 * @argument version if null then check if any version of eDOM is supported - otherwise check a particular version
 *
 * POST02: Would be much better to be able to add a feature dynamically to implementation.hasFeature. 
 * Don't see how to do this. Get security exceptions
 */
Document.prototype.supportsEDOM = function(version)
{
	if((version == eDOM_VERSION) || (version == ""))
		return true;
	return false;
}

/**
 * Removes all children of an Element
 */
Element.prototype.appendAllChildren = function(node) {
	var child = node.firstChild;
	while (child) {
		var oldchild = child;
		child = child.nextSibling;
		this.appendChild(oldchild);
	}
}

/**
 * Get content of all text nodes of an element (idea stolen from libxml2)
 */
Element.prototype.getContent = function() {
	var walker = document.createTreeWalker(
		this, NodeFilter.SHOW_TEXT,
		null, 
		this.ownerDocument);
	var returnString = "";
	var node = null;
	while( node = walker.nextNode() ) {
		returnString += node.nodeValue ;
	}
	return returnString;
}

 
/*********************************** Node/Element/Text/CSS ****************************
 * POST05: Range rework will remove last XHTML references and a number of the following
 * methods.
 * 
 *************************************************************************************/

/*
 * What is a node's offset within its parent?
 */
Node.prototype.__defineGetter__(
	'offset',
	function()
	{
		var parentNode = this.parentNode;

		var nextChild = parentNode.firstChild;
		var offset = 0;
		while(nextChild)
		{
			if(nextChild == this)
				return offset;

			offset++;
			nextChild=parentNode.childNodes[offset];
		}
	}
);

Node.prototype.__defineGetter__(
	'parentElement',
	function()
	{
		if((this.nodeType == Node.TEXT_NODE) || (this.contentType == Element.EMPTY_CONTENTTYPE))
			return this.parentNode;
		return this;
	}
);

Node.prototype.__defineGetter__(
	'topInlineAncestor',
	function()
	{
		var nodeToCheck = this.parentNode;
		if(document.defaultView.getComputedStyle(nodeToCheck, null).getPropertyValue("display") == "inline")
			return nodeToCheck.topInlineAncestor;
		return this;
	}
);

Node.prototype.hasOnlyInternalAttributes = function() {
	var attr = this.attributes;
	for (var i = 0; i < attr.length; i++) { 
		if (attr[i].nodeName.substr(0,5) != "_edom") {
			return false;
		}
	}
	return true;
}

Node.prototype.descendent = function(ancestor)
{
	var nodeParent = this.parentNode;
	while(nodeParent)	
	{
		if(nodeParent == ancestor)
			return true;
		nodeParent = nodeParent.parentNode;
	}
	return false;
}

// POST05: account for document.documentElement (ie/ top of document)
Node.prototype.__defineGetter__(
	"__nilParentElement",
	function()
	{
		var nilParent = this.parentElement;
		while(document.defaultView.getComputedStyle(nilParent, null).getPropertyValue("display") == "inline")
			nilParent = nilParent.parentNode;
		return nilParent;
	}
);

/**
 * POST05: 
 * - effects Range
 */
Node.prototype.insertParent = function(newParent)
{
	var currentParent = this.parentNode;
	newParent = currentParent.insertBefore(newParent, this);
	newParent.appendChild(this);
	return newParent;
}

/**
 * @returns the first previous sibling that is not an empty text node
 * 
 * POST04: need to change to use IPNodeFilter (get rid of nonEmpty check everywhere) - will need to account for
 * skip here! Careful - need to see if rework works in normalize etc
 */
Node.prototype.__defineGetter__(
	"__editablePreviousSibling",
	function()
	{
		var previousSibling = this.previousSibling;
		while(previousSibling)
		{
			if(__NodeFilter.nonEmptyText(previousSibling) == NodeFilter.FILTER_ACCEPT)
				return previousSibling;
			previousSibling = previousSibling.previousSibling;
		}
		return null;
	}
);

/**
 * Get the next editable sibling of a node. An editable sibling is one with at least one non-empty text node as
 * a decendent.
 * 
 * POST04: need to change to use IPNodeFilter
 *
 * @returns non empty node after this one or null if there isn't one
 */
Node.prototype.__defineGetter__(
	"__editableNextSibling",
	function()
	{
		var nextSibling = this.nextSibling;
		while(nextSibling)
		{
			if(__NodeFilter.nonEmptyText(nextSibling) == NodeFilter.FILTER_ACCEPT)
				return nextSibling;
			nextSibling = nextSibling.nextSibling;
		}
		return null;
	}
);

/**
 * @returns the first previous sibling that is not an empty text node
 * 
 * POST04: need to change to use IPNodeFilter (get rid of nonEmpty check everywhere) - will need to account for
 * skip here! Careful - need to see if rework works in normalize etc
 *
 * Could use contentType for Element but need to dig deeper if contentType is "_ELEMENT". If ANY then skip element.
 */
Node.prototype.__defineGetter__(
	"editablePreviousSibling",
	function()
	{
		var previousSibling = this.previousSibling;
		while(previousSibling)
		{
			if(__NodeFilter.nonEmptyText(previousSibling) == NodeFilter.FILTER_ACCEPT)
				return previousSibling;
			previousSibling = previousSibling.previousSibling;
		}
		return null;
	}
);

/**
 * Get the next editable sibling of a node. An editable sibling is one with at least one non-empty text node as
 * a decendent.
 * 
 * POST04: need to change to use IPNodeFilter
 *
 * @returns non empty node after this one or null if there isn't one
 */
Node.prototype.__defineGetter__(
	"editableNextSibling",
	function()
	{
		var nextSibling = this.nextSibling;
		while(nextSibling)
		{
			if(__NodeFilter.nonEmptyText(nextSibling) == NodeFilter.FILTER_ACCEPT)
				return nextSibling;
			nextSibling = nextSibling.nextSibling;
		}
		return null;
	}
);

/**
 * Removes all children of an Element
 */ 
Element.prototype.removeAllChildren = function() 
{
	var child = this.firstChild;        
	while (child) {
		var oldchild = child;
		child = child.nextSibling;
		this.removeChild(oldchild);        
	}
}

/**
 * Peer of insertBefore
 * 
 * @argument elementToInsert
 * @argument child
 */
Element.prototype.insertAfter = function(elementToInsert, child)
{
	if(this.lastChild == child) {
		return this.appendChild(elementToInsert);
	}
	else
		return this.insertBefore(elementToInsert, child.nextSibling);
}

/**
 * Split the element in two with some children staying with this element and others going to a new 
 * sibling. Split won't happen if offset points before the first or after the last editable child 
 * of the element.
 *
 * @argument offset this element's index in its parent's child list
 *
 * @returns new element inserted after this element in the tree or null if no split happens
 */
Element.prototype.split = function(offset)
{
	// don't split if offset is after the last child!
	if(this.childNodes.length <= offset)
		return null;

	// if no previous node to leave in existing container then can't split container
	// in other words: ul|li
	if(!this.childNodes[offset].__editablePreviousSibling)
		return null;

	// won't get here unless there is a previous sibling so this check can see if the element before this
	// one has a next sibling. This catches situations where you are beyond the end of a list ie/
	// li|</ul>. It will check if li.__editableNextSibling which it won't!
	if(!this.childNodes[offset-1].__editableNextSibling)
		return null;

	var newElement = this.cloneNode(false);
	this.parentNode.insertAfter(newElement, this);
	var child;
	while(child = this.childNodes[offset])
	{
		newElement.appendChild(child);
	}
	return newElement;
}

Element.prototype.replaceChildOnly = function(child, newChildName)
{
	var newElement = documentCreateXHTMLElement(newChildName);
	// copy attributes of this element
	for(var i=0; i<child.attributes.length; i++)
	{
		var childAttribute = child.attributes.item(i);
		var childAttributeCopy = childAttribute.cloneNode(true);
		newElement.setAttributeNode(childAttributeCopy);
	}
	var childContents = document.createRange();
	childContents.selectNodeContents(child);

	newElement.appendChild(childContents.extractContents());
	childContents.detach();
	this.replaceChild(newElement, child);

	return newElement;
}

Element.prototype.removeChildOnly = function(oldChild)
{
	if(oldChild.childNodes.length > 0)
	{
		var childContents = document.createRange();
		childContents.selectNodeContents(oldChild);
		this.insertBefore(childContents.extractContents(), oldChild);
	}

	return this.removeChild(oldChild); 
}

/**
 * Note: this will insert a token to allow selection if need be
 */
Element.prototype.firstInsertionPoint = function(top)
{
	// special case: first insertion point in an empty element is right after that element
	if(this.contentType == Element.EMPTY_CONTENTTYPE)
		return __createInsertionPoint(this.top, this.parentNode, (this.offset+1));

	var __ipni = new __IPNodeIterator(this);
	if(__ipni.currentNode)
	{
		var firstip = (__ipni.currentNode.nodeType == Node.TEXT_NODE) ? __createInsertionPoint(top, __ipni.currentNode, 0) : __createInsertionPoint(top, __ipni.currentNode.parentNode, __ipni.currentNode.offset);
		return firstip;
	}	

	// we make one - add NBSP
	var textNode = document.createTextNode(STRING_NBSP);
	this.appendChild(textNode);
	var onlyip = __createInsertionPoint(top, textNode, 0);
	return onlyip;
}

Element.prototype.lastInsertionPoint = function(top)
{
	var __ipni = new __IPNodeIterator(this);
	__ipni.setToEnd();
	if(__ipni.currentNode)
	{
		var lastip = (__ipni.currentNode.nodeType == Node.TEXT_NODE) ? __createInsertionPoint(top, __ipni.currentNode, __ipni.currentNode.nodeValue.length) : __createInsertionPoint(top, __ipni.currentNode.parentNode, (__ipni.currentNode.offset+1));
		return lastip;
	}	

	// we make one - add NBSP
	var textNode = document.createTextNode(STRING_NBSP);
	this.appendChild(textNode);
	var onlyip = __createInsertionPoint(top, textNode, 0);
	return onlyip;
}

/*
 * POST04: 
 * - account for "class" as well as style (treat as generic enum attr where order
 * doesn't matter)
 * - account for "display": never match table columns
 * - xml: do full attribute matching
 * - xml: style doesn't come from a style method
 */
Element.prototype.match = function(node)
{
	if(!node)
		return false;

	if(this.nodeType != node.ELEMENT_NODE)
		return false;

	if(this.nodeName.toLowerCase() != node.nodeName.toLowerCase())
		return false;

	if(this.attributes.length != node.attributes.length)
		return false;

	for(var i=0; i<this.attributes.length; i++)
	{
		var thisAttribute = this.attributes.item(i);
		var nodeAttribute = node.attributes.getNamedItem(thisAttribute.nodeName);
		if(!nodeAttribute)
			return false;

		// do literal string compare on all but style attribute. This may not work with
		// XML which may have other attributes like style. Need more sophisticated attribute
		// match handling.

		// POST04: this may not work for XML. Seem to remember not having style explicitly.
		if(nodeAttribute.nodeName == "style")
		{
			if(!node.style.match(this.style))	
				return false;
		}
		// assume all but style need to be exactly the same!	
		else if(nodeAttribute.nodeValue != thisAttribute.nodeValue)
		{
			return false;
		}		
	}	

	return true;
}

/**
 * Set the value of style
 *
 * POST04:
 * - distinguish inline and block level styles ...
 * - may move most of this into "CSSLine.setStyle": list all styles, see if any integer styles for inline
 * - needs to use style property meta; now only works for integer value styles
 * [meta: name, displays, int or enum or ..., units (if int) etc
 * - text justify: CSSLine ... text-align (justify is default?)
 */
Element.prototype.setStyle = function(styleName, styleValue)
{
	var intStyleRegExp = /(\D?)(\d+)(.*)/i;
	var regResult = intStyleRegExp.exec(styleValue);

	// non integer style - just set the value for now
	if(!regResult)
	{
		this.style.setProperty(styleName, styleValue, "");
		return;
	}

	// integer style from here on ...
	var plusOrMinus = regResult[1];
	var intVal = parseInt(regResult[2]);
	var units = regResult[3];

	// style attribute value is a string (this.attributes.getNamedItem("style").value). This is far from ideal
	// for easy setting: basically reduces style manip to string matching. For now use, HTML style attribute
	// which gives a CSSDeclaration ... Not ideal as doesn't work for XML.

	// shouldn't this come from computedValue? POST04 ie/ factor in inherited offsets ...
	var currentStyleValue = parseInt(this.style.getPropertyValue(styleName));
	if(isNaN(currentStyleValue))
	{
		if(plusOrMinus != "-") // for now, don't allow for - values
			this.style.setProperty(styleName, intVal+units, "");
		return;
	}
	
	if(plusOrMinus == "+") // increment
		currentStyleValue += intVal;
	else if(plusOrMinus == "-") // decrement
	{
		// for now, allow no negatives!
		if(currentStyleValue <= intVal)
		{
			this.style.removeProperty(styleName);
			// if style is now empty, remove it!
			if(this.style.length == 0) // nix style if no other style setting!
				this.attributes.removeNamedItem("style");
			return;
		}

		currentStyleValue -= intVal;		
	}

	this.style.setProperty(styleName, currentStyleValue + units, "");
}
/**
 * Does a node have a particular name - case insensitive
 * 
 * POST05: make into accessor giving back lower case name every time
 */
Element.prototype.nodeNamed = function(nodeName)
{
	return(this.nodeName.toLowerCase() == nodeName.toLowerCase());
}

/*
 * POST04: need to account for synonyms and complex css values
 */
CSSStyleDeclaration.prototype.match = function(declToMatch)
{
	if(this.length != declToMatch.length)
		return false;

	for(var j=0; j<this.length; j++)
	{
		var aStyleName = this.item(j);
		var bStyleValue = declToMatch.getPropertyValue(aStyleName);
		var aStyleValue = this.getPropertyValue(aStyleName);
		if(aStyleValue != bStyleValue)
		{
			return false;
		}				
	}

	return true;
}

/************************************ InsertionPoint *********************************************
 * An IP or Insertion Point node is:
 * - a point within a visible text node
 * - on or right after an element node of EMPTY_CONTENTTYPE. The element can have inline or block
 * display
 *
 * Text:
 * -----
 * Text rendering varies according to the handling of whitespace. All flavors of XML distinguish
 * between whitespace that displays and whitespace that makes page markup more presentable.
 * 
 * As the XML standard says: "In editing XML documents, it is often convenient to use 
 * "white space" (spaces, tabs, and blank lines) to set apart the markup for greater 
 * readability. Such white space is typically not intended for inclusion in the delivered 
 * version of the document. On the other hand, "significant" white space that should be 
 * preserved in the delivered version is common, for example in poetry and source code."
 * eDOM must traverse the rendered whitespace based on the actual whitespace in the "raw"
 * text.
 * - handle whitespace = pre: http://www.w3.org/TR/html4/struct/text.html#h-9.3.4 
 * - XML: http://www.w3.org/TR/2000/REC-xml-20001006#sec-white-space
 * - XHTML: http://www.w3.org/TR/xhtml1/#uacon
 *
 * eDOM's whitespace handling is based on the CSS "white-space" property. InsertionPoint skips 
 * invisible whitespace.
 * 
 * EMPTY Elements:
 * ---------------
 * Empty elements such as xhtml:img or xhtml:hr may display as blocks or within lines. In both cases,
 * you may wish to insert text or other empty elements before or after them - in other words, there are
 * insertion points immediately before or after these elements.
 *
 * Tokens:
 * -------
 * Geiko-based browsers don't display childless, content bearing elements. 
 * eDOM supports the concept of an "InsertionPoint Token" (IPT) whose presense makes Geiko show
 * an element and for eDOM marks that insertion is allowed within an otherwise empty. 
 * The first inserted item will replace and opposed to adding to the token. 
 *
 * Valid tokens are:
 * - an XHTML BR (invisible element) in a line with nothing other than whitespace [TODO]
 * - an NBSP on its own in a line
 *
 * When laying out a page a user can choose to put nothing but tokens in various editable elements.
 *
 * New lines, same line and at top:
 * --------------------------------
 * InsertionPoint's forward and back methods return whether they keep the point within a line or
 * move it to a new line or remain in the same position because the point is now at its top boundary.
 *
 * "Top" is important: it is used to restrict a pointer within an area so that editing will only take
 * place within that area and that the area remains editable irrespective of what operations are performed
 * on it through eDOM.
 *
 * POST05: 
 * - consider rename CROSSED_BLOCK to CROSSED_LINE or "NEW_LINE"
 * - add replaceCharacter (insert mode)
 * - iptoken (expand to cover BR etc)
 * - consider getters for "endOfContainer", "startOfContainer" to cover whether an IP starts
 * or ends its inline or block level container
 ************************************************************************************************/

InsertionPoint.SAME_LINE = 0;
InsertionPoint.CROSSED_BLOCK = 1;
InsertionPoint.AT_TOP = 2;

Document.prototype.createInsertionPoint = documentCreateInsertionPoint;

/** 
 * Only three types of valid seed: 
 * - non whitespace only text node (nbsp is ok)
 * - parent and offset that points to an empty (block or inline) element node
 * - parent and offset that points immediately AFTER an empty (block or inline) element node
 * 
 * Creation corrects the default IP for five different whitespace issues:
 * - middle of collapsed midline whitespace
 * - within or before the whitespace of a start of line non whitespace text node that begins with whitespace
 * - within or after the whitespace of an end of line non whitespace text node that ends with whitespace
 * - a whitespace only text node that starts a line (skip after)
 * - a whitespace only text node that ends a line (skip before)
 *
 * POST05:
 * - check for seed validity (move up from Range.firstInsertionPoint)
 */
function documentCreateInsertionPoint(top, seed, seedOffset)
{	
	// a/c for special case of a reference to a text node rather than a reference within it
	if((seed.nodeType == Node.ELEMENT_NODE) && (seed.childNodes.length > seedOffset) && (seed.childNodes[seedOffset].nodeType == Node.TEXT_NODE))
	{
		seed = seed.childNodes[seedOffset];
		seedOffset = 0;	
	}

	var ip = __createInsertionPoint(top, seed, seedOffset);

	// can't be within or before whitespace at the start of a line; within whitespace within the line; or 
 	// within or after whitespace at the end of a line
	if((ip.__cssWhitespace != "pre") && ip.whitespace)
	{
		// go to start of whitespace sequence or token (same_line) or end of previous block (crossed_block) or stay where you are (at_top)
		var result = ip.backOne();

		// special case: whitespace only text node that COULD BE at the end of the line. Jump after previous valid ip
		if((result == InsertionPoint.SAME_LINE) && ip.whitespace && (ip.ipOffset == 0) && ip.__ipNode.isWhitespaceOnly)
		{
			ip.__backOne();
			ip.__ipOffset++;
			return ip;	
		}

		// three cases where must move forward:
		// - same_line && not whitespace but also not IPToken: move back to whitespace!
		// - at_top: didn't move so move forward as whitespace must be collapsed at block start
		// - crossed_block: move forward to valid character after seed whitespace
		if(((result == InsertionPoint.SAME_LINE) && !ip.whitespace && !ip.IPToken) ||
		   (result == InsertionPoint.AT_TOP) ||
		   (result == InsertionPoint.CROSSED_BLOCK))
			ip.forwardOne();
	}	
	
	return ip;
}

/*
 * Simple version of creation that doesn't check if the seed and seedOffset are valid. This is used for basic 
 * creation, forward and back testing.
 */
function __createInsertionPoint(top, seed, seedOffset)
{
	var ip = new InsertionPoint(top, seed, seedOffset, null, document.defaultView.getComputedStyle(seed.parentNode, null).getPropertyValue("white-space"));
	return ip;
}

/*
 * POST04: need to accept selection of inline isolated whitespace
 */
function InsertionPoint(top, seed, seedOffset, cw, csswsp)
{
	this.__top = top;
	this.__ipNode = seed;
	this.__ipOffset = seedOffset;
	this.__cw = cw;
	this.__cssWhitespace = csswsp;
}

InsertionPoint.prototype.__defineGetter__(
	"top",
	function() {return this.__top;}
);

InsertionPoint.prototype.__defineGetter__(
	"cssWhitespace",
	function() {return this.__cssWhitespace;}
);

InsertionPoint.prototype.__defineGetter__(
	"ipNode",
	function() {return this.__ipNode;}
);

InsertionPoint.prototype.__defineGetter__(
	"ipOffset",
	function() {return this.__ipOffset;}
);

InsertionPoint.prototype.__defineSetter__(
	"ipOffset",
	function(value) {this.__ipOffset = value;}
);

/**
 * Get the offset of an insertion point with its line
 */
InsertionPoint.prototype.__defineGetter__(
	"lineOffset",
	function()
	{
		var line = this.line;
		var ipForCount = line.firstInsertionPoint;
		var count = 0;
		//prevent endless loop in rare cases... with count < 10
		while(!ipForCount.equivalent(this) && count < 10)
		{
			count++;
			ipForCount.forwardOne();		
		}
		return count;
	}
);

/**
 * Return node referenced
 */ 
InsertionPoint.prototype.__defineGetter__(
	"ipReferencedNode",
	function()
	{
		if(this.__ipNode.nodeType == Node.TEXT_NODE)
			return this.__ipNode;
		
		// element reference: either right before or right after an element
		var offsetToUse = this.__ipOffset;
		if((this.__ipOffset == this.__ipNode.childNodes.length) || (this.__ipNode.childNodes[this.__ipOffset].nodeType == Node.TEXT_NODE))
			offsetToUse = this.__ipOffset-1;

		return this.__ipNode.childNodes[offsetToUse];
	}
);

/**
 * Does the insertion point point to a whitespace?
 *
 * http://www.w3.org/TR/REC-CSS2/text.html#white-space-prop
 * "space" (Unicode code 32), "tab" (9), "line feed" (10), "carriage return" (13), and "form feed" (12)
 *
 * Note that this presumes white-space:pre
 */
const SPCHARS = "\f\n\r\t\u0020\u2028\u2029"; // NOTE: no nbsp - \u00A0 - consider rename to normalSpace

InsertionPoint.prototype.__defineGetter__(
	"whitespace",
	function()
	{
		var character = this.character;

		if(character == null) // means on element node or at end of text node followed by element node
			return false;

		if(character == "") // means end of text node at end of line
			return true;

		return(SPCHARS.indexOf(character) != -1);
	}
);

InsertionPoint.prototype.__defineGetter__(
	"startOfLine",
	function()
	{
		var previp = this.clone();
		var prevResult = previp.backOne();
		if(prevResult != InsertionPoint.SAME_LINE)
			return true;
		return false;
	}
);

InsertionPoint.prototype.__defineGetter__(
	"endOfLine",
	function()
	{
		var nextip = this.clone();
		var nextResult = nextip.forwardOne();
		if(nextResult != InsertionPoint.SAME_LINE)
			return true;
		return false;
	}
);

/**
 * IP tokens serve as the only non whitespace children of otherwise "ANY_CONTENTTTYPE" Elements. Without the token, Geiko
 * wouldn't render these nodes properly and an editor wouldn't know that it is valid to enter text within them. An editor
 * should replace a token when inserting content rather than just appending content around it.
 *
 * For XHTML, a IP token is an empty BR element OR an NBSP-only text node that are the only elements in their line.
 * 
 * POST05:
 * - add support for BR (move into eDOMHTML?) ie/ override this method with an equivalent ie/ copy its contents (specialization?)
 */
InsertionPoint.prototype.__defineGetter__(
	"IPToken",
	function()
	{
		// first case: NBSP on its own: POST05 - move this down to line as shouldn't presume what a token line is
		if((this.__ipNode.nodeType == Node.TEXT_NODE) && 
		   (this.__ipOffset < this.__ipNode.nodeValue.length) &&
		   (this.__ipNode.nodeValue.charAt(this.__ipOffset) == STRING_NBSP))
		{
			// See if more than this nbsp in the line (ie/ match non-nbsp whitespace) - if not then go before the NBSP
			var line = __createCSSLineFromNonBlockIP(this, false);
			if(line.tokenLine)
				return true;
		}
		return false;
	}
);

/**
 * Return line that insertion point is in
 */
InsertionPoint.prototype.__defineGetter__(
	"line",
	function()
	{
		return documentCreateCSSLine(this);
	}
);

/**
 * What character is at the point. Three cases:
 * - character at offset in referenced text node
 * - empty string "" if references the end of a text node that ends a line
 * - null if "character" is or is effectively an element
 *
 * Note: this assumes white-space of pre so end of line whitespace will be treated as valid characters rather
 * than as invisible collapsed markup
 */
InsertionPoint.prototype.__defineGetter__(
	"character",
	function()
	{
		// may be premature: could have text node ahead
		if(this.__ipNode.nodeType == Node.ELEMENT_NODE)
			return null;

		if(this.__ipOffset < this.__ipNode.length)
			return this.__ipNode.nodeValue.charAt(this.__ipOffset);

		var forwardip = this.clone();

		var result = forwardip.__forwardOne();

		var returnValue = ""; // at end of text node and forward is crossed_block or at_top then ""

		if(result == InsertionPoint.SAME_LINE)
		{
			if(forwardip.__ipNode.nodeType == Node.ELEMENT_NODE)
				return null;

			returnValue = forwardip.__ipNode.nodeValue.charAt(0);
		}

		return returnValue;
	}
);

/**
 * One ip may directly reference a point in a text node while another may reference before or after a block or inline
 * empty element. They are equivalent if inserting text at this point leads to the same outcome.
 */
InsertionPoint.prototype.equivalent = function(ipToTest)
{
	if(this.ipNode == ipToTest.ipNode) 
	{
		if(this.ipOffset == ipToTest.ipOffset)
			return true;
		return false;
	}
	
	// this refers to a text node: ipToTest has element offset
	if((this.ipNode.nodeType == Node.TEXT_NODE) && (ipToTest.ipNode.nodeType == Node.ELEMENT_NODE))
		return __elementReferenceEqualsTextReference(ipToTest.ipNode, ipToTest.ipOffset, this.ipNode, this.ipOffset);

	// this refers to element node: ipTo
	if((this.ipNode.nodeType == Node.ELEMENT_NODE) && (ipToTest.ipNode.nodeType == Node.TEXT_NODE))
		return __elementReferenceEqualsTextReference(this.ipNode, this.ipOffset, ipToTest.ipNode, ipToTest.ipOffset);

	return false;
}

function __elementReferenceEqualsTextReference(el, elOffset, text, textOffset)
{
	// all comparisons must be against top inline ancestor: a/cs for Text and for <span>Text</span>
	var textILAncestor = text.topInlineAncestor;

	// element must be block ancestor of text node for an equivalent reference
	if(textILAncestor.parentNode != el)
		return false;

	// text ref points to end of text node and element reference points after text node
	if((textOffset == text.nodeValue.length) && (elOffset == (textILAncestor.offset + 1)))
		return true;

	// text ref points to start of text node and element reference points to text node
	if((textOffset == 0) && (elOffset == textILAncestor.offset))
		return true;

	return false;
}

InsertionPoint.prototype.clone = function()
{
	var clone = new InsertionPoint(this.__top, this.__ipNode, this.__ipOffset, this.__cw, this.__cssWhitespace);
	return clone;
}

InsertionPoint.prototype.set = function(ip)
{
	this.__top = ip.__top;
	this.__ipNode = ip.__ipNode;
	this.__ipOffset = ip.__ipOffset;
	this.__cw = ip.__cw;
	this.__cssWhitespace = ip.__cssWhitespace;
}

/**
 * @returns the table cell element that contains this line or that is an ancestor of this line's container or null
 * if there is no such element.
 */
InsertionPoint.prototype.__defineGetter__(
	"tableCellAncestor",
	function()
	{
		var nodeToTest = this.ipNode.parentElement;
		while(nodeToTest != this.top)
		{
			if(document.defaultView.getComputedStyle(nodeToTest, null).getPropertyValue("display") == "table-cell")
				return nodeToTest;
			nodeToTest = nodeToTest.parentNode;				
		}
		return null;
	}
);

InsertionPoint.prototype.setToStart = function()
{
	var fip = this.__top.firstInsertionPoint(this.__top);
	if(fip)
		this.set(fip);
}

InsertionPoint.prototype.__backOne = function()
{
	var currentNode;

	// special case 1: within a text node
	if(this.__ipNode.nodeType == Node.TEXT_NODE)
	{
		// in text node and not at the start then move back one in that node: assumption "pre" value won't change!
		if(this.__ipOffset > 0)
		{
			this.__ipOffset--;
			return InsertionPoint.SAME_LINE;
		}
		currentNode = this.__ipNode;
	}
	// special case 2: beyond last child (an empty element) in a container
	else if(this.__ipNode.childNodes.length == this.__ipOffset)
	{
		this.__ipOffset--;
		if(document.defaultView.getComputedStyle(this.__ipNode.childNodes[this.__ipOffset], null).getPropertyValue("display") == "inline")
			return InsertionPoint.SAME_LINE;		
		else // need to go to text node before empty element if can!
		{
			var ipni = new __IPNodeIterator(this.__top);
			ipni.currentNode = this.__ipNode.childNodes[this.__ipOffset];
			var ipniResult = ipni.previousNode();
			if(!ipniResult && ipni.currentNode && (ipni.currentNode.nodeType == Node.TEXT_NODE))
			{
				this.__ipNode = ipni.currentNode;
				this.__ipOffset = ipni.currentNode.nodeValue.length;
			}
			return InsertionPoint.CROSSED_BLOCK;
		}
	}
	else
		currentNode = this.__ipNode.childNodes[this.__ipOffset];

	// Must move back to previous editable node	
	var ipni = new __IPNodeIterator(this.__top);

	ipni.currentNode = currentNode;
		
	var ipniBlockCrossed = ipni.previousNode();

	if(ipni.currentNode == null) // at top - no change to IPTYPE
		return InsertionPoint.AT_TOP

	// may have crossed to new parent: reset cssWhitespace setting
	this.__cssWhitespace = document.defaultView.getComputedStyle(ipni.currentNode.parentNode, null).getPropertyValue("white-space");

	if(ipni.currentNode.nodeType == Node.TEXT_NODE)
	{
		this.__ipNode = ipni.currentNode;
		this.__ipOffset = this.__ipNode.nodeValue.length; 

		if(ipniBlockCrossed)
			return InsertionPoint.CROSSED_BLOCK;

		this.__ipOffset--; // back one if same line

		return InsertionPoint.SAME_LINE;
	}
	
	// currentNode is empty element
	this.__ipNode = ipni.currentNode.parentNode;

	if(ipniBlockCrossed)
	{
		// special case: if block level node is the empty element then jump before it
		if(ipniBlockCrossed == ipni.currentNode)
		{
			// special case: need to reach ahead into a text node if ipniResult and it is the current node. That may
			// expose whitespace and we don't want that
			currentNode = ipni.currentNode;
			ipniResult = ipni.previousNode();
			if(!ipniResult && ipni.currentNode && (ipni.currentNode.nodeType == Node.TEXT_NODE))
			{
				this.__ipNode = ipni.currentNode;
				this.__ipOffset = ipni.currentNode.nodeValue.length;
			}
			else
				this.__ipOffset = currentNode.offset;
		}
		else		
			// if block crossed and now on a different empty element then go after it
			this.__ipOffset = ipni.currentNode.offset + 1; // after empty element - default is block

		return InsertionPoint.CROSSED_BLOCK;
	}

	this.__ipOffset = ipni.currentNode.offset;
	return InsertionPoint.SAME_LINE;
}

/**
 * Assume the InsertionPoint is created in a valid position.
 *
 * POST05:
 * - account for linefeed right before a tag. May actually do in "__backOne"
 */
InsertionPoint.prototype.backOne = function()
{
	var origip = this.clone(); // jump back here if only whitespace between current position and top

	var result = this.__backOne();

	if(result == InsertionPoint.AT_TOP)
		return InsertionPoint.AT_TOP;

	// Collapsed whitespace: MAY have to jump back if in a non pre text node, either at its end or on a whitespace
	if((this.__ipNode.nodeType == Node.TEXT_NODE) && (this.__cssWhitespace != "pre") &&
	   ((this.__ipNode.nodeValue.length == this.__ipOffset) ||
	    (SPCHARS.indexOf(this.__ipNode.nodeValue.charAt(this.__ipOffset)) != -1)))
	{			
		// Now on whitespace in TEXT_NODE: go back until get to non whitespace
		var backip = this.clone();
		var firstwspip;
		var resultBack;
		do
		{
			firstwspip = backip.clone();

			resultBack = backip.__backOne();

			if(resultBack != InsertionPoint.SAME_LINE)
				result = resultBack;

			if(resultBack == InsertionPoint.AT_TOP)
				break;
		}
		// text node and not pre and whitespace (end of text node or literally a whitespace character)
		while((backip.__ipNode.nodeType == Node.TEXT_NODE) && (backip.__cssWhitespace != "pre") &&
		      ((backip.__ipNode.nodeValue.length == backip.__ipOffset) ||
		       (SPCHARS.indexOf(backip.__ipNode.nodeValue.charAt(backip.__ipOffset)) != -1)))	

		if(result == InsertionPoint.CROSSED_BLOCK) // CROSSED_BLOCK: adopt when backcp takes you
		{
			// Keep the place that backcp went to
			this.set(backip);
			
			// Jump right after the non whitespace unless already there (resultBack is CROSSED_BLOCK if line ends in Element)
			// or this now points to an editable token
			if(!((resultBack == InsertionPoint.CROSSED_BLOCK) || this.IPToken))
				this.__ipOffset++;
		}
		else if(result == InsertionPoint.AT_TOP) // AT_TOP: return to original cp position
			this.set(origip);
		else // SAME_LINE: go back to the first whitespace in the sequence
		{
			if(backip.IPToken) // only for create in wrong place!
				this.set(backip);
			else
				this.set(firstwspip);
		}
	}
	return result;
}

/**
 * Move forward: follows behavior of white-space:pre
 */
InsertionPoint.prototype.__forwardOne = function()
{
	if(this.__ipNode.nodeType == Node.TEXT_NODE)
	{
		if(this.__ipOffset < this.__ipNode.length)
		{
			this.__ipOffset++;
			return InsertionPoint.SAME_LINE;
		}
	}

	var currentNode = this.__ipNode; // text node

	// if current reference is to or beyond an empty element then set iterator appropriately
	if(currentNode.nodeType == Node.ELEMENT_NODE)
	{
		if(this.__ipOffset > 0)
			currentNode = this.__ipNode.childNodes[this.__ipOffset-1];
	}

	// Must move forward to next editable node	
	var ipni = new __IPNodeIterator(this.__top);

	ipni.currentNode = currentNode;

	var ipniResult = ipni.nextNode();

	if(ipni.currentNode == null) // at top!
		return InsertionPoint.AT_TOP;

	// may have crossed to new parent: reset cssWhitespace setting
	this.__cssWhitespace = document.defaultView.getComputedStyle(ipni.currentNode.parentNode, null).getPropertyValue("white-space");
		
	if(ipni.currentNode.nodeType == Node.TEXT_NODE)
	{
		this.__ipNode = ipni.currentNode;
		if(ipniResult)
		{
			this.__ipOffset = 0; 
			return InsertionPoint.CROSSED_BLOCK;
		}
		this.__ipOffset = 1;
		return InsertionPoint.SAME_LINE;
	}
	
	// empty (always inline if get this far) element
	var currentNode = ipni.currentNode;
	this.__ipNode = currentNode.parentNode;

	// we've crossed to a new line
	if(ipniResult)
	{
		// if new line starts with an empty element ie/ block crossed is the empty element
		if(ipniResult == ipni.currentNode)
		{
			// special case: need to reach ahead into a text node if ipniResult and it is the current node. That may
			// expose whitespace and we don't want that
			ipniResult = ipni.nextNode();
			if(!ipniResult && ipni.currentNode && (ipni.currentNode.nodeType == Node.TEXT_NODE))
			{
				this.__ipNode = ipni.currentNode;
				this.__ipOffset = 0;
			}
			else
				this.__ipOffset = currentNode.offset + 1;
		}
		else
			this.__ipOffset = currentNode.offset;
		return InsertionPoint.CROSSED_BLOCK;
	}		

	this.__ipOffset = ipni.currentNode.offset + 1;

	return InsertionPoint.SAME_LINE;
}

/**
 * Accounts for display setting of the block, top and the whitespace property. The whitespace property is testimont
 * to hand coding. As you can style lines properly with tagging, the need to accept text formatting characters as
 * more than whitespace is a throwback to hand coded HTML and limited ability to style tagged text.
 *
 * POST05:
 * - add skips for newlines right after or before any tag (http://www.w3.org/TR/REC-html40/appendix/notes.html#notes-line-breaks)
 */
InsertionPoint.prototype.forwardOne = function()
{
	var origip = this.clone(); // jump back here if only whitespace between current position and top

	// Special case: skip any editable token completely and move to the next line or stay put if at top
	if(origip.IPToken)
	{
		var result;
		do {
			result = this.__forwardOne();
		}
		while(result == InsertionPoint.SAME_LINE)

		if(result == InsertionPoint.AT_TOP) // revert to original position if AT_TOP
			this.set(origip);
		return result;
	}

	var result = this.__forwardOne();

	// at top - no need to chase whitespace
	if(result == InsertionPoint.AT_TOP)
		return result;

	// Collapsed whitespace: if now on a non pre text node on whitespace and either original was whitespace OR
	// original was after an empty element that isn't succeeded by another element then must eat whitespace 
 	// and get to a non whitespace character or element
	if(((this.__cssWhitespace != "pre") && this.whitespace) &&
	   (origip.whitespace || (result == InsertionPoint.CROSSED_BLOCK) ||
            ((origip.__ipNode.nodeType == Node.ELEMENT_NODE) && ((origip.__ipNode.childNodes.length == origip.__ipOffset) || (origip.__ipNode.childNodes[origip.__ipOffset].nodeType != Node.ELEMENT_NODE)))))
 	{
		var resultforward;	
		do
		{
			resultForward = this.__forwardOne();

			if(resultForward != InsertionPoint.SAME_LINE)
				result = resultForward;
		}
		while((this.__cssWhitespace != "pre") && this.whitespace && (result != InsertionPoint.AT_TOP))

		// AT_TOP: return to original cp position
		if(result == InsertionPoint.AT_TOP) 
			this.set(origip);
		else if(result == InsertionPoint.CROSSED_BLOCK)
		{
			// Hate this but special case: leading collapsed whitespace of a line you cross into
			if((this.__cssWhitespace != "pre") && 
		           (this.__ipNode.nodeType == Node.TEXT_NODE) && 
			   (this.__ipNode.nodeValue.length == this.__ipOffset))
			{
				// move forward one and then reduce offset by 1
				this.__forwardOne();
				this.__ipOffset--;
			}
		}
	}

	return result;
}

/** 
 * Delete a character or empty inline element that preceeds an insertion point. If only one character or element
 * in the line then this turns the line into a token line
 *
 * This is used as part of a more complex and editor specific "delete" operation
 *
 * @return false if at start of line
 */
InsertionPoint.prototype.deletePreviousInLine = function()
{
	// if at start => merge
	var startip = this.clone();
	var startResult = startip.backOne();

	// Special case one: start of line/at_top
	if(startResult != InsertionPoint.SAME_LINE)
		return false;

	// special case only element: check if only element - if it is then turn into ipToken line
	var previp = startip.clone();
	var prevResult = previp.backOne();
	var nextip = this.clone();
	var nextResult = nextip.forwardOne();
	if(!(this.__ipNode.previousSibling) && (prevResult != InsertionPoint.SAME_LINE) && (nextResult != InsertionPoint.SAME_LINE))
	{
		var line = this.line.deleteContents();
		line.container.updateXMLNode();
		
		this.set(line.firstInsertionPoint);
		return true;	
	}

	// special case eol wsp exposure: if deletion would expose a space then make sure that space becomes an nbsp
	if(nextResult != InsertionPoint.SAME_LINE)
	{
		
		if(previp.whitespace) // assuming previp in same line as checked one element line already
			previp.__ipNode.replaceData(previp.ipOffset, 1, STRING_NBSP);		
	}
	// special case wsp collapse: if deletion would collapse a whitespace, no matter where it would be
	// exposed then preserve it with an NBSP
	else 
	{
		if(this.whitespace && ((prevResult != InsertionPoint.SAME_LINE) || previp.whitespace))
		{
			// if whitespace actually starts following text node
			if(this.__ipNode.nodeValue.length == this.__ipOffset)
				nextip.__ipNode.replaceData(0, 1, STRING_NBSP);
			else
				this.__ipNode.replaceData(this.__ipOffset, 1, STRING_NBSP);
		}
	}

	// majority case: delete one character and any exclusive parents it has - restore to after the character
	// before this character
	var range = document.createRange();
	range.setStart(startip.ipNode, startip.ipOffset);
	range.setEnd(this.__ipNode, this.__ipOffset);
	var keepRange = range.cloneRange();
	var cssr = documentCreateCSSTextRange(range, this.__top);

	cssr.includeExclusiveParents(); 
	// TMP: POST05: include exclusive shouldn't do a partial grab of one end or the other of a range but it does now!
	if(this.__ipNode.previousSibling || (cssr.startContainer == startip.ipNode) || (cssr.endContainer == this.ipNode)) {
		keepRange.deleteContents();
	} else {
		//FIXME: HERE WILL THE NODE BE DELETED, MAKE  AN EVENT!!!!!!!!
				
				
		var sC = cssr.startContainer;
		cssr.deleteContents();
		if (sC.parentNode.userModifiable) {
			sC.parentNode.updateXMLNode();
		} else {
			sC.updateXMLNode();
		}
		
	}
	

	this.__top.normalize();

	// restore
	if(prevResult != InsertionPoint.AT_TOP)
	{
		previp.forwardOne();
		this.set(previp);
	}
	else
		this.setToStart();

	return true;
}

/**
 * Can only split a contained line. 
 *
 * POST05: a/c for pre element reference!
 */
InsertionPoint.prototype.splitContainedLine = function()
{
	var line = this.line;
	if(line.lineType == CSSLine.BOUNDED_LINE)
		return; // exception later?

	if(line.container == this.top)
		return; // exception later?

	/*if(line.container == line.tableCellAncestor)
		return; // exception later?*/

	if(this.__cssWhitespace == "pre")
	{
		// Mozilla bug: the line feed isn't treated properly by the renderer. If select the offset
		// after the linefeed, you end up on the same line as the line and suddenly jump once you
		// start inserting more characters. There is a mismatch in the offset setting of Selection
		// and the rendering of the selection. There is no workaround.
		// POST05: need to enter bug in Bugzilla
		this.__ipNode.insertData((this.__ipOffset), "\n"); // if pre - insert linefeed
		this.__ipOffset++;		
		return;
	}

	var newLineContainer = line.container.cloneNode(false);

	// if at end of line then create an empty line after this one and move to it
	if(this.equivalent(line.lastInsertionPoint))
	{
		var emptyLineToken = document.createTextNode(STRING_NBSP);
		newLineContainer.appendChild(emptyLineToken);
		line.container.parentNode.insertAfter(newLineContainer, line.container);
		eDOMEventCall("NodeInsertedBefore", line.container, newLineContainer);
		var newIP = this.clone();
		newIP.forwardOne();
		this.set(newIP);
		return;
	}

	// If split at start of line then create a new empty line before this line but leave the CP where it is
	if(this.equivalent(line.firstInsertionPoint))
	{
		newLineContainer.appendChild(document.createTextNode(STRING_NBSP));
		line.container.parentNode.insertBefore(newLineContainer, line.container);
		
		eDOMEventCall("NodeInsertedBefore", line.container, newLineContainer);

		return;
	}

	// make a new line after this one with the end!
	var newLineRange = document.createRange();
	newLineRange.selectNodeContents(line.container);
	// issue - don't want to grab 
	newLineRange.setStart(this.ipNode, this.ipOffset);
	var newLineContents = newLineRange.extractContents();
	line = documentCreateCSSLine(line.firstInsertionPoint);
	line.normalizeWhitespace(); // takes care of rubbish at start or end ie/ part of invalid styled text
	newLineContainer.appendChild(newLineContents);
	
	line.container.parentNode.insertAfter(newLineContainer, line.container);
	eDOMEventCall("NodeInsertedBefore", line.container, newLineContainer);
	
	var newLine = documentCreateCSSLine(newLineContainer.firstInsertionPoint(line.top));
	newLine.normalizeWhitespace(); // takes care of rubbish at start or end ie/ part of invalid styled text
	newLine.forceLineBreaksBeforeAfter();
	this.set(newLine.firstInsertionPoint);
}

/**
 * Insert a character into a line
 * 
 * POST05:
 * - fix to check if next inline element is a text node and insert there to a/c for span after object: use clone.forwardOne
 * - mix with insertTextNode
 */
InsertionPoint.prototype.insertCharacter = function(charCode)
{
	// Simple - experiment - doesn't account for tokens [still need whitespace tests in most cases!]
	if(this.__ipNode.nodeType == Node.ELEMENT_NODE)
	{
		// after element node at end of line with container or editable area: insert after
		if(this.__ipOffset == this.__ipNode.childNodes.length)
		{
			this.__ipNode.appendChild(document.createTextNode(""));
			this.__ipNode = this.__ipNode.lastChild;
			this.__ipOffset = 0;
		}
		else
		{
			var referencedNode = this.__ipNode.childNodes[this.__ipOffset];
			
			// ie/ between element node and text node: ie/ after element node
			if(referencedNode.nodeType == Node.TEXT_NODE)
			{
				this.__ipNode = referencedNode;
				this.__ipOffset = 0;
			}
			// references element node: either before element node OR between element nodes
			else // [Bug: insert after if before block level node ie/ end of containerless line!]
			{
				// ie/ between text node and element node: ie/ before element node with text node before it
				if(referencedNode.previousSibling && (referencedNode.previousSibling.nodeType == Node.TEXT_NODE))
				{
					this.__ipNode = referencedNode.previousSibling;
					this.__ipOffset = this.__ipNode.nodeValue.length;
				}
				// no text node before the element node - insert one
				else
				{
					var emptyTextNode = document.createTextNode("");
					this.__ipNode.insertBefore(emptyTextNode, referencedNode);
					this.__ipNode = emptyTextNode;
					this.__ipOffset = 0;
				}				
			}			
		}
	}
	// lot's of whitespace/nbsp handling if not dealing with "pre"
	else if(this.__cssWhitespace != "pre")
	{
		// TODO: problem for CHARCODE_SPACE - if only textToken ... node.textToken should be a first class check

		// Cases: start line, end line, space is current character or space before
		if(charCode == CHARCODE_SPACE) 
		{
			// whitespace ahead or end of line
			if(this.whitespace)
			{
				charCode = CHARCODE_NBSP;
			}
			// check if previous character is a space or at beginning of line
			else 
			{
				var prevCharPointer = this.clone();
				// beginning of line if going back brings us over a block boundary
				if((prevCharPointer.backOne() != InsertionPoint.SAME_LINE) || prevCharPointer.whitespace)
					charCode = CHARCODE_NBSP;	
			}
		}	
		// add non whitespace - switch a preceding nbsp if it is in same line and isn't preceded by a
       		// space or nbsp (reason for leaving sequences of nbsp's is that it makes dealing with at top situations
		// easier.	
		else 
		{ 
			var prevCharPointer = this.clone();
			var prevResult = prevCharPointer.backOne();
			if((prevResult == InsertionPoint.SAME_LINE) && (prevCharPointer.character == STRING_NBSP))
			{
				var tnToReplace = prevCharPointer.ipNode;
				var tnOffset = prevCharPointer.ipOffset;
				if((prevCharPointer.backOne() == InsertionPoint.SAME_LINE) && !prevCharPointer.whitespace)
					tnToReplace.replaceData(tnOffset, 1, STRING_SPACE);
			}

			// TODO: use node.textToken here

			// if we're at the first point in a line and nbsp is the only character then replace nbsp with a character
			if((this.clone().backOne() != InsertionPoint.SAME_LINE) && (this.character == STRING_NBSP)) // start line
			{
				var next = this.clone();
				var result = next.forwardOne(); // beyond nbsp
				if(result != InsertionPoint.SAME_LINE)
				{
					this.__ipNode.replaceData(this.__ipOffset, 1, String.fromCharCode(charCode));
					this.__ipOffset++;
					return;	
				}
			}
		}	
	}
	this.__ipNode.insertData(this.__ipOffset, String.fromCharCode(charCode));
	this.__ipOffset++;	
}

/**
 * Insert a node
 *
 * Valid nodes are:
 * - text nodes
 * - empty elements (block or inline) ex/ BR, HR or IMG in XHTML
 * - other elements (any but those with display list-item or table-cell)
 * If the current position is in a contained line and the node being inserted is a block
 * or table element then the line will be split and the node inserted between the two 
 * halves. Otherwise the node is inserted within the line.
 *
 * IP is reset to first position within or for empty elements, after, the node.
 */
InsertionPoint.prototype.insertNode = function(node)
{
	if(node.nodeType == Node.TEXT_NODE)
	{
		this.__insertTextNode(node);
		return;
	}
	if(node.nodeType == 11) // Node.DOCUMENT_FRAGMENT)
	{
		var child = node.firstChild;
		while(child) { 
			var oldChild = child; 
			child = child.nextSibling;
			if (oldChild.nodeType == Node.TEXT_NODE) {
				this.insertNode(oldChild);
			} else {
				this.insertNode(oldChild);
				var _ip = oldChild.lastInsertionPoint(this.top);
				this.set(_ip);
				//ugly ugly ugly, try to find a better solution later
				this.forwardOne();
				this.backOne();
			}
		} 
	}
	
	// assume element node - if not then exception
	if(node.nodeType != Node.ELEMENT_NODE)
		return; // POST05: exception



	// empty element
	if(node.contentType == Element.EMPTY_CONTENTTYPE)
	{
		this.__insertElement(node);
		return;
	}

	var nodeDisplayValue = document.defaultView.getComputedStyle(node, null).getPropertyValue("display");

	// inline element
	if(nodeDisplayValue == "inline")
	{
		this.__insertElement(node);
		return;
	}

	// can only insert blocks or tables
	if(!((nodeDisplayValue == "block") || (nodeDisplayValue == "table")))
		return; // EXCEPTION POST05

	var line = this.line;

	if(line.lineType == CSSLine.BOUNDED_LINE)
	{
		this.__insertElement(node);
		return;
	}

	// treat a list or table line just like a bounded line: POST05 - treat these better?
	if(line.containedLineType != ContainedLine.BLOCK)
	{
		this.__insertElement(node);
		return;
	}

	// check if at start or end of line
	if(this.equivalent(line.firstInsertionPoint))
		line.container.parentNode.insertBefore(node, line.container);
	else if(this.equivalent(line.lastInsertionPoint))
		line.container.parentNode.insertAfter(node, line.container);
	else
	{
		// split contained line
		this.splitContainedLine();
		line.container.parentNode.insertAfter(node, line.container);
	}
		
	var newip = node.firstInsertionPoint(this.top);

	this.set(newip);
}	

/**
 * Insert an element into a line
 */
InsertionPoint.prototype.__insertElement = function(element)
{
	if(this.ipNode.nodeType == Node.ELEMENT_NODE)
	{
		if(this.ipNode.childNodes.length == this.ipOffset)
			this.ipNode.appendChild(element);
		else
			this.ipNode.childNodes[this.ipOffset].parentNode.insertBefore(element, this.ipNode.childNodes[this.ipOffset]);
	}
	// text ip
	else
	{
		var nextip = this.clone();
		nextip.forwardOne();
		var previp = this.clone();
		previp.backOne();

		var inlineParent = null;
		if(document.defaultView.getComputedStyle(this.ipNode.parentNode, null).getPropertyValue("display") == "inline")
			inlineParent = this.ipNode.parentNode;

		// at end of text node => nothing to return ...
		if(nextip.ipNode != this.ipNode)
		{
			if(inlineParent)
				inlineParent.parentNode.insertAfter(element, inlineParent);
			else
				this.ipNode.parentNode.insertAfter(element, this.ipNode);
		}
		else if(previp.ipNode != this.ipNode)
		{
			if(inlineParent)
				inlineParent.parentNode.insertBefore(element, inlineParent);
			else
				this.ipNode.parentNode.insertBefore(element, this.ipNode);
		}
		else
		{
			var secondTextNode = this.ipNode.splitText(this.ipOffset);

			if(inlineParent)
			{
				var newInlineContainer = inlineParent.cloneNode(false);
				inlineParent.parentNode.insertAfter(newInlineContainer, inlineParent);
				newInlineContainer.appendChild(secondTextNode);
				newInlineContainer.parentNode.insertBefore(element, newInlineContainer);
			}
			else
				this.ipNode.parentNode.insertBefore(element, secondTextNode);
		}
	}

	var newip = element.firstInsertionPoint(this.top);
	this.set(newip);
}

/**
 * Insert a text node into a line
 */
InsertionPoint.prototype.__insertTextNode = function(text)
{
	if(this.ipNode.nodeType == Node.TEXT_NODE)
	{
		var secondPart = this.ipNode.splitText(this.ipOffset);
		secondPart.parentNode.insertBefore(text, secondPart);
	}
	else
	{
		// element reference
		if(this.ipNode.childNodes.length == this.ipOffset)
			this.ipNode.appendChild(text);
		else 
			this.ipNode.insertBefore(text, this.ipNode.childNodes[this.ipOffset]);
	}

	var newip = documentCreateInsertionPoint(this.top, text, text.nodeValue.length);
	this.set(newip);
}

/**
 * Insertion point node iterator.
 *
 * Iterates through three types of Insertion Point Node that lie within a specific container or scope:
 * - non empty TextNodes: text nodes with one non whitespace or nbsp character
 * - inline empty element
 * - block level empty element
 *
 * Next/previous return true if iteration crosses or happens on a block level element. They
 * return false otherwise.
 *
 * The iterator is initialized on the first InsertionPoint node within the iterators scope
 *
 * currentNode contains the current InsertionPoint node of the iterator. It is null if the insertionPoint
 * is placed before or beyond valid insertion points within its scope.
 */
function __IPNodeFilter()
{
	this.__crossedBlock = null;
}

__IPNodeFilter.prototype.__defineGetter__(
	"crossedBlock",
	function() {return this.__crossedBlock;}
);

__IPNodeFilter.prototype.reset = function()
{
	this.__crossedBlock = null;
}

/**
 * Accept any node that could be selected as an insertion point when white-space is "pre" ie/ the most liberal definition.
 * This filter (like most of eDOM) is driven by CSS and not other indicators of whitespace such as XML attributes
 * (see: http://www.w3.org/TR/2000/REC-xml-20001006#sec-white-space)
 *
 * POST05: 
 * - consider returning AT_TOP, CROSSED_LINE, SAME_LINE from next and previous ala InsertionPoint (easier currentNode == null means top etc)
 * - search down the preceding or succeeding inline element to make sure it has a selectable child
 * - consider redoing special case text handling: put in memory of where we are: if in line then 
 */
__IPNodeFilter.prototype.acceptNode = function(node)
{
	if(node.nodeType == Node.TEXT_NODE)
	{
		// empty node - not visible
		if(node.nodeValue.length == 0)
			return NodeFilter.FILTER_REJECT;

		// if non NBSP whitespace then normally reject - except (special cases) if it is before/after an inline node OR it is
		// within an inline node
		if(!(/\u00A0+/.test(node.nodeValue)) && node.isWhitespaceOnly)
		{
			// accept empty text node that is within an inline element
			if(document.defaultView.getComputedStyle(node.parentNode, null).getPropertyValue("display") == "inline")
				return NodeFilter.FILTER_ACCEPT;

			// accept empty text that has either an inline node before or after it: strictly speaking should
			// make sure that the inline node has a text node or empty object as a child!
			if((node.previousSibling && (node.previousSibling.nodeType == Node.ELEMENT_NODE) && (document.defaultView.getComputedStyle(node.previousSibling, null).getPropertyValue("display") == "inline")) &&
			   (node.nextSibling && (node.nextSibling.nodeType == Node.ELEMENT_NODE) && (document.defaultView.getComputedStyle(node.nextSibling, null).getPropertyValue("display") == "inline")))
				return NodeFilter.FILTER_ACCEPT;

			return NodeFilter.FILTER_REJECT;
		}
			
		return NodeFilter.FILTER_ACCEPT;
	}

	// normally we skip elements: two cases are of interest - when we cross a block and when we find an
	// empty inline element. We need to note when we are in a new line and empty inline elements are 
	// selectable. However, we skip all other types of element includin childless, child bearing elements 
	// as Geiko doesn't display these. eDOM assumes that they are just for spacing and should not support insertion.
	if(node.nodeType == Node.ELEMENT_NODE)
	{
		if(document.defaultView.getComputedStyle(node, null).getPropertyValue("display") != "inline")
		{
			if(!this.__crossedBlock)
				this.__crossedBlock = node;
		}
		
		if(node.contentType == Element.EMPTY_CONTENTTYPE) // allows empty block and inline elements
		{
			return NodeFilter.FILTER_ACCEPT;
		}
	}

	return NodeFilter.FILTER_SKIP;
}

/**
 * By default the current node is set to the first child under root that passes the filter or null if there are
 * no nodes to iterate through
 */
function __IPNodeIterator(root)
{
	this.__ipNodeFilter = new __IPNodeFilter();

	this.__root = root;

	this.__ipNodeWalker = document.createTreeWalker(root,
					NodeFilter.SHOW_ALL,
					this.__ipNodeFilter,
					false);
	this.__currentNode = this.__ipNodeWalker.firstChild();
}

__IPNodeIterator.prototype.setToStart = function()
{
	this.__ipNodeWalker.currentNode = this.__root;
	this.__currentNode = this.__ipNodeWalker.firstChild();
	return this.__currentNode;
}

__IPNodeIterator.prototype.setToEnd = function()
{
	this.__ipNodeWalker.currentNode = this.__root;
	this.__currentNode = this.__ipNodeWalker.lastChild();
	return this.__currentNode;
}

__IPNodeIterator.prototype.__defineGetter__(
	"currentNode",
	function() {return this.__currentNode;}
);

__IPNodeIterator.prototype.__defineSetter__(
	"currentNode",
	function(value) {this.__currentNode = value; this.__ipNodeWalker.currentNode = value;}
);

__IPNodeIterator.prototype.nextNode = function()
{
	this.__ipNodeFilter.reset();
	var oldBlockParentNode = this.__currentNode ? this.__currentNode.__nilParentElement : null;
	this.__currentNode = this.__ipNodeWalker.nextNode(); 
	// check special case: if cross to new bounded line from existing contained line then "real" block
	// is the container of the contained line. Tree skips that naturally so must a/c for it. Two situations - 
	// didn't cross a block according to the tree so let's check and now on the "block", the empty element. Is
	// this really the only block?
	if(!this.__ipNodeFilter.crossedBlock || (this.__currentNode && (this.__ipNodeFilter.crossedBlock == this.__currentNode)))
	{
		var newBlockParentNode = this.__currentNode ? this.__currentNode.__nilParentElement: null;
		if(newBlockParentNode != oldBlockParentNode)
			return newBlockParentNode;
	}
	return this.__ipNodeFilter.crossedBlock;
}

__IPNodeIterator.prototype.previousNode = function()
{
	this.__ipNodeFilter.reset();

	this.__currentNode = this.__ipNodeWalker.previousNode(); 

	return this.__ipNodeFilter.crossedBlock;
}

/*************************************************************************************************************
 * CSSLine captures the concept of a line. Lines may be bounded or contained. 
 *
 * IP establishes a valid seed for a line:
 * - before block level empty element (means will be bounded)
 * - after block level element (means bounded - but need not be empty)
 * - before inline element or within text (means creates non empty bounded or a contained line)
 *
 * TODO:
 * - Element.lines (ala Range.lines)
 * - line.removeCollapsedWhitespace()
 * - line.newLineBeforeAfter()
 * - insertElementAfter?
 * 
 * POST04:
 * - handle previous/next ie/ up/down 
 * - handle line length and offset
 * - handle destroyed line ie/ collapsed range - must throw exceptions ...
 * - add style to this: ie/ styleLine == styleElement with container application if 
 * necessary
 * - utility: normalizeWhiteSpace removes white space before the beginning or after the end of lines. Note that this 
 * should be applied carefully because it will effect TextPointers. It will also merge any a sequence of white
 * spaces into one space and remove white space at the beginning or end of the line. This should remove the
 * need for a lot of special case handling of white spaces. It should also simplify post split behaviors.
 * - utility: formatLineMarkup - does normalize of whitespace AND sets container on its own line
 *************************************************************************************************************/

Document.prototype.createCSSLine = documentCreateCSSLine;

/**
 * IP establishes a valid seed for a line:
 * - before block level empty element (means will be bounded)
 * - after block level element (means bounded - but need not be empty)
 * - before inline element or within text (means creates non empty bounded or a contained line)
 */
function documentCreateCSSLine(ip)
{
	if(ip.ipNode.nodeType == Node.TEXT_NODE)
		return __createCSSLineFromNonBlockIP(ip, true);
	
	var seed = ip.ipReferencedNode;
	if(document.defaultView.getComputedStyle(seed, null).getPropertyValue("display") == "inline")
		return __createCSSLineFromNonBlockIP(ip, true);

	// special handling
	return __createBoundedLineFromBlockIP(ip, true);
}

/**
 * Create a bounded line from an ip that references a block level element
 *
 * - either after an element node or before one (may be both in which case, treated as if before one!)
 * - block element is a boundary and not part of a line per se
 * - always creates a bounded line and that line may be empty: in this case, startip == endip == seed ip
 */
function __createBoundedLineFromBlockIP(ip, handleCollapsedWhitespace)
{
	var seed = ip.ipReferencedNode;
	var startBoundary = null;
	var endBoundary = null;
	var startip;
	var endip;
	var elementCount = 0;
	var ipNodeIterator = new __IPNodeIterator(ip.top);

	// if before a block element then this is the end node
	if(seed.offset == ip.ipOffset)
	{
		endBoundary = seed;
		endip = ip.clone();
		var startIPNode = null;
		ipNodeIterator.currentNode = seed;

		// go back while not at top and in same line
		while(!ipNodeIterator.previousNode() && ipNodeIterator.currentNode)
		{
			startIPNode = ipNodeIterator.currentNode;
			if(startIPNode.nodeType == Node.ELEMENT_NODE)
				elementCount++;
		}

		// at least one node is within the line!
		if(startIPNode)
		{ 
			startBoundary = startIPNode.topInlineAncestor.previousSibling;
			if(startIPNode.nodeType == Node.ELEMENT_NODE)
				startip = handleCollapsedWhitespace ? documentCreateInsertionPoint(ip.top, startIPNode.parentNode, startIPNode.offset) : __createInsertionPoint(ip.top, startIPnode.parentNode, startIPNode.offset);
			else
				startip = handleCollapsedWhitespace ? documentCreateInsertionPoint(ip.top, startIPNode, 0) : __createInsertionPoint(ip.top, startIPNode, 0);
		}
		else
		{
			startip = ip.clone(); // empty!
			// make sure spans around seed element don't cause problems!
			startBoundary = seed.topInlineAncestor.previousSibling;
		}

		// if non null, make sure start boundary is a not a text node (could be an empty one!)
		while(startBoundary && (startBoundary.nodeType == Node.TEXT_NODE))
			startBoundary = startBoundary.previousSibling;

	}
	// after block element - treat as start
	else
	{
		startBoundary = seed;
		startip = ip.clone();
		var endIPNode = null;
		ipNodeIterator.currentNode = seed;

		// go forward while not at top and in same line
		while(!ipNodeIterator.nextNode() && ipNodeIterator.currentNode)
		{
			endIPNode = ipNodeIterator.currentNode;
			if(endIPNode.nodeType == Node.ELEMENT_NODE)
				elementCount++;
		}

		// at least one node is within the line
		if(endIPNode)
		{
			endBoundary = endIPNode.topInlineAncestor.nextSibling;

			if(endIPNode.nodeType == Node.ELEMENT_NODE)
				endip = handleCollapsedWhitespace ? documentCreateInsertionPoint(ip.top, endIPNode.parentNode, (endIPNode.offset+1)) : __createInsertionPoint(ip.top, endIPnode.parentNode, (endIPNode.offset+1));
			else
				endip = handleCollapsedWhitespace ? documentCreateInsertionPoint(ip.top, endIPNode, endIPNode.nodeValue.length) : __createInsertionPoint(ip.top, endIPNode, endIPNode.nodeValue.length);			
		}
		else // empty
		{
			endip = ip.clone(); // empty!
			// make sure spans around seed element don't cause problems!
			endBoundary = seed.topInlineAncestor.nextSibling;
		}

		// if non null, make sure end boundary is a not a text node (could be an empty one!)
		while(endBoundary && (endBoundary.nodeType == Node.TEXT_NODE))
			endBoundary = endBoundary.nextSibling;
	}

	return new BoundedLine(ip.top, startip, endip, startBoundary, endBoundary, elementCount);
}

/**
 * Non block ip (text node or nil element or after nil element) 
 *
 * - case of after block level element but before inline element: this is treated as a non block ip
 * - these lines are never empty: have at least got "seed" as contents
 */
function __createCSSLineFromNonBlockIP(ip, handleCollapsedWhitespace)
{
	var seed = ip.ipReferencedNode;
	var elementCount = (seed.nodeType == Node.ELEMENT_NODE) ? 1 : 0;
	var ipNodeIterator = new __IPNodeIterator(ip.top);

	// first get end ip node - could stay being seed!
	var endIPNode = seed;
	ipNodeIterator.currentNode = seed;
	while(!ipNodeIterator.nextNode() && ipNodeIterator.currentNode)
	{
		endIPNode = ipNodeIterator.currentNode;
		if(endIPNode.nodeType == Node.ELEMENT_NODE)
			elementCount++;
	}

	// get start ip node - could stay being seed!
	var startIPNode = seed;
	ipNodeIterator.currentNode = seed;
	while(!ipNodeIterator.previousNode() && ipNodeIterator.currentNode)
	{
		startIPNode = ipNodeIterator.currentNode;
		if(startIPNode.nodeType == Node.ELEMENT_NODE)
			elementCount++;
	}

	var startNode = startIPNode.topInlineAncestor;
	var startBoundary = startNode.previousSibling;
	// if start boundary then previous could just be blank text node or other clutter - skip it
	while(startBoundary && ((startBoundary.nodeType == Node.TEXT_NODE) || (document.defaultView.getComputedStyle(startBoundary, null).getPropertyValue("display") == "inline")))
		startBoundary = startBoundary.previousSibling;

	var startIPOffset = 0;
	if(startIPNode.nodeType == Node.ELEMENT_NODE)
	{
		startIPOffset = startIPNode.offset;
		startIPNode = startIPNode.parentNode;
	}

	var endNode = endIPNode.topInlineAncestor;
	var endBoundary = endNode.nextSibling;
	// if end boundary is non null then next could just be blank text node or other clutter - skip it
	while(endBoundary && ((endBoundary.nodeType == Node.TEXT_NODE) || endBoundary.nodeType == Node.COMMENT_NODE || (document.defaultView.getComputedStyle(endBoundary, null).getPropertyValue("display") == "inline")))
		endBoundary = endBoundary.nextSibling;

	var endIPOffset;
	if(endIPNode.nodeType == Node.ELEMENT_NODE)
	{
		endIPOffset = endIPNode.offset+1;
		endIPNode = endIPNode.parentNode;
	}
	else
		endIPOffset = endIPNode.nodeValue.length;

	var startip;
	var endip;
	if(handleCollapsedWhitespace)
	{
		startip = documentCreateInsertionPoint(ip.top, startIPNode, startIPOffset);
		endip = documentCreateInsertionPoint(ip.top, endIPNode, endIPOffset);
	}
	else // don't handle whitespace so make IP's at exact points we start and end with (only used internally)
	{
		startip = __createInsertionPoint(ip.top, startIPNode, startIPOffset);
		endip = __createInsertionPoint(ip.top, endIPNode, endIPOffset);
	}

	

	// if there is either a start or end boundary then this is a bounded line
	if(endBoundary || startBoundary)
		return new BoundedLine(ip.top, startip, endip, startBoundary, endBoundary, elementCount);
	return new ContainedLine(ip.top, startip, endip, startNode.parentNode, elementCount);
}

/**
 * Base class
 */
function CSSLine()
{
}

CSSLine.prototype.init = function(top, startip, endip, lineRange, elementCount, lineParent)
{
	this.__top = top;
	this.__firstInsertionPoint = startip; // only put into subclasses
	this.__lastInsertionPoint = endip;
	this.__lineRange = lineRange;
	this.__elementCount = elementCount;
	this.__lineParent = lineParent;
}

CSSLine.BOUNDED_LINE = 1;
CSSLine.CONTAINED_LINE = 2;

CSSLine.prototype.__defineGetter__(
	"lineType",
	function()
	{
		if(this instanceof ContainedLine)
			return CSSLine.CONTAINED_LINE;
		return CSSLine.BOUNDED_LINE;
	}
);

CSSLine.prototype.__defineGetter__(
	"top",
	function()
	{
		return this.__top;
	}
);

CSSLine.prototype.__defineGetter__(
	"lineParent",
	function()
	{
		return this.__lineParent;
	}
);

/**
 * Return DocumentFragment of line's contents: if empty then empty document fragment is returned. Note that
 * this will not include any collapsed whitespace that may appear at the beginning or end of a line.
 */ 
CSSLine.prototype.__defineGetter__(
	'lineContents',
	function()
	{
		var contentsRange = document.createRange();
		contentsRange.selectNodeContents(this.lineParent);
		/*
		 * Not sure, if we really can just leave out that here... Let's try it (chregu)
		 * Without it, it solves issues with block within block elements (object/images for example) 
		contentsRange.setStart(this.firstInsertionPoint.ipNode, this.firstInsertionPoint.ipOffset);
		contentsRange.setEnd(this.lastInsertionPoint.ipNode, this.lastInsertionPoint.ipOffset);
		*/
		return contentsRange.cloneContents();
	}
);

/**
 * Return a Range equivalent to the extent of the line: this does contain collapsed whitespace.
 */ 
CSSLine.prototype.__defineGetter__(
	'lineRange',
	function()
	{
		return(this.__lineRange.cloneRange());
	}
);

CSSLine.prototype.equivalent = function(otherLine)
{
	if(this.firstInsertionPoint.equivalent(otherLine.firstInsertionPoint))
		return true;

	return false;
}

/**
 * Reduce the line to a string
 */
CSSLine.prototype.toString = function()
{
	return this.__lineRange.toString();
}

/**
 * Calculate line length for the length of it as a String
 */
CSSLine.prototype.__defineGetter__(
	"length",
	function()
	{
		return(this.toString().length + this.__elementCount);
	}
);

/**
 * Return the insertion point at a particular offset within a line
 */
CSSLine.prototype.insertionPointAt = function(lineOffset)
{
	var ip = this.firstInsertionPoint;

	while(lineOffset)
	{	
		var thisip = ip.clone();
		var result = ip.forwardOne();
		if(result != InsertionPoint.SAME_LINE)
			return thisip;
		lineOffset--;
	}
	return ip;
}

/**
 * A line is empty if the first and last ip's are the equivalent: it may only have a token or can actually be contentless but one of 
 * its boundaries is selectable
 */
CSSLine.prototype.__defineGetter__(
	"emptyLine",
	function()
	{
		return(this.firstInsertionPoint.equivalent(this.lastInsertionPoint));
	}
);

/**
 * Token line - different from an empty line. A token line is always an empty line but an empty line may
 * not be a token line.
 *
 * POST05:
 * - a/c for BR as well as NBSP ie/ get token characters/elements from some meta and see if present
 */
CSSLine.prototype.__defineGetter__(
	"tokenLine",
	function()
	{
		// for now: no BR
		if(this.__elementCount > 0)
			return false;

		// See if any non whitespace/non nbsp characters - if none then this is a token line
		var lineString = this.__lineRange.toString();

		if(/\S+/.test(lineString)) // any non white space visible characters
			return false;

		// of remaining: must have one and only one nbsp
		var matches = lineString.match(/([\u00A0])/g);
		if(!matches || (matches.length > 1))
			return false;
		return true;
	}
);

/**
 * Base class sets default of "topLine" to false
 */
CSSLine.prototype.__defineGetter__(
	"topLine",
	function()
	{
		return false;
	}
);

CSSLine.prototype.__defineGetter__(
	"firstInsertionPoint",
	function()
	{
		return this.__firstInsertionPoint.clone();
	}
);

CSSLine.prototype.__defineGetter__(
	"lastInsertionPoint",
	function()
	{
		return this.__lastInsertionPoint.clone();
	}
);

CSSLine.prototype.__defineGetter__(
	"previousLine",
	function()
	{
		var firstIP = this.firstInsertionPoint;
		var prevLineLastIP = firstIP.clone();
		var result = prevLineLastIP.backOne();
		if(result == InsertionPoint.AT_TOP)
			return null;
		var pline = documentCreateCSSLine(prevLineLastIP);
		return pline;
	}
);

CSSLine.prototype.__defineGetter__(
	"nextLine",
	function()
	{
		var lastIP = this.lastInsertionPoint;
		var nextLineFirstIP = lastIP.clone();
		var result = nextLineFirstIP.forwardOne();
		if(result == InsertionPoint.AT_TOP)
			return null;
		var nextLine = documentCreateCSSLine(nextLineFirstIP);
		return nextLine;	
	}
);

/**
 * @returns the list-item element that contains this line or that is an ancestor of this line's container or null
 * if there is no such element.
 */
CSSLine.prototype.__defineGetter__(
	"listItemAncestor",
	function()
	{
		var nodeToTest = this.__lineParent;
		while(nodeToTest != document)
		{
			if(document.defaultView.getComputedStyle(nodeToTest, null).getPropertyValue("display") == "list-item")
				return nodeToTest;
			nodeToTest = nodeToTest.parentNode;				
		}
		return null;
	}
);

/**
 * @returns the table cell element that contains this line or that is an ancestor of this line's container or null
 * if there is no such element.
 */
CSSLine.prototype.__defineGetter__(
	"tableCellAncestor",
	function()
	{
		var nodeToTest = this.__lineParent;
		while(nodeToTest != this.__top)
		{
			if(document.defaultView.getComputedStyle(nodeToTest, null).getPropertyValue("display") == "table-cell")
				return nodeToTest;
			nodeToTest = nodeToTest.parentNode;				
		}
		return null;
	}
);

CSSLine.prototype.setTopToTableCellAncestor = function()
{
	var tca = this.tableCellAncestor;
	if(tca)
		this.__top = tca;
}

CSSLine.prototype.containsNode = function(nodeToTest)
{
	var testRange = document.createRange();
	testRange.selectNodeContents(nodeToTest);
	if(this.__lineRange.containsRange(testRange))
		return true;
	return false;
}

CSSLine.prototype.normalizeTextNodes = function()
{
	this.lineParent.normalize();
	this.__recalculateLastInsertionPoint();

	return this;
}

/**
 * POST05: implement properly to stick within line: - ie/ do specialized version for bounded line
 */
CSSLine.prototype.normalizeInlineElements = function()
{
	// then normalize the inline elements
	var normalizeRange = document.createRange();
	normalizeRange.selectNode(this.lineParent);
	normalizeRange.normalizeElements("span");
	this.__recalculateLastInsertionPoint();

	return this;
}

CSSLine.prototype.__recalculateLastInsertionPoint = function()
{
	var referencedNode = this.firstInsertionPoint.ipReferencedNode;

	var ipNodeIterator = new __IPNodeIterator(this.lineParent);
	var endIPNode = ((referencedNode.nodeType == Node.TEXT_NODE) || (document.defaultView.getComputedStyle(referencedNode, null).getPropertyValue("display") == "inline")) ? referencedNode :  null;
	ipNodeIterator.currentNode = referencedNode;
	while(!ipNodeIterator.nextNode() && ipNodeIterator.currentNode)
		endIPNode = ipNodeIterator.currentNode;
	var endIPOffset;
	if(!endIPNode)
		this.__lastInsertionPoint = this.__firstInsertionPoint.clone();
	else
	{ 
		if(endIPNode.nodeType == Node.ELEMENT_NODE)
		{
			endIPOffset = endIPNode.offset+1;
			endIPNode = endIPNode.parentNode;
		}
		else
			endIPOffset = endIPNode.nodeValue.length;

		this.__lastInsertionPoint = documentCreateInsertionPoint(this.top, endIPNode, endIPOffset);
	}

	return this.__lastInsertionPoint;
}

/*************************************** ContainedLine *************************************************/

ContainedLine.prototype = new CSSLine();
ContainedLine.prototype.constructor = ContainedLine;
ContainedLine.superclass = CSSLine.prototype;
function ContainedLine(top, startip, endip, container, elementCount)
{
	var lineRange = document.createRange();
	lineRange.selectNodeContents(container);
	ContainedLine.superclass.init.call(this, top, startip, endip, lineRange, elementCount, container);
	this.__container = container;
}

ContainedLine.prototype.__defineGetter__(
	"container",
	function()
	{
		return this.__container;
	}
);

/**
 * Highest exclusive container/parent of this line: this may be top!
 */
ContainedLine.prototype.__defineGetter__(
	"topMostContainer",
	function()
	{
		var topMostContainer = this.__container;
		var parentNode = topMostContainer.parentNode;
		while(oneSignificantChild(parentNode) && (topMostContainer != this.__top))
		{
			topMostContainer = parentNode;
			parentNode = parentNode.parentNode;
		}
		return topMostContainer;
	}
);

// Significant means non empty text nodes or elements
function oneSignificantChild(element)
{
	var sigChildren = 0;
	for(var i=0; i< element.childNodes.length; i++)
	{
		if(!((element.childNodes[i].nodeType == Node.TEXT_NODE) && (element.childNodes[i].isWhitespaceOnly)))
			sigChildren++;
	}
						
	if(sigChildren == 1)
		return true;

	return false;
}

/**
 * This is a topLine is the container or topMostContainer is top
 */
ContainedLine.prototype.__defineGetter__(
	"topLine",
	function()
	{
		if((this.container == this.__top) || (this.topMostContainer == this.__top))
			return true;
		return false;
	}
);

/**
 * Is this line embedded within multiple exclusive containers?
 */
ContainedLine.prototype.__defineGetter__(
	"embeddedLine",
	function()
	{
		if(this.topMostContainer != this.container)
			return true;
		return false;
	}
);

ContainedLine.BLOCK = 0; // container is block
ContainedLine.LIST_ITEM = 1; // container is list-item
ContainedLine.TABLE_CELL = 2; // container is table-cell
ContainedLine.TOP = 3; // container is top - overrides importance of any other container type

ContainedLine.prototype.__defineGetter__(
	"containedLineType",
	function()
	{
		if(this.container == this.__top)
			return ContainedLine.TOP;

		var containerDisplay = document.defaultView.getComputedStyle(this.__container, null).getPropertyValue("display");
		if(containerDisplay == "block")
			return ContainedLine.BLOCK;
		else if(containerDisplay == "list-item")
			return ContainedLine.LIST_ITEM;
		else if(containerDisplay == "table-cell")
			return ContainedLine.TABLE_CELL;
		return -1; // should never happen
	}
);

/**
 * Either replace or insert a container of a ContainedLine. Note that this always inserts a new container under
 * the original container if that container is top.
 *
 * Still issue of indent into text node
 */
ContainedLine.prototype.setContainer = function(newContainer, replace)
{
	/* 
	 * looks like this causes more harm than good (chregu)
	if(this.topLine) // always insert/not replace if the line is a top line
	replace = false;
	*/

	// replace current container
	if(replace) {
		newContainer = this.__container.parentNode.replaceChildOnly(this.__container, newContainer.nodeName);
	}
	else // usually for table-cell, at top etc
	{
		this.__container.appendChild(newContainer);
		newContainer.appendChild(this.__lineRange.extractContents());
	}

	var linenow = documentCreateCSSLine(newContainer.firstInsertionPoint(this.top));
	
	linenow.forceLineBreaksBeforeAfter();

	return linenow;
}

/**
 * Make sure the line is formatted properly!
 *
 * POST05: 
 * - merge with "normalizeLine" to put in invisible whitespace only where formatting is needed. For now, can't do
 * as selectRange restoration depends not on line offsets but absolute offsets.
 */
ContainedLine.prototype.forceLineBreaksBeforeAfter = function()
{
	if(this.topLine)
		return;

	if(this.containedLineType == ContainedLine.TABLE_CELL)
		return;

	// insert a newline before the line's container if there isn't one there already
	// - no prev sib
	// - prev sib is element
	// - prev sib is text but doesn't end in a newline!
	if(!this.container.previousSibling || (this.container.previousSibling.nodeType == Node.ELEMENT_NODE) || !(/\n$/.test(this.container.previousSibling.nodeValue)))
		this.container.parentNode.insertBefore(document.createTextNode(STRING_LINEFEED), this.container);

	if(!this.container.nextSibling || (this.container.nextSibling.nodeType == Node.ELEMENT_NODE) || !(/^\n/.test(this.container.nextSibling.nodeValue)))
		this.container.parentNode.insertAfter(document.createTextNode(STRING_LINEFEED), this.container);
}

/*
 * Removing a container of a line may make it a BoundedLine or just may remove an intermediate container
 * and so reset the ContainedLine's container. Equivalent to "removeStartBoundary" for BoundedLine.
 * 
 * Note that removing the container of an empty line will delete the line: in this case, this method returns null.
 */
ContainedLine.prototype.removeContainer = function()
{
	if(this.topLine)
		return this; // POST05: throw exception

	if(this.emptyLine)
	{
		this.deleteLine;
		return null;
	}

	var lineSeedIP = this.firstInsertionPoint;

	var result = lineSeedIP.backOne();

	if(result == InsertionPoint.AT_TOP)
		lineSeedIP = null;

	this.container.parentNode.insertBefore(this.lineContents, this.container);
	this.container.parentNode.removeChild(this.container);

	if(lineSeedIP)
		lineSeedIP.forwardOne();
	else
		lineSeedIP = this.top.firstInsertionPoint(this.top);

	return documentCreateCSSLine(lineSeedIP);
}

/**
 * This method does three things for a non-pre line
 * - removes collapsed whitespace at the beginning of the line
 * - removes collapsed whitespace at the end of the line
 * - POST05: removes collapsed whitespace within the line
 * - POST05: work on empty lines: see if token and work from there
 */
ContainedLine.prototype.normalizeWhitespace = function()
{

	if(document.defaultView.getComputedStyle(this.container, null).getPropertyValue("white-space") == "pre")
		return;

	// for now: don't normalize empty lines
	if(this.emptyLine)
		return;

	// first normalize end: before start as if start is in same text node, it may make lastInsertionPoint invalid
	var range = document.createRange();
	range.selectNode(this.container.lastChild);
	range.setStart(this.__lastInsertionPoint.ipNode, this.__lastInsertionPoint.ipOffset);
//fix by chregu, it deleted to much, when span/object was in the node
//only delete contents, if there really is no content..
	if (range.toString().replace(/\s*/g,"").length == 0) {
		if (this.container.lastChild.childNodes.length == 0) {
			range.deleteContents();
		}
	}
	// then normalize start
	range.selectNode(this.container.firstChild);
	range.setEnd(this.__firstInsertionPoint.ipNode, this.__firstInsertionPoint.ipOffset);
	// the same fix by chregu as 5 lines above
	if (range.toString().replace(/\s*/g,"").length == 0) {
		if (this.container.firstChild.childNodes.length == 0) {
			range.deleteContents();
		}
	}
	this.__firstInsertionPoint.ipOffset = 0;

	// set end after deletion of start - covers complication when start and end are in same node
	if(this.__lastInsertionPoint.ipNode.nodeType == Node.ELEMENT_NODE)
		this.__lastInsertionPoint.ipOffset = this.container.childNodes.length;
	else
		this.__lastInsertionPoint.ipOffset = this.__lastInsertionPoint.ipNode.nodeValue.length;

	// Geiko bug?: leaves empty text nodes after normalize
	if((this.container.lastChild.nodeType == Node.TEXT_NODE) && (this.container.lastChild.nodeValue.length == 0))
		this.container.removeChild(this.container.lastChild);

	// reset Range
	this.__lineRange.selectNodeContents(this.container);

	return this;
}

/**
 * Delete line (includes exclusive parent's of container); Bounded line has "deleteContents" but nothing else
 *
 * TODO: only delete contents of table cell's? [perhaps table cells turn into "top" automatically!]
 */
ContainedLine.prototype.deleteLine = function()
{
	var rangeToDelete = document.createRange();
	//handle th/td differently
	if (this.topMostContainer.localName == "td" || this.topMostContainer.localName == "th") {
		rangeToDelete.selectNode(this.container);
		var par = this.container.parentNode;
		eDOMEventCall("NodeBeforeDelete",this.container);
		rangeToDelete.deleteContents();
		par.appendChild(document.createTextNode(STRING_NBSP));
	} else {
		rangeToDelete.selectNode(this.topMostContainer);
		eDOMEventCall("NodeBeforeDelete",this.topMostContainer);
		rangeToDelete.deleteContents();
		
	}

}
/**
 * Empty this line - it becomes an empty (token) line
 */
ContainedLine.prototype.deleteContents = function()
{
	// empty out contents
	this.__lineRange.deleteContents();
	var tokenTextNode = document.createTextNode(STRING_NBSP);
	this.__container.appendChild(tokenTextNode); 
	return documentCreateCSSLine(__createInsertionPoint(this.__top, tokenTextNode, 0));
}

/**
 * POST05: rebuild line - is still contained
 */
ContainedLine.prototype.appendContent = function(contents)
{
	// first normalize whitespace in line
	this.normalizeWhitespace();

	// nix any token if there is one
	if(this.emptyLine)
		this.__lineRange.deleteContents();

	// then append contents that are assumed to be only text or inline elements and assumed to be normalized for whitespace
	ret = contents.firstChild;
	this.container.appendChild(contents);
	
	eDOMEventCall("NodePositionChanged",ret);
	// give back "join" point
	return this.lastInsertionPoint.clone();
}

/**
 * This method will set a style for the line as a whole. 
 * 
 * POST05:
 * - text-align in Mozilla seems to have some CSS3 support - default is "start". Some but not all - supports "start" but
 * not "end". Need to handle cases like this. http://www.w3.org/TR/css3-text/#alignment-prop
 */
ContainedLine.prototype.setStyle = function(styleName, styleValue)
{
	this.__container.setStyle(styleName, styleValue);
}

/**
 * Add or remove a class
 */

/**
 * delete handler when selection is at the start of a contained line
 */
ContainedLine.prototype.deleteStructure = function()
{
	// if this is in a table cell then treat table cell as top! Can't delete beyond the cell!
	var tba = this.tableCellAncestor;
	if(tba)
		this.setTopToTableCellAncestor();

	// Case 1: if top line then noop
	if(this.containedLineType == ContainedLine.TOP)
		return this.firstInsertionPoint;

	var pline = this.previousLine;

	// Case 2: this is a token line and not only line in top (caught above!)
	if(this.emptyLine)
	{
		var top = this.top;
		this.deleteLine();
		var ip = pline ? pline.lastInsertionPoint: top.firstInsertionPoint;
		return ip;		
	}
	
	// Case 3: this in list item: special case handling for it
	var la = this.listItemAncestor;
	if(la)
	{
		// Sub case 1: contained line within a list item
		if(this.containedLineType != ContainedLine.LIST_ITEM)
			return this.removeContainer().firstInsertionPoint;
		
		// Sub case 2: there is no list item ancestor before this one
		if(!pline || !pline.listItemAncestor)
			return this.firstInsertionPoint; // POST05: do outdent?

		// Sub case 3: merge into previous list-item (works for any bounded stuff too!)
		var ip = pline.appendContent(this.lineContents);

		// remove line from its topmost container downwards
		this.deleteLine();
		return ip;
	}

	// Case 4: (not list-item!) if topmostContainer is top then remove container and return
	if(this.topMostContainer == this.top)
	{
		var line = this.removeContainer();
		return line.firstInsertionPoint;
	}	

	// Case 5: no previous line - so we're at the start
	if(!pline)
	{
		var line = this.removeContainer(); // this isn't token line - caught above
		return line.firstInsertionPoint;
	}

	// Case 6: table before this line
	var ptca = pline.tableCellAncestor;
	if(ptca && (ptca != this.top)) // rem: if same tca as this then this.top is same!
	{
		var line = this.removeContainer(); // this isn't token line - caught above
		return line.firstInsertionPoint;		
	}

	// Case 7: pline is bounded with an end boundary of an empty element or this line's top most container
	if((pline.lineType == CSSLine.BOUNDED_LINE) && pline.endBoundary && 
           ((pline.endBoundary.contentType == Element.EMPTY_CONTENTTYPE) || (pline.endBoundary == this.topMostContainer)))
	{
		var line = this.removeContainer(); // this isn't token line - caught above
		return line.firstInsertionPoint;
	}	

	// Case 8: merge contents into previous line and delete this line	
	
	if (bxe_config.options['mergeDifferentBlocksOnDelete'] != 'false') {
		var ip = pline.appendContent(this.lineContents);
		this.deleteLine();
	} else if (pline.container.XMLNode.localName == this.container.XMLNode.localName && 
		pline.container.XMLNode.namespaceUri == this.container.XMLNode.namespaceUri) {
		var ip = pline.appendContent(this.lineContents);
		this.deleteLine();
	} else {
		ip = this.firstInsertionPoint.clone();
		ip.__needBackspace = true;
	}
	return ip;
}

/******************************************* bounded line *******************************************/

/**
 * BoundedLine
 *
 * Processed differently depending on whether it has non-null boundaries, whether those boundaries
 * are EMPTY elements or whether those boundaries have selectable children. 
 */
BoundedLine.prototype = new CSSLine();
BoundedLine.prototype.constructor = BoundedLine;
BoundedLine.superclass = CSSLine.prototype;

function BoundedLine(top, startip, endip, startBoundary, endBoundary, elementCount)
{
	this.__startBoundary = startBoundary;
	this.__endBoundary = endBoundary;

	var lineParentNode = (this.startBoundary != null) ? this.startBoundary.parentNode: this.endBoundary.parentNode;
	var lineRange = document.createRange();
	lineRange.selectNodeContents(lineParentNode);
	if(this.startBoundary)
		lineRange.setStartAfter(this.startBoundary)
	else
		lineRange.setStartBefore(lineParentNode.firstChild);
	
	if(this.endBoundary)
		lineRange.setEndBefore(this.endBoundary);
	else
		lineRange.setEndAfter(lineParentNode.lastChild);

	BoundedLine.superclass.init.call(this, top, startip, endip, lineRange, elementCount, lineParentNode);
}

BoundedLine.prototype.toString = function()
{
	return("sb: " + this.startBoundary + "= eb: " + this.endBoundary + "= lineParent: " + this.lineParent + "= contents: " + this.lineRange.toString());
}

/** 
 * Boundary only line: A line has a start boundary if it is bounded and it is not the first line within an element
 *
 * Note: this can be null but if it is then endBoundary is not null
 */
BoundedLine.prototype.__defineGetter__(
	"startBoundary",
	function() 
	{
		return this.__startBoundary;
	}
);

/**
 * Boundary only line: A line has an end boundary if it is bounded and it is not the last line of an element
 *
 * Note: this can be null but if it is then startBoundary cannot be null
 */
BoundedLine.prototype.__defineGetter__(
	"endBoundary",
	function()
	{
		return this.__endBoundary;	
	}
);

/**
 * Create a ContainedLine out of the contents of this line: this a/cs for empty lines
 */
BoundedLine.prototype.setContainer = function(lineContainer, replace)
{
	// noop for empty lines
	if(this.emptyLine)
		return this;

	var lineContents = this.__lineRange.extractContents();
	
	if(this.__startBoundary)
		this.__startBoundary.parentNode.insertAfter(lineContainer, this.__startBoundary);
	else // always one or other!
		this.__endBoundary.parentNode.insertBefore(lineContainer, this.__endBoundary);

	
	
	lineContainer.appendChild(lineContents);

	var newLine = documentCreateCSSLine(lineContainer.firstInsertionPoint(this.top));

	newLine.forceLineBreaksBeforeAfter();
	return newLine;
}

/**
 * This method does three things for a non-pre line
 * - removes collapsed whitespace at the beginning of the line
 * - removes collapsed whitespace at the end of the line
 * - POST05: removes collapsed whitespace within the line
 */
BoundedLine.prototype.normalizeWhitespace = function()
{
	var lineParentNode = this.lineParent;
	if(document.defaultView.getComputedStyle(lineParentNode, null).getPropertyValue("white-space") == "pre")
		return;

	// for now - don't normalize token lines
	if(this.empty)
		return;

	var range = document.createRange();
	range.selectNodeContents(lineParentNode);
	range.setStart(this.lastInsertionPoint.ipNode, this.lastInsertionPoint.ipOffset);
	if(this.endBoundary)
		range.setEndBefore(this.endBoundary);
	else
		range.setEndAfter(lineParentNode.lastChild);
	range.deleteContents();

	// then normalize start
	range.selectNodeContents(lineParentNode);
	if(this.startBoundary)
		range.setStartAfter(this.startBoundary)
	else
		range.setStartBefore(lineParentNode.firstChild);
	range.setEnd(this.firstInsertionPoint.ipNode, this.firstInsertionPoint.ipOffset);
	range.deleteContents();

	// partially selected text insertion points: start will now be gone
	if(this.__firstInsertionPoint.ipNode.nodeType == Node.TEXT_NODE)
		this.__firstInsertionPoint.ipOffset = 0;

	// recalculate last insertion point
	this.__recalculateLastInsertionPoint();

	// reset Range
	this.__lineRange.selectNodeContents(lineParentNode);
	this.__lineRange.setStart(this.firstInsertionPoint.ipNode, this.firstInsertionPoint.ipOffset);
	this.__lineRange.setEnd(this.lastInsertionPoint.ipNode, this.lastInsertionPoint.ipOffset);
	
	/**
	// set end after deletion of start - covers complication when start and end are in same node
	if(this.__lastInsertionPoint.ipNode.nodeType == Node.ELEMENT_NODE)
		this.__lastInsertionPoint.ipOffset = (this.endBoundary) ? this.endBoundary.offset : lineParentNode.childNodes.length;
	else
		this.__lastInsertionPoint.ipOffset = this.lastInsertionPoint.ipNode.nodeValue.length;
	**/
}

/**
 * Deleting the start boundary will delete the bounded line itself. This only works for start boundary's with EMPTY_CONTENTTYPE.
 * This is equivalent to ContainedLine's "removeContainer".
 */
BoundedLine.prototype.deleteStartBoundary = function()
{
	if(this.__startBoundary && (this.__startBoundary.contentType == Element.EMPTY_CONTENTTYPE))
	{
		// BUG SEEMS TO BE: |</div>|<hr/><ul><li> vs </div>|<hr/>|<hr/></ul> ie/ now nothing after - HOW TO KNOW NOT TO JUMP!
		var ip = documentCreateInsertionPoint(this.top, this.__startBoundary.parentNode, this.__startBoundary.offset);
		var result = ip.backOne();
		if(result == InsertionPoint.AT_TOP)
			ip = null;
		this.__startBoundary.parentNode.removeChild(this.__startBoundary);
		if(ip)
		{
			//if(result == InsertionPoint.CROSSED_BLOCK)
			ip.forwardOne();
		}
		else
			ip = this.top.firstInsertionPoint(this.top);
		return ip;
	}

	return this.firstInsertionPoint;
}

// OTHER ISSUES: not start boundary ie/ if null but <div>|<hr>stuff< ... remove div?

/**
 * DeleteContents: the line becomes empty
 */
BoundedLine.prototype.deleteContents = function()
{
	if(this.emptyLine)
		return this; // it's a noop!

	// empty out contents
	this.__lineRange.deleteContents();
	this.__elementCount = 0;

	// case 1: line starts or ends with empty element - this is enough to make an empty line
	var elementToSelect = (this.startBoundary && (this.startBoundary.contentType == Element.EMPTY_CONTENTTYPE)) ? this.startBoundary : null;
	elementToSelect = (!elementToSelect && this.endBoundary && (this.endBoundary.contentType == Element.EMPTY_CONTENTTYPE)) ? this.endBoundary : elementToSelect;
	if(elementToSelect)
	{
		this.__firstInsertionPoint = __createInsertionPoint(this.top, elementToSelect.parentNode, (elementToSelect.offset+1));
		this.__lastInsertionPoint = this.__firstInsertionPoint.clone();
		return this;
	}
		
	// case 2: empty line needs selectable token
	var tokenTextNode = document.createTextNode(STRING_NBSP);
	if(this.startBoundary)
		this.startBoundary.parentNode.insertAfter(tokenTextNode, this.startBoundary);
	else
		this.endBoundary.parentNode.insertBefore(tokenTextNode, this.endBoundary);

	this.__firstInsertionPoint = __createInsertionPoint(this.__top, tokenTextNode, 0);
	this.__lastInsertionPoint = this.__firstInsertionPoint.clone();
	this.__lineRange.selectNodeContents(tokenTextNode);
	return this;
}

/**
 * POST05:
 * - rebuild line (reset constants)
 */
BoundedLine.prototype.appendContent = function(contents)
{
	// first normalize whitespace in line
	this.normalizeWhitespace();

	// nix any token if there is one
	if(this.emptyLine && !this.__lineRange.collapsed)
		this.__lineRange.deleteContents();

	if(this.endBoundary)
		this.endBoundary.parentNode.insertBefore(contents, this.endBoundary);
	else
		this.lineParent.appendChild(contents);

	return this.lastInsertionPoint;
}

BoundedLine.prototype.deleteLineParent = function()
{
	var previp = this.lineParent.firstInsertionPoint;
	var result = previp.backOne();
	if(result != InsertionPoint.AT_TOP)
	{
		this.lineParent.parentNode.removeChildOnly(this.lineParent);
		previp.forwardOne();
	}
	return previp;
}

/**
 * Delete handler when selection is at the start of a bounded line
 *
 * This is driven purely by CSS which works for XHTML but is unlike to work for semantically richer markup
 * 
 * - POST05: handle the case of <div>xx</div><div>|<hr>stuff</div> where lineParent is deleted
 * and content of div is put into previous div or perhaps just the line parent is removed. ie/ use deleteLineParent
 */ 
BoundedLine.prototype.deleteStructure = function()
{
	// Case 1: delete empty element if bounded line starts with an empty element
	if(this.startBoundary && (this.startBoundary.contentType == Element.EMPTY_CONTENTTYPE))
	{	
		var ip = this.deleteStartBoundary();
		return ip;
	}

	// Case 2: no previous line then noop
	var pline = this.previousLine;
	if(!pline)
		return this.firstInsertionPoint;

	// Case 3: bounded line or previous line in table cell but both are not!
	var tba = this.tableCellAncestor;
	var ptba = pline.tableCellAncestor;
	if((tba || ptba) && (ptba != tba))
		return this.firstInsertionPoint;

	// Case 4: if previous line's lineParent is not the same as this line's line parent and line is empty ...

	// Case 4: if previous line is contained then merge contents with previous line and delete the line
	if(pline.lineType == CSSLine.CONTAINED_LINE)
	{
		ip = pline.appendContent(this.lineContents);
		this.deleteContents();
		return ip;
	}

	// Case 5: pline is bounded and has an end boundary that is different than this line's start boundary: special case - noop
	if(pline.endBoundary)
		return this.firstInsertionPoint; // May want instead to delete that boundary or delete lineParent of line

	// Case 6: otherwise merge this's contents into pline and empty this
	var ip = pline.appendContent(this.lineContents);
	this.deleteContents();
	return ip;
}

/**
 * Turns the bounded line into a token line and returns the token's text node
 */
BoundedLine.prototype.setToTokenLine = function()
{
	if(!this.emptyLine)
		this.deleteContents();

	// could change to insert NBSP!
	var textNode = document.createTextNode(STRING_NBSP);
	if(this.startBoundary)
		this.startBoundary.parentNode.insertAfter(textNode, this.startBoundary);
	else
		this.endBoundary.parentNode.insertBefore(textNode, this.endBoundary);

	return textNode;
}

/**************************************************************************************************
 * CSSRange is a Range with methods driven by CSS.
 * 
 * POST05: 
 * - rename to be CSSRange (from CSSTextRange)
 * - for now, just implement as a Range
 * - rework to remove any explicit reference to XHTML (move into eDOM XHTML)
 * - creation should make sure that the range boundaries are insertion points
 **************************************************************************************************/

/**
 * Create a CSSTextRange
 */
Document.prototype.createCSSTextRange = documentCreateCSSTextRange; 
function documentCreateCSSTextRange(range, top)
{	
	range.__top = top;
	range.__markTextBoundaries(true);
	return range;
}

function __createCSSRangeFromIPs(top, startip, endip)
{
	var range = document.createRange();
	range.selectNode(startip.ipNode);
	range.setStart(startip.ipNode, startip.ipOffset);
	range.setEnd(endip.ipNode, endip.ipOffset);
	range = documentCreateCSSTextRange(range, top);
	return range;
}

Range.prototype.__defineGetter__(
	"firstInsertionPoint",
	function()
	{
		return documentCreateInsertionPoint(this.__top, this.startContainer, this.startOffset);
	}
);

Range.prototype.__defineGetter__(
	"lastInsertionPoint",
	function()
	{
		return documentCreateInsertionPoint(this.__top, this.endContainer, this.endOffset);
	}
);

Range.prototype.__defineGetter__(
	"lines",
	function()
	{
		var lines = new Array();
		var startip = this.firstInsertionPoint;
		var nextLine = documentCreateCSSLine(startip);
		do 
		{
			lines.push(nextLine);
			nextLine = nextLine.nextLine;
		}
		while(nextLine && (this.containsInsertionPoint(nextLine.firstInsertionPoint)));

		return lines;
	}
);

Range.prototype.__defineGetter__(
	"firstLine",
	function()
	{
		var startip = this.firstInsertionPoint;
		return documentCreateCSSLine(startip);
	}
);


Range.prototype.containsInsertionPoint = function(ipToTest)
{
	var testRange = this.cloneRange();
	if(testRange.startContainer.nodeType == Node.TEXT_NODE)
		testRange.setStart(testRange.startContainer.parentNode, testRange.startContainer.offset);
	if(testRange.endContainer.nodeType == Node.TEXT_NODE)
		testRange.setEnd(testRange.endContainer.parentNode, (testRange.endContainer.offset+1));
	
	var nodeRange = document.createRange();
	nodeRange.selectNodeContents(ipToTest.ipReferencedNode);

	if(!testRange.containsRange(nodeRange))
		return false; 
	return true;
}

Range.prototype.__defineGetter__(
	"firstInsertionPoint",
	function()
	{
		if(this.startContainer.nodeType == Node.TEXT_NODE)
			return documentCreateInsertionPoint(this.top, this.startContainer, this.startOffset);

		if(this.startContainer.childNodes.length > this.startOffset)
		{
			var seed = this.startContainer.childNodes[this.startOffset];
			if(seed.nodeType == Node.TEXT_NODE)
				return documentCreateInsertionPoint(this.top, seed, 0);
			if(seed.contentType == Element.EMPTY_CONTENTTYPE)
				return documentCreateInsertionPoint(this.top, this.startContainer, this.startOffset);
			var ipni = new __IPNodeIterator(this.startContainer);
			ipni.currentNode = seed;
			ipni.nextNode();
			seed = ipni.currentNode;

			if(!seed)
			{
				ipni.previousNode();
				seed = ipni.nextNode();
			}
			if(!seed)
				return null;
			return documentCreateInsertionPoint(this.top, seed.parentNode, seed.offset);
		}

		var ipni = new __IPNodeIterator(this.startContainer);
		var seed = ipni.setToEnd();				
		if(!seed)
			return null;
		return documentCreateInsertionPoint(this.top, seed.parentNode, seed.offset);
	}
);

/**
 * 
 */
Range.prototype.selectInsertionPoint = function(ip)
{
	//stupid bug on windows. It doesn't allows work here
	// we loose the cursor after that, but it seems to be only
	// an issue with empty paragraphs...
	// see http://cvs.wyona.org/cgi-bin/bugzilla/show_bug.cgi?id=1715 for how to reproduce it
	try {
		this.selectNode(ip.ipNode);
	}
	catch (e) {}
	this.setStart(ip.ipNode, ip.ipOffset);
	this.collapse(true);
}

/**
 * POST05: rename to "topCommonAncestorNode"?
 *
 * @return topmost common ancestor for Range
 */
Range.prototype.__defineGetter__(
	"top",
	function() {return this.__top;}
);

function parentBelow(ancestor, element)
{
	var parentNode = element;
	var parentBelow;
	do
	{
		parentBelow = parentNode;	
		parentNode = parentNode.parentNode;
	}
	while(parentNode != ancestor);

	return parentBelow;
}

/**
 * Moves Range boundaries up to include exclusive parents ie/ parentNodes that only contain the selected Range.
 *
 * POST05: make sure InsertionPoint calculation for a Range a/cs for such higher boundaries
 */
Range.prototype.includeExclusiveParents = function()
{	
	var startip = this.firstInsertionPoint;
	var startLine = startip.line;
	var prevResult = InsertionPoint.SAME_LINE; // default
	// check if already at start of line unless have bounded line with start boundary
	if(!((startLine.lineType == CSSLine.BOUNDED_LINE) && startLine.startBoundary))
	{
		var previp = startip.clone();
		prevResult = previp.backOne();

		if(prevResult == InsertionPoint.AT_TOP)
		{
			this.setStart(this.top, 0);	
		}
		else if(prevResult == InsertionPoint.CROSSED_BLOCK)
		{	
			var commonAncestorRange = document.createRange();
			commonAncestorRange.setStart(previp.ipNode, previp.ipOffset);
			commonAncestorRange.setEnd(startip.ipNode, startip.ipOffset);
			var startContainer = commonAncestorRange.commonAncestorContainer;
			var startOffset = parentBelow(startContainer, startip.ipNode).offset;
			this.setStart(startContainer, startOffset);
		}
		// SAME_LINE - see if different parent
		else if(previp.ipNode != startip.ipNode)
		{
			this.setStart(previp.ipNode, (previp.ipOffset+1));
		}
	}

	var endip = this.lastInsertionPoint;
	var endLine = endip.line;
	var nextResult = InsertionPoint.SAME_LINE; // default
	// check if already at start of line unless have bounded line with start boundary
	if(!((endLine.lineType == CSSLine.BOUNDED_LINE) && endLine.endBoundary))
	{
		var nextip = endip.clone();
		nextResult = nextip.forwardOne();

		if(nextResult == InsertionPoint.AT_TOP)
		{
			this.setEnd(this.top, this.top.childNodes.length);
		}
		else if(nextResult == InsertionPoint.CROSSED_BLOCK)
		{
			var commonAncestorRange = document.createRange();
			commonAncestorRange.setStart(endip.ipNode, endip.ipOffset);
			commonAncestorRange.setEnd(nextip.ipNode, nextip.ipOffset);
			var endContainer = commonAncestorRange.commonAncestorContainer;
			var endOffset = parentBelow(endContainer, endip.ipNode).offset + 1; // go right beyond the endip.ipNodes parent
			this.setEnd(endContainer, endOffset); 
		}
		else if(nextip.ipNode != endip.ipNode)
		{
			this.setEnd(nextip.ipNode, (nextip.ipOffset-1));
		}
	}

	// maximize covers top?
	return((nextResult == InsertionPoint.AT_TOP) && (prevResult == InsertionPoint.AT_TOP));
}

/**
 * Extract the contents of a Range based on CSS: this is greedy, obeys the top boundary and a/cs for table settings
 */
Range.prototype.extractContentsByCSS = function()
{
	var extractedContents = document.createDocumentFragment();

	var startTableRanges = this.__startTableRanges();
	var endTableRanges = this.__endTableRanges();

	for(var i=0; i<startTableRanges.length; i++)
		extractedContents.appendChild(startTableRanges[i].__simpleExtractContentsByCSS());

	if(!this.collapsed)
		extractedContents.appendChild(this.__simpleExtractContentsByCSS());

	for(var i=0; i<endTableRanges.length; i++)
		extractedContents.appendChild(endTableRanges[i].__simpleExtractContentsByCSS());

	// correct default insertion point if there were table cells in line first!
	if(startTableRanges.length)
		this.selectInsertionPoint(startTableRanges[0].firstInsertionPoint);
	
	return extractedContents;
}

/**
 * Don't account for table cells
 *
 * POST05: 
 * - do the ip marker stuff more neatly ie/ see sequence below to account for whitespace. Too complex
 */
Range.prototype.__simpleExtractContentsByCSS = function()
{
	if(this.collapsed) // return empty document fragment
		return document.createDocumentFragment();

	var startip = this.firstInsertionPoint;
	var returnip = startip.clone();
	var partialEndSelection = false;
	var endip = this.lastInsertionPoint;

	if(this.includeExclusiveParents())
	{	
		// special case one: surrounds top!
		this.top.appendChild(document.createTextNode(STRING_NBSP));
		returnip = null;
	}
	// a/c for two things: start of line and partial selection of last line
	else
	{
		var startLine = startip.line;
		var endLine = endip.line;

		// test if crossed block or reference element node: go back one if we do 
		var returnResult = InsertionPoint.SAME_LINE;
		if((this.startContainer.nodeType == Node.ELEMENT_NODE) || (this.startContainer != startip.ipNode))
		{		
			returnResult = returnip.backOne();
			if(returnResult == InsertionPoint.AT_TOP)
				returnip = null;
		}

		// if only partially select end line then must merge unless it is the same as the start line or whole start line is in the range to be deleted
		if(!endip.equivalent(endLine.lastInsertionPoint) && !startLine.equivalent(endLine) && !startip.equivalent(startLine.firstInsertionPoint))
			partialEndSelection = true;
	}

	var extractedContents = this.extractContents(); 

	// note that this will only remove immediate container of a contained line. If a line has multiple levels of 
	// containment then just the first is removed and the line isn't "merged". May change this?
	if(partialEndSelection)
	{
		if(returnip)
		{
			var returnline = returnip.line;
			var returnipoffset = returnip.lineOffset;
			var endlineip = endip.line.deleteStructure();
			returnip = documentCreateCSSLine(endlineip).insertionPointAt(returnipoffset);
		}
		else
			endip.line.deleteStructure();
	}

	// restore
	this.selectInsertionPoint((returnip ? returnip : this.top.firstInsertionPoint(this.top)));

	return extractedContents;
}

Range.prototype.__startTableRanges = function()
{
	var ranges = new Array();

	while(!this.collapsed)
	{
		var startip = this.firstInsertionPoint;
		var sta = startip.tableCellAncestor;

		if(!sta)
			break;

		var eta = this.lastInsertionPoint.tableCellAncestor;

		if(eta != sta)
		{
			// move this range on beyond this table cell
			var lip = sta.lastInsertionPoint(this.top);
			var nextip = lip.clone();
			nextip.forwardOne();
			this.setStart(nextip.ipNode, nextip.ipOffset);
			startRange = __createCSSRangeFromIPs(sta, startip, lip);
		}
		else // startRange is this Range!
		{	
			startRange = documentCreateCSSTextRange(this.cloneRange(), sta);
			this.collapse(false);
		}

		ranges.push(startRange); // if this range ends up collapsed then all are table ranges ...
	}

	return ranges;
}

Range.prototype.__endTableRanges = function()
{
	var ranges = new Array();

	while(!this.collapsed)
	{
		var endip = this.lastInsertionPoint;
		var eta = endip.tableCellAncestor;

		if(!eta)
			break;

		var sta = this.firstInsertionPoint.tableCellAncestor;

		if(eta != sta)
		{
			// move this range on beyond this table cell
			var fip = eta.firstInsertionPoint(this.top);
			var previp = fip.clone();
			previp.backOne();
			this.setEnd(previp.ipNode, previp.ipOffset);
			endRange = __createCSSRangeFromIPs(eta, fip, endip);
		}
		else // startRange is this Range!
		{
			endRange = documentCreateCSSTextRange(this, eta);
			this.collapse(true);
		}

		ranges.push(endRange); // if this range ends up collapsed then all are table ranges ...
	}

	return ranges;
}

/***************************************** The remainder of eDOM is due to be rewritten POST05 ******************/

/**
 * POST04: handle not just text ranges; move over to use VisualText tree 
 */
Range.prototype.__defineGetter__(
	'textNodes',
	function() {

		// normalize safely: sets marker by side effect
		this.normalizeText();

		// not at start of start text node so split it 
		// split the text.
		var currentNode = this.startContainer;
		if(this.startOffset != 0)
 		{
			this.startContainer.splitText(this.startOffset);
			this.__restoreTextBoundaries(); // needed to restore endContainer when in same node!
			currentNode = this.startContainer;
		}

		// not at end so split end node of this 
		if(this.endOffset != this.endContainer.length) 
			this.endContainer.splitText(this.endOffset);

		var textTW = document.createTreeWalker(this.commonAncestorContainer,
							   NodeFilter.SHOW_TEXT,
							   null,
							   false);

		// change to walk elements or text and nonEmptyText ie/ show all!
		var textNodes = new Array();
		textTW.currentNode = currentNode;
		var testNode = textTW.currentNode;
		while(testNode)
		{
			// must allow empty text to get this far as last node could be empty (offset = 0 before
			// split)
			if(__NodeFilter.nonEmptyText(testNode) == NodeFilter.FILTER_ACCEPT)
				textNodes[textNodes.length] = testNode;
			// at end!
			if(testNode == this.endContainer)
				break;
			testNode = textTW.nextNode();
		}

		// do one global one at the end?
		this.__restoreTextBoundaries();

		return textNodes;
	}
);

/**
 * Does one range contain another?
 *
 * @argument compareRange range to check
 * @returns true if one contains the other
 * 
 * POST04: 
 * - try out compareNode of Mozilla. May be no use case for Range in Range - all Node in Range
 * - see if this is called too much (ie/ trace it)
 */
Range.prototype.containsRange = function(compareRange)
{
	// Compare against a maximized version of this range.
	var baseRange = this.cloneRange();
	baseRange.__maximizeContext();			
	
	var val = baseRange.compareBoundaryPoints(Range.START_TO_START, compareRange);
	if(baseRange.compareBoundaryPoints(Range.START_TO_START, compareRange) == 1)
		return false;

	if(baseRange.compareBoundaryPoints(Range.END_TO_END, compareRange) == -1)
		return false;

	return true;	
}

/**
 * recursively combine adjacent editable elements in a Range.
 *
 * POST04:
 * - change to generic: normalizeInlineElements
 * - move out of Range and make this an Element method. Then if necessary do a 
 * Range equivalent that just resets boundaries. Typical use case is to 
 * selectNode and then call this. IMPORTANT
 * - not for text ranges: formalize this - consider general issue of range setting. Caller should expand
 * a range appropriately. This and other methods SHOULD NOT expand it by side effect.
 * - efficiency: only check top most nodes for inclusion in Range: once find included node then don't
 * check their children ... [general pattern for all Range change operations ...]
 */
Range.prototype.normalizeElements = function(elementName)
{
	elementName = elementName.toLowerCase();

	// note: no range markers as range isn't a text range

	// normalize element filter checks two things:
	// - is the element within the Range
	// - does the element have an identical previous sibling (prelude to merge!)
	var range = this;
	var normalizeFilter = function(node)
	{
		var compareRange = document.createRange();
		compareRange.selectNodeContents(node);			
		
		if(!range.containsRange(compareRange))
		{
			return NodeFilter.FILTER_REJECT;
		}
		compareRange.detach();
							
		if(node.nodeName.toLowerCase() == elementName) 
		{
			var previousSibling = node.__editablePreviousSibling;
			if(previousSibling && previousSibling.match && previousSibling.match(node))
			{
				return NodeFilter.FILTER_ACCEPT;
			}
	
			return NodeFilter.FILTER_SKIP;
		}	  	
		return NodeFilter.FILTER_SKIP;		
	}

	var namedElTW = document.createTreeWalker(this.commonAncestorContainer,
					   NodeFilter.SHOW_ELEMENT,
					   normalizeFilter,
					   false);

	// recurse through a tree processing the leaf nodes before their parents
	this.normalizeElementsInTree = function(namedElTW)
	{
		// children will all be nodes of the type we want to rename 
		var child = namedElTW.firstChild();

		while(child != null)
		{
			// process the next child before processing its parent!
			this.normalizeElementsInTree(namedElTW);
			namedElTW.currentNode = child; // jump back up!
			var childToMerge = child;
			child = namedElTW.nextSibling(); // moves current on if one more!

			var childToMergeInto = childToMerge.__editablePreviousSibling;
			// delete intermediate empty text nodes
			while(childToMergeInto.nextSibling != childToMerge)
				childToMergeInto.parentNode.removeChild(childToMergeInto.nextSibling);
			var contentsToMerge = document.createRange();
			contentsToMerge.selectNodeContents(childToMerge);
			childToMergeInto.appendChild(contentsToMerge.extractContents());
			childToMergeInto.normalize();
			// now delete the merged child
			childToMerge.parentNode.removeChild(childToMerge);	

		}
	}

	// now normalize elements
	this.normalizeElementsInTree(namedElTW);
}

/**
 * Change all elements of one type to another type! This will NOT normalize elements.
 */
Range.prototype.renameElements = function(currentName, newName)
{
	// before processing, let's mark text boundaries if the range has them but only if not already set
	this.__markTextBoundaries(false);

	currentName = currentName.toLowerCase();
	newName = newName.toLowerCase();

	// - is the element within the Range
	// - does the element have a particular name
	var range = this;
	var renameFilter = function(node)
	{
		var compareRange = document.createRange();
		compareRange.selectNodeContents(node);			
		
		if(!range.containsRange(compareRange))
			return NodeFilter.FILTER_REJECT;
		compareRange.detach();
		if(node.nodeName.toLowerCase() == currentName) 
			return NodeFilter.FILTER_ACCEPT;
		return NodeFilter.FILTER_SKIP;		
	}

	var namedElTW = document.createTreeWalker(this.commonAncestorContainer,
					   NodeFilter.SHOW_ELEMENT,
					   renameFilter,
					   false);

	// recurse through a tree processing the leaf nodes before their parents
	var atLeastOneRenamed = false;
	this.renameElementsInTree = function(namedElTW)
	{
		// children will all be nodes of the type we want to rename 
		var child = namedElTW.firstChild();

		while(child != null)
		{
			// process the next child before processing its parent!
			this.renameElementsInTree(namedElTW);
			namedElTW.currentNode = child; // jump back up!
			var childToRename = child;
			child = namedElTW.nextSibling(); // moves current on if one more!
			childToRename.parentNode.replaceChildOnly(childToRename, newName);
			atLeastOneRenamed = true;
		}
	}

	// now normalize elements (test result!)
	this.renameElementsInTree(namedElTW);
		
	// now restore the text boundaries
	if(atLeastOneRenamed)
	{
		this.__restoreTextBoundaries();
		return true;
	}
	return false;
}

/**
 * Normalize text in a Range without effecting its boundaries
 */
Range.prototype.normalizeText = function()
{
	this.__markTextBoundaries(false);
	this.commonAncestorContainer.parentElement.normalize();
	this.__restoreTextBoundaries();
}

var keepTxtNodes = null;

/**
 * Does a Range have a style?
 */
Range.prototype.hasStyle = function(styleName, styleValue)
{
	// note that this can split text nodes
	var textNodes = this.textNodes;
	keepTxtNodes = textNodes;

	for(i=0; i<textNodes.length; i++)
	{
		var textContainer = textNodes[i].parentNode;

		// if any node doesn't have the style then the Range doesn't have it!
		if(document.defaultView.getComputedStyle(textContainer, null).getPropertyValue(styleName) != styleValue)
		{
			//this.normalizeText();
			return false;	
		}
	}

	// efficiency given that apply comes next - will be combined with apply POST 03
	// this.normalizeText();

	return true;
}

/**
 * Does a Range have a style?
 */
Range.prototype.hasClass = function(className)
{
	// note that this can split text nodes
	var textNodes = this.textNodes;
	keepTxtNodes = textNodes;
 
	for(var i=0; i<textNodes.length; i++)
	{
		var textContainer = textNodes[i].parentNode;
		// if any node doesn't have the style then the Range doesn't have it!
		if(!textContainer.hasClassOrIsElement(className))
		{
			//this.normalizeText();
			return false;	
		}
	}
	return true;
}

/**
 * @returns all the elements with a particular display value
 *
 * POST04:
 * - don't expand beyond text but skip the text
 */
Range.prototype.elementsWithDisplay = function(displayValue)
{
	// expand range to test to cover start/end of any text nodes
	var range = this.cloneRange();
	if(range.startContainer.nodeType == Node.TEXT_NODE)
		range.setStart(range.startContainer, 0);
	if(range.endContainer.nodeType == Node.TEXT_NODE)
		range.setEnd(range.endContainer, range.endContainer.nodeValue.length);

	// expand range to test to cover start/end of any text nodes
	var displayFilter = function(element)
	{
		if(document.defaultView.getComputedStyle(element, null).getPropertyValue("display") != displayValue)
			return NodeFilter.FILTER_SKIP;

		var elementRange = document.createRange();
		elementRange.selectNodeContents(element);
		if(!range.containsRange(elementRange))
			return NodeFilter.FILTER_REJECT; 
		return NodeFilter.FILTER_ACCEPT;
	}

	// Tree to allow us to walk and loop through seeds producing unique lines
	var tw = document.createTreeWalker(range.commonAncestorContainer.parentElement,
					   NodeFilter.SHOW_ELEMENT,
					   displayFilter,
					   false);

	var displayElements = new Array();
	var displayElement = tw.firstChild();
	while(displayElement)
	{
		displayElements.push(displayElement);
		displayElement = tw.nextNode();	 
	}

	return displayElements;
} 
// - POST05: efficiency: make sure not called too often, in particular from read-only operations
// - POST05: make public or at least have collapse and other methods reset this method!
Range.prototype.__markTextBoundaries = function(replace)
{
	if(!replace && (this.startTextPointer || this.endTextPointer))
		return;

	if(this.startContainer.nodeType == Node.TEXT_NODE)
	{
		this.startTextPointer = new __TextPointer(this.__top, this.startContainer, this.startOffset);
	}
	else
	{
		if(this.startTextPointer)
			delete this.startTextPointer;
	}

	// optimization possibility: calculate end from start ie/ combine both into a CSSTextRangeMarker
	if(this.endContainer.nodeType == Node.TEXT_NODE)
	{
		if(this.collapsed)
			this.endTextPointer = this.startTextPointer;
		else
		{
			this.endTextPointer = new __TextPointer(this.__top, this.endContainer, this.endOffset);
		}
	}
	else
	{
		if(this.endTextPointer)
			delete this.endTextPointer;
	}
}

/*
 * Restore a Range to the boundaries set by MarkTextBoundaries if they exist. Otherwise leave Range as is.
 * 
 * POST04: could change this to always take up text boundaries even where there is no pointer etc. ie/ textBoundaries?
 * - move to being a utility/impl method of CSSTextRange
 * - if one or other end fails then revert to one end; one both fail then ?
 */ 
Range.prototype.__restoreTextBoundaries = function()
{
	if(this.startTextPointer)
	{
		var startNode = this.startTextPointer.referencedTextNode; // avoid order error: start after end as being set
		this.selectNode(startNode);
		this.setStart(this.startTextPointer.referencedTextNode, this.startTextPointer.referencedOffset);
	}
	if(this.endTextPointer)
	{	
		this.setEnd(this.endTextPointer.referencedTextNode, this.endTextPointer.referencedOffset);		
	}
}

Range.prototype.__clearTextBoundaries = function()
{
	if(this.startTextPointer)
		delete this.startTextPointer;
	if(this.endTextPointer)
		delete this.endTextPointer;
}

Range.prototype.__maximizeContext = function()
{
	// is start at beginning or end of text node?
	if((this.startContainer.nodeType == Node.TEXT_NODE) &&
	   ((this.startOffset == 0) || (this.startOffset == this.startContainer.nodeValue.length)))
	{
		// at beginning or end? Decides how to index within its context
		var selOffset = 1;
		if(this.startOffset == 0)
			selOffset = 0;

		// get in terms of parent
		var parentNode = this.startContainer.parentNode;
		var parentOffset = this.startContainer.offset;	

		if(this.collapsed)
		{
			this.selectNode(parentNode); // makes sure start is temporarily put after end
			this.setStart(parentNode, parentOffset+selOffset);
			this.collapse(true);
			return;
		}

		this.setStart(parentNode, parentOffset+selOffset);	
	}

	// is end at beginning or end of text node?
	if((this.endContainer.nodeType == Node.TEXT_NODE) &&
	   ((this.endOffset == 0) || (this.endOffset == this.endContainer.nodeValue.length)))
	{
		// at beginning or end? Decides how to index within its context
		var selOffset = 1;
		if(this.endOffset == 0)
			selOffset = 0;

		// get in terms of parent
		var parentNode = this.endContainer.parentNode;
		var parentOffset = this.endContainer.offset;

		this.setEnd(parentNode, parentOffset+selOffset);			
	}
}

/******************************** TextPointer ****************************************/

/*
 * Many methods (appendChild, normalize) that don't remove text from the DOM, do delete
 * a particular TextNode that holds the text at one time or another. They effect 
 * boundaries of Ranges if a Range begins or ends in a TextNode they manipulate. This
 * object, by recording an absolute offset for text, allows the restoration of a text
 * pointer even after specific text nodes disappear.
 *
 * Uses:
 * - restore a Range after expansion or contraction 
 * - restore a Range after its ends are effected by splitting or normalizing or being moved
 * 
 * Two accessors:
 * - referencedTextNode
 * - referencedOffset
 *
 * POST02:
 * - CSSTextRangeMarker may replace this: calculates end from start and start from a context
 * logical for a Range.
 * - issue: goToStart etc doesn't follow InsertionPoint logic and so treats whitespace as significant
 * - look into using xpointer like syntax for debugging: ie/ id/1/2 etc. Alternatively
 * may implement using xpointers where the child numbers change based on note manipulation
 * - look into dropping skip of non empty text (ie/ orphan white space) because it may
 * be orphaned by applying styles after having been part of a text node. That means that
 * restoration after applying styles may fail. Alternatively, may make routines like "mark"
 * jump to the "real" start or end of a line if already at the effective start or end of a
 * line
 * - may add ability to decide whether to end in one node or begin in a subsequent node. Right
 * now, the default always beings in a new node if it can. 
 * - maybe allow increment and decrement of offset to reflect user controller insertion
 * and deletion of preceding text of a particular size.
 */ 
function __TextPointer(root, text, offset)
{
	// public properties of this marker
	this.root = root;
	this.absTextOffset = 0;
	this.textNode = text; // original text node - may go away doing processing
	this.textOffset = offset; // original offset within text
	this.currentTextNode = text;
	this.currentTextOffset = offset;
	this.__goToStart = true;

	this.textTW = document.createTreeWalker(this.root,
					       NodeFilter.SHOW_TEXT,
					       __NodeFilter.nonEmptyText,
					       false);

	this.absTextOffset = 0;
	this.textTW.currentNode = this.root;
	for(var next = this.textTW.firstChild(); next != null; next = this.textTW.nextNode())
	{
		if(next == this.textNode)
		{
			this.absTextOffset += this.textOffset;
			break;
		}
		this.absTextOffset += next.nodeValue.length;
	}

	/*
         * Now know the absolute offset. If no text deletion then this makes text position
         * recoverable.
         * 
         * The following lets us work back from calculated offset to the current active 
         * text node at any time
         */
	// POST04: should do alert if can't find a TextNode - exception back to caller
	this.calculateTextNode = function()
	{
		this.textTW.currentNode = this.root;
		var lastTextNode = null; // null means no TextNode is referenced
		var endSoFar = possEndSoFar = 0;

		for(var currentTextNode = this.textTW.firstChild(); currentTextNode != null; currentTextNode = this.textTW.nextNode())
		{
			// at start of node - return with node and offset of 0
			if(this.__goToStart && (this.absTextOffset == possEndSoFar))	
			{
				this.currentTextNode = currentTextNode;
				this.currentTextOffset = 0;
				return;
			}

			possEndSoFar += currentTextNode.nodeValue.length;

			// if now within a node then return that node and the offset within it
			if((this.absTextOffset < possEndSoFar) || ((!this.__goToStart) && (this.absTextOffset == possEndSoFar)))
			{
				this.currentTextNode = currentTextNode;
				this.currentTextOffset = this.absTextOffset - endSoFar;
				return;
			}		

			endSoFar = possEndSoFar;
			lastTextNode = currentTextNode;
		}
		// don't like this: get here if goToStart is true but "start" is outside editable area
		this.currentTextNode = lastTextNode;
		this.currentTextOffset = lastTextNode.nodeValue.length;
		return;
	}
}

/*
 * The pointer could lead to the end of a text node or the start of the following node. This setting
 * decides which way to go.
 *
 * POST04: remove need for this or build into pointer creation. 
 */
__TextPointer.prototype.__defineSetter__(
	"goToStart",
	function(value) {this.__goToStart = value;}
);

/*
 * Return the offset within the text node currently referenced by the pointer
 */ 
__TextPointer.prototype.__defineGetter__(
	"referencedOffset",
	function() {
		return this.currentTextOffset;
	}
);

/*
 * Return the text node currently referenced by the pointer. Note that this also causes the pointer to
 * be recalculated. 
 *
 * POST04:
 * - separate calculation from accessors
 */
__TextPointer.prototype.__defineGetter__(
	"referencedTextNode",
	function() {
		this.calculateTextNode();
		return this.currentTextNode;
	}
);

/*
 * Pretty up toString
 */ 
__TextPointer.prototype.toString = function()
{
	return("TextPointer: " + this.referencedTextNode.nodeValue + ":" + this.referencedOffset);
}

/*************************************************************************************************************
 * __NodeFilter provides centralized access to frequently used node filters. They can be 
 * applied directly, within filter enabled methods and in tree traversal.
 *
 * POST05:
 * - will go as Range methods are tightened up 
 *************************************************************************************************************/

// Class to hold the filters
function __NodeFilter() {}

/*
 * need to filter out empty text nodes, those only in the DOM to format raw HTML
 *
 * POST04: rename to be "nonWhiteSpaceNode"
 */
__NodeFilter.nonEmptyText = function(node)
{
	if(node.nodeType != Node.TEXT_NODE) // may have text in el; POST04: may be no need for this
		return NodeFilter.FILTER_ACCEPT;

	if(node.nodeValue.length == 0) // empty node is obviously empty!
		return NodeFilter.FILTER_REJECT;

	if(/\S+/.test(node.nodeValue)) // any non white space visible characters
		return NodeFilter.FILTER_ACCEPT;

	if(/\u00A0+/.test(node.nodeValue)) // any nbsp 
		return NodeFilter.FILTER_ACCEPT;

	// POST04: if(node.isWhitespaceOnly) ...

	return NodeFilter.FILTER_REJECT;
}

/***************************************************************************************
 * Constants
 ***************************************************************************************/
const STRING_NBSP = "\u00A0";
const CHARCODE_NBSP = 160;
//const STRING_NBSP = "\u007B"; (test vals)
//const CHARCODE_NBSP = "123";
const CHARCODE_SPACE = 32;
const STRING_SPACE = "\u0020";
const CHARCODE_NEWLINE = "012";	
const STRING_LINEFEED = "\u000A";
const CHARCODE_TAB = "009";

/************************************** Test Feedback **************************************************/

var feedback = false;
function __Feedbackack(text)
{
	if(!feedback)
		return;

	var feedbackArea = document.getElementById("feedbackArea");
	if(!feedbackArea)
		return;
	var feedback = document.createTextNode(text);
	var feedbackBlock = documentCreateXHTMLElement("div");
	feedbackBlock.appendChild(feedback);
	feedbackArea.appendChild(feedbackBlock);
}
// for whatever reason, jsdoc needs this line