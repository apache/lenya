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
// $Id: NodeVAL.js 1411 2005-10-06 18:52:18Z chregu $

const BXE_VALID_NOMESSAGE = 1;

XMLNode.prototype.isNodeValid = function(deep, wFValidityCheckLevel, noError, undo ) {
	if ( this._node && this._node._SourceMode == true) {
		return true;
	}
	var c  = this._isNodeValid(deep,wFValidityCheckLevel);
	if (c.isError) {
		c.dumpErrorMessages();
		if (!noError) {
			
		
			for (i in c.errormsg) {
				if (c.errormsg[i]["node"]._node && c.errormsg[i]["node"]._node.nodeType == 1) {
					c.errormsg[i]["node"]._node.setAttribute("__bxe_invalid","true");
				} 
			}
			if (!(wFValidityCheckLevel & 1)) {
				bxe_validationAlert(c.errormsg);
			}
		} 
		if (undo) {
			bxe_history_undo();
		}
		return false;
	} else {
		return true;
	}
		
}

Attr.prototype._isNodeValid = function(wFValidityCheckLevel ) {
	//dump("Attr Check: " + this.name + "\n");
}

XMLNode.prototype._isNodeValid = function(deep,wFValidityCheckLevel ) {
	// if it's a root node.
	if(this.parentNode && this.parentNode.nodeType == 9) {
		if (!this.vdom.canBeRoot) {
			alert("root element is not allowed to be root");
			return false;
		}
	} else {
		if (this.nodeType == 1) {
		// TODO: test if this node is valid, we do only check childrens for the moment..	
		}
	}
	try {
		if (this.vdom) {
			var ctxt = new ContextVDOM(this, this.vdom);
			ctxt.wFValidityCheckLevel = wFValidityCheckLevel;
		} else {
			return false;
		}
	
	} catch (e) { bxe_catch_alert(e); debug ("couldn't make new context..");}
	if (ctxt && ctxt.node) {
	do {
		if (ctxt.node.nodeType == "3" && ctxt.node.isWhitespaceOnly) {
			continue;
		} 	
		if (ctxt.node.nodeType == Node.COMMENT_NODE) {
			continue;
		}
		//FIXME: check CDATA_SECTIONS AS WELL
		if (ctxt.node.nodeType == Node.CDATA_SECTION_NODE) {
			continue;
		}
		
		if (  ctxt.isValid()) {
			/*dump(ctxt.node.parentNode.nodeName +  " -> " +ctxt.node.nodeName + " is valid and has ");
			dump(ctxt.node.hasChildNodes() + " childNodes \n");*/
			
			if(ctxt.node.hasChildNodes()) {
				if (deep) {
					//dump (ctxt.node.localName + " has  ChildNodes\n");
					var refsPosition = ctxt.refs.length;
					//var oldVdom = ctxt.node.vdom;
					oldVdom = ctxt.vdom;	
					var retctxt = ctxt.node._isNodeValid(deep,  wFValidityCheckLevel )
					if (retctxt.isError) {
						ctxt.addErrorMessages(retctxt.errormsg);
					} 
				}
			} else if (ctxt.node.nodeType == 1) {
				//dump (ctxt.node.localName + " has no ChildNodes\n");
				/*var refsPosition = ctxt.refs.length;
				oldVdom = ctxt.vdom;	
				var retctxt = ctxt.node._isNodeValid(deep,  wFValidityCheckLevel )
				if (retctxt.isError) {
					ctxt.addErrorMessages(retctxt.errormsg);
				}*/
				
			}
		} else {
				var _msg = "";
				if (ctxt.node.nodeType == 3) {
						_msg = "#text ";
					} else { 
						_msg = ctxt.node.localName +"("+ctxt.node.namespaceURI+ ") ";
					}
				if (ctxt.node.parentNode.isAllowedChild(ctxt.node.namespaceURI, ctxt.node.localName)) {
					
					_msg += "is not allowed at this position as child of  " + this.localName ;
				}
				else {
					_msg += " is not allowed as child of  " + this.localName +"("+this.namespaceURI+ ")";
				}
				ctxt.setErrorMessage(_msg);
		}
		//debug ("---------");
		
	} while (ctxt.next() )

	
		}
	return ctxt;
	
}
ctxtcounter = 0;
function ContextVDOM (node,vdom) {
	this.node = node.firstChild;
	this.nr = ctxtcounter++;
	
/*	debug (node.nodeName);
	debug (vdom.nodeName);
*/	this.isError = false;
	this.errormsg = new Array();
	this.refs = new Array();

	if (vdom && typeof vdom.firstChild != "undefined") {
		this.vdom = vdom.getFirstChild(this);
	} else {
		this.vdom = null;
	}
	
}

