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

// $Id: mozCE.js 1374 2005-09-03 11:29:39Z chregu $

/* 
 * mozCE V0.5
 * 
 * Mozilla Inline text editing that relies on eDOM, extensions to the standard w3c DOM.
 *
 * This file implements contenteditable/user modify in Mozilla by leverging
 * Mozilla's proprietary Selection object as well as eDOM, a set of browser independent
 * extensions to the w3c DOM for building editors. 
 *
 * POST05:
 * - refactor userModify code as part of "EditableElement" 
 * - IE's "ContentEditable" means that an element is editable whenever its "ContentEditable"
 * setting is true. However, we may change this so that you have to set editing on for a
 * document as a whole before its individual editable sections become editable. This would
 * allow a user to browse an editable document and explicitly choose to edit it or not.
 * - see if can move to using DOM events and away from Window.getSelection() if possible 
 * (effects how generic it can be!)
 * - selection model: word, line etc. Write custom handlers of clicks and use new Range
 * expansion methods
 */

/****************************************************************************************
 *
 * MozUserModify and ContentEditable: allow precise designation of editing scope. This
 * file implements user-modify/contentEditable. The following utilities let the implementation
 * determine scope.
 *
 * - http://www.w3.org/TR/1999/WD-css3-userint-19990916#user-modify
 *
 * POST04
 * - rename to be "mozSelection.js"
 * - remove need to spec ContentEditable as equivalent to mozUserModify: do mapping in
 * style sheet: *[contentEditable="true"] 
 * - to change as part of "editableElement": may also move Selection methods into EditableElement
 * - support for tracking whether changes were made to elements or not ie/ does a user
 * need to save? Should MozCE warn a user to save before exiting the browser? Some of
 * this may go into eDOM itself in enhancements to document or to all elements ie/ changed?
 *
 ****************************************************************************************/

/**
 * Start of "EditableElement": this will move into eDOM once it is fleshed out.
 *
 * POST04: set user-input and user-select properly as a side effect of setting user-modify. 
 * Need to chase to explicit parent of the editable area and check if true
 * Also for contentEditable - make sure set moz user modify and other properties!
 */
Element.prototype.__defineGetter__(
	"mozUserModify",
	function()
	{
		return document.defaultView.getComputedStyle(this, null).MozUserModify;
	}
);

/**
 * Does MozUserModify set this element modifiable
 */
Element.prototype.__defineGetter__(
		"mozUserModifiable",
		function()
		{
			// first check user modify!
			if (!this._mozUserModify) {
				var mozUserModify = this.mozUserModify;
				if(mozUserModify == "read-write")
				{	
					this._mozUserModify = true;
					return true;
				}
				
				return false;
			}
			return true;
		}
);

/**
 * mozUserModify and contentEditable both count
 */
Element.prototype.__defineGetter__(
	"userModify",
	function()
	{
		if (!this._userModify) {
		// special case: allow MS attribute to set modify level
			if(this.isContentEditable)
				return("read-write");
			var mozUserModify = this.mozUserModify;
			this._userModify = mozUserModify;
			return mozUserModify;
		}
		return this._userModify;
	}
);

/**
 * If either contentEditable is true or userModify is not read-only then return true. This makes
 * it easy to support a single approach to user modification of elements in a page using either
 * the W3c or Microsoft approaches.
 * 
 * POST04:
 * - consider not supporting contentEditable here
 */
Element.prototype.__defineGetter__(
		"userModifiable",
		function()
		{
			// first check user modify!
			if (this._userModify) {
				return true;
			} else {
				if(this.userModify == "read-write")
				{
					this._userModify = true;
					return true;
				}
			}
			return false;
		}
);

/*
 * UserModifiableContext means a parent element that is explicitly set to userModifiable. Note that this accounts for
 * different degrees of userModify. If say "writetext" is inside a "write" then context will stop at the writetext
 * element. That is the context for that level of usermodify. 
 */ 
Element.prototype.__defineGetter__(
	"userModifiableContext",
	function()
	{
		// Moz route (userModify) 
		if(this.mozUserModifiable)
		{
			var context = this;
			contextUserModify = this.mozUserModify;
			while(context.parentNode)
			{
				var contextParentUserModify = context.parentNode.mozUserModify;
				if(contextParentUserModify != contextUserModify)
					break;
				context = context.parentNode;
				contextUserModify = contextParentUserModify;
			}
			return context;
		}

		// try IE route
		return this.contentEditableContext;
	}
);

