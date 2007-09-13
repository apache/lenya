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
// $Id: bxeFunctions.js 1502 2006-07-26 07:18:28Z chregu $

const BXENS = "http://bitfluxeditor.org/namespace";
const XMLNS = "http://www.w3.org/2000/xmlns/";

const E_FATAL = 1;

const BXE_SELECTION = 1;
const BXE_APPEND = 2;
const BXE_SPLIT_IF_INLINE = 1;

var bxe_snapshots = new Array();
var bxe_snapshots_position = 0;
var bxe_snapshots_last = 0;
const BXE_SNAPSHOT_LENGTH = 5;
function __bxeSave(e) {
    if (bxe_bug248172_check()) {
		alert ("THIS DOCUMENT COULD NOT BE SAVED!\n You are using a Mozilla release with a broken XMLSerializer implementation.\n Mozilla 1.7 and Firefox 0.9/0.9.1 are known to have this bug.\n Please up- or downgrade.");
		return false;
	}

	var td = new mozileTransportDriver(bxe_config.xmlfile_method);
	td.Docu = this;
	if (e.additionalInfo ) {
		td.Exit = e.additionalInfo.exit;
	} else {
		td.Exit = null;
	}
	var xml = bxe_getXmlDomDocument();
	if (!xml) {
		alert("You're in Source Mode. Not possible to use this button");
	}
	if (!(xml.XMLNode.validateDocument())) 
	{
		return false;
	}
	var xmlstr =xml.saveXML(xml);
	
	function callback (e) {
		
		
		if (e.isError) {
			var widg = mozilla.getWidgetModalBox("Saving");
			widg.addText("Document couldn't be saved");
			widg.addText(e.statusText,true);
			widg.show((window.innerWidth- 500)/2,50, "fixed");
			return;
		}
		bxe_lastSavedXML = bxe_getXmlDocument();
		bxe_status_bar.showMessage("Document successfully saved");
		if	(e.status == 201 && bxe_config.options['onSaveFileCreated']) {
			eval(bxe_config.options['onSaveFileCreated']);
			
		}
		
		//widg.show((window.innerWidth- 500)/2,50, "fixed");
		if (e.td.Exit) {
			eDOMEventCall("Exit",document);
		}
	}
	var url = bxe_config.xmlfile;
	if (td.Exit) {
		url = bxe_addParamToUrl(url,"exit=true");
	} else {
		url = bxe_addParamToUrl(url,"exit=false");
	}
	td.save(url, xmlstr, callback);
}


function bxe_addParamToUrl(url, param) {
	if (url.indexOf("?") == -1) {
		url += "?" + param;
	} else {
		url += "&" + param;
	}
	return url;
}

function bench(func, string,iter) {
	
	
	var start = new Date();
	for (var i = 0; i< iter; i++) {
		func();
	}
	var end = new Date();
	

	debug ("Benchmark " + string);
//	debug ("Start " + start.getTime());
//	debug ("End   " + end.getTime() );
	debug ("Total " +(end-start) + " / " +  iter + " = " + (end-start)/iter); 
}

function bxe_bench() {
	
	bench(function() {xmlstr = bxe_getXmlDocument()}, "getXML", 2);
}

function bxe_history_snapshot_async()  {
	window.setTimeout("bxe_history_snapshot()",1);
}


function bxe_history_snapshot() {
	var xmlstr = bxe_getXmlDocument();
	if (!xmlstr) { return false;}
	bxe_snapshots_position++;
	bxe_snapshots_last = bxe_snapshots_position;
	bxe_snapshots[bxe_snapshots_position] = xmlstr;
	var i = bxe_snapshots_last + 1;
	while (bxe_snapshots[i]) {
		bxe_snapshots[i] = null;
		i++;
	}
	if (bxe_snapshots.length >  BXE_SNAPSHOT_LENGTH ) {
		var _temp = new Array();
		
		for (var i = bxe_snapshots_last; i >= bxe_snapshots_last - BXE_SNAPSHOT_LENGTH; i--) {
			_temp[i] = bxe_snapshots[i];
		}
		bxe_snapshots = _temp;
	}
	return (xmlstr);
}

function bxe_history_redo() {
	if (bxe_snapshots_position >= 0 && bxe_snapshots[( bxe_snapshots_position + 1)]) {
		var currXmlStr = bxe_getXmlDocument();
		if (!currXmlStr) { alert("You're in Source Mode. Not possible to use this button"); return false;} 
		bxe_snapshots_position++;
		var xmlstr = bxe_snapshots[bxe_snapshots_position];
		if (currXmlStr == xmlstr && bxe_snapshots[bxe_snapshots_position + 1]) {
			bxe_snapshots_position++;
			var xmlstr = bxe_snapshots[bxe_snapshots_position];
		}
		var BX_parser = new DOMParser();
		var xmldoc = BX_parser.parseFromString(xmlstr,"text/xml");
		var vdom = bxe_config.xmldoc.XMLNode.vdom;
		bxe_config.xmldoc = xmldoc;
		xmldoc.init();
		xmldoc.insertIntoHTMLDocument();
		bxe_config.xmldoc.XMLNode.vdom = vdom;
		try {
			bxe_config.xmldoc.XMLNode.validateDocument();
		} catch(e) {
			bxe_catch_alert(e);
		}
		
	}
	
}
function bxe_history_undo() {
	if (bxe_snapshots_position >= 0) {
		if (bxe_snapshots_position == bxe_snapshots_last) {
			var currXmlStr = bxe_history_snapshot();
			bxe_snapshots_position--;
		} else {
			var currXmlStr = bxe_getXmlDocument();
		}
		
		if (!currXmlStr) { alert("You're in Source Mode. Not possible to use this button"); return false;} 
		var xmlstr = bxe_snapshots[bxe_snapshots_position];
		if (xmlstr) {
			bxe_snapshots_position--;
			while(currXmlStr == xmlstr && bxe_snapshots[bxe_snapshots_position ] ) {
				xmlstr = bxe_snapshots[bxe_snapshots_position];
				bxe_snapshots_position--;
			}
		}
		
		if (bxe_snapshots_position < 0) {
			bxe_snapshots_position = 0;
			return false;
		}
		var BX_parser = new DOMParser();
		if (xmlstr) {
			var xmldoc = BX_parser.parseFromString(xmlstr,"text/xml");
			var vdom = bxe_config.xmldoc.XMLNode.vdom;
			bxe_config.xmldoc = xmldoc;
			xmldoc.init();
			xmldoc.insertIntoHTMLDocument();
			bxe_config.xmldoc.XMLNode.vdom = vdom;
			try {
				bxe_config.xmldoc.XMLNode.validateDocument();
			} catch(e) {
				bxe_catch_alert(e);
			}
			return true;
		}
	} 
	return false;
	/*bxe_snapshots[bxe_snapshots_position] == xmlstr;
	bxe_snapshots_position++;*/
}

function bxe_getXmlDomDocument() {
	var areaNodes = bxe_getAllEditableAreas();
	var xml;
	if(areaNodes.length == 0) {
		var widg = mozilla.getWidgetModalBox("Error");

		   widg.addText( "" );
                widg.addText( "" );
                widg.addText( "" );
                widg.addText( "No bxe_xpath found found in your Layout (HTML) file" );
                widg.addText( "" );
                widg.addText( "" );
                widg.addText( "" );

                widg.show((window.innerWidth- 500)/2,50, "fixed");
		return false;
	}
		
		
	for (var i = 0; i < areaNodes.length; i++) {
		if ((areaNodes[i]._SourceMode)) {
			return false;
		}
		xml = areaNodes[i].XMLNode.buildXML();
		
	}
	return xml.ownerDocument;
}
	

function bxe_getXmlDocument() {
	
	var xml = bxe_getXmlDomDocument();
	if (!xml ) { return xml;}
	return xml.saveXML(xml);

}

function bxe_getRelaxNGDocument() {
	
	var areaNodes = bxe_getAllEditableAreas();
	var xml = areaNodes[0].XMLNode.ownerDocument._vdom.xmldoc;
	return xml.saveXML(xml);
}



/* Mode toggles */

function bxe_toggleTagMode(e) {
	try {
	var editableArea = e.target;
	if (editableArea._SourceMode) {
			e = new eDOMEvent();
			e.setTarget(editableArea);
			e.initEvent("toggleSourceMode");
	}
	var xmldoc = document.implementation.createDocument("","",null);
	
	if (!editableArea._TagMode) {
		createTagNameAttributes(editableArea);
		editableArea._TagMode = true;
		editableArea.AreaInfo.TagModeMenu.Checked = true;
		editableArea.AreaInfo.NormalModeMenu.Checked = false;
	} else {
		var walker = document.createTreeWalker(
			editableArea, NodeFilter.SHOW_ELEMENT,
			null, 
			true);
		var node = editableArea;
		
		do {
			if (node.hasChildNodes()) {
				node.removeAttribute("_edom_tagnameopen");
			}
			node.removeAttribute("_edom_tagnameclose");
			node =   walker.nextNode() 
		} while(node)
		editableArea._TagMode = false;
		editableArea.AreaInfo.TagModeMenu.Checked = false;
		editableArea.AreaInfo.NormalModeMenu.Checked = true;
	}
	}
	catch(e) {alert(e);}

}

