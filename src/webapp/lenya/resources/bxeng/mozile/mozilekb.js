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

// $Id: mozilekb.js 1377 2005-09-03 12:11:03Z chregu $

/* 
 * mozilekb V0.46
 * 
 * Keyboard handling for Mozile. You can replace this if you want different keyboard
 * behaviors.
 *
 * POST04:
 * - reimplement ip use: reuse ip whereever possible. Big performance gain.
 * - support keyboard shortcuts for navigation and style settings
 * - consider xbl equivalent
 * - make sure event handlers aren't loaded twice: if user includes script twice, should
 * not register handlers twice (spotted by Chris McCormick)
 * - see if can move to using DOM events and away from Window.getSelection() if possible 
 * (effects how generic it can be!)
 * - selection model: word, line etc. Write custom handlers of clicks and use new Range
 * expansion methods
 */

/*
 * Handle key presses
 *
 * POST04:
 * - IP isn't recreated everytime with its own text pointer; text pointer isn't (in Range
 * create) set up for every key press.
 * - need up and down arrows to be implemented here too (via eDOM!): that way, no problem with
 * not deselecting toolbar at right time
 * - add in support for typical editing shortcuts based on use of ctrl key or tabs; can synthesize events to
 * force selection. http://www.mozilla.org/docs/end-user/moz_shortcuts.html and ctrl-v seems to effect caret mode?
 * - arrow keys: mode concept where if in text mode then only traverse text AND do not traverse objects. If
 * mixed mode, then select objects too.
 * - each editable area gets a CP? If valid (add method that checks TextNode validity?)
 */
bxe_registerKeyHandlers();
   
function keyPressHandler(event)
{	
	var handled = false;
//Mac OSX standard is using the "Apple"-Key for Copy/Paste operation and not the Ctrl-Key
// the Apple Key is event.metaKey in JS Terms
	if(event.ctrlKey || event.metaKey)
		handled = ctrlKeyPressHandler(event);
	else
		handled = nonctrlKeyPressHandler(event);

	// handled event so do let things go any further.
	if(handled)
	{
		//cancel event: TODO02: why all three?
		event.stopPropagation();
		event.returnValue = false;
  		event.preventDefault();
  		return false;
	}
	return true;
}

function keyDownHandler(event)
{	
	var handled = false;
//Mac OSX standard is using the "Apple"-Key for Copy/Paste operation and not the Ctrl-Key
// the Apple Key is event.metaKey in JS Terms
	if(event.ctrlKey || event.metaKey)
		handled = ctrlKeyDownHandler(event);

	// handled event so do let things go any further.
	if(handled)
	{
		//cancel event: TODO02: why all three?
		event.stopPropagation();
		event.returnValue = false;
  		event.preventDefault();
  		return false;
	}
	return true;
}

function keyUpHandler(event)
{	
	var handled = false;
//Mac OSX standard is using the "Apple"-Key for Copy/Paste operation and not the Ctrl-Key
// the Apple Key is event.metaKey in JS Terms
	if(event.ctrlKey || event.metaKey)
		handled = ctrlKeyUpHandler(event);

	// handled event so do let things go any further.
	if(handled)
	{
		//cancel event: TODO02: why all three?
		event.stopPropagation();
		event.returnValue = false;
  		event.preventDefault();
  		return false;
	}
	return true;
}