ContextVDOM.prototype.next = function() {
/*	debug ("ctxt.next " + typeof this.node +  " " + this.node.nodeName);
	debug ("ctxt.VDOM " + this.vdom.nodeName +  " " + this.vdom.name);
	debug ("data " + this.node._node);
	*/
	if (this.node.nextSibling) {
		
		this.node = this.node.nextSibling;
		if (this.node.nodeType == 3 ) {
			debug("ctxt.next next.nodeName is null...");
			return this.next();
		}
		return this.node;
	} else {
		debug ("no next sibling...");
		return null;
	}
}
ContextVDOM.prototype.setErrorMessage = function(text) {
	
	if (!this.errormsg) {
		this.errormsg = new Array();
	}
	this.isError = true;
	var tmpArr = new Array();
	tmpArr["text"] = text;
	tmpArr["node"] = this.node;
	this.errormsg.push(tmpArr);
}

ContextVDOM.prototype.addErrorMessages = function(msgs) {
	this.isError = true;
	this.errormsg = this.errormsg.concat(msgs);
}

ContextVDOM.prototype.getErrorMessagesAsText = function() {
	var out = "";
	for (i in this.errormsg) {
		out += this.errormsg[i]["text"] + "\n";
	}
	return out;
}

ContextVDOM.prototype.dumpErrorMessages = function() {
	dump("Error :\n" + this.getErrorMessagesAsText());
}

ContextVDOM.prototype.nextVDOM = function() {
	if (this.vdom == null || typeof this.vdom == 'undefined') {
		return null;
	}
	var nextSib = this.vdom.getNextSibling(this);
	if (nextSib) {
		this.vdom = nextSib;
	}  else {
		this.vdom = null;
		return null;
	}
	return this.vdom;
}

ContextVDOM.prototype.isValid = function() {
	if (this.vdom) {
		return this.vdom.isValid(this);
	} else {
		if (this.node.hasChildNodes()) {
			//dump(this.node.nodeName + " is not allowed to have children \n");
			this.setErrorMessage(this.node.nodeName + " is not allowed to have children")
			return false;
		} else {
			this.node.vdom = this.vdom;
			return true;
		}
	}
}


ContextVDOM.prototype.setVDOM = function(vdom, refsPosition) {

	if (refsPosition && this.refs.length >  0 ) {
		while (this.refs.length > refsPosition) {
			var bla = this.refs.pop();
			debug ("this.refs.pop" + bla.name);
			 
		}
	}
	this.vdom = vdom;
}

XMLNode.prototype.__defineGetter__(
	"vdom", function () {
		if (typeof this._vdom == "undefined" || !this._vdom) {
			// if documentElement
			if (this.parentNode ) {
				if (this.parentNode.nodeType == 9) {
					if (this.localName == this.ownerDocument.vdom.firstChild.localName) {
						this._vdom = this.ownerDocument.vdom.firstChild;
					} else {
						alert(" Document has root node named " + this.localName + "\n RelaxNG expects  " +this.ownerDocument.vdom.firstChild.nodeName);
						this._vdom = null;
					}
				} else if (this.parentNode.vdom) {
					this._vdom = this.parentNode.vdom.getVdomForChild(this);
				} else {
					return null;
				}
			} else {
				return null;
			}
			
		}
		return this._vdom;
	}
	)


XMLNode.prototype.__defineSetter__(
	"vdom", function (value) {
		this._vdom = value;
	}
	)


	