function bxe_toggleNormalMode (e) {
	try {
	var editableArea = e.target;
	if (editableArea._SourceMode) {
			e = new eDOMEvent();
			e.setTarget(editableArea);
			e.initEvent("toggleSourceMode");
	}
	if (editableArea._TagMode) {
			e = new eDOMEvent();
			e.setTarget(editableArea);
			e.initEvent("toggleTagMode");
	}
	editableArea.AreaInfo.NormalModeMenu.Checked = true;
	}
	catch(e) {alert(e);}

}

function addTagnames_bxe (e) {		
	
	e.currentTarget.removeEventListener("DOMAttrModified",addTagnames_bxe,false);
	
	var nodeTarget = e.target; 
try {
	createTagNameAttributes(nodeTarget.parentNode.parentNode);
} catch (e) {bxe_catch_alert(e);}
	e.currentTarget.addEventListener("DOMAttrModified",addTagnames_bxe,false);
	
}

function createTagNameAttributes(startNode, startHere) {
	var walker = startNode.XMLNode.createTreeWalker();
	if (!startHere) {
		var node = walker.nextNode();
	} else {
		var node = walker.currentNode;
	}
	
	while( node) {
		if (node.nodeType == 1) {
			var xmlstring = node.getBeforeAndAfterString(false,true);
			node._node.setAttribute("_edom_tagnameopen",xmlstring[0]);
			if (xmlstring[1]) {
				node._node.setAttribute("_edom_tagnameclose",xmlstring[1]);
			}
		}
		node = walker.nextNode();
	}
}

function bxe_toggleAllToSourceMode() {
	var nodes = bxe_getAllEditableAreas();
	for (var i = 0; i < nodes.length; i++) {
		var e = new Object();
		e.target =  nodes[i];
		bxe_toggleSourceMode(e);
	}
	
}

function bxe_toggleSourceMode(e) {
	try {
	var editableArea = e.target;

	if (editableArea._TagMode) {
			e = new eDOMEvent();
			e.setTarget(editableArea);
			e.initEvent("toggleTagMode");
	}
	if (!editableArea._SourceMode) {
		var xmldoc = editableArea.convertToXMLDocFrag();
		
		var form = document.createElement("textarea");
		//some stuff could go into a css file
		form.setAttribute("name","sourceArea");
		form.setAttribute("wrap","soft");
		form.style.backgroundColor = "rgb(255,255,200)";
		form.style.border = "0px";
		form.style.height = editableArea.getCStyle("height");
		form.style.width = editableArea.getCStyle("width");
		/*form.style.fontFamily = editableArea.getCStyle("font-family");
		form.style.fontSize = "12px";
		*/
		editableArea.removeAllChildren();
		
		var xmlstr = document.saveChildrenXML(xmldoc,true);
		form.value = xmlstr.str;
		
		var breaks = form.value.match(/[\n\r]/g);
		if (breaks) {
			breaks = breaks.length;
			form.style.minHeight = ((breaks + 1) * 13) + "px";
		}
		
		editableArea.appendChild(form)
		form.focus();
		//editableArea.appendChild(document.createTextNode(xmlstr.str));
		editableArea.XMLNode.prefix = xmlstr.rootPrefix;
		editableArea._SourceMode = true;
		editableArea.AreaInfo.SourceModeMenu.Checked = true;
		editableArea.AreaInfo.NormalModeMenu.Checked = false;
		bxe_updateXPath(editableArea);
		
	} else {
		var rootNodeName = editableArea.XMLNode.localName;
		if (editableArea.XMLNode.prefix != null) {
			rootNodeName = editableArea.XMLNode.prefix +":"+rootNodeName;
		}
		var innerHTML = '<'+rootNodeName;
		ns = editableArea.XMLNode.xmlBridge.getNamespaceDefinitions();
		for (var i in ns ) {
			if  (i == "xmlns") {
				innerHTML += ' xmlns="'+ ns[i] + '"';
			} else {
				innerHTML += ' xmlns:' + i + '="' + ns[i] +'"';
			}
		}
		
		innerHTML += '>'+editableArea.firstChild.value +'</'+rootNodeName +'>';
		
		var innerhtmlValue = documentLoadXML( innerHTML);
		if (innerhtmlValue) {
			editableArea.XMLNode._node = editableArea.XMLNode.xmlBridge;
			
			editableArea.XMLNode.removeAllChildren();
			editableArea.XMLNode._node.removeAllChildren();
			
			editableArea.XMLNode._node.appendAllChildren(innerhtmlValue.firstChild);

			
			
			editableArea._SourceMode = false;
			//preserve vdom...
			var eaVDOM = editableArea.XMLNode._vdom;
			editableArea.XMLNode = editableArea.XMLNode._node.ownerDocument.init(editableArea.XMLNode._node);
			editableArea.XMLNode.vdom = eaVDOM;

			editableArea.removeAllChildren();
			/*
			
			innerhtmlValue.documentElement.insertIntoHTMLDocument(editableArea,true);
			*/
			editableArea.setStyle("white-space",null);
			var xmlnode = editableArea.XMLNode._node;
			
			editableArea.XMLNode.insertIntoHTMLDocument(editableArea,true);
			editableArea.XMLNode.xmlBridge = xmlnode;
			
			editableArea.AreaInfo.SourceModeMenu.Checked = false;
			editableArea.AreaInfo.NormalModeMenu.Checked = true;
			/*normalize namesapces */
			if (editableArea.XMLNode.xmlBridge.parentNode.nodeType == 1) {
				nsparent = editableArea.XMLNode.xmlBridge.parentNode.getNamespaceDefinitions();
				for (var prefix in nsparent) {
					if (nsparent[prefix] == ns[prefix]) {
						xmlnode.removeAttributeNS(XMLNS,prefix);
					}
				}
			}
			var valid = editableArea.XMLNode.isNodeValid(true);
			if ( ! valid) {
				bxe_toggleSourceMode(e);
			}
			bxe_updateXPath(editableArea);
			
		}
	}
	}
	catch (e) {bxe_catch_alert(e);}

}

function bxe_toggleTextClass(e) {
	var sel = window.getSelection();
	var cssr = sel.getEditableRange();
	if (typeof e.additionalInfo.namespaceURI == "undefined") {
		e.additionalInfo.namespaceURI = "";
	}
	if (cssr == null) {
		alert("Not possible to use this button. You must select a field to edit first");
		return false;
	}	 
	if (bxe_checkForSourceMode(sel)) {
		alert("You're in Source Mode. Not possible to use this button");
		return false;
	}
	//search, if we are already in this mode for anchorNode
	var node = sel.anchorNode.parentNode.XMLNode;
	
	while (node) {
		if (node.localName == e.additionalInfo.localName && node.namespaceURI == e.additionalInfo.namespaceURI) {
			return bxe_CleanInlineIntern(e.additionalInfo.localName,e.additionalInfo.namespaceURI);
		}
		node = node.parentNode;
	}
	
	/*if (sel.anchorNode != sel.focusNode) {
		//Do the same for the endnode
		
		var node = sel.focusNode.parentNode.XMLNode;
		dump(" **** \n " + sel.focusNode.data + "\n***\n");
		while (node) {
			if (node.localName == e.additionalInfo.localName && node.namespaceURI == e.additionalInfo.namespaceURI) {
				dump ("cleanInline2\n");
				return bxe_CleanInlineIntern(e.additionalInfo.localName,e.additionalInfo.namespaceURI);
			}
			node = node.parentNode;
		}
	}*/
	
	if (!bxe_checkIsAllowedChild( e.additionalInfo.namespaceURI,e.additionalInfo.localName,sel)) {
		return false;
	}
	var cb = bxe_getCallback(e.additionalInfo.localName, e.additionalInfo.namespaceURI);
	if (cb ) {
		bxe_doCallback(cb, BXE_SELECTION);
		return;
	}
	
	if (sel.isCollapsed) {
			var newNode = new XMLNodeElement(e.additionalInfo.namespaceURI,e.additionalInfo.localName, 1 , true) ;
		
			sel.insertNode(newNode._node);
	/*		debug("valid? : " + newNode.isNodeValid());
	*/		
			newNode.makeDefaultNodes(false);
			if (newNode._node.firstChild) {
				var sel = window.getSelection();
				var startip = newNode._node.firstInsertionPoint();
				var lastip = newNode._node.lastInsertionPoint();
				sel.collapse(startip.ipNode, startip.ipOffset);
				sel.extend(lastip.ipNode, lastip.ipOffset);
				
			}
	} else {
		sel.toggleTextClass(e.additionalInfo.localName,e.additionalInfo.namespaceURI);
	}
	sel = window.getSelection();
	cssr = sel.getEditableRange();
	
	var _node = cssr.updateXMLNodes();
	debug("isValid?" + _node.XMLNode.isNodeValid());
	bxe_history_snapshot_async();
}


