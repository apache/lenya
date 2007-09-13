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
// $Id: DocumentVAL.js 938 2004-11-24 21:17:32Z chregu $

XMLNodeDocument.prototype.loadSchema = function(file ,callback) {
	this._vdom = new DocumentVDOM();
	return this._vdom.loadSchema(file, callback);
}

XMLNodeDocument.prototype.validateDocument = function() {
	
	if (!this.vdom) {
		//if vdom was not attached to the document, try to find the global one...
		this.vdom = bxe_config.DocumentVDOM;
	}
	if (!this.vdom) {
		alert ("no Schema assigned to Document, but " + this.vdom);
		return false;
	}
	

	if (!this.documentElement) {
		this.documentElement = this._node.documentElement.XMLNode;
	}
	var c =  this.documentElement.isNodeValid(true);
	/*FIXME: HACK... Sometimes the above statement does not check
	   deep enough, do another check here for all editable Areas
	   
	*/
	   
	var areaNodes = bxe_getAllEditableAreas();

	for (var i = 0; i < areaNodes.length; i++) {
		if ((areaNodes[i]._SourceMode)) {
			return false;
		}
		c = c & areaNodes[i].XMLNode.isNodeValid(true)
	}
	
	
	return c;
}

XMLNodeDocument.prototype.getVdom = function(name) {
	return this._vdom.globalElements[name.toLowerCase()];
}
XMLNodeDocument.prototype.__defineGetter__(
	"vdom", function () {
		return this._vdom;
	}
	)


XMLNodeDocument.prototype.__defineSetter__(
	"vdom", function (value) {
		this._vdom = value;
	}
	)

