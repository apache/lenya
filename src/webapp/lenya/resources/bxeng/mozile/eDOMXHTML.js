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

// $Id: eDOMXHTML.js 1374 2005-09-03 11:29:39Z chregu $

/**********************************************************************************
 * eDOMXHTML.js V0.5
 *
 * The basic w3c DOM is not honed for interactive editing. eDOM extends the standard DOM
 * to make it easy to build CSS-enabled editing applications that work
 * within a browser. eDOMXHTML builds on eDOM for XHTML specific routines.
 *
 * This file represents the limits to completely CSS and Validation Module based
 * editing. You need to have HTML specific logic in some cases. As Mozile and eDOM
 * evolve this file will expand and contract. It is preferable if it goes away but
 * that may not be possible or efficient.
 *
 * POST04: 
 * - move all XHTML specific routines in here. This will include Range routines
 * (part of split of eDOM into eDOMCSS, eDOMHTML
 * - in particular containerless line stuff that mandates inserting "div"
 * parents
 * - open question: is new validation module useful for XHTML editing?
 * 
 **********************************************************************************/
  
/********************************* xhtml lists *******************************************
 *
 * These methods are XHTML specific (there's a general problem with lists
 * as CSS has no "list-container" display type) and partially selection model specific.
 *
 * POST04:
 * - turn methods into those of an XHTML Range object
 * - this stuff could move into eDOM iff methods allow a user to pass in array of 
 * list container names. Consider this step but only after work on issues with DL.
 * - add support for DL methods
 *
 *****************************************************************************************/

// As outdent or indent lines, need to make bounded or list-item contained lines into default contained lines. Could use "p" instead.
var defaultContainerName = "div";

/**
 * If one or more non list item lines in a range then turn them into list-items; if only list item lines
 * in a Range then promote the list-items up one level.
 */
function listLinesToggle(cssr, listContainerName, listContainerToChange)
{
	var lines = cssr.lines;

	var topToNormalize = cssr.commonAncestorContainer.parentElement;
	var newListElement = false;
	var toggle = false;
	var firstListElement;
	var lastListElement;
	for(var i=0; i<lines.length; i++)
	{
		var listElement = lines[i].listItemAncestor;
		if(!listElement)
		{
			if((lines[i].lineType == CSSLine.CONTAINED_LINE) && (lines[i].container == topToNormalize)) {
				topToNormalize = topToNormalize.parentNode;
			}
			// fix for http://cvs.wyona.org/cgi-bin/bugzilla/show_bug.cgi?id=2562 ( liste in tabelle funktioniert nicht richtig)
			if (lines[i].container.getCStyle("display") == "table-cell") {
				lines[i] = lines[i].setContainer(documentCreateXHTMLElement(defaultContainerName),false);
				if (lines[i].container.childNodes[0].data.replace(/ /g,"") == "") {
					lines[i].container.childNodes[0].data = STRING_NBSP;
				}
			}
				
			lines[i] = lines[i].setContainer(documentCreateXHTMLElement("li"), true); // ok - this a/cs for top
			// line may be bounded and empty => don't make into list-item!	
			if(lines[i].lineType == CSSLine.CONTAINED_LINE)
			{
				var listContainer = documentCreateXHTMLElement(listContainerName);
				
				lines[i].container.insertParent(listContainer);
				newListElement = true; 
			}				
		}
		// toggle only if one list element and its parent isn't top (catches catches case of only bounded line)
		else
		{
			// first one!
			if(!toggle && (listElement != cssr.top) && (listElement.parentNode != cssr.top))
			{
				firstListElement = listElement;
				toggle = true;
			}
			lastListElement = listElement;
		}
	}
	// if have a new list element then merge appropriate adjacent lists
	if(newListElement)
	{
		var range = document.createRange();
		range.selectNodeContents(topToNormalize);
		range.normalizeElements(listContainerName); 
	}
	// all list elements already so should toggle: BUT ONLY IF list container AND/OR list-item is not top!
	else if(toggle)
	{
		// there must be at least one element
		var firstListContainer = __topListContainer(firstListElement);
		var lastListContainer = __topListContainer(lastListElement);
		var range = document.createRange();
		range.selectNode(__topListContainer(firstListElement));
		if(firstListContainer != lastListContainer) {
			range.setEnd(lastListContainer.parentNode, lastListContainer.offset+1);
		}
		range = documentCreateCSSTextRange(range, cssr.top);		
		var anyRenamed = range.renameElements(listContainerToChange, listContainerName);
		if(!anyRenamed) {
			outdentLines(range); // range is still valid as wasn't used and all are list items!
		} else { 
			// must normalize [range might be old and gone now?
			range.normalizeElements(listContainerName);
		}
	}
}

