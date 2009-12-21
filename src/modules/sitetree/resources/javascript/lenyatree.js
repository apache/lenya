/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/


/*
  The following variables must be set:
  WEBAPP_BASE_PATH - path to the web application root, including the trailing slash
  PUBLICATION_ID
  PIPELINE_PATH
  IMAGE_PATH
  USECASE
  SHOW_LOADING_HINT (optional)
  SHOW_ICONS (optional)
*/
  
var xmlhttp;
var SHOW_LOADING_HINT = false;
var SHOW_ICONS = true;

/******************************************
 *  LenyaNode 
 ******************************************/

function LenyaNode(id, parent) {
    this.id = id;
    this.parent = parent;
    this.items = {};
    this.isfolder = false;
    this.label = '';
};

LenyaNode.prototype = new Node;

LenyaNode.prototype.addItems = function(items) {
    this.items = {};
    this.itemids = [];
    for (var i = items.length - 1; i >= 0; i--) {
        var item = items[i];
        this.items[item.id] = item;
        this.itemids.unshift(item.id);
    };
};

LenyaNode.prototype.isCollection = function() {
    return this.isfolder;
};

LenyaNode.prototype.getStyle = function() {
    if (this.tree.root == this) {
        return 'lenya-info-root';
    } else {
        return 'treenode_label';
    }
}

LenyaNode.prototype.loadSubTree = function(handler) {
    // display a 'loading' that the tree is being loaded
    if (SHOW_LOADING_HINT) {
        var div = this.tree.createElement('div');
        var text = this.tree.doc.createTextNode('loading...');
        div.appendChild(text);
        this.element.firstChild.firstChild.firstChild.lastChild.appendChild(div);
    }
    var url = this.getLoadSubTreeURL();
    var callback = function (xml, item) {
        item.subTreeLoaded(xml);
    }
    loadAsyncXML(url, callback, this);
}

LenyaNode.prototype.getLoadSubTreeURL = function() {
    var path = this.getPath();
    return encodeURI(WEBAPP_BASE_PATH + PIPELINE_PATH + '?path='+path+'&lenya.module=sitetree');
}


LenyaNode.prototype.subTreeLoaded = function(xml) {
    // remove the 'loading' hint
    if (SHOW_LOADING_HINT) {
        var td = this.element.firstChild.firstChild.firstChild.lastChild;
        td.removeChild(td.lastChild);
    }
    
    var root = xml.documentElement;

    var children = root.childNodes;
    var items=[];
    for (var i = 0; i < children.length; i++) {
        if (this.canAcceptNode(children[i])) {
            items.push(this.createNewNode(children[i]));
        } 
    }
    //handler(items);
    this._continueOpen(items);
}

LenyaNode.prototype.canAcceptNode = function(item) {
    if (getTagName(item) == "nav:node") return true;
    return false;
}

LenyaNode.prototype.addNodesRec = function(parentNode) {
    var children = parentNode.childNodes;
    var items = [];
    var nodes = [];
    var item;
    for (var i = 0; i < children.length; i++) {
       if (this.canAcceptNode(children[i])) {
          this.reopen = true; // this causes the parent to unfold
          item = this.createNewNode(children[i]);
          items.push(item);
          item.addNodesRec(children[i]); 
       } 
    }
    this.addItems(items);
}

LenyaNode.prototype.createNewNode = function(node) {
    var newItem = new LenyaNode(node.getAttribute('id'), this);
    newItem.init();
    var isfolder = node.getAttribute('folder') =='true' ? true : false;
    newItem.isfolder = isfolder;
    return newItem;
}

//workaround for http://issues.apache.org/bugzilla/show_bug.cgi?id=35227
function getTagName(element) {
    var tagName = element.tagName;
    var prefix = element.prefix;

    if(tagName.indexOf(prefix + ':') == -1) {
        tagName = prefix + ':' + tagName;
    }
    return tagName;
}


