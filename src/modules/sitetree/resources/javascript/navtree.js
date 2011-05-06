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
Required variables: see lenyatree.js
*/

/******************************************
 *  NavNode 
 ******************************************/

function NavNode(id, parent) {
    this.id = id;
    this.parent = parent;
    this.items = {};
    this.isfolder = false;
    this.uuid = '';
    this.href = '';
    this.label = '';
    this.area = '';
    this.path = '';
    this.isresource = false;
    this.isprotected = true;
    this.mimetype = '';
    this.existsChosenLanguage = true;
    this.langSuffix = '';
};

NavNode.prototype = new LenyaNode;

NavNode.prototype.createNewNode = function(node)
{
    var newItem = new NavNode(node.getAttribute('id'), this);
    newItem.init();
  
    newItem.isfolder = isNodeFolder(node);
    newItem.area = this.area;
    newItem.path = node.getAttribute('path');
    newItem.uuid = node.getAttribute('uuid');
    newItem.isprotected = isNodeProtected(node);
    newItem.mimetype = getMimeType(node);
    newItem.isresource =isResource(node);
    newItem.href = node.getAttribute('href');
    newItem.icon = node.getAttribute('icon');
    newItem.label = getLabel(node);
    newItem.existsChosenLanguage = existsChosenLanguage(node);
    newItem.langSuffix = node.getAttribute('language-suffix');
  
    return newItem;
}

NavNode.prototype.getBasePath = function() {
    var path = this.path != '/' ? this.path : '' ;
    return path;
}

NavNode.prototype.getLoadSubTreeURL = function() {
    area = this.area;
    var path = this.getBasePath();
    return encodeURI(WEBAPP_BASE_PATH + PIPELINE_PATH
        + '?pub=' + PUBLICATION_ID
        + '&area=' + area
        + '&path=' + path
        + '&mimetype=true' 
        + '&language=' + CHOSEN_LANGUAGE
        + '&defaultLanguage=' + DEFAULT_LANGUAGE
        + '&areas=' + ALL_AREAS);
}

NavNode.prototype.getStyle = function() {
    if (this.tree.root == this) {
        return 'lenya-info-root';
    } else if (this.parent == this.tree.root) {
        return 'lenya-info-area';
    } else if (this.isprotected) {
        return 'lenya-info-protected';
    } else if (this.path == CUT_DOCUMENT_ID) {
        return 'lenya-info-cut';
    } else if (!this.existsChosenLanguage) {
        return 'lenya-info-nolanguage';
    } else {
        return 'treenode_label';
    }
}

function getLabel(node) {
    var cs = node.childNodes;
    var l = cs.length;
    // lenya generates the xml and is responsible to insert the label
    // of the correct language
    for (var i = 0; i < l; i++) {
       if (getTagName(cs[i]) =='nav:label') {
          if (cs[i].hasChildNodes()) {
              return cs[i].firstChild.nodeValue;
          }
          else {
              return "[no title]";
          }
       } 
    }
    return '';
}

function isNodeProtected(node) {
    var prot = node.getAttribute('protected');
    if (prot == 'true') return true;
    return false;
}

function getMimeType(node) {
    var prot = node.getAttribute('mimetype');
    if (prot === null) return '';
    return prot;
}

function isResource(node) {
    var prot = node.getAttribute('resourceType');
    if (prot === "resource") return true;
    return false;
}

function isNodeFolder(node) {
    var isfolder = node.getAttribute('folder');
    if (isfolder == 'true') return true;
    return false;
}

// check if the node has a label of the chosen language
function existsChosenLanguage(node) {
    var children = node.childNodes;
    for (var i = 0; i < children.length; i++) {
       var lang = children[i].getAttribute('xml:lang');
       if (!lang) {
           var lang = children[i].getAttribute('lang');
       }
       if (getTagName(children[i]) == 'nav:label' && lang == CHOSEN_LANGUAGE) {
          return true;
       } 
    }
    return false;
}

/******************************************
 *  NavTree 
 ******************************************/
 
function NavTree(doc, treeElement) {
    this.doc = doc;
    this.treeElement = treeElement;
    this.selected = null;
};

NavTree.prototype = new LenyaTree;

NavTree.prototype.init = function(id, label) {
    this.root = new NavNode(id);
    this.root.tree = this;
    this.root.depth = 0;
    this.root.reopen = false;
    this.root.isprotected = false;
    this.root.isfolder = true;
    this.root.label = label;
    this._currentId = 0;
};

NavTree.prototype.loadInitialTree = function(area, path) {
    var url = encodeURI(WEBAPP_BASE_PATH + PIPELINE_PATH + '?'
        + 'pub=' + PUBLICATION_ID
        + '&area=' + area
        + '&path=' + path 
        + '&mimetype=true' 
        + '&defaultLanguage=' + DEFAULT_LANGUAGE
        + '&language=' + CHOSEN_LANGUAGE
        + '&initial=true'
        + '&areas=' + ALL_AREAS);
    
    callback = function(fragment, param) {
        var tree = param[0];
        var area = param[1];
        var path = param[2];
        tree.initialTreeLoaded(fragment);
        var selectedItem = tree.getItemByPath(PUBLICATION_ID+'/'+area+path)
        if (selectedItem != false) {
            tree.select(selectedItem);
        }
    }
    
    var param = new Array(this, area, path);
    loadAsyncXML(url, callback, param);
};

NavTree.prototype.initialTreeLoaded = function(xml)
{
  var root = xml.documentElement;
  var children = root.childNodes;
  var items=[];
  var item;
  for (var i = 0; i < children.length; i++) {
     if (getTagName(children[i]) == "nav:site") {
        item = this.addLoadedSite(children[i]);
        items.push(item);
        item.addNodesRec(children[i]);
     }
  }
  this.root.addItems(items);
  this.root.open();
}

NavTree.prototype.addLoadedSite = function(site)
{
  var langSuffix = '';
  if (CHOSEN_LANGUAGE!=DEFAULT_LANGUAGE) langSuffix = '_'+CHOSEN_LANGUAGE;

  var siteArea = site.getAttribute('area');
  var newSite = new NavNode(siteArea, this.root);
  newSite.init();
  
  newSite.isfolder = isNodeFolder(site);
  newSite.area = siteArea;
  newSite.path = '/';
  newSite.isprotected = isNodeProtected(site);
  newSite.mimetype = getMimeType(site);
  newSite.isresource =isResource(site);
  newSite.href = site.getAttribute('href') + "/" + langSuffix;
  newSite.langSuffix = langSuffix;
  newSite.label = site.getAttribute('label');
  return newSite;
}

NavTree.prototype.handleItemClick = function(item, event) {
    if (!item.isprotected) { // && item.root!=item) {
        href = encodeURI(item.href + "?lenya.usecase=" + USECASE); 
        window.location = href;
    }
};

/*
  overriding this function because of a bug in lenya:
  the area cannot be clickable because lenya expects
  a document for the area.
*/
NavTree.prototype.createItemHtml = function(item) {
    var div = this.doc.createElement('div');
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
    
    // make root node not clickable
    if (item.depth > 0) {
      addEventHandler(line, 'click', this.handleItemClick, this, item);
    }
    addEventHandler(item.opensign, 'click', this.handleItemSignClick, 
                    this, item);

    if (this.selected == item.getPath()) {
        this.unselect();
        this.select(item);
        div.className = 'selected_node';
    };

    return div;
};