// indent lists as lists and lines as lines (unlike listLines which makes or removes lists of all lines)
function indentLines(cssr)
{
	var lines = cssr.lines;	

	// first off, split list lines from non-list lines
	var listElements = new Array();
	var nonListLines = new Array();
	for(var i=0; i<lines.length; i++)
	{
		var listElement = lines[i].listItemAncestor;
		if(listElement)
		{
			// take all unique list elements
			if((listElements.length == 0) || (listElements[listElements.length-1] != listElement))
				listElements.push(listElement);
		}
		else
			nonListLines.push(lines[i]);
	}
	
	if(listElements.length)
	{
		// Special case: only list elements and the list element is top - no indent possible. Just return
		if((listElements.length == 1) && (listElements[0] == cssr.top))
			return;

		var topToNormalize = cssr.commonAncestorContainer.parentElement;

		// indent the list elements
		for(var i=0; i<listElements.length; i++)
		{
			if(!listElements[i].descendent(topToNormalize)) // if only now covering one line then move up!
				topToNormalize = listElements[i].parentNode;

			listElements[i].insertParent(listElements[i].parentNode.cloneNode(false));
		}
	
		var range = document.createRange();
		range.selectNodeContents(topToNormalize);
		range.normalizeElements("ul"); 
		range.normalizeElements("ol");	
	}
	// a/c for one non list line and it is the top line: insert intermediate container
	else if((nonListLines.length == 1) && nonListLines[0].topLine)
	{
		nonListLines[0] = nonListLines[0].setContainer(documentCreateXHTMLElement(defaultContainerName), true);
		nonListLines[0].setStyle("margin-left", "+40px");
		return;
	}
	
	// indent the non list lines (none are top!)
	for(var i=0; i<nonListLines.length; i++)
	{
		// turn bounded line into contained line
		if(nonListLines[i].lineType == CSSLine.BOUNDED_LINE)
		{
			// special case: empty bounded line - don't try to indent this!
			if(nonListLines[i].emptyLine)
				continue;
			nonListLines[i] = nonListLines[i].setContainer(documentCreateXHTMLElement(defaultContainerName), true);
		}
		// don't indent table cells!
		else if(nonListLines[i].containedLineType == ContainedLine.TABLE_CELL)
			continue;
		nonListLines[i].setStyle("margin-left", "+40px");
	}
}

function outdentLines(cssr)
{
	var lines = cssr.lines;
	// first off, split list lines from non-list lines
	var listElements = new Array();
	var nonListLines = new Array();
	for(var i=0; i<lines.length; i++)
	{
		var listElement = lines[i].listItemAncestor;
		if(listElement)
		{
			if((listElements.length == 0) || (listElements[listElements.length-1] != listElement))
				listElements.push(listElement);
		}
		else
			nonListLines.push(lines[i]);
	}

	// special cases: return and don't outdent if ...
	// - only one non list item and it is top
	// - only one list item and it is top 
	// - one or more list items and their parent (ul/ol) is top
	if(((listElements.length == 1) && (listElements[0] == cssr.top)) ||
	   ((listElements.length > 0) && (listElements[0].parentNode == cssr.top)) ||
	   ((nonListLines.length == 1) && (nonListLines[0].topLine)))
		return;

	// outdent the list elements
	for(var i=0; i<listElements.length; i++)
		__outdentListItem(listElements[i]);

	// outdent the non list lines
	for(var i=0; i<nonListLines.length; i++)
	{
		// turn bounded line into contained line
		if(nonListLines[i].lineType == CSSLine.BOUNDED_LINE)
		{
			// special case: empty bounded line - don't try to indent this!
			if(nonListLines[i].emptyLine)
				continue;
			nonListLines[i] = nonListLines[i].setContainer(documentCreateXHTMLElement("p"), true);
		}
		// don't indent table cells!
		else if(nonListLines[i].containedLineType == ContainedLine.TABLE_CELL)
			continue;

		nonListLines[i].setStyle("margin-left", "-40px");
	}
}