/***************************************************************************************************************
 * New Selection methods to support styling the current selection
 *
 * POST05:
 * - move alot of the content here (the XHTML specific stuff) to eDOMXHTML leaving these methods as just Selection
 * wrappers for Range methods.
 ***************************************************************************************************************/

/**
 * "Delete" for selected XHTML represents three behaviors:
 * - if range isn't collapsed then delete contents of the range - treat table contents properly (see code for behavior)
 * - if range is collapsed
 *   - if at start of line then merge line with previous line if there is one and this is appropriate
 *   - otherwise delete character or element before the selected point in the line
 *
 * Note: this is an XHTML compliant deletion. It is driven solely by CSS settings. This works for XHTML selections but
 * it is unlikely to work for semantically rich and restrictive XML. Deletion of an XML document would have to pay 
 * attention to that document's semantics.
 */
Selection.prototype.deleteSelection = function(backspace)
{
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	if(cssr.collapsed)
	{
		var ip = documentCreateInsertionPoint(cssr.top, cssr.startContainer, cssr.startOffset);
		if (backspace) {
			ip.forwardOne();
		}
		var result = ip.deletePreviousInLine();
		if(!result)
		{
			var line = ip.line;
			ip = line.deleteStructure();
			if (ip.__needBackspace) {
				ip.backOne();
				ip.__needBackspace = false;
			}
		}

		cssr.selectInsertionPoint(ip);
	}
	else
	{
		cssr.extractContentsByCSS();
	}

	// this.selectEditableRange(cssr);

	this.removeAllRanges();
	this.addRange(cssr.cloneRange());
}

/**
 * POST05: change so defaultValue doesn't have to be passed in; think about toggling whole line if selection collapsed
 */
Selection.prototype.toggleTextStyle = function(styleName, styleValue, defaultValue, styleClass)
{
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	if(cssr.hasStyle(styleName, styleValue))
		cssr.styleText(styleName, defaultValue, styleClass);
	else
		cssr.styleText(styleName, styleValue, styleClass);

	this.selectEditableRange(cssr);
}
/**
* adds or removes a class from a selection
*/
Selection.prototype.toggleTextClass = function(styleClass, namespaceURI)
{
	if (typeof namespaceURI == "undefined") {
		namespaceURI = "";
	}
	var cssr = this.getEditableRange();

	if(!cssr)
		return;


	cssr.styleText(styleClass, true, true, namespaceURI);

	this.selectEditableRange(cssr);
}



/**
 * POST05: think about toggling whole line if selection collapsed
 */
Selection.prototype.styleText = function(styleName, styleValue)
{
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	cssr.styleText(styleName, styleValue);

	this.selectEditableRange(cssr);
}

Selection.prototype.linkText = function(href,title)
{
	
	
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	cssr.linkText(href,title);

	this.selectEditableRange(cssr);
}

Selection.prototype.clearTextLinks = function()
{
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	cssr.clearTextLinks();

	this.selectEditableRange(cssr);
}

/**
 * This will only style contained lines
 */
Selection.prototype.styleLines = function(styleName, styleValue)
{
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	var lines = cssr.lines;	

	for(var i=0; i<lines.length; i++)
	{
		// turn bounded line into contained line or put in container for top line
		if((lines[i].lineType == CSSLine.BOUNDED_LINE) || lines[i].topLine)
		{
			// special case: empty bounded line - don't try to style this!
			if(lines[i].emptyLine)
				continue;
			lines[i] = lines[i].setContainer(documentCreateXHTMLElement(defaultContainerName), false);
		}

		lines[i].setStyle(styleName, styleValue);
	}

	this.selectEditableRange(cssr);
}

Selection.prototype.changeLinesContainer = function(containerName, namespace)
{
	var cssr = this.getEditableRange();

	if(!cssr)
		return;
	var newContainer = new Array();
	var lines = cssr.lines;
	for(var i=0; i<lines.length; i++)
	{
		// keep container if it is a contained line but not a block:
		// - it is top
		// - it is a table cell
		// - it is a list item
		//var keep = ((lines[i].lineType == CSSLine.CONTAINED_LINE) && (lines[i].containedLineType != ContainedLine.BLOCK));
		var keep = false;
		if (namespace == XHTMLNS) {
			var removeClass = false;
			//if (lines[i].__container.getAttribute("class"));
			if (lines[i].__container.XMLNode) {
				if (lines[i].__container.XMLNode.nodeName == lines[i].__container.getAttribute("class")) {
					removeClass = true;
					
				}
			}
			var line = lines[i].setContainer(documentCreateXHTMLElement(containerName), !keep);
			if (removeClass) {
				line.__container.removeAttribute("class");
			}
		} else {
			var newNode = document.createElementNS(XHTMLNS,"div");
			var line = lines[i].setContainer( newNode,true);
			line.__container.setAttribute("class", containerName);
		}
		line.__container.setAttribute("__bxe_ns", namespace);
	
		
		newContainer.push(line.__container)

	}

	this.selectEditableRange(cssr);
	return newContainer;
}

