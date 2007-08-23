/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

dojo.require("dojo.logging.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");

function loadMenu(event) {
    var menuNumber = event.target.id.substring("nav".length);
	var menuFunction = {
		url: MENU_URL + "&lenya.module=menubar&lenya.menu=" + menuNumber,
		load: function(type, data, evt) {
		    var docElement = data.documentElement
		    var attrValue = docElement.getAttribute("id");
            var menuNumber = attrValue.substring("menu".length);
			var element = document.getElementById("menu" + menuNumber);
			var placeholderElement = document.getElementById("menuPlaceholder" + menuNumber);
			element.removeChild(placeholderElement);
			var children = docElement.childNodes;
			for (var i = 0; i < children.length; i++) {
				element.appendChild(children[i].cloneNode(true));
			}
			dojo.event.disconnect(dojo.byId("nav" + menuNumber), "onclick", "loadMenu");
		},
		error: function(type, error) {
			dojo.log.error(error.message);
		},
		mimetype: "text/xml",
		method: "GET"
	};
	dojo.io.bind(menuFunction);
}

/*
* Customize this method for event-based loading.
*/
function initAjax() {
	var menuNumber = 5;
	for (var i = 1; i <= menuNumber; i++) {
		dojo.event.connect(dojo.byId("nav" + i), "onclick", "loadMenu");
	}
}

dojo.addOnLoad(initAjax);