/**
 * POST04: consider merge into outdent with ul split being done once
 */
__outdentListItem = function(listItem)
{
	// if not the first element in the container then split the container and work on the new container
	var newContainer = listItem.parentNode.split(listItem.offset);
	if(newContainer)
		listItem = newContainer.firstChild;

	// promote list item and if is was the only editable element then delete the container
	var container = listItem.parentNode;
	var nextSibling = listItem.__editableNextSibling;
	var listItem = container.parentNode.insertBefore(listItem, container);
	if(!nextSibling) { // if container has no more children then nix it!
		var _par = container.parentNode;
		container.parentNode.removeChild(container);
	}
	// should we keep listItem? Only if it is not now in a list
	if(!(listItem.parentNode.nodeNamed("ul") || listItem.parentNode.nodeNamed("ol")))
	{
		// go through lines promoting where necessary
		var listContents = document.createRange();
		listContents.selectNodeContents(listItem);
		listContents = documentCreateCSSTextRange(listContents, listItem.parentNode);
		var linesInList = listContents.lines;
	
		// typical case: only one line, a list line: make into a div
		if(linesInList.length == 1) 
		{
			// only change name 
			if(linesInList[0].container == listItem)
				linesInList[0].setContainer(documentCreateXHTMLElement("p"), false);
		}
		else // first or last lines bounded - make into contained if necessary
		{
			// first line in list item is bounded by list-item on one side and isn't empty: put into "div"!
			if((linesInList[0].lineType == CSSLine.BOUNDED_LINE) && !linesInList[0].startBoundary && !linesInList[0].emptyLine)
				linesInList[0] = linesInList[0].setContainer(documentCreateXHTMLElement(defaultContainerName), true);
			var lastNo = linesInList.length - 1 ;
			if((linesInList[lastNo].lineType == CSSLine.BOUNDED_LINE) && !linesInList[lastNo].endBoundary && !linesInList[lastNo].emptyLine)
				linesInList[lastNo] = linesInList[lastNo].setContainer(documentCreateXHTMLElement(defaultContainerName), true);
		}
		var _par = listItem.parentNode;
		listItem.parentNode.removeChildOnly(listItem); // nix the list-item

	}
}

// POST04: move into Range methods - part of methods to expand the context of a range
function __topListContainer(node)
{
	var container = node;
	while(container.parentNode.nodeNamed("ul") ||
	      container.parentNode.nodeNamed("ol"))
		container = container.parentNode;
	return container;	
}	


/* sets the class attribute of an element */
Element.prototype.setClass = function(className) {
	this.setAttribute("class",className);
	
}

Element.prototype.getClass = function() {
	return this.getAttribute("class");
}

/********************************* XHTML specific line handling *******************/

/**
 * This is specific because we explicitly choose what container element to insert
 */
InsertionPoint.prototype.splitXHTMLLine = function()
{
	var line = this.line;

	if(line.lineType == CSSLine.BOUNDED_LINE)
	{
		if(line.emptyLine)
		{
			var textNode = line.setToTokenLine();
			var ip = documentCreateInsertionPoint(line.top, textNode, 0);
			this.set(ip);
			line = this.line;
		}

		// make into contained line
		var lineOffset = this.lineOffset;
		line = line.setContainer(documentCreateXHTMLElement(defaultContainerName), true);
		var ip = line.insertionPointAt(lineOffset);
		this.set(ip);
	}
	else if ((line.container == this.top)) {
		//don't do anything, if we're on top
		return true;
	}
	else if((line.container == this.top) || (line.container == line.tableCellAncestor))
	{
		
		var lineOffset = this.lineOffset;
		line = line.setContainer(documentCreateXHTMLElement(defaultContainerName), false);
		var ip = line.insertionPointAt(lineOffset);
		this.set(ip);
	}

	this.splitContainedLine();
}

/********************************* XHTML namespace ********************************/

const XHTMLNS = "http://www.w3.org/1999/xhtml"; // XHTML name space

