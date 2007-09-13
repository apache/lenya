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

/*
 * mozIECE V0.5
 * 
 * Support for IE's contentEditable commands to allow for simple cross-browser editors
 * and toolbars. A cross browser toolbar (unlike the basic Mozile toolbar) should use these
 * IE commands rather than window.getSelection() editor commands directly. Such a toolbar/
 * editor should then work in both IE and Mozilla/Firebird.
 * 
 * IE docs: http://msdn.microsoft.com/workshop/author/dhtml/reference/properties/contenteditable.asp
 *
 * POST05:
 * - reimpl to delegate to ptdButtonClick for command-only options
 * - add strikeThrough, Subscript, Superscript
 * - add Delete [replicate the above ...]
 * - add Copy, Cut, Paste, Undo (depend on running locally: general approach of warn user first!)
 * - support IE's SaveAs semantics
 * - add queryCommand too
 */

Document.prototype.execCommand = function(command, userInterface, vValue)
{
	switch(command)
	{
		case 'FormatBlock':
			window.getSelection().changeLinesContainer(vValue);
			break;

		case 'RemoveFormat':
			window.getSelection().removeLinesContainer();
			break;

		case 'FontName':
			window.getSelection().styleText('font-family', vValue);
			break;

		case 'FontSize':
			window.getSelection().styleText('font-size', vValue);
			break;

		case 'FontColor':
			window.getSelection().styleText('color', vValue);
			break;

		case 'BackColor':
			window.getSelection().styleText('background-color', vValue);
			break;

		case "Unlink":
			window.getSelection().clearTextLinks();
			break;

		case 'CreateLink':
			window.getSelection().linkText();
			break;

		case "SaveAs": 
			mozileSave(); // assume this is implemented to have the same function as IE's saveAs!
			break;

		case 'Bold':
			window.getSelection().toggleTextStyle('font-weight', 'bold', '400');
			break;
		case 'Italic':
			window.getSelection().toggleTextStyle('font-style', 'italic', 'normal');
			break;
		case 'Underline':
			window.getSelection().toggleTextStyle('text-decoration', 'underline', 'none');			
			break;
		case 'InsertOrderedList':
			window.getSelection().toggleListLines("ol", "ul");
			break;
		case 'InsertUnorderedList':
			window.getSelection().toggleListLines("ul", "ol");
			break;
		case 'Indent':
			window.getSelection().indentLines();
			break;
		case 'Outdent':
			window.getSelection().outdentLines();
			break;
		case 'JustifyLeft':
			window.getSelection().styleLines("text-align", "left");
			break;
		case 'JustifyRight':
			window.getSelection().styleLines("text-align", "right");
			break;
		case 'JustifyCenter':
			window.getSelection().styleLines("text-align", "center");
			break;
	}
}

/****************************** IE "contentEditable" *****************************
 * IE based scripts will use this and not the MozUserModify style
 *
 * See: http://msdn.microsoft.com/workshop/author/dhtml/reference/properties/contenteditable.asp
 *********************************************************************************/

Element.prototype.__defineGetter__(
	"contentEditable", 
	function()
	{
  		return this.isContentEditable;
	}
);

/**
 * POST04:
 * - account for "inherit" as ContentEditable value
 */
Element.prototype.__defineGetter__(
	"isContentEditable",
	function()
	{
		// go up the tree until no more elements or one is contentEditable
		var nodeToCheck = this;
		while(nodeToCheck)
		{
			// find a non element node - return: probably DOCUMENT_NODE reached!
			if(nodeToCheck.nodeType != Node.ELEMENT_NODE)
				return false;

			// if has a content editable attribute then sync it with mozusermodify
			if(nodeToCheck.hasXHTMLAttribute("contentEditable"))
			{
				if(nodeToCheck.getXHTMLAttribute("contentEditable") == "true")
					return true;

				return false;
			}
			nodeToCheck = nodeToCheck.parentNode;
		}

		return false;		
	}
);

/*
 * contentEditable set through setting userModify
 * 
 * POST04: set ContentEditable property too or only ie/ don't set user modify here? (after all, IE compatible) ...
 */
Element.prototype.__defineSetter__(
	"contentEditable",
	function(value)
	{
		if(value)
		{
			this.style.MozUserModify = "read-write";
			this.style.MozUserInput = "enabled"; // only effective if caret browsing is on
      			this.style.MozUserFocus = "normal"; // POST05: don't think this effects anything
		}
		else
		{
			this.style.MozUserModify = "read-only";
			this.style.MozUserInput = "disabled";
		}
	}
);

/*
 * contentEditableContext means an element which is explicitly set to be contentEditable
 */ 
Element.prototype.__defineGetter__(
	"contentEditableContext",
	function()
	{
		// it must be content editable	
		if(!this.isContentEditable)
			return null;

		// editable but not a context => has at least a parent
		var textContext = this;
		while(textContext.parentNode)
			if(!textContext.parentNode.isContentEditable)
				break;
			else
				textContext = textContext.parentNode;

		return textContext;
	}
);