function ctrlKeyPressHandler(event)
{
	var cssr;
	if(!event.charCode)
		return false;

	if(String.fromCharCode(event.charCode).toLowerCase() == "v")
	{
		//don't handle it, if we're in a textarea
		if (event.target.localName == "TEXTAREA") {
			return false;
		}
		if (mozilla.__allowedNativeCalls) {
			return window.getSelection().paste();
		} else {
			return false;
		}
	}
	else if(String.fromCharCode(event.charCode).toLowerCase() == "b")
	{
		bxe_bench();
		return true;
	}
	else if(String.fromCharCode(event.charCode).toLowerCase() == "x")
	{
		//don't handle it, if we're in a textarea
		if (event.target.localName == "TEXTAREA") {
			return false;
		}
		if (mozilla.__allowedNativeCalls) {
			return window.getSelection().cut();
		} else {
			return false;
		}
	}
	else if(String.fromCharCode(event.charCode).toLowerCase() == "c")
	{
		//don't handle it, if we're in a textarea
		if (event.target.localName == "TEXTAREA") {
			return false;
		}
		if (mozilla.__allowedNativeCalls) {
			return window.getSelection().copy();
		} else {
			return false;
		}
	}
	else if(String.fromCharCode(event.charCode).toLowerCase() == "s")
	{
		eDOMEventCall("DocumentSave",document);
		return true;
	}
	else if(String.fromCharCode(event.charCode)==  "y")
	{
		cssr = window.getSelection().getEditableRange();
		if(!cssr)
		{
			alert("You have to select an edit area first");
			return false;
		}
		eDOMEventCall("toggleTagMode",cssr.top);
		return true;
	}
	else if(String.fromCharCode(event.charCode) == "Y")
	{
		cssr = window.getSelection().getEditableRange();
		if(!cssr)
		{
			alert("You have to select an edit area first");
			return false;
		}
		if (event.target.localName == "TEXTAREA") {
			eDOMEventCall("toggleSourceMode",event.target.parentNode);
		} else {
			eDOMEventCall("toggleSourceMode",cssr.top);
		}

		return true;
	}
	return false;
}

function ctrlKeyDownHandler(event,cssr) {
	
	if(!event.keyCode)
		return false;
	
	if(String.fromCharCode(event.keyCode).toLowerCase() == "v")
	{
		//don't handle it, if we're in a textarea
		if (event.target.localName == "TEXTAREA" && event.target.id != 'hiddenform') {
			return false;
		}		
		if (!mozilla.__allowedNativeCalls) {
			window.getSelection().pasteKeyDown();
			return true;
		}
		return false;
	}
	if(String.fromCharCode(event.keyCode).toLowerCase() == "c")
	{
		//don't handle it, if we're in a textarea
		if (event.target.localName == "TEXTAREA" && event.target.id != 'hiddenform') {
			return false;
		}
		if (!mozilla.__allowedNativeCalls) {
			window.getSelection().copyKeyDown();
			return true;
		}
		return false;
	}
	
	// cut doesn't work yet
	/*
	if(String.fromCharCode(event.keyCode).toLowerCase() == "x")
	{
		if (!mozilla.__allowedNativeCalls) {
			window.getSelection().cutKeyDown();
			return true;
		}
		return false;
	}*/
	return false;
}

function ctrlKeyUpHandler(event,cssr) {
	
	if(!event.keyCode)
		return false;
	
	if(String.fromCharCode(event.keyCode).toLowerCase() == "v")
	{
		//don't handle it, if we're in a textarea
		if (event.target.localName == "TEXTAREA" && event.target.id != 'hiddenform') {
			return false;
		}
		if (!mozilla.__allowedNativeCalls) {
			window.getSelection().pasteKeyUp();
			return true;
		}
		return false;
	}
	if(String.fromCharCode(event.keyCode).toLowerCase() == "c")
	{
		//don't handle it, if we're in a textarea
		if (event.target.localName == "TEXTAREA" && event.target.id != 'hiddenform') {
			return false;
		}
		if (!mozilla.__allowedNativeCalls) {
			window.getSelection().copyKeyUp();
			return true;
		}
		return false;
	}
	//cut doesn't work yet, 'cause we can't cut from html-selections, just from forms
	/*if(String.fromCharCode(event.keyCode).toLowerCase() == "x")
	{
		if (!mozilla.__allowedNativeCalls) {
			window.getSelection().copyKeyUp();
			return true;
		}
		return false;
	}*/
	return false;
}

/**
 * POST04: 
 * - carefully move selectEditableRange in here
 * - distinguish editable range of deleteOne at start of line and deleteOne
 * on same line [need to stop merge but allow character deletion]. Perhaps
 * need to change eDOM granularity.
 */