/**
 * Ensure that element creation uses the XHTML name space only if necessary
 *
 * POST04:
 * - move any of the XHTML explicit stuff into eDOMXHTML.js or work on using default namespaces. 
 * - force use of name spaces for all cases: main draw back is can't use
 * XML serializer for basic HTML (need to do own in domlevel3.js) but that's fine.
 * - for non XHTML pages, ensure that the XHTML name space declaration is at the top of the document: add it if it isn't
 */
function documentCreateXHTMLElement(elementName)
{
	if(document.body)
		return document.createElement(elementName);
	
	// POST05: do more experiments with this. Do experiments: if insert HTML: then things look
	// better but don't work so well when doing plain name comparisons!
	return document.createElementNS(XHTMLNS, elementName.toLowerCase());
}

Element.prototype.hasXHTMLAttribute = function(attributeName)
{
	if(document.body)
		return this.hasAttribute(attributeName);
	
	return this.hasAttributeNS(XHTMLNS, attributeName);
}

Element.prototype.getXHTMLAttribute = function(attributeName)
{
	if(document.body)
		return this.getAttribute(attributeName);
	
	return this.getAttributeNS(XHTMLNS, attributeName);
}

/********************************** XHTML CSS Range *********************************/

/**
 * Assumption: for now that xhtml is adhered to and that there are no elements partially embedded in elements
 * ex/ [span]XX[span]YY[/span][/span]
 *
 * @argument styleName name of style
 * @argument styleValue valid valid for style
 *
 * POST04: 
 * - dont' use textNodes ... instead walk tree ala normalize; could use if text node partially in range then
 * split ...
 * - consider replacing compareRanges with compareNode (Mozilla): need to move from looking at node contents
 * and then including elements that contain those contents to making Range inclusive enough that nodes themselves
 * can be checked (efficiency from a comprehensive set of Range boundary setters)
 * - account for top where we won't style the whole of top: use span instead to allow more nuance with subsequent
 * settings: insert div in between
 * - allow "inline" span element to be specified explcitly
 * - allow to pass in a number of "inline style holders". For HTML, A and span should be equivalent in that
 * A should be split just like span is split as style is applied. STRONG/EM etc must be accounted for too.
 * They should be treated as inline style holders within reason. If inline holder has style of some sort
 * that needs to be preserved. May also treat FONT/B/I and other deprecated inline style holders as peers
 * of span.
 * - recognize non text styles and exception. Some styles should only be applied to elements explicitly 
 * - issue of [a] getting inline style. Not reset - only span's are! They are just overridden.
 * - recognize equivalency between different ways to express the same style. For example,
 * "bold" and its numeric equivalent for "font-weight". This may also require accepting a range
 * of "equivalent values" for a style that may not be exact matches in official style but which look
 * the same. "bolder" and "bold" for instance display the same: setting one where another already 
 * applies in a higher context is redundant.
 * - test code needed for messy HTML like spans within or overlapping other spanS. The latter isn't 
 * allowed in XHTML but most browsers tolerate it.
 * - ensure span is inline before removing it. If it is not inline then it has another purpose.
 * - if applied to text outside the XHTML name space then make sure span has the correct name space
 * setting.
 * - support concept of +/-# for numeric settings where settings are applied incrementally when a 
 * setting is already present either explicitly or inherited. Used for position and even for making
 * font-weight bolder ...
 * - if <span> expands to cover <div> or other parent then merge its style into that parent. Right
 * now, certain sequences will leave the span around if style is built up to incrementally cover a
 * larger block.
 */
if (typeof eDOM_bxe_mode == "undefined") {
	var eDOM_bxe_mode = false;
}
 