function bxe_NodeInsertedParent(e) {
//	alert("document wide");
	var oldNode = e.target.XMLNode;
	var parent = e.additionalInfo;
	
	parent.XMLNode =  bxe_XMLNodeInit(parent);
	parent.XMLNode.previousSibling = oldNode.previousSibling;
	parent.XMLNode.nextSibling = oldNode.nextSibling;
	if (parent.XMLNode.previousSibling) {
		parent.XMLNode.previousSibling.nextSibling = parent.XMLNode;
	} 
	if (parent.XMLNode.nextSibling) {
		parent.XMLNode.nextSibling.previousSibling = parent.XMLNode;
	}
	parent.XMLNode.firstChild = oldNode;
	parent.XMLNode.lastChild = oldNode;
	parent.XMLNode.parentNode = oldNode.parentNode;
	oldNode.parentNode = parent.XMLNode;
	oldNode.previousSibling = null;
	oldNode.nextSibling = null;
	
}

function bxe_NodeRemovedChild (e) {
	var parent = e.target.XMLNode;
	var oldNode  = e.additionalInfo.XMLNode;
	oldNode.unlink();
}

function bxe_NodeBeforeDelete (e) {
	var node = e.target.XMLNode;
	node.unlink();
}

function bxe_NodePositionChanged(e) {
	var node = e.target;
	node.updateXMLNode();
}
	

function bxe_NodeAppendedChild(e) {
	var parent = e.target.XMLNode;
	var newNode  = e.additionalInfo;
	if (newNode.nodeType == 11) {
		var child = newNode.firstChild;
		while (child) {
			this.appendChildIntern(child.XMLNode);
			child = child.nextSibling;
			
		}
	} else {
		newNode  = newNode.XMLNode;
		parent.appendChildIntern(newNode);
	}
	
}

function bxe_NodeRemovedChildOnly (e) {
	var parent = e.target.XMLNode;
	var oldNode  = e.additionalInfo.XMLNode;

	var div = oldNode.lastChild;
	if (oldNode.firstChild) {
		var child = oldNode.firstChild;
		while (child ) {
			child.parentNode = oldNode.parentNode;
			child = child.nextSibling;
		}
		oldNode.previousSibling.nextSibling = oldNode.firstChild;
		oldNode.nextSibling.previousSibling = oldNode.lastChild;
		oldNode.firstChild.previousSibling = oldNode.previousSibling;
		oldNode.lastChild.nextSibling = oldNode.nextSibling;
		
	} else {
		oldNode.previousSibling.nextSibling = old.nextSibling;
		oldNode.nextSibling.previousSibling = old.previousSibling;
	}
	if (parent.firstChild == oldNode) {
		parent.firstChild = oldNode.nextSibling;
	}
	if (parent.lastChild == oldNode) {
		parent.lastChild = oldNode.previousSibling;
	}
	//oldNode.unlink();

	
}
function bxe_ContextPopup(e) {
	try {
	var node = e.target.XMLNode;
	var popup = e.additionalInfo;
	
	//return on xmlBridge Root nodes
	if (node.xmlBridge) {
		return 
	}
	if (node.vdom && node.vdom.hasAttributes ) {
		
		var menui = popup.addMenuItem("Edit " + e.target.XMLNode.nodeName  + " Attributes", mozilla.getWidgetGlobals().EditAttributes.popup);
		menui.MenuPopup._node = node._node;
	}
	popup.addMenuItem("Copy "  + e.target.XMLNode.nodeName  + " Element", function (e) {
		var widget = e.currentTarget.Widget;
		var delNode = widget.MenuPopup.MainNode;
		delNode.copy();
	});
	var clip = mozilla.getClipboard();
	
	if (clip._clipboard) {
		var _clipboardNodeName = "";
		var _clipboardNamespaceUri = "";
		if (clip._clipboard.firstChild.XMLNode) {
			_clipboardNodeName = clip._clipboard.firstChild.XMLNode.nodeName;
			_clipboardNamespaceUri = clip._clipboard.firstChild.XMLNode.namespaceURI;
		} else {
			_clipboardNodeName = clip._clipboard.firstChild.nodeName;
			_clipboardNamespaceUri = XHTMLNS;
		}
		if (node.parentNode.isAllowedChild(_clipboardNamespaceUri, _clipboardNodeName)) {
			
			
			popup.addMenuItem("Append " + _clipboardNodeName + " from Clipboard", function (e) {
				var widget = e.currentTarget.Widget;
				var appNode = widget.MenuPopup.MainNode;
				var clip = mozilla.getClipboard();
				var clipNode = clip.getData(MozClipboard.TEXT_FLAVOR);
				
				eDOMEventCall("appendNode",document,{"appendToNode":appNode, "node": clipNode})
			});
		}
	}
	
	popup.addMenuItem("Delete "  + e.target.XMLNode.nodeName  + " Element", function (e) {
		var widget = e.currentTarget.Widget;
		var delNode = widget.MenuPopup.MainNode;
		if (delNode._node.InternalParentNode) {
			delNode = delNode._node.InternalParentNode.XMLNode
		}
		var _par = delNode.parentNode;
		
		var _upNode = delNode.previousSibling;
		if (!_upNode) {
			_upNode = delNode.parentNode;
		}
		bxe_history_snapshot();
		_par.removeChild(delNode);
//		_upNode.updateXMLNode();
		
	});

	if (node.previousSibling) {
		popup.addMenuItem("Move up", function (e) {
			var widget = e.currentTarget.Widget;
			var appNode = widget.MenuPopup.MainNode;
			var prevSibling = appNode.previousSibling;
			while (prevSibling && prevSibling._node.nodeType != 1) {
				if (prevSibling._node.nodeType == 3 && !prevSibling._node.isWhitespaceOnly) {
					break;
				}
				prevSibling = prevSibling.previousSibling;
			}
			if (prevSibling) {
				appNode.parentNode.insertBefore(appNode, prevSibling);
			}
		});
	}
	
	if (node.nextSibling) {
		popup.addMenuItem("Move down", function (e) {
			var widget = e.currentTarget.Widget;
			var appNode = widget.MenuPopup.MainNode;
			var nextSibling = appNode.nextSibling;
			while (nextSibling && nextSibling._node.nodeType != 1) {
				if (nextSibling._node.nodeType == 3 && !nextSibling._node.isWhitespaceOnly) {
					break;
				}

				nextSibling = nextSibling.nextSibling;
			}
			if (nextSibling) {
				appNode.parentNode.insertAfter(appNode, nextSibling);
			}
		});
	}
	



	popup.addSeparator();
	if (node.localName == "td" || node.localName == "th") {
		
		// merge right
	//	popup.addSeparator();
		
		
		//split
		if (node._node.getAttribute("colspan") > 1) {
			var menui = popup.addMenuItem("Split right", function(e) {
				var widget = e.currentTarget.Widget;
				var table =  widget.MenuPopup.MainNode._node.TableCellSplitRight();
				table.updateXMLNode();
			});
		}
		
		if (node._node.getAttribute("rowspan") > 1) {
			
			var menui = popup.addMenuItem("Split down", function(e) {
				var widget = e.currentTarget.Widget;
				var table = widget.MenuPopup.MainNode._node.TableCellSplitDown();
				table.updateXMLNode();
			});
		}
		
		
		var nextSibling = node.nextSibling;
		while (nextSibling && nextSibling.nodeType != 1) {
			nextSibling = nextSibling.nextSibling;
		}
		if (nextSibling && (nextSibling.localName == "td" || nextSibling.localName == "th")) {
			var menui = popup.addMenuItem("Merge right", function(e) {
				var widget = e.currentTarget.Widget;
				var _par = widget.MenuPopup.MainNode._node.parentNode;
				var table = widget.MenuPopup.MainNode._node.TableCellMergeRight();
				table.updateXMLNode();
			});
		}
		//TODO fix for last row
		var menui = popup.addMenuItem("Merge down", function(e) {
			var widget = e.currentTarget.Widget;
			var table = widget.MenuPopup.MainNode._node.TableCellMergeDown();
			table.updateXMLNode();
		});
		
		var menui = popup.addMenuItem("Append Row", function(e) {
			var widget = e.currentTarget.Widget;
			var table = widget.MenuPopup.MainNode._node.TableAppendRow();
			table.updateXMLNode();
		});
		var menui = popup.addMenuItem("Append Col", function(e) {
			var widget = e.currentTarget.Widget;
			var table = widget.MenuPopup.MainNode._node.TableAppendCol();
			table.updateXMLNode();
		});
		var menui = popup.addMenuItem("Remove Row", function(e) {
			var widget = e.currentTarget.Widget;
			var table = widget.MenuPopup.MainNode._node.TableRemoveRow();
			table.updateXMLNode();
		});
		
		var menui = popup.addMenuItem("Remove Col", function(e) {
			var widget = e.currentTarget.Widget;
			var table = widget.MenuPopup.MainNode._node.TableRemoveCol();
			table.updateXMLNode();
		});
		
		
		popup.addSeparator();
	}
	popup.MainNode = node;
	} catch (e) { bxe_catch_alert(e);}
}