/******************************************
 *  LenyaTree 
 ******************************************/
 
function LenyaTree(doc, treeElement) {
    this.doc = doc;
    this.treeElement = treeElement;
    this.selected = null;
};

LenyaTree.prototype = new Tree;

LenyaTree.prototype.init = function(id) {
    this.root = new LenyaNode(id);
    this.root.tree = this;
    this.root.depth = 0;
    this.root.reopen = false;
    this.root.isfolder = true;
    this._currentId = 0;
};

LenyaTree.prototype.createElement = function(name) {
    if (typeof this.doc.createElementNS != 'undefined') {
        return this.doc.createElementNS('http://www.w3.org/1999/xhtml', name);
    }
		if (typeof this.doc.createElement != 'undefined') {
				return this.doc.createElement(name);
		}
		return false;

/*    return this.doc.createElementNS("http://www.w3.org/1999/xhtml", name); */
};


LenyaTree.prototype.getItems = function(item, handler, allow_cache) {
    if (item.tree.root == item) {
        alert('getTtems() of root called. This should not happen, loadInitialTree should be called first.');
    } else {
        item.loadSubTree(handler);
    }
};

LenyaTree.prototype.loadInitialTree = function(path) {
    var url = this.getLoadInitialTreeURL(path);
    callback = function(fragment, param) {
        var tree = param[0];
        var path = param[1];
        tree.initialTreeLoaded(fragment);
        var selectedItem = tree.getItemByPath(path);
        if (selectedItem != false) {
            tree.select(selectedItem);
        }
    }
    var param = new Array(this, path);
    loadAsyncXML(url, callback, param);
};

LenyaTree.prototype.getLoadInitialTreeURL = function(path) {
    return encodeURI(WEBAPP_BASE_PATH + PIPELINE_PATH + '?path='+path+'&lenya.module=sitetree');
}

LenyaTree.prototype.initialTreeLoaded = function(xml) {
    var root = xml.documentElement;
    var children = root.childNodes;
    var items=[];
    var item;
    for (var i = 0; i < children.length; i++) {
        if (this.canAcceptNode(children[i])) {
            item = this.createNewNode(children[i]);
            items.push(item);
            //item.addNodesRec(children[i]);
        }
    }
    this.root.addItems(items);
    this.root.open();
}

LenyaTree.prototype.canAcceptNode = function(item) {
    if (getTagName(item) == "nav:site") return true;
    return false;
}

LenyaTree.prototype.createNewNode = function(node) {
    var newItem = new LenyaNode(node.getAttribute('id'), this.root);
    newItem.init();
    return newItem;
}

/* LenyaTree.prototype.getIcon = function(item) {
    // don't use an icon for root and area nodes
    if (item.depth<2) return this.doc.createTextNode('');

    var img = this.doc.createElement('img');
    img.setAttribute('src', IMAGE_PATH + 'document.gif');
    return img;
}; */

/* return an img object that represents the file type */
LenyaTree.prototype.getIcon = function(item) {

    if (SHOW_ICONS) {
        var steps = new Array();
        steps = item.getPath().split('/');
        if (steps.length < 2) {
            return this.doc.createTextNode('');
        }
        else {
            var href;
            if (steps.length < 4) {
                href = WEBAPP_BASE_PATH + "modules/sitetree/folder.gif";
            }
            else {
                href = item.icon;
            }
            var img = this.createElement('img');
            var language = CHOSEN_LANGUAGE;
            img.setAttribute('src', href);
            img.setAttribute('alt', '');
            return img;
        }
    } else {
        return this.doc.createTextNode('');
    }
};

/* creates the item name and any icons and such */
LenyaTree.prototype.createItemLine = function(item) {
    var div = this.createElement('div');
    var span = this.createElement('span');
    var icon = this.getIcon(item);
    if (icon.nodeType == 1) {
        icon.className = 'treenode_icon';
    };
    div.appendChild(icon);
    
    var text = this.doc.createTextNode(item.label ? item.label : item.id);
    
    span.className = item.getStyle();
    
    span.appendChild(text);
    div.appendChild(span);
    
    return div;
};

