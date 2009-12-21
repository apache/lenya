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

var numberOfMenus = 5; // change this if you have a different number of menus
var activeMenu = null; //defines which menu is currently open

//runs at onload of window        
function initialize() {
    userEventsInit(); //set up all user events
}

function menuOver() {
    document.onclick = null;
    if (window.event != null) window.event.cancelBubble=true; // IE requires this
}

function menuOut() {
    document.onclick = eventHideMenu;
    if (window.event != null) window.event.cancelBubble=true; // IE requires this
}

//turns on the display for nav menus
function eventShowMenu(e) {
    var obj;
    if (activeMenu != null) eventHideMenu();
    activeMenu = "menu"+this.id.substring(3,this.id.length)
    obj = document.getElementById(activeMenu);
    if (obj != null) obj.style.visibility = "visible";
    if (document.all) document.onclick = eventHideMenu; // IE requires this
    if (window.event != null) window.event.cancelBubble=true; // IE requires this
}

//hides nav menus and pop ups
function eventHideMenu() {
    var obj;
    document.onclick = null;
    if (activeMenu != null) {
        obj = document.getElementById(activeMenu);
        obj.style.visibility = "hidden";
    }
}

//nav events
function userEventsInit() {
    var obj;
    var i;
    for (i=1; i <= numberOfMenus; i++) {
        obj = document.getElementById("nav"+i);
        if (obj != null) {
            obj.onclick = eventShowMenu;
            obj.onmouseout = menuOut;
        }
    
        obj = document.getElementById("menu"+i);
        if (obj != null) {
            obj.onmouseover = menuOver;
            obj.onmouseout = menuOut;
        }
    }
 }