function nonctrlKeyPressHandler(event)
{

	var sel = window.getSelection();
	var ip;
	var cssr;
	var rng;
	
	//don't handle it, if we're in a textarea
	if (event.target.localName == "TEXTAREA") {
		return false;
	}
	// BACKSPACE AND DELETE (DOM_VK_BACK_SPACE, DOM_VK_DELETE)
	if((event.keyCode == 8) || (event.keyCode == 46)) {
		return bxe_deleteEventKey(sel,(event.keyCode == 46));

	}

	
	// PREV (event.DOM_VK_LEFT) Required as Moz left/right doesn't handle white space properly
	if(event.keyCode == 37 && !event.shiftKey)
	{
		cssr = sel.getEditableRange();
		if(!cssr)
		{
			return false;
		}

		if(!cssr.collapsed)
			cssr.collapse(true);

		ip = documentCreateInsertionPoint(cssr.top, cssr.startContainer, cssr.startOffset);
		ip.backOne();
		cssr.selectInsertionPoint(ip);
		sel.removeAllRanges();
		rng = cssr.cloneRange();
		sel.addRange(rng);
		bxe_delayedUpdateXPath();
		return true;
		
	}

	// NEXT (event.DOM_VK_RIGHT) Required as Moz left/right doesn't handle white space properly
	if(event.keyCode == 39 && !event.shiftKey)
	{	
		cssr = sel.getEditableRange();
		if(!cssr)
		{
			return false;
		}

		if(!cssr.collapsed)
			cssr.collapse(false);

		var caretTop = cssr.top;

		ip = documentCreateInsertionPoint(caretTop, cssr.startContainer, cssr.startOffset);
	
		ip.forwardOne();
		
		cssr.setEnd(ip.ipNode, ip.ipOffset);
		cssr.collapse(false);

		sel.removeAllRanges();
		rng = cssr.cloneRange();
		sel.addRange(rng);

		bxe_delayedUpdateXPath();
		return true;
	}

	// UP/DOWN (event.DOM_VK_UP/DOWN)

	if (event.keyCode == 38 || event.keyCode == 40) {
		bxe_delayedUpdateXPath();
		return false;
	}
	// RETURN OR ENTER (event.DOM_VK_ENTER DOM_VK_RETURN)
	if(event.keyCode == 13)
	{
		cssr = sel.getEditableRange();
		if(!cssr)
		{
			return false;
		}

		if(!cssr.collapsed)
		{ // POST04: delete text when write over it!	
			bxe_deleteWholeSelection(sel,false);
		}
		sel.removeAllRanges();
		ip = documentCreateInsertionPoint(cssr.top, cssr.startContainer, cssr.startOffset);

		// POST04: support concept of not splitting line if mozUserModify indicates writeText ...
		if (cssr.top._SourceMode) {
			ip.insertCharacter(10);
		}
		else {
			td = false;
			var _con = ip.line.container;
			if (_con == ip.line.tableCellAncestor) {
				td = true
			}  
			var _par = ip.ipNode.parentNode;
			//FIXME make soft breaks configurable
			if (  event.shiftKey) {
				if (_par.XMLNode.isAllowedChild(XHTMLNS,"br")) {
					var secondTextNode = ip.ipNode.splitText(ip.ipOffset);
					ip.ipNode.parentNode.insertBefore(documentCreateXHTMLElement("br"), secondTextNode);
					ip.forwardOne();
					_par.updateXMLNode();
				}
			}
			else if (_par.XMLNode && _par.XMLNode.localName == "object") {
				
				var secondTextNode = ip.ipNode.splitText(ip.ipOffset);
				ip.ipNode.parentNode.insertBefore(documentCreateXHTMLElement("br"), secondTextNode);
				_par.parentNode.updateXMLNode();
			} else {
				
				if (_con.XMLNode.isAllowedNextSibling(_con.XMLNode.namespaceURI,_con.XMLNode.localName)) {
					ip.splitXHTMLLine(); // add logic to split off say a "P" after a Heading element: if at end line
					if (td) {
						ip.line.tableCellAncestor.updateXMLNode();
					}
				}
			}
			bxe_history_snapshot_async();
		}
		cssr.selectInsertionPoint(ip);
		sel.removeAllRanges();
		sel.addRange(cssr);
		bxe_delayedUpdateXPath();
		return true;
	}

	// POST04: for non-pre, may change to mean switch to next editable area
	if(event.keyCode == event.DOM_VK_TAB)
	{
		cssr = sel.getEditableRange();
		if(!cssr)
		{
			return false;
		}

		// if there's a selection then delete it
		if(!cssr.collapsed)
		{
			cssr.extractContentsByCSS();
		}

		// seems to mess up the current position!
		ip = documentCreateInsertionPoint(cssr.top, cssr.startContainer, cssr.startOffset);
		if(ip.cssWhitespace == "pre")
			ip.insertCharacter(CHARCODE_TAB);
		else
			ip.insertCharacter(CHARCODE_SPACE); // POST05: may change to insert a set of spaces
	
		// put cursor after inserted text: TODO - move on CSSR instead
		sel.collapse(ip.ipNode, ip.ipOffset);
		return true;
	}
	if(event.keyCode == event.DOM_VK_HOME) {
		var cssr = sel.getEditableRange();
		if(!cssr )
		{
			return false;
		}
		var startip = cssr.lines[0].firstInsertionPoint;
		sel.collapse(startip.ipNode, startip.ipOffset); 	
		return true;
	}
	if (event.keyCode == event.DOM_VK_END) {
		var cssr = sel.getEditableRange();
		if(!cssr )
		{
			return false;
		}
		var endip = cssr.lines[0].lastInsertionPoint; 
		sel.collapse(endip.ipNode, endip.ipOffset); 	
		return true;
	}
	
	// ALPHANUM
	if(event.charCode)
	{
		cssr = sel.getEditableRange();
		if(!cssr)
		{
			return false;
		}

		// if there's a selection then delete it
		if(!cssr.collapsed)
		{
			var backspace = false;
			
			bxe_deleteEventKey(sel);
		}

		// seems to mess up the current position!
		ip = documentCreateInsertionPoint(cssr.top, cssr.startContainer, cssr.startOffset);

		ip.insertCharacter(event.charCode);		

		// put cursor after inserted text: TODO - move on CSSR instead
		sel.collapse(ip.ipNode, ip.ipOffset);
		return true;
	}

	return false;
}