function bxe_NodeChanged(e) {

	var newNode = e.target;
	var oldNode = e.additionalInfo.XMLNode;
	newNode.XMLNode = bxe_XMLNodeInit(newNode);
	newNode.XMLNode.previousSibling = oldNode.previousSibling;
	newNode.XMLNode.nextSibling = oldNode.nextSibling;
	newNode.XMLNode.parentNode = oldNode.parentNode;
	newNode.XMLNode.firstChild = oldNode.firstChild;
	newNode.XMLNode.lastChild = oldNode.lastChild;

	if (!newNode.XMLNode.previousSibling ) {
		newNode.XMLNode.parentNode.firstChild = newNode.XMLNode;
	} else {
		newNode.XMLNode.previousSibling.nextSibling = newNode.XMLNode;
	}
	if (!newNode.XMLNode.nextSibling ) {
		newNode.XMLNode.parentNode.lastChild = newNode.XMLNode;
	} else {
		newNode.XMLNode.nextSibling.previousSibling = newNode.XMLNode;
	}
		
	oldNode.unlink();
	
}

function bxe_NodeInsertedBefore(e) {
	try {
		var oldNode = e.target.XMLNode;
		var newNode = e.additionalInfo;
		newNode.XMLNode =  bxe_XMLNodeInit(newNode);
		if (oldNode.parentNode) {
			oldNode.parentNode.insertBeforeIntern(newNode.XMLNode, oldNode);
		}
		if (newNode.firstChild ) {
			newNode.updateXMLNode();
		}
		if (oldNode.firstChild ) {
			oldNode.unlinkChildren();
			oldNode._node.updateXMLNode();
		}
	}
	catch(e) { 
		bxe_catch_alert(e);
	}
	

}
function bxe_getChildPosition(node) {
	if (!node) {
		return 0;
	}
	
	var z = 0;
	var textNode = node.previousSibling;
	while (textNode) {
		textNode = textNode.previousSibling;
		z++;
	}
	return z;
}

function bxe_splitAtSelection(node) {
	
	var sel= window.getSelection();
	var cssr = sel.getEditableRange();
	var xmlnode = cssr.startContainer;
	xmlnode.normalize();
	
	var newnode = xmlnode.splitText(cssr.startOffset);
	if (newnode.nodeValue == '') {
		newnode.nodeValue = STRING_NBSP;
	}
	
	if (xmlnode.nodeValue == '') {
		xmlnode.nodeValue = STRING_NBSP;
	}
	
	
	//split all up to node	
	if (node) {
		var _pos = 0;
		while (xmlnode && xmlnode != node) {
			_pos = bxe_getChildPosition(xmlnode);
			xmlnode = xmlnode.parentNode
			xmlnode.split(_pos + 1);
		}
	} else {
		xmlnode.parentNode.split(1);
	}
	return xmlnode;
}

function bxe_appendNode(e) {
	var aNode = e.additionalInfo.appendToNode;
	bxe_history_snapshot();
	
	if (e.additionalInfo.node) {
		if (e.additionalInfo.node.nodeType == 11) {
			var _fragNode = e.additionalInfo.node;
			var _child = _fragNode.lastChild;
			
			while (_child) {
				
				var _next = _child.previousSibling;
				if (_child.nodeType == 1) {
					var newNode = _child.init();
					aNode.parentNode.insertAfter(newNode,aNode);
					newNode._node.updateXMLNode();
					newNode.parentNode.isNodeValid(true,2,false,true);
				} 
				_child = _next;
			}
		} else {
			var cb = bxe_getCallback(e.additionalInfo.node.localName, e.additionalInfo.node.namespaceURI);
			
			if (cb ) {
				if (bxe_doCallback(cb, aNode)) {
					return;
				}
			}
			
			var newNode = e.additionalInfo.node.init();
			
			aNode.parentNode.insertAfter(newNode,aNode);
			newNode._node.updateXMLNode();
			newNode.parentNode.isNodeValid(true,2,false,true);
		}
	} else {
		var cb = bxe_getCallback(e.additionalInfo.localName,e.additionalInfo.namespaceURI);
		
		if (cb ) {
			var sel = window.getSelection();
			sel.collapse(aNode._node.nextSibling,0);
			bxe_doCallback(cb, aNode, true);
			return;
		}
		var newNode = new XMLNodeElement(e.additionalInfo.namespaceURI,e.additionalInfo.localName, 1 ) ;
		
		aNode.parentNode.insertAfter(newNode,aNode);
		newNode.parentNode.isNodeValid(true,2,false,true);
		// looks like not needed, not sure...
		//newNode._node.parentNode.insertBefore(newNode._node.ownerDocument.createTextNode(" "),newNode._node);
		
		newNode.makeDefaultNodes(e.additionalInfo.noPlaceholderText);
		
	}

	
	if (newNode._node.firstChild) {
		var sel = window.getSelection();
		var startip = newNode._node.firstInsertionPoint();
		var lastip = newNode._node.lastInsertionPoint();
		sel.collapse(startip.ipNode, startip.ipOffset);
		sel.extend(lastip.ipNode, lastip.ipOffset);
		
	}
		
}


function bxe_appendChildNode(e) {
		var aNode = e.additionalInfo.appendToNode;
		var newNode = new XMLNodeElement(e.additionalInfo.namespaceURI,e.additionalInfo.localName, 1 ) ;
		aNode.appendChild(newNode);
		newNode.parentNode.isNodeValid(true,2,false,true);
		var cb = bxe_getCallback(e.additionalInfo.localName, e.additionalInfo.namespaceURI);
		if (cb ) {
			bxe_doCallback(cb, newNode);
		} else {
			var cHT  =  newNode.canHaveText;
			if (cHT) {
				if (!e.additionalInfo.noPlaceholderText) {
					newNode.setContent("#" + e.additionalInfo.localName + " ");
					newNode._node.removeAttribute("_edom_tagnameopen");
					newNode.parentNode._node.removeAttribute("_edom_tagnameopen");
				}
			} else {
				var ac = newNode.allowedChildren;
				if (ac.length == 1)  {
					eDOMEventCall("appendChildNode",document,{"appendToNode": newNode, "localName":ac[0].nodeName,"namespaceURI":ac[0].namespaceURI});
				} else if (ac.length > 1) {
					bxe_context_menu.buildElementChooserPopup(newNode,ac);
				}
				else {
					var xmlstring = newNode.getBeforeAndAfterString(false,true);
					newNode.setAttribute("_edom_tagnameopen",xmlstring[0]);
				}
			}
		}
		
}

function bxe_changeLinesContainer2(e) {
	bxe_history_snapshot();
	//alert (window.bxe_ContextNode.nodeName);
	
	var nodeParts = e.split("=");
	if (nodeParts.length < 2 ) {
		nodeParts[1] = null;
	}
	
	var newContainer = window.bxe_ContextNode._node.changeContainer(nodeParts[1],  nodeParts[0]);
	
		newContainer.XMLNode = new XMLNodeElement( nodeParts[1], nodeParts[0], window.bxe_ContextNode._node.nodeType);
		try {
			newContainer.updateXMLNode();
		} catch(e) {alert(newContainer + " can't be updateXMLNode()'ed\n" + e);
		}
	
	newContainer.XMLNode.parentNode.isNodeValid(false, 2,false,true);
	bxe_history_snapshot_async();
	window.bxe_ContextNode = newContainer.XMLNode;
	//bxe_delayedUpdateXPath();
}



function bxe_changeLinesContainer(e) {
	bxe_history_snapshot();
	var nodeParts = e.additionalInfo.split("=");
	if (nodeParts.length < 2 ) {
		nodeParts[1] = null;
	}
	var newContainer = window.getSelection().changeLinesContainer(nodeParts[0],  nodeParts[1]);
	for(var i=0; i<newContainer.length; i++)
	{ 
		newContainer[i].XMLNode = new XMLNodeElement( nodeParts[1], nodeParts[0], newContainer[i].nodeType);
		try {
			newContainer[i].updateXMLNode();
		} catch(e) {alert(newContainer[i] + " can't be updateXMLNode()'ed\n" + e);
		}
	}
	newContainer[0].XMLNode.parentNode.isNodeValid(false, 2,false,true);
	bxe_history_snapshot_async();
	bxe_delayedUpdateXPath();
}



/* end mode toggles */

/* area mode stuff */

function bxe_getAllEditableAreas() {
	var nsResolver = new bxe_nsResolver(document.documentElement);
	var result = document.evaluate("/html/body//*[@bxe_xpath]", document.documentElement,nsResolver, 0, null);
	var node = null;
	var nodes = new Array();
	node = result.iterateNext()
	while (node) {
		nodes.push(node);
		node = result.iterateNext()
	}
	return nodes;
}