Selection.prototype.removeLinesContainer = function()
{
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	var lines = cssr.lines;
	for(var i=0; i<lines.length; i++)
	{
		if((lines[i].lineType == CSSLine.CONTAINED_LINE) && !lines[i].topLine) // as long as contained line and container isn't top then remove it
			lines[i].removeContainer();
	}

	this.selectEditableRange(cssr);
}

Selection.prototype.indentLines = function()
{	
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	indentLines(cssr);	

	this.selectEditableRange(cssr);
}

Selection.prototype.outdentLines = function()
{	
	var cssr = this.getEditableRange();

	if(!cssr)
		return;

	outdentLines(cssr);	

	this.selectEditableRange(cssr);
}

Selection.prototype.toggleListLines = function(requestedList, alternateList)
{	
	var _moved = false;
	//if cursor at end, shit happens. prevent that here
	// see http://cvs.wyona.org/cgi-bin/bugzilla/show_bug.cgi?id=3188
	if(this.isCollapsed && this.anchorOffset > 0) {
		this.collapse(this.anchorNode, this.anchorOffset -1);
		 _moved = true;
	}
	var cssr = this.getEditableRange();

	if(!cssr)
		return;
	listLinesToggle(cssr, requestedList, alternateList);
	this.selectEditableRange(cssr);
	if (_moved) {
		this.collapse(this.anchorNode, this.anchorOffset +1);
		 
	}
	return  cssr.lines;

}

Selection.prototype.insertNodeRaw = function (node, oldStyleInsertion) {

	var cssr = this.getEditableRange();
	if(!cssr)
		return;
	// if there's a selection then delete it
	if(!cssr.collapsed)
	{
		bxe_deleteEventKey(window.getSelection(), false);
		cssr = this.getEditableRange();
	
	} /*else {
		if (cssr.startContainer.nodeType == 3 && cssr.startContainer.data == STRING_NBSP) {
			cssr.startContainer.data = "llll";
		}
	}*/
	
	if (oldStyleInsertion) {
		var ip = cssr.firstInsertionPoint;
		ip.insertNode(node);
		var _upNode = ip.ipNode;	
	} else {
		cssr.insertNode(node);
	}
	
	var _upNode = this.getEditableRange().startContainer;
		
	if (_upNode.nodeType == 3) {
		_upNode = _upNode.parentNode;
	}
	while(! _upNode._XMLNode) {
		_upNode = _upNode.parentNode;
	}
	
	_upNode.normalize();
	_upNode.updateXMLNode();

	
	/*cssr.selectInsertionPoint(ip);

	cssr.__clearTextBoundaries(); // POST05: don't want to have to use this
	*/
	this.selectEditableRange(cssr);
	return node;
}

Selection.prototype.insertNode = function(node)
{
	var checkNode = node;
	if (node.nodeType == 11 ) {
		checkNode = node.firstChild;
	}
	if (checkNode && checkNode.XMLNode) {
		if (!bxe_checkIsAllowedChild(checkNode.XMLNode.namespaceURI,checkNode.XMLNode.localName,this)) {
			return false;
		}
	}
	var cb = bxe_getCallback(node.XMLNode.localName, node.XMLNode.namespaceURI);
	if (cb ) {
		bxe_doCallback(cb, BXE_SELECTION);
		return;
	}
	return this.insertNodeRaw(node);

	
}

/**
 * POST05: paste more than text
 */
