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
// $Id: NodeVDOM.js 938 2004-11-24 21:17:32Z chregu $

function NodeVDOM (node) {
	this.node = node;
	this.minOccurs = null;
	this.maxOccurs = null;
	
};


NodeVDOM.prototype.getVdomForChild = function (child ) {
	var ctxt = child.parentNode._isNodeValid(false);
	return child._vdom;
}

NodeVDOM.prototype.allowedElements = function(ctxt) {
	return this.localName;
}

NodeVDOM.prototype.parseChildren = function() {};



NodeVDOM.prototype.appendChild = function(newElement) {
	
	newElement.parentNode = this;
	if (typeof this.firstChild == "undefined" || this.firstChild == null) {
		this.firstChild =  newElement;
		this.lastChild =  newElement;
		newElement.nextSibling = null;
		newElement.previousSibling = null;
	} else {
		newElement.previousSibling = this.lastChild;
		newElement.previousSibling.nextSibling = newElement;
		newElement.nextSibling = null;
		this.lastChild = newElement;
	}
}
NodeVDOM.prototype.isValid = function(node) {
	//dump("\n---NodeVDOM.prototype.isValid----\n");
	return true;
}
	