function bxe_alignAllAreaNodes() {
	var nodes = bxe_getAllEditableAreas();
	for (var i = 0; i < nodes.length; i++) {
		bxe_alignAreaNode(nodes[i].parentNode,nodes[i]);
	}
}

function bxe_alignAreaNode(menuNode,areaNode) {
	if (areaNode.display == "block") {
		menuNode.position("-8","5");
	} else {
		menuNode.position("0","0");
	}
	menuNode.draw();
}

/* debug stuff */
function BX_debug(object)
{
    var win = window.open("","debug");
	bla = "";
    for (b in object)
    {

        bla += b;
        try {

            bla +=  ": "+object.eval(b) ;
        }
        catch(e)
        {
            bla += ": NOT EVALED";
        };
        bla += "\n";
    }
    win.document.innerHTML = "";

    win.document.writeln("<pre>");
    win.document.writeln(bla);
    win.document.writeln("<hr>");
}

function BX_showInWindow(string)
{
    var win = window.open("","debug");

    win.document.innerHTML = "";
	win.document.writeln("<html>");
	win.document.writeln("<body>");

    win.document.writeln("<pre>");
	if (typeof string == "string") {
		win.document.writeln(string.replace(/</g,"&lt;"));
	}
	win.document.writeln("</pre>");
	win.document.writeln("</body>");
	win.document.writeln("</html>");
}

function bxe_about_box_fade_out (e) {
	bxe_about_box.node.style.display = "none";
	window.status = null;
}

function bxe_draw_widgets() {
	
	
	// make menubar
	bxe_menubar = new Widget_MenuBar();
	var img = document.createElement("img");
	img.setAttribute("src",mozile_root_dir + "images/bxe.png");
	
	img.setAttribute("align","right");
	bxe_menubar.node.appendChild(img);
	var submenu = new Array("Save",function() {eDOMEventCall("DocumentSave",document);});
	submenu.push("Save & Exit",function() {eDOMEventCall("DocumentSave",document,{"exit": true});});
	submenu.push("Exit",function() {eDOMEventCall("Exit",document);});
	bxe_menubar.addMenu("File",submenu);

	var submenu2 = new Array("Undo",function() {eDOMEventCall("Undo",document);},"Redo",function () {eDOMEventCall("Redo",document)});
	bxe_menubar.addMenu("Edit",submenu2);
	
	var submenu3 = new Array();
	submenu3.push("Show XML Document",function(e) {BX_showInWindow(bxe_getXmlDocument());})
	submenu3.push("Show RNG Document",function(e) {BX_showInWindow(bxe_getRelaxNGDocument());})
	
	bxe_menubar.addMenu("Debug",submenu3);
	
	
	var submenu4 = new Array();
	
	submenu4.push("About Bitflux Editor",function(e) { 
		bxe_about_box.setText("");
		bxe_about_box.show(true);
		
	});
	
	submenu4.push("Help",function (e) { 
		bla = window.open("http://wiki.bitfluxeditor.org","help","width=800,height=600,left=0,top=0");
		bla.focus();
	
	});

	submenu4.push("BXE Website",function (e) { 
		bla = window.open("http://www.bitfluxeditor.org","help","width=800,height=600,left=0,top=0");
		bla.focus();
	
	});

	submenu4.push("Show System Info", function(e) {
		var modal = new Widget_ModalBox();
		modal.node = modal.initNode("div","ModalBox");
		modal.Display = "block";
		modal.node.appendToBody();
		modal.position(100,100,"absolute");
		modal.initTitle("System Info");
		modal.initPane();
		var innerhtml =  "<br/>BXE Version: " + BXE_VERSION  + "<br />";
		innerhtml += "BXE Build Date: " + BXE_BUILD + "<br/>";
		innerhtml += "BXE Revision: " + BXE_REVISION + "<br/><br/>";
		innerhtml += "User Agent: " + navigator.userAgent + "<br/><br/>";
		modal.PaneNode.innerHTML = innerhtml;
		modal.draw();
		var subm = document.createElement("input");
		subm.setAttribute("type","submit");
		subm.setAttribute("value","OK");
		subm.onclick = function(e) {
			var Widget = e.target.parentNode.parentNode.Widget;
			e.target.parentNode.parentNode.style.display = "none";
		}
		modal.PaneNode.appendChild(subm);
		
	});

	submenu4.push("Report Bug",function(e) { 
		bla = window.open("http://bugs.bitfluxeditor.org/enter_bug.cgi?product=Editor&version="+BXE_VERSION+"&priority=P3&bug_severity=normal&bug_status=NEW&assigned_to=&cc=&bug_file_loc=http%3A%2F%2F&short_desc=&comment=***%0DVersion: "+BXE_VERSION + "%0DBuild: " + BXE_BUILD +"%0DUser Agent: "+navigator.userAgent + "%0D***&maketemplate=Remember+values+as+bookmarkable+template&form_name=enter_bug","help","");
		bla.focus();
		
	});
	
	
	bxe_menubar.addMenu("Help",submenu4);
	
	bxe_menubar.draw();
	
	//make toolbar
	
	bxe_toolbar = new Widget_ToolBar();
	bxe_format_list = new Widget_MenuList("m",function(e) {
		bxe_changeLinesContainer2(this.value);
	//	eDOMEventCall("changeLinesContainer",document,this.value)
	});

	bxe_toolbar.addItem(bxe_format_list);
	
	bxe_toolbar.addButtons(bxe_config.getButtons());
	
	
	bxe_toolbar.draw();

	bxe_status_bar = new Widget_StatusBar();
	var ea = bxe_getAllEditableAreas();
	for (var i = 0; i < ea.length; i++) {
		
	ea[i].addEventListener("click",MouseClickEvent,false);
	}

	// if not content editable and ptb is enabled then hide the toolbar (watch out
	// for selection within the toolbar itself though!)
	
	
	window.setTimeout(bxe_about_box_fade_out, 1000);
}

function MouseClickEvent(e) {
	
	var target = e.target.parentElement;

	if(target.userModifiable && bxe_editable_page) {
		return bxe_updateXPath(e.target);
	}
	return true;
}

function bxe_updateXPath(e) {
	var sel = window.getSelection();
	var cssr = sel.getEditableRange();
	if (e && e.localName == "TEXTAREA") {
		bxe_format_list.removeAllItems();
		bxe_format_list.appendItem("-Source Mode-","");
		bxe_status_bar.buildXPath(e.parentNode);
		
	}
	else if (cssr) {
		if ( cssr.top._SourceMode) {
			//clear list
			bxe_format_list.removeAllItems();
			bxe_format_list.appendItem("-Source Mode-","");
			bxe_status_bar.buildXPath(cssr.top);

		} else {
			if (e) {
				bxe_status_bar.buildXPath(e);
			} else {
				bxe_status_bar.buildXPath(sel.anchorNode);
			}
			var line = cssr.firstLine;
			bxe_format_list.removeAllItems();
			if (line && line.container  ) {
				var thisNode = line.container.XMLNode;
				if (thisNode.xmlBridge) {
					var pref = "";
					if (thisNode.prefix) {
						pref = thisNode.prefix + ":";
					}
					menuitem = bxe_format_list.appendItem(pref + thisNode.nodeName, thisNode.localName + "=" + thisNode.namespaceURI);
				} else {
					window.bxe_ContextNode = thisNode;
					var ac = thisNode.parentNode.allowedChildren;
					var menuitem;
					var thisLocalName = thisNode.localName;
					var thisNamespaceURI = thisNode.namespaceURI;
					
					for (i = 0; i < ac.length; i++) {
						var _name = ac[i].namespaceURI + ":" +ac[i].localName;
						if (!bxe_config.dontShowInContext[_name]  && !bxe_config.dontShowInContextBlock[_name] && ac[i].nodeType != 3 && ac[i].vdom.canHaveChildren)  {
							menuitem = bxe_format_list.appendItem(ac[i].nodeName, ac[i].localName + "=" + ac[i].namespaceURI);
							if (ac[i].localName == thisLocalName &&  ac[i].namespaceURI == thisNamespaceURI) {
								menuitem.selected=true;
							}
						}
					}
				}
			} else {
				bxe_format_list.appendItem("no block found","");
			}
		}
	}
}

function bxe_delayedUpdateXPath() {
	if (bxe_delayedUpdate) {
		window.clearTimeout(bxe_delayedUpdate);
	}
	bxe_delayedUpdate = window.setTimeout("bxe_updateXPath()",100);
}