Range.prototype.styleText = function(styleName, foo, bar, namespaceURI)
{
	
	
	// if collapsed then return - works for inline style or block: make editor do work
	if(this.collapsed)
		return;

	// go through all text nodes in the range and apply style to 'em unless there already
	if(!keepTxtNodes)
	{
		textNodes = this.textNodes;
	}
	else
		textNodes = keepTxtNodes;

	// POST04: replace with walker - work like normalizeElements
	forLoop:
	for(i=0; i<textNodes.length; i++)
	{
		var textContainer = textNodes[i].parentNode;
		
		//check if that inline style already was applied somewhere within this block element
		while (textContainer && textContainer.getCStyle("display") == "inline") {
			if (textContainer.XMLNode.namespaceURI == namespaceURI && textContainer.XMLNode.localName == styleName) {
				continue forLoop;;
			}
			textContainer = textContainer.parentNode;
		}
		
		textContainer = textNodes[i].parentNode; 
		
		var styleHolder;
		
		if (namespaceURI != XHTMLNS) {
			var _node = new XMLNodeElement(namespaceURI,styleName,1,true);
			var styleHolder = _node._node;
			//remove _XMLNode, otherwise updateXMLNode gets confused later
			styleHolder._XMLNode = null;
		} else {
			var styleHolder = documentCreateXHTMLElement(styleName);
		}
		
		textContainer.insertBefore(styleHolder, textNodes[i]);
		styleHolder.appendChild(textNodes[i]);
		eDOMEventCall("NodeInserted",styleHolder);
		
		textNodes[i] = styleHolder.firstChild;
		textContainer = styleHolder;
		
	}
					
	this.__restoreTextBoundaries(); // restore boundaries after styles are applied: CAN NIX IF GET CONTAINER AT START?

	var treeTop = this.commonAncestorContainer.parentElement;
	if(document.defaultView.getComputedStyle(treeTop, null).getPropertyValue("display") == "inline")
		treeTop = treeTop.parentNode;

	treeTop.normalize(); // make sure text nodes are normalized

	treeTop.__normalizeXHTMLTextStyle(); // normalize the styles

	this.__restoreTextBoundaries(); // restore boundaries after styles are normalized.
	keepTxtNodes = null;
	return;
}

/**
 * Apply a link to a selection of text
 */
Range.prototype.linkText = function(hrefValue,titleValue)
{
	// if collapsed then return - works for inline style or block: make editor do work
	if(this.collapsed)
		return;

	// go through all text nodes in the range and link to them unless already set to this link
	var textNodes = this.textNodes;
	for(i=0; i<textNodes.length; i++)
	{
		var textContainer = textNodes[i].parentNode;

		// if selected text is part of a span or a then we need to give it an exclusive parent of its own
		// this would only happen when part of a text node is selected either at the beginning or end of a
		// range or both.
		if((textContainer.childNodes.length > 1) &&
		   (textContainer.nodeNamed("span") || textContainer.nodeNamed("a")))
		{ 
				var siblingHolder;

				// leave any nodes before or after this one with their own copy of the container
				if(textNodes[i].previousSibling)
				{
					var siblingHolder = textContainer.cloneNode(false);
					textContainer.parentNode.insertBefore(siblingHolder, textContainer);
					siblingHolder.appendChild(textNodes[i].previousSibling);	
				}

				if(textNodes[i].nextSibling)
				{
					var siblingHolder = textContainer.cloneNode(false);
					if(textContainer.nextSibling)
						textContainer.parentNode.insertBefore(siblingHolder, textContainer.nextSibling);
					else 
						textContainer.parentNode.appendChild(siblingHolder);
					siblingHolder.appendChild(textNodes[i].nextSibling);	
				}									
		}

		// from now on, we assume that text has an exclusive A or span parent OR it is inside a container
		// that can have an A inserted into it and around the text.
		if(textContainer.nodeName.toLowerCase() != "a")
		{
			// replace a span with an A
			if(textContainer.nodeNamed("span"))
				textContainer = textContainer.parentNode.replaceChildOnly(textContainer, "a");
			// insert A inside a non span or A container!
			else
			{	
				var linkHolder = documentCreateXHTMLElement('a');
				textContainer.insertBefore(linkHolder, textNodes[i]);
				linkHolder.appendChild(textNodes[i]);
				textContainer = linkHolder;		
			}
		}

		textNodes[i] = textContainer.firstChild;
		textContainer.setAttribute("href", hrefValue);
		if (titleValue) {
			textContainer.setAttribute("title", titleValue);
		}
	}

	// normalize A elements [may be a waste - why not normalizeElements at the node level?]
	var normalizeRange = document.createRange();
	normalizeRange.selectNode(this.commonAncestorContainer);
	normalizeRange.normalizeElements("a");
	normalizeRange.detach();

	// now normalize text  
	this.commonAncestorContainer.parentElement.normalize();
	this.__restoreTextBoundaries();
}

/**
 * Clear links from text
 */
