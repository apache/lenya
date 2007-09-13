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
// $Id: ElementVDOM.js 1413 2005-12-06 13:13:45Z chregu $

function ElementVDOM(node) {
	this.node = node;
	this._allowedChildren = new Array();
	this.type = "RELAXNG_ELEMENT";
	this.canBeRoot = false;
	this.nextSibling = null;
	this.previousSibling = null;
	this.minOccurs = 1;
	this.maxOccurs = 1;
	this._attributes = new Array();
}

ElementVDOM.prototype = new NodeVDOM();

NodeVDOM.prototype.addAttributeNode = function(attribute) {
try {
	this._attributes[attribute.name] = attribute;
} catch(e) {
	/* FIXME: This try/catch had to be inserted for making it work for MIT
	should look into it, what really was the issue.. */
	try {
		this._attributes = new Array();
		this._attributes[attribute.name] = attribute;
	} catch (e) {
	}
};
}

ElementVDOM.prototype.__defineGetter__ ( 
	"hasAttributes",
	function() {
		for (var i in this.attributes) {
			return true;
		}
		return false;
		
	}
);

ElementVDOM.prototype.addAllowedChild = function(node) {
	this._allowedChildren[node.name] = node;
}

NodeVDOM.prototype.getAllAttributes = function () {
	var child = this.firstChild;
	var attr = this._attributes;
	while (child) {
		
		if (child.nodeName == "RELAXNG_REF" && child.DefineVDOM) {
			var AA = child.DefineVDOM.getAllAttributes()
			if (AA) {
				for (i in AA) {
					attr[AA[i].name]= AA[i];
					//attr.push(AA[i]);
				}
			}
		} else if (child.nodeName == "RELAXNG_CHOICE") {
			var AA = child.getAllAttributes();
			if (AA) {
				if (! attr['__bxe_choices']) {
					attr['__bxe_choices'] = new Array();
				}
				var _choicesAttr = new Array();
				for (i in AA) {
					_choicesAttr[AA[i].name]= AA[i];
				}
				attr['__bxe_choices'].push(_choicesAttr);
			}
		}
		child = child.nextSibling;
	}
	return attr;
}


ElementVDOM.prototype.__defineGetter__ ( 
	"attributes",
	function() {
		if (typeof this._cachedAttributes == "undefined") {
			
			this._cachedAttributes = this.getAllAttributes();
		}
		return this._cachedAttributes;
	}
	);
	


ElementVDOM.prototype.isValid = function(ctxt) {
	//dump("ElementVDOM.isValid " + this.localName + "\n");
	if (ctxt.node.localName == this.localName && ctxt.node.namespaceURI == this.namespaceURI) {
		
		
		var _attr = this.attributes;
		var _vdomAttr = new Array();
		var _nodeAttr = ctxt.node.attributes;
		
		for(var i in _attr) {
			//choice attributes
			if(i == '__bxe_choices') {
				for (var j in _attr['__bxe_choices']) {
					var _choices = _attr['__bxe_choices'][j];
					//loop through all available attributes
					var _hasIt = 0;
					var _attrList = "";
					for (var k in _choices) {
						_attrList += ", " + _choices[k].name; 
						//check if it's in the node
						// and if there is one, check it's validity
						if ( ctxt.node.getAttribute && ctxt.node.getAttribute(_choices[k].name)) {
							_choices[k].isValid(ctxt);
							// if we already found one attribute of the choices list -> alert
							_hasIt ++
						}
						_vdomAttr[_choices[k].name] = true;
					}
					if (_hasIt > 1) {
						var errMsg = "Only one of the following attributes is allowed in " + ctxt.node.nodeName + ": ";
						errMsg += _attrList.substring(1,_attrList.length);
						ctxt.setErrorMessage(errMsg );
					} else if (_hasIt == 0) {
						var errMsg = ctxt.node.nodeName +  " needs one of the following attributes : ";
						errMsg += _attrList.substring(1,_attrList.length);
						ctxt.setErrorMessage(errMsg );
					}
				}
			} else {
				_attr[i].isValid(ctxt);
			 	_vdomAttr[_attr[i].name] = true;
			}
		}
		
		
		for(var i in _nodeAttr) {
			if (typeof _vdomAttr[_nodeAttr[i].nodeName] == "undefined") {
				var errMsg = "The attribute " + _nodeAttr[i].nodeName + " is not allowed in " +  ctxt.node.nodeName;
				if (ctxt.wFValidityCheckLevel & 2) {
					if (confirm(errMsg + "\n Should it be removed?")) {
						ctxt.node.removeAttribute(_nodeAttr[i].nodeName);
						return this.isValid(ctxt);
					}
				}
				ctxt.setErrorMessage(errMsg );
			}
		}
		
		ctxt.node.vdom = this;
		ctxt.nextVDOM();
		return true;
	} else {
		return false;
	}
}