function bxe_ContextMenuEvent(e) {

	var sel = window.getSelection();
	var cssr = sel.getEditableRange();
	
	if(!cssr)
	{
		return true;
	}
	if (cssr.top._SourceMode) {
		return true;
	}
	var node ;
	if (cssr.startContainer.nodeType == Node.TEXT_NODE) {
		node = cssr.startContainer.parentNode;
	} else {
		node = cssr.startContainer;
	}
	if (node != e.target) {
		node = e.target;
	}
	var _n = node;
	while(_n.nodeType == 1) {
		if (_n == cssr.top) {
			break;
		}
		_n = _n.parentNode;
	}
	if (_n != cssr.top) {
		return false;
	}
	bxe_context_menu.show(e,node);
	e.stopPropagation();
	e.returnValue = false;
	e.preventDefault();
	return false;
}

function bxe_UnorderedList() {
	var sel = window.getSelection();
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	
		var cssr = sel.getEditableRange();

	if(!cssr) {
		return;
	}
	var lines = cssr.lines;
	if (lines[0].container.XMLNode.xmlBridge) {
		alert("You can't change '" + lines[0].container.XMLNode.nodeName + "' to a list");
		return;
	}
	
	if (lines[0].container.XMLNode.nodeName != "li" && !bxe_checkIsAllowedChildOfNode( XHTMLNS,"ul",lines[0].container.parentNode)) {
		return;
	}

	var lines = window.getSelection().toggleListLines("ul", "ol");
	lines[0].container.updateXMLNode();
	var li = lines[0].container;
	while (li ) {
		if (li.nodeName == "li") {
			li.XMLNode.namespaceURI = XHTMLNS;
		}
		var attr = li.XMLNode.attributes;
		for (var i in attr) {
			if (! li.XMLNode.isAllowedAttribute(attr[i].nodeName)) {
				li.XMLNode.removeAttribute(attr[i].nodeName);
			}
		}

		li = li.nextSibling;
	}
	//lines[0].container.parentNode.setAttribute("class","type1");
	bxe_updateXPath();
}

function bxe_OrderedList() {
	var sel = window.getSelection();
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	
	var cssr = sel.getEditableRange();

	if(!cssr) {
		return;
	}
	var lines = cssr.lines;
	if (lines[0].container.XMLNode.xmlBridge) {
		alert("You can't change " + lines[0].container.XMLNode.nodeName + " to a list");
		return;
	}
	
	if (lines[0].container.XMLNode.nodeName != "li" && !bxe_checkIsAllowedChildOfNode( XHTMLNS,"ol",lines[0].container.parentNode)) {
		return;
	}
	
	var lines = window.getSelection().toggleListLines("ol", "ul");

	lines[0].container.updateXMLNode();
	
	var li = lines[0].container;
	while (li ) {
		if (li.nodeName == "li") {
			li.XMLNode.namespaceURI = XHTMLNS;
		}
		var attr = li.XMLNode.attributes;
		for (var i in attr) {
			if (! li.XMLNode.isAllowedAttribute(attr[i].nodeName)) {
				li.XMLNode.removeAttribute(attr[i].nodeName);
			}
		}
		li = li.nextSibling;
	}
	
	// needed by unizh
	//lines[0].container.parentNode.setAttribute("class","type1");
	bxe_updateXPath();
}

function bxe_InsertObject() {
	var sel = window.getSelection();
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	var object = documentCreateXHTMLElement("object");
	
	sel.insertNode(object);
}

function bxe_InsertAsset() {
	//this code is quite lenya specific....
	// especially the unizh: check
	var sel = window.getSelection();
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	
	var cssr = sel.getEditableRange();
	var pN = cssr.startContainer.parentNode;
	//FIXME: unizh code should be outsourced...
	if ((pN.XMLNode.localName == "highlight-title" && pN.XMLNode.namespaceURI == "http://unizh.ch/doctypes/elements/1.0") ||
	(pN.XMLNode.localName == "asset" && pN.XMLNode.namespaceURI == "http://apache.org/cocoon/lenya/page-envelope/1.0")) {
		alert("Asset is not allowed here");
		return false;
	}
	
	if (!bxe_checkIsAllowedChild("http://apache.org/cocoon/lenya/page-envelope/1.0","asset",sel, true) && !bxe_checkIsAllowedChildOfNode("http://apache.org/cocoon/lenya/page-envelope/1.0","asset",pN.parentNode, true)) {
		alert ("Asset is not allowed here");
		return false;
	}
	var object = document.createElementNS("http://apache.org/cocoon/lenya/page-envelope/1.0","asset");
	var cb = bxe_getCallback("asset","http://apache.org/cocoon/lenya/page-envelope/1.0");
	if (cb ) {
		bxe_doCallback(cb, object);
	} 
	else {
	
		sel.insertNode(object);
	}
}

function bxe_InsertImage() {
	
	var sel = window.getSelection();
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	
	var mod = mozilla.getWidgetModalBox("Enter the image url or file name:", function(values) {
		if(values.imgref == null) // null href means prompt canceled
			return;
		if(values.imgref == "") 
			return; // ok with no name filled in

		
		var img = documentCreateXHTMLElement("img");
		img.firstChild.setAttribute("src",values.imgref);
		sel.insertNode(img);
		img.updateXMLNode();
		img.setAttribute("src",values.imgref);
	});
	
	mod.addItem("imgref", "", "textfield","Image URL:");
	mod.show(100,50,"fixed");
	
}

function bxe_checkForSourceMode(sel) {
	if (bxe_format_list.node.options.length == 1 && bxe_format_list.node.options.selectedIndex == 0) {
		if ( bxe_format_list.node.options[0].text == "-Source Mode-") {
			alert("You're in Source Mode. Not possible to use this button");
			return true;
		}
	}
	// the following is legacy code. actually not needed anymore, AFAIK..
	var cssr = sel.getEditableRange();
	if (cssr && cssr.top._SourceMode) {
		alert("You're in Source Mode. Not possible to use this button");
		return true;
	}
	return false;
}

function bxe_checkIsAllowedChild(namespaceURI, localName, sel, noAlert) {
	if (!sel) {
		sel = window.getSelection();
	}
	
	var cssr = sel.getEditableRange();
	var parentnode = null;
	if (cssr.startContainer.nodeType != 1) {
		parentnode = cssr.startContainer.parentNode;
	} else {
		parentnode = cssr.startContainer;
	}
	return bxe_checkIsAllowedChildOfNode(namespaceURI,localName, parentnode, noAlert);
	
}

function bxe_checkIsAllowedChildOfNode(namespaceURI,localName, node, noAlert) {
	if (localName == "#text") {
		localName = null;
	}
	if (localName == null || node.XMLNode.isAllowedChild(namespaceURI, localName) ) {
		return true;
	} else {
		if (!noAlert) {
			alert (localName + " is not allowed as child of " + node.XMLNode.localName);
		}
		return false;
	}
}

function bxe_InsertTable() {
	var sel = window.getSelection();
	var cssr = sel.getEditableRange();
	
	if (!bxe_checkIsAllowedChild(XHTMLNS,"table",sel, true) &&  !bxe_checkIsAllowedChildOfNode(XHTMLNS,"table",cssr.startContainer.parentNode.parentNode, true)) {
		alert ("Table is not allowed here");
		return false;
	}

	var object = documentCreateXHTMLElement("table");
	//sel.insertNode(object);
	window.bxe_ContextNode = BXE_SELECTION;
	bxe_InsertTableCallback();
}


function bxe_InsertTableCallback(node) {
	
	var sel = window.getSelection();

	if (node && node.firstChild) {
		return false;
	}
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	
	var mod = mozilla.getWidgetModalBox("Create Table", function(values) {
		var te = documentCreateTable(values["rows"], values["cols"]);
		if(!te) {
			alert("Can't create table: invalid data");
		}
		else if (window.bxe_ContextNode == BXE_SELECTION) {
			te.setAttribute("class", bxe_config.options[OPTION_DEFAULTTABLECLASS]);

			var sel = window.getSelection(); 	
			if (!bxe_checkIsAllowedChild(XHTMLNS,"table",sel, true)) {
				var cssr = sel.getEditableRange();
				ip = documentCreateInsertionPoint(cssr.top, cssr.startContainer, cssr.startOffset);
				ip.splitXHTMLLine()
				cssr.selectInsertionPoint(ip);
			}
			sel.insertNodeRaw(te, true);
			sel.insertNodeRaw(document.createTextNode("\n"));
			te.parentNode.insertBefore(document.createTextNode("\n"),te);
			te.updateXMLNode();
		} else if (window.bxe_ContextNode){
			te.setAttribute("class", bxe_config.options[OPTION_DEFAULTTABLECLASS]);
			var newNode = te.init();
			window.bxe_ContextNode.parentNode.insertAfter(newNode, window.bxe_ContextNode);
			newNode.isNodeValid();
		}
	});
	mod.addItem("rows",2,"textfield","number of rows");
	mod.addItem("cols",2,"textfield","number of cols");
	mod.show(100,50, "fixed");
	
}

function bxe_CleanInline(e) {
	bxe_CleanInlineIntern();
}