Range.prototype.clearTextLinks = function()
{
	// if collapsed then return - works for inline style or block: make editor do work
	if(this.collapsed)
		return;
 
	// go through all text nodes in the range and link to them unless already set to this link
	var textNodes = this.textNodes;
	for(i=0; i<textNodes.length; i++)
	{		
		// figure out this and then it's on to efficiency before subroutines ... ex of sub ... 
		// try text nodes returning one node ie/ node itself! could cut down on normalize calls ...
		var textContainer = textNodes[i].parentNode;

		if(textContainer.nodeNamed("span") && textContainer.getAttribute("class") == "a" )
		{
			if(textContainer.childNodes.length > 1)
			{
				var siblingHolder;

				// leave any nodes before or after this one with their own copy of the container
				if(textNodes[i].previousSibling)
				{
					var siblingHolder = textContainer.cloneNode(false);
					textContainer.parentNode.insertBefore(siblingHolder, textContainer);
					siblingHolder.appendChild(textNodes[i].previousSibling);	
				}

				if(textNodes[i].nextSibling)
				{
					var siblingHolder = textContainer.cloneNode(false);
					if(textContainer.nextSibling)
						textContainer.parentNode.insertBefore(siblingHolder, textContainer.nextSibling);
					else 
						textContainer.parentNode.appendChild(siblingHolder);
					siblingHolder.appendChild(textNodes[i].nextSibling);	
				}
			}

			// rename it to span and remove its href. If span is empty then delete span
			if(textContainer.attributes.length > 1)
			{
				textContainer = textContainer.parentNode.replaceChildOnly(textContainer, "span");
				textContainer.removeAttribute("href");
				textContainer.removeAttribute("title");
			}
			// else remove the A!
			else
			{
				textContainer.parentNode.removeChildOnly(textContainer);
			}
		}
	}

	// normalize A elements 
	var normalizeRange = document.createRange();
	normalizeRange.selectNode(this.commonAncestorContainer);
	normalizeRange.normalizeElements("a");
	normalizeRange.detach();

	// now normalize text
	this.commonAncestorContainer.parentElement.normalize();
	this.__restoreTextBoundaries();
}


Range.prototype.updateXMLNodes = function() {
	
		
	var _node = this.commonAncestorContainer;
	_node.updateXMLNode();
	//make sure start and endcontainer are nodes
	if (this.startContainer.nodeType == 3) {
		var startContainer = this.startContainer.parentNode;
	} else {
		var startContainer = this.startContainer;
	}
	
	if (this.endContainer.nodeType == 3) {
		var endContainer = this.endContainer.parentNode;
	} else {
		var endContainer = this.endContainer;
	}
	
	// if startContainer is after endContainer, make start = end (otherwise it won't stop)
	if (startContainer.compareDocumentPosition(endContainer) & Node.DOCUMENT_POSITION_PRECEDING) {
		startContainer = endContainer;
	}
	
	//walk through all nodes from start to endContainer
	var walker = document.createTreeWalker(
		document.documentElement, NodeFilter.SHOW_ELEMENT,
		null, 
		true);
	walker.currentNode = startContainer;
	var node = walker.currentNode;
	
	do {
		if (node.nodeType == 1) {
			node.updateXMLNode();
		} 
		if (endContainer == node) {
			break;
		}
		node =   walker.nextNode() 
	} while(node)
	return _node;
	
}
/******************************************* Misc ***********************************************/

/**
 * Cleanup standard conformant inline CSS ie/ remove redundancies
 *
 * Three types of A or span need normalization:
 * - those that only contain space: delete the element but keep the space
 * - those with redundant inline CSS, CSS that their parent's have: delete the redundant settings
 * - those that follow an A or span that has identical properties: merge 
 * - those with no CSS: delete the element but keep its contents
 *
 * POST04:
 * - change to take inline container list so that it is more generic
 */ 