function bxe_deleteWholeSelection(sel,backspace) {
	bxe_history_snapshot();
	var n = sel.focusNode;
	var o = sel.focusOffset;
	sel.collapse(sel.anchorNode,1)
	sel.extend(n,o);
	sel.deleteSelection(backspace);
	sel.anchorNode.parentNode.updateXMLNode();
}

function bxe_switchSelectionEnds(sel) {
		var an = sel.anchorNode;
			var ao = sel.anchorOffset;
			var fn = sel.focusNode;
			var fo = sel.focusOffset;
			
			sel.collapse(fn,fo)
			sel.extend(an,ao);
}

function bxe_deleteEventKey(sel, backspace) {

		//switch focus and anchor, if focus is before anchor
		// this prevents some problems with updateXMLNode and unifies the handling
		// Bug 635 caused this and
		// http://cvs.wyona.org/cgi-bin/bugzilla/show_bug.cgi?id=3015
		if (!sel.isCollapsed && sel.focusNode.nodeType == 3 ) {
			var _pos = sel.focusNode.compareDocumentPosition(sel.anchorNode);
			if (_pos  & 4) {
				bxe_switchSelectionEnds(sel);
			} else if ( _pos == 0 && sel.focusOffset < sel.anchorOffset) {
				bxe_switchSelectionEnds(sel);
			}
		}
		if (sel.isCollapsed) {
			sel.deleteSelection(backspace);
		}
		else if (!sel.isCollapsed && sel.anchorNode.nodeType == 3 && sel.anchorOffset == 0) {
			bxe_deleteWholeSelection(sel,backspace);
			sel = window.getSelection();
			sel.deleteSelection(false);
			sel.anchorNode.parentNode.updateXMLNode();
			
		} else {
			bxe_history_snapshot();
			cssr = sel.getEditableRange();
			
			var _conode = cssr.commonAncestorContainer;
			sel.deleteSelection(backspace);
			if (sel.anchorNode.nodeType == 3) {
				var n  = sel.anchorNode.parentNode;
			} else {
				var n = sel.anchorNode;
			}
			var i = 0;
			if (!cssr.top._SourceMode) {
				if (_conode && _conode.nodeType != 3 ) {
					while (n && n != _conode && !n.XMLNode.xmlBridge && i < 10) {
						i++;
						n.updateXMLNode();
						n = n.parentNode;
					}
					_conode.updateXMLNode();
				} else {
					n.updateXMLNode();
				}
			}
		}
		return true;
}