function bxe_CleanInlineIntern(localName, namespaceUri) {
	var sel = window.getSelection();
	var doitagain = 0;
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	
	var cssr = sel.getEditableRange();
	if(cssr.collapsed)
		return;
 
	// go through all text nodes in the range and link to them unless already set to cssr link
	var textNodes = cssr.textNodes;
	for(i=0; i<textNodes.length; i++) {
		// figure out cssr and then it's on to efficiency before subroutines ... ex of sub ... 
		// try text nodes returning one node ie/ node itself! could cut down on normalize calls ...
		var textContainer = textNodes[i].parentNode;
		if (textContainer && textContainer.getCStyle("display") == "inline") {
			if (localName) {
				if (textContainer.parentNode.firstChild == textContainer) {
					textNodes.push(textContainer);
				}
				if(!(textContainer.XMLNode.localName == localName &&
				 textContainer.XMLNode.namespaceURI == namespaceUri)) {
					 continue;
				}
			}
			if(textContainer.childNodes.length > 1) {
				var siblingHolder;
				
				// leave any nodes before or after cssr one with their own copy of the container
				if(textNodes[i].previousSibling) {
					if (textNodes[i].previousSibling.nodeType == 3) {
						var siblingHolder = textContainer.cloneNode(false);
						textContainer.parentNode.insertBefore(siblingHolder, textContainer);
						siblingHolder.appendChild(textNodes[i].previousSibling);
					}
				}
				
				if(textNodes[i].nextSibling) { 
					if (textNodes[i].nextSibling.nodeType == 3) {
						var siblingHolder = textContainer.cloneNode(false);
						if(textContainer.nextSibling) {
							textContainer.parentNode.insertBefore(siblingHolder, textContainer.nextSibling);
						} else {  
							textContainer.parentNode.appendChild(siblingHolder);
						}
						siblingHolder.appendChild(textNodes[i].nextSibling);
					} else {
						textContainer.split(1);
					}
					
				}
			}
			// rename it to span and remove its href. If span is empty then delete span
			doitagain++;
			textContainer.parentNode.removeChildOnly(textContainer);
		}
	}
	
	// normalize A elements 
	var normalizeRange = document.createRange();
	normalizeRange.selectNode(cssr.commonAncestorContainer);
	normalizeRange.normalizeElements("span");
	normalizeRange.detach();

	// now normalize text
	cssr.commonAncestorContainer.parentElement.normalize();
	var _node = cssr.updateXMLNodes();
	sel.selectEditableRange(cssr);
	
	
	if (doitagain > 1 || (!localName && cssr.startContainer.parentNode.getCStyle("display") == "inline")) {
		bxe_CleanInlineIntern(localName,namespaceUri);
	}
	
}


function bxe_DeleteLink(e) {
	var sel = window.getSelection();
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	
	var cssr = sel.getEditableRange();
	
	var textContainer = sel.anchorNode.parentNode;
	
	if(textContainer.nodeNamed("span") && textContainer.getAttribute("class") == "a" )
	{
		textContainer.parentNode.removeChildOnly(textContainer);
		
	}
	
	
	
	sel.selectEditableRange(cssr);
	
	
	sel.anchorNode.updateXMLNode();
}

function bxe_InsertLinkExtern(href,title,text) {
	var sel = window.getSelection();
	if (sel.toString().length == 0) {
		var text = document.createTextNode(text);
		var  a = document.createElementNS(XHTMLNS,"span");
		a.setAttribute("class","a");
		a.setAttribute("href",href);
		if (title) {
			a.setAttribute("title",title);
		}
		a.appendChild(text);
		sel.insertNodeRaw(a);
	} else {
		sel.linkText(href,title);
	}
	sel.anchorNode.parentNode.updateXMLNode(true);
	sel.focusNode.parentNode.updateXMLNode(true);
}


function bxe_InsertLink(e) {
	
	var sel = window.getSelection();
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	var aValue = "";
	if (sel.anchorNode.parentNode.XMLNode.localName == "a") {
		aValue = sel.anchorNode.parentNode.getAttribute("href");
	}
	else if(sel.isCollapsed) { // must have a selection or don't prompt
		return;
	}
	
	if (!bxe_checkIsAllowedChild(XHTMLNS,"a",sel)) {
		return false;
	}
	
	
	var mod = mozilla.getWidgetModalBox("Enter a URL:", function(values) {
		var href = values["href"];
		if(href == null) // null href means prompt canceled - BUG FIX FROM Karl Guertin
			return;
		var sel = window.getSelection();
		if (sel.anchorNode.parentNode.XMLNode.localName == "a") {
		 sel.anchorNode.parentNode.setAttribute("href", href);
		 return true;
		}
		if(href != "") 
			sel.linkText(href);
		else
			sel.clearTextLinks();
		
		sel.anchorNode.parentNode.updateXMLNode();
	}
	);
		
	
	mod.addItem("href",aValue,"textfield","Enter a URL:");
	mod.show(100,50, "fixed");
	
	
	return;
}

function bxe_insertLibraryLink() {
	drawertool.cssr = window.getSelection().getEditableRange();
	drawertool.openDrawer( 'liblinkdrawer' );
	return;

}

function bxe_catch_alert(e ) {
	
	alert(bxe_catch_alert_message(e));
}

function bxe_catch_alert_message(e) {
	var mes = "ERROR in Bitflux Editor:\n"+e.message +"\n";
	try
	{
		if (e.filename) {
			mes += "In File: " + e.filename +"\n";
		} else {
			mes += "In File: " + e.fileName +"\n";
		}
		
	}
	catch (e)
	{
		mes += "In File: " + e.fileName +"\n";
	}
	try
	{
		mes += "Linenumber: " + e.lineNumber + "\n";
	}
	catch(e) {}
	
	mes += "Type: " + e.name + "\n";
	mes += "Stack:" + e.stack + "\n";
	return mes;
}

function bxe_exit(e) {
	if (bxe_checkChangedDocument()) {
		if (confirm( "You have unsaved changes.\n Click cancel to return to the document.\n Click OK to really leave the page.")) {
			bxe_lastSavedXML = bxe_getXmlDocument();
			window.location = bxe_config.exitdestination;
		}
	} else {
		bxe_lastSavedXML = bxe_getXmlDocument();
		window.location = bxe_config.exitdestination;
	}
	
}

function bxe_checkChangedDocument() {
	var xmlstr = bxe_getXmlDocument();
	if (bxe_editable_page && xmlstr && xmlstr != bxe_lastSavedXML) {
		return true;
	} else {
		return false;
	}
}

function bxe_not_yet_implemented() {
	alert("not yet implemented");
}


/* bxe_nsResolver */

function bxe_nsResolver (node) {
	this.metaTagNSResolver = null;
	this.metaTagNSResolverUri = null;
	
	//this.htmlDocNSResolver = null;
	this.xmlDocNSResolver = null;
	this.node = node;
	
	
}

bxe_nsResolver.prototype.lookupNamespaceURI = function (prefix) {
	var url = null;
	// if we never checked for meta bxeNS tags, do it here and save the values in an array for later reusal..
	if (!this.metaTagNSResolver) {
		var metas = document.getElementsByName("bxeNS");
		this.metaTagNSResolver = new Array();
		for (var i=0; i < metas.length; i++) {
			if (metas[i].localName.toLowerCase() == "meta") {
				var ns = metas[i].getAttribute("content").split("=");
				this.metaTagNSResolver[ns[0]] = ns[1]
			}
		}
	}
	//check if the prefix was there and return it
	if (this.metaTagNSResolver[prefix]) {
		return this.metaTagNSResolver[prefix];
	}
	/* there are no namespaces in even xhtml documents (or mozilla discards them somehow or i made a stupid mistake
	therefore no NS-lookup in document. */
	/*
	if (! this.htmlDocNSResolver) {
		this.htmlDocNSResolver = document.createNSResolver(document.documentElement);
	}
	url = this.htmlDocNSResolver.lookupNamespaceURI(prefix);
	if (url) {
		return url;
	}
	*/
	
	//create NSResolver, if not done yet
	if (! this.xmlDocNSResolver) {
		this.xmlDocNSResolver = this.node.ownerDocument.createNSResolver(this.node.ownerDocument.documentElement);
	}
	
	//lookup the prefix
	url = this.xmlDocNSResolver.lookupNamespaceURI(prefix);
	if (url) {
		return url;
	}
	// if still not found and we want the bxe prefix.. return that
	if (prefix == "bxe") {
		return BXENS;
	}
	
	if (prefix == "xhtml") {
		return XHTMLNS;
	}
	
	//prefix not found
	return null;
}

