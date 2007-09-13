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
// $Id: eDOMEvents.js 938 2004-11-24 21:17:32Z chregu $

Node.prototype.eDOMaddEventListener = function (eventType, func, captures) {
	if (!this._events) {
		this._events = new Array();
	}
	eventType = eventType.toLowerCase();
	if (!this._events[eventType]) {
		this._events[eventType] = new Array();
	}
	
	var funcname = func.name
	
	if (! funcname) {
		funcname = this._events[eventType].length;
	}
	
	this._events[eventType][funcname] = func;
}


Node.prototype.eDOMremoveEventListener = function (eventType, func, captures) {
	var funcname = func.name
	eventType = eventType.toLowerCase();
	if (! funcname) {
		funcname = this._events[eventType].length;
	}
	if (this._events && this._events[eventType] && this._events[eventType][funcname]) {
		this._events[eventType][funcname] = null;
	}
}
Node.prototype.doEvents = function(event) {
	event.currentTarget = this;
	if(this._events && this._events[event.eventType]) {
		for (var i in this._events[event.eventType]) {
			if (this._events[event.eventType][i]) {
				
				this._events[event.eventType][i](event);
			}
		}
	}
	if (this.parentNode) {
		try {
			this.parentNode.doEvents(event);
		} catch(e) {
			//dump("no checkEvents on parentNode " + this.parentNode);
		}
	}
}


function eDOMEvent () { }

eDOMEvent.prototype.initEvent = function (eventType) {
	this.eventType = eventType.toLowerCase();
	dump ("Event: " + eventType + " " + this.target + " "  + this.additionalInfo  +"\n");
	this.target.doEvents(this);
}

eDOMEvent.prototype.setTarget = function (target) {
	this.target = target;
}

function eDOMEventCall(eventType, target, addInfo) {

	var e = new eDOMEvent();
	if (addInfo) {
		e.additionalInfo = addInfo;
	}
	e.setTarget(target);
	e.initEvent(eventType);
	
}



	