Selection.prototype.paste = function()
{
	var clipboard = mozilla.getClipboard();
	var content = clipboard.getData(MozClipboard.TEXT_FLAVOR);
	if (content.nodeType == 11 && content.firstChild.nodeType == 3 && content.childNodes.length == 1) {
		content.data = content.firstChild.data;
	}
	if (content && content.data) {
		var elementName = bxe_config.options['autoParaElementName'];
		if (elementName && clipboard._system && content.data.search(/[\n\r]./) > -1) {
			content = content.data;
			content = content.replace(/&/g,"&amp;").replace(/</g,"&lt;");
			var elementNamespace = bxe_config.options['autoParaElementNamespace']
			var elementName_start = elementName;
			if (elementNamespace) {
				elementName_start += " xmlns='"+elementNamespace +"'";
			}
			content = "<"+elementName_start + ">"+ content.replace(/[\n\r]+/g,"</"+elementName+"><"+elementName_start+" >")+"</"+elementName+">";
			bxe_insertContent_async(content,BXE_SELECTION,BXE_SPLIT_IF_INLINE);
		} else {
			window.getSelection().insertNode(content);
		}
	} else {
		bxe_insertContent_async(content,BXE_SELECTION,BXE_SPLIT_IF_INLINE);
	} 
	
	var node = window.getSelection().anchorNode;
	if( node.nodeType == 3) {
		node.normalize();
	}
	bxe_history_snapshot_async();
	return node;
	
}
// creates a hidden form field for interapp copy/paste support without native-method support
Selection.prototype._createHiddenForm = function() {
		var iframe = document.createElement("div");
		iframe.setAttribute("ID","ClipboardIFrame");
		iframe.setAttribute("style","  -moz-user-input: enabled; position: fixed; width: 0px; height: 0px; top: 0px; left: 0px; overflow: hidden; ");
		iframe =  document.getElementsByTagName("body")[0].appendChild(iframe);
		var input = document.createElement("textarea");
		input.id =  'hiddenform';
		input.setAttribute("style","height: 3000px;");
		// don't know of any other solution to get a Range object for the input value
		// therefore we create a span element, so we can use selectNodeContents on that later
		var placeholder = document.createElement("span");
		iframe.appendChild(input);
		iframe.appendChild(placeholder);
		iframe._placeholder = placeholder;
		iframe._input = input;
		return iframe;
}

/**
 * copies the selection to the hidden form field on key down
 */
Selection.prototype.copyKeyDown = function() {
	
	//copy the selection into the internal clipboard
	this.copy();
	
	//clipboard._clipboardText.replace(/[\n\r]+/," ");
	//check if hidden form already exists
	var iframe = document.getElementById("ClipboardIFrame");
	if (!iframe) {
		iframe = this._createHiddenForm();
	}
	
	//store the editable range for later retrieval
	var clipboard = mozilla.getClipboard();
	var cssr = this.getEditableRange();
	iframe._cssr = cssr;  
	
	//remove all children in the placeholder span
	iframe._placeholder.removeAllChildren();
	//get the clipboard object
	clipboard = mozilla.getClipboard();
	
	//insert the text from the internal clipboard in the placeholder span
	iframe._placeholder.appendChild(document.createTextNode(clipboard._clipboardText));
	
	//select the content of the placeholder span, so the ctrl+c keypress event can catch it
	var rng = document.createRange();
    rng.selectNodeContents(iframe._placeholder);
    this.removeAllRanges();
	this.addRange(rng);
	
}

/**
 * restores the selection back to what it was before the copy event
 */
Selection.prototype.copyKeyUp = function() {
	
	var iframe = document.getElementById("ClipboardIFrame");
	this.selectEditableRange(iframe._cssr);
/*	var clipboard = mozilla.getClipboard();
	clipboard._clipboardText = clipboard._clipboardText.replace(/[\r\n]/g," ");
*/
}

/**
 * sets the focus to the hidden form on a paste key down event
 */
Selection.prototype.pasteKeyDown = function() {
	var iframe = document.getElementById("ClipboardIFrame");
	if (!iframe) {
		iframe = this._createHiddenForm();
	}
	// delete value of hidden form
	iframe._input.value = "";
	// delete childnodes of placeholder
	iframe._placeholder.removeAllChildren();
	//store the range for later retrieval
	
	var cssr = this.getEditableRange();

	iframe._cssr = cssr;
	iframe._input.focus();
}
/**
 * pastes the stuff from the hidden form field in to the internal clipboard
 */