bxe_nsResolver.prototype.lookupNamespacePrefix = function (uri) {
	
	if (!this.metaTagNSResolverUri) {
		var metas = document.getElementsByName("bxeNS");
		this.metaTagNSResolverUri = new Array();
		for (var i=0; i < metas.length; i++) {
			if (metas[i].localName.toLowerCase() == "meta") {
				var ns = metas[i].getAttribute("content").split("=");
				this.metaTagNSResolverUri[ns[1]] = ns[0]
			}
		}
	}
	//check if the prefix was there and return it
	if (this.metaTagNSResolverUri[uri]) {
		return this.metaTagNSResolverUri[uri];
	}
	return null;
}
// replaces the function from mozile...
documentCreateXHTMLElement = function (elementName,attribs) {
	var newNode;
	var childNode;
	switch( elementName) {
		case "a":
			htmlelementname = "span";
			break;
		case "object":
		case "img":
			htmlelementname = "span";
			childNode = document.createElementNS(XHTMLNS,elementName);
			childNode.setAttribute("_edom_internal_node","true");
			break;
		default:
			htmlelementname = elementName;
	}
	newNode = document.createElementNS(XHTMLNS,htmlelementname);
	if (elementName != htmlelementname) {
		newNode.setAttribute("class", elementName);
	}
	
	if (elementName == "span") {
		newNode.setAttribute("__bxe_keep_span","true");
	}
	if (childNode) {
		if (attribs) {
			for (var i = 0; i < attribs.length ;  i++) {
				if (attribs[i].namespaceURI != XMLNS) {
					childNode.setAttributeNS(attribs[i].namespaceURI, attribs[i].localName,attribs[i].value);
				}
			}
		}
		newNode.appendChild(childNode);
		newNode.InternalChildNode = childNode;
		childNode.InternalParentNode = newNode;
		newNode.eDOMaddEventListener("NodeAttributesModified",bxe_InternalChildNodesAttrChanged,false);
	
	}
	return newNode;
}

function bxe_InternalChildNodesAttrChanged(e) {
	var node = e.target;
	var attribs = node.attributes;
	//we have to replace the old internalnode, redrawing of new object-sources seem not to work...
	var newNode = document.createElementNS(node.InternalChildNode.namespaceURI, node.InternalChildNode.localName);
	for (var i = 0; i < attribs.length ;  i++) {
		var prefix = attribs[i].localName.substr(0,5);
		if (prefix != "_edom" && prefix != "__bxe") {
			newNode.setAttributeNS(attribs[i].namespaceURI,attribs[i].localName,attribs[i].value);
		}
	}
	node.replaceChild(newNode,node.InternalChildNode);
	newNode.setAttribute("_edom_internal_node","true");
	newNode.InternalParentNode = node;
	node.InternalChildNode = newNode;
	if (!node.XMLNode.hasChildNodes()) {
		createTagNameAttributes(node,true);
	}
}

function bxe_registerKeyHandlers() {
	if (bxe_editable_page) {
		document.addEventListener("keypress", keyPressHandler, true);
//key up and down handlers are needed for interapplication copy/paste without having native-methods access
//if you're sure you have native-methods access you can turn them off
		document.addEventListener("keydown", keyDownHandler, true);
		document.addEventListener("keyup", keyUpHandler, true);
	}
}

function bxe_disableEditablePage() {
	
	bxe_deregisterKeyHandlers();
	bxe_editable_page = false;
	document.removeEventListener("contextmenu",bxe_ContextMenuEvent, false);
	
}

function bxe_deregisterKeyHandlers() {
	document.removeEventListener("keypress", keyPressHandler, true);
//key up and down handlers are needed for interapplication copy/paste without having native-methods access
//if you're sure you have native-methods access you can turn them off
	document.removeEventListener("keydown", keyDownHandler, true);
	document.removeEventListener("keyup", keyUpHandler, true);
}

function bxe_insertContent(content, replaceNode, options) {
	window.setTimeout(function() {bxe_insertContent_async(content,replaceNode,options);},1);
}

function bxe_insertContent_async(node,replaceNode, options) {
	var docfrag;
	if (typeof node == "string") {
        docfrag = node.convertToXML()
	} else {
		docfrag = node;
	}
	var oldStyleInsertion = false;
	bxe_history_snapshot();
	if (replaceNode == BXE_SELECTION) {
		var sel = window.getSelection();
		var  _currentNode = docfrag.lastChild;
		while (_currentNode && _currentNode.nodeType == 3) {
			_currentNode = _currentNode.previousSibling;
		}
		if (!_currentNode) {
			_currentNode = docfrag.lastChild;
		}
		var _node = _currentNode.prepareForInsert();
		if (options & BXE_SPLIT_IF_INLINE) {
			
			if (!bxe_checkIsAllowedChild(_node.XMLNode.namespaceURI,_node.XMLNode.localName,sel, true)) {
				var cssr = sel.getEditableRange();
				ip = documentCreateInsertionPoint(cssr.top, cssr.startContainer, cssr.startOffset);
				ip.splitXHTMLLine()
				cssr.selectInsertionPoint(ip);
				oldStyleInsertion = true;
			}
		}
		if (sel.anchorNode.nodeType == 3) {
			// if nodeValue == " ", shit happens.
			
			var regexp = new RegExp('^[ '+STRING_NBSP+']+$');
			sel.anchorNode.parentNode.normalize();
			if (regexp.test(sel.anchorNode.nodeValue)) {
				sel.anchorNode.nodeValue = "";
			}
		}
		sel.anchorNode.normalize();
		while (_currentNode) {
			sel.insertNodeRaw(_node,oldStyleInsertion);
		
			_node.updateXMLNode();
			
			_currentNode = _currentNode.previousSibling;
			if (_currentNode) {
				_node = _currentNode.prepareForInsert();
			}
		}
		bxe_history_snapshot_async();
		return _node;
	} else if (replaceNode) {
		
		var newNode = docfrag.firstChild.init();

		
		replaceNode.parentNode.insertAfter(newNode,replaceNode);
		newNode._node.updateXMLNode();
		newNode.isNodeValid(true,2,false,true);
		bxe_history_snapshot_async();
		
	} else {
		docfrag.firstChild.init();
		var sel= window.getSelection();
		var cssr =sel.getEditableRange();
		eDOMEventCall("appendNode",document,{"appendToNode":cssr.startContainer.parentNode.XMLNode, "node": docfrag.firstChild})
	}
}

String.prototype.convertToXML = function() {
	var BX_parser = new DOMParser();
	var content = this.toString();
	if (content.indexOf("<") >= 0) {
		
		content = BX_parser.parseFromString("<?xml version='1.0'?><rooot>"+content+"</rooot>","text/xml");
		content = content.documentElement;
		
		BX_tmp_r1 = document.createRange();
		
		BX_tmp_r1.selectNodeContents(content);
		content = BX_tmp_r1.extractContents();
		
	} else {
		content = document.createTextNode(content);
	}
	return content;
	
}

function bxe_getCallback (nodeName, namespaceURI) {
	
	if (bxe_config.callbacks[namespaceURI + ":" + nodeName]) {
		return bxe_config.callbacks[namespaceURI + ":" + nodeName];
	} else {
		return null;
	}
}

function bxe_doCallback(cb, node,dontPrecheck ) {
	window.bxe_ContextNode = node;
	//this is for prechecking, if an eventual popup should be called at all
	if (cb["precheck"] && !dontPrecheck) {
		if (!(eval(cb["precheck"] +"(node)"))) {
			return false;
		} 
	}
	if (cb["type"] == "popup") {
		
		
		var pop = window.open(cb["content"],"popup","width=600,height=600,resizable=yes,scrollbars=yes");
		pop.focus();
		
	} else if (cb["type"] == "function") {
		return eval(cb["content"] +"(node)");
	}
}
		
function bxe_checkIfNotALink (node) {
	var sel = window.getSelection();
	if (sel.anchorNode.parentNode.XMLNode.localName == "a" || sel.focusNode.parentNode.XMLNode.localName == "a") {
		alert("There is already a link here, please use the \"Edit Attributes\" function, to edit the link.");
		return false;
	}
	return true;
}

function bxe_alert(text) {
	var widg = mozilla.getWidgetModalBox("Alert");
	widg.addText(text);
	widg.show(100,50, "fixed");
}

function bxe_validationAlert(messages) {
	var widg = mozilla.getWidgetModalBox("Validation Alert");
	for (i in messages) {
		widg.addText( messages[i]["text"] );
	}
	widg.show((window.innerWidth- 500)/2,50, "fixed");
	
}
function bxe_getDirPart(path) {
	
	return path.substring(0,path.lastIndexOf("/") + 1);
}

function bxe_nodeSort(a,b) {
	if (a.nodeName > b.nodeName) {
		return 1;
	} else {
		return -1;
	}
}

function bxe_showImageDrawer() {
	drawertool.cssr = window.getSelection().getEditableRange();
	drawertool.openDrawer('imagedrawer');
}

function bxe_ShowAssetDrawer() {
    drawertool.cssr = window.getSelection().getEditableRange();
    if (drawertool.cssr) {
        drawertool.openDrawer('assetdrawer');
    }
}

function bxe_start_plugins () {
	
	var ps = bxe_config.getPlugins();
	
	if (ps.length > 0) {
		for (var i = 0; i < ps.length; i++) {
			var p = bxe_plugins[ps[i]];
			if (p.start) {
				p.start(bxe_config.getPluginOptions(ps[i]));
			}
		}
	}
}