Element.prototype.__normalizeXHTMLTextStyle = function()
{

	// normalize element filter checks two things:
	// - is the element within the Range
	// - does the element have an identical previous sibling (prelude to merge!)
	var normalizeFilter = function(node)
	{	
		// any inline or specifically fix on these two?						
		// will only normalize spans or A's with one child. This prevents bad XHTML from complicating
		// merge logic below	
		if((node.parentNode.nodeNamed("span") || node.parentNode.nodeNamed("a")) && (node.parentNode.childNodes.length == 1))
		{
			return NodeFilter.FILTER_ACCEPT;
		}	  	
		return NodeFilter.FILTER_REJECT;		
	}

	var tw = document.createTreeWalker(this,
					   NodeFilter.SHOW_TEXT,
					   normalizeFilter,
					   false);

	// go through all text nodes that appear in spans or A's
	var nextTextNode = tw.firstChild();
	while(nextTextNode)
	{
		var thisTextNode = nextTextNode;
		nextTextNode = tw.nextNode();
		var thisISC = thisTextNode.parentNode;
		thisISC.removeRedundantInlineStyles();	

		// nix isc if empty text node or redundant inline styles
		if((__NodeFilter.nonEmptyText(thisTextNode) == NodeFilter.FILTER_REJECT) ||
		   !thisISC.hasAttributes())
		{
			var elContents = document.createRange();
			elContents.selectNodeContents(thisISC);
			thisISC.parentNode.insertBefore(elContents.extractContents(), thisISC);
			thisISC.parentNode.normalize(); // no problem to tree traversal as it gets text in own spans!
			thisISC.parentNode.removeChild(thisISC);
		}
		else // if didn't nix then try to merge!
		{
			// merge ISC with previous sibling if they match
			var previousSibling = thisISC.__editablePreviousSibling;

			if(previousSibling && previousSibling.match && previousSibling.match(thisISC))
			{
				// delete intermediate text nodes and other "useless" markup
				var thisISCParentNode = thisISC.parentNode;
				while(previousSibling.nextSibling != thisISC)
					thisISCParentNode.removeChild(previousSibling.nextSibling);
	
				// merge siblings
				var contentsToMerge = document.createRange();
				contentsToMerge.selectNodeContents(thisISC);
				previousSibling.appendChild(contentsToMerge.extractContents());
				previousSibling.normalize();
				thisISCParentNode.removeChild(thisISC);	
			}
		}
	}
}

/**
 * Remove redundant inline styles - those styles inherited already from the parent of an element
 *
 * POST04: rename to normalizeStyleSettings? Issue of default "transparent" for color and "start" for "text-align"
 */
Element.prototype.removeRedundantInlineStyles = function()
{
	for(var j=0; j<this.style.length; j++)
	{
		var styleName = this.style.item(j);
		var styleValue = this.style.getPropertyValue(styleName);
		var parentStyleValue = document.defaultView.getComputedStyle(this.parentNode, null).getPropertyValue(styleName);

		// problem: if the element itself/class sets the style even when we remove the inline then there's a prob!

		// if parent already has setting then remove it
		if(parentStyleValue == styleValue)
		{
			this.style.removeProperty(styleName);	

			// now make sure that removing the property actually expose's the parent's setting. If not
			// then we must put it back! It is possible that the tag of the element fixes the style or
			// at least a style different than the one intended.
			if(document.defaultView.getComputedStyle(this, null).getPropertyValue(styleName) != parentStyleValue)
					this.style.setProperty(styleName, styleValue, "");		
		}
	}

	if(this.hasXHTMLAttribute("style") && (this.style.length == 0))
		this.attributes.removeNamedItem("style");
}

Document.prototype.createTable = documentCreateTable;

function documentCreateTable(noRows, noColumns)
{
	if((/\D+/.test(noRows)) || (/\D+/.test(noColumns)) || (noRows==0) || (noColumns==0))
		return null; // go to exception

	var te = documentCreateXHTMLElement("table");
	te.appendChild(document.createTextNode("\n"));
	for(var i=0; i<noRows; i++)
	{
		var tre = documentCreateXHTMLElement("tr");
		te.appendChild(tre);
		tre.appendChild(document.createTextNode("\n"));
		for(var j=0; j<noColumns;j++)
		{
			tre.appendChild(document.createTextNode("  "));
			var tde = documentCreateXHTMLElement("td");
			tde.appendChild(document.createTextNode(STRING_NBSP)); // POST05: CHANGE TO BR TOKEN 
			tre.appendChild(tde);
			tre.appendChild(document.createTextNode("\n"));
		}
		te.appendChild(document.createTextNode("\n"));
	}
	return te;
}