LenyaTree.prototype.createItemHtml = function(item) {
    var div = this.createElement('div');
    div.className = 'treenode';

    // place a reference to the item on the div
    div.treeitem = item;
    
    var table = this.createElement('table');
    div.appendChild(table);
    var tbody = this.createElement('tbody');
    table.appendChild(tbody);
    var tr = this.createElement('tr');
    tbody.appendChild(tr);

    // add the lines:
    this.addLines(item, tr);

    // add the opensign
    var td1 = this.createElement('td');
    tr.appendChild(td1);
    item.opensign = this.getCloseSign(item);
    item.opensign.className = 'treenode_sign';

    if (this.getSignType(item) == 'T') {
        // if the label of this item is too long the line will wrap
        // by setting the background image we get a continuos line
        td1.style.backgroundImage = 'url('+IMAGE_PATH+'vertical-line.gif)';
    }

    td1.appendChild(item.opensign);

    // add the label
    var td2 = this.createElement('td');
    tr.appendChild(td2);
    td2.className = 'lenya-info-label-td';
    var line = this.createItemLine(item);
    td2.appendChild(line);

    addEventHandler(line, 'click', this.handleItemClick, this, item);
    addEventHandler(item.opensign, 'click', this.handleItemSignClick, 
                    this, item);
    if (this.selected == item.getPath()) {
        this.unselect();
        this.select(item);
        div.className = 'selected_node';
    };

    return div;
};

/* decide if the [+] or [-] sign is 'L' or '|-' type */
LenyaTree.prototype.getSignType = function(item) {
    if (item.tree.root != item) {
        if (isLastChild(item.parent, item)) {
            return 'L';
        } else {
            return 'T';
        }
    }
    return 'X';  // invalid type
};

/* get the [+] sign for a collection or resource */
LenyaTree.prototype.getCloseSign = function(item) {
    var opensign = this.createElement('img');
    opensign.className = 'treenode_sign';
    
    if (item.tree.root == item) {
        // the root needs no opensign
        opensign.setAttribute('src', IMAGE_PATH+'empty.gif');
        opensign.setAttribute('width', '0');
    } else {
        var suffix = this.getSignType(item);
        if (item.isCollection()) {
            opensign.setAttribute('src', IMAGE_PATH+'closed-collection-'+suffix+'.gif');
        } else {
            opensign.setAttribute('src', IMAGE_PATH+'non-collection-'+suffix+'.gif');
        };
    }
    return opensign;
};

/* get the [-] sign for a collection */
LenyaTree.prototype.getOpenSign = function(item) {
    var opensign = this.createElement('img');
    opensign.className = 'treenode_sign';

    if (item.tree.root == item) {
        // the root needs no opensign
        opensign.setAttribute('src', IMAGE_PATH+'empty.gif');
        opensign.setAttribute('width', '0');
    } else {
        var suffix = this.getSignType(item);
        opensign.setAttribute('src', IMAGE_PATH+'opened-collection-'+suffix+'.gif');
    }
    return opensign;
};

/* add the lines of an item (the edges of the tree) */
LenyaTree.prototype.addLines = function(item, parentElement) {
    var linesStr = this.computeLinesString(item);
    // linesStr is in the reverse order
    for (var i=linesStr.length-1; i>0; i--) {
        var td = this.createElement('td');
        var img = this.createElement('img');
        
        var imageName = '';
        if (linesStr.charAt(i)=='I') {
            imageName = 'vertical-line.gif';
            // if the label of this item is too long the line will wrap
            // by setting the background image we get a continuos line
            td.style.backgroundImage = 'url('+IMAGE_PATH+'vertical-line.gif)';
        } else {
            imageName = 'empty.gif';
        }
        img.setAttribute('src', IMAGE_PATH+imageName);
        img.className = 'treenode_sign';

        td.appendChild(img);
        parentElement.appendChild(td);
    }
}