Selection.prototype.pasteKeyUp = function () {
	
	var iframe = document.getElementById("ClipboardIFrame");
	iframe._input.blur();

	//copy the content of the hidden form into the placeholder span
	var text = iframe._placeholder.appendChild(document.createTextNode(iframe._input.value));
	
	//make a range with the content of the placesholder span
	var rng = document.createRange();
	rng.selectNodeContents(iframe._placeholder);
	
	//put the data of the placeholder span in the internal clipboard, if it's different
	// than the content in the internal clipboard (then we assume, it's newer..)
	var clipboard = mozilla.getClipboard();
	
	if (!clipboard._clipboardText) {
		clipboard.setData(rng);
		clipboard._system = true;
	}
	else if (rng.toString().replace(/[\n\r\s]+/g," ") != clipboard._clipboardText.replace(/[\n\r\s]+/g," ")) {
		var promptText = "Internal and System-Clipboard are differing: \n\n";
		var _sysString = rng.toString();
		var _intString = clipboard._clipboardText;
		if (_sysString.length > 200) {
			_sysString = _sysString.substr(0,200) + " \n<too long, rest snipped>";
		}
		if (_intString.length > 200) {
			_intString = _intString.substr(0,200) + " \n<too long, rest snipped> ";
		}
		promptText += "******************\n";
		promptText += "System   (Cancel): \n'" + _sysString  +"'\n\n";
		promptText += "******************\n";
		promptText += "Internal   (OK)  : \n'" + _intString  + "'\n\n";
		promptText += "******************\n";
		promptText += "If you want to use the Internal, click OK, otherwise (using System) Cancel\n";
		//this try/catch is here, because we had some problems with confirm and absolutely unrelated errors
		
		try {
			var internal = confirm( promptText)
		} catch(e) {}
		
		if(!internal) {
			clipboard.setData(rng);
			clipboard._system = true;
		} else {
			clipboard._system = false;
		}
	}
	//restore the selection
	var cssr = iframe._cssr;
	var _eol = cssr.firstInsertionPoint.endOfLine;
	this.selectEditableRange(cssr);
	
	if (_eol && !cssr.firstInsertionPoint.endOfLine) {
		ip = documentCreateInsertionPoint(cssr.top, cssr.startContainer, cssr.startOffset);
		
		if(ip != InsertionPoint.SAME_LINE)
		ip.backOne();
		cssr.selectInsertionPoint(ip);
		this.removeAllRanges();
		rng = cssr.cloneRange();
		this.addRange(rng);
	}
	
	// paste the content of the internal clipboard
	this.paste();
}

Selection.prototype.copy = function()
{
	var cssr = this.getEditableRange();

	if(!cssr || cssr.collapsed) // not an editable area or nothing selected
		return; 

	// data to save - render as text (temporary thing - move to html later)
	//var text = cssr.toString().replace(/\n/g," ");

	var clipboard = mozilla.getClipboard();
	clipboard._system = false;
	// clipboard.setData(deletedFragment.saveXML(), "text/html"); // go back to this once, paste supports html paste!
	clipboard.setData(cssr,MozClipboard.TEXT_FLAVOR);
}

Selection.prototype.cut = function()
{
	this.copy();
	bxe_history_snapshot();
	var sel = window.getSelection();
	bxe_deleteEventKey(sel, false);
}

/*
 * Shorthand way to get CSS Range for the current selection. This range will be marked
 * ie/ it can easily be restored.
 *
 * POST04: 
 * - consider not calculating textpointers here (createCSSTextRange) but only within the
 *   editing functions in eDOM where the context can be given more precisely.
 * - allow text selection to only begin and end on word boundaries (part of CSSTextRange 
 * selection methods)
 * - consider moving into Selection (bad for XUL/XML?)
 * - account for empty editable area (maybe in isContentEditable); account for selection
 * type ie/ element or object or text.
 */
Selection.prototype.getEditableRange = function()
{	
	try 
	{
		var selr = window.getSelection().getRangeAt(0);
		var commonAncestor = selr.commonAncestorContainer;

		if(!commonAncestor.parentElement.userModifiable) {
			return null;
		}
		var cec = commonAncestor.parentElement.userModifiableContext;
		var cssr = documentCreateCSSTextRange(selr.cloneRange(), cec);
		return cssr;
	}
	catch(e)
	{
		return null;
	}
}

/*
 * Restore the range
 */
Selection.prototype.selectEditableRange = function(cssr)
{
	if (cssr) {
	//TESTME: I'm not sure about the sideeffects of just leaving restoreTextBoundaries out for some
	// special cases. See http://cvs.wyona.org/cgi-bin/bugzilla/show_bug.cgi?id=1185 for the actual test case
	if (!(cssr.startContainer.nodeType == 3 && cssr.startContainer.data.length == cssr.startOffset && cssr.collapsed == true)) {
		cssr.__restoreTextBoundaries(); // POST04: required cause of line manip that effects range but makes rest more complex
	}
	this.removeAllRanges();
	this.addRange(cssr.cloneRange());
	}	
}