/* computes the lines of an item as a string (the edges of the tree)
     E: ' ' empty
     I: '|' vertical line
     L: 'L' 
     T: '|-'
*/
LenyaTree.prototype.computeLinesString = function(item) {
    var lines = ''
    
    if (item.tree.root == item) return lines;
    
    // first level, decide T or L
    if (isLastChild(item.parent, item)) {
        lines += 'L';
    } else {
        lines += 'T';
    }
    item = item.parent;

    // subsequent levels, decide I or E
    while (item.tree.root != item) {
        if (isLastChild(item.parent, item)) {
            lines += 'E'; // empty
        } else {
            lines += 'I'; // vertical line
        }
        item = item.parent;
    }
    
    return lines;
}

LenyaTree.prototype.handleItemClick = function(item, event) {
    alert('you clicked on '+item.id);
    // do something useful here
};

// helper function
function isLastChild(parent, child) {
    // this should be a method of Node
    for (var i=0; i<parent.itemids.length-1; i++) {
        if (parent.itemids[i] == child.id) return false;
    };
    return true;
}


/******************************************
 *  Dynamic loading functions 
 ******************************************/

function loadSyncXML(url) {
    if (xmlhttp==null) createXMLHttp();
    
    //alert('loading '+url);
    // do synchronous loading 
    xmlhttp.open("GET",url,false);  
    xmlhttp.setRequestHeader('Accept','text/xml');
    
    xmlhttp.send(null);
    
    //alert('result: '+xmlhttp.responseText);
    
    var xml = xmlhttp.responseXML;
    if (xml == null || xml.documentElement == null) {
        alert('Error: could not load response xml for url: '+url);
        return null;
    }
    return xml;
}

function loadAsyncXML(url, callbackFunction, callbackParam) {
    if (xmlhttp==null) createXMLHttp();
    
    //alert('loading '+url);
    // do asynchronous loading 
    xmlhttp.open("GET",url,true);  
    xmlhttp.setRequestHeader('Accept','text/xml');
    
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4) {
            var xml = xmlhttp.responseXML;
            if (xml == null || xml.documentElement == null) {
                alert('Error: could not load response xml for url: '+url);
                return null;
            }
            callbackFunction(xml, callbackParam);
        }
    };
    
    xmlhttp.send(null);
}

function loadAsyncText(url, callbackFunction, callbackParam) {
    if (xmlhttp==null) createXMLHttp();
    
    //alert('loading '+url);
    // do asynchronous loading 
    xmlhttp.open("GET",url,true);  
    
    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState == 4) {
            var text = xmlhttp.responseText;
            if (text == null) {
                alert('Error: could not load response text for url: '+url);
                return null;
            }
            callbackFunction(text, callbackParam);
        }
    };
    
    xmlhttp.send(null);
}

function updateElement(placeholder, url) {
    var callback = function (fragment) {
        var div = document.getElementById(placeholder);
        div.innerHTML = fragment;
    }
    loadAsyncText(url, callback);
}

// create the xmlhttp object
function createXMLHttp() {
    /*@cc_on @*/
    /*@if (@_jscript_version >= 5)
    // JScript gives us Conditional compilation, we can cope with old IE versions.
    // and security blocked creation of the objects.
     try {
      xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
     } catch (e) {
      try {
       xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
      } catch (E) {
       xmlhttp = false;
      }
     }
    @end @*/
    if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
      xmlhttp = new XMLHttpRequest();
    }
}

/*******************************************
 * Misc
 *******************************************/

function stripPrefix(s) {
    if (s.indexOf(':')==-1) return s; 
    return s.split(':')[1];
}
