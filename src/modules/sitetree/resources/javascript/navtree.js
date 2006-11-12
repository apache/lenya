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
    this.isprotected = true;
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
    newItem.path = node.getAttribute('basic-url');
    newItem.uuid = node.getAttribute('uuid');
    newItem.isprotected = isNodeProtected(node);
    newItem.href = node.getAttribute('href');
    newItem.label = getLabel(node);
    newItem.existsChosenLanguage = existsChosenLanguage(node);
    newItem.langSuffix = node.getAttribute('language-suffix');
  
    return newItem;
}

NavNode.prototype.getLoadSubTreeURL = function() {
    area = this.area;
    path = this.path;  
    return encodeURI(CONTEXT_PREFIX + '/' + PUBLICATION_ID + PIPELINE_PATH + '?area='+area+'&path='+path+'&language='+CHOSEN_LANGUAGE+'&areas='+ALL_AREAS+'&lenya.module=sitetree');
}

NavNode.prototype.getStyle = function() {
    if (this.tree.root == this) {
        return 'lenya-info-root';
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
          return cs[i].firstChild.nodeValue;
       } 
    }
    return '';
}

function isNodeProtected(node) {
    var prot = node.getAttribute('protected');
    if (prot == 'true') return true;
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
       if (getTagName(children[i]) =='nav:label' && children[i].getAttribute('xml:lang')==CHOSEN_LANGUAGE) {
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

NavTree.prototype.init = function(id) {
    this.root = new NavNode(id);
    this.root.tree = this;
    this.root.depth = 0;
    this.root.reopen = false;
    this.root.isprotected = false;
    this.root.isfolder = true;
    this._currentId = 0;
};

NavTree.prototype.loadInitialTree = function(area, path) {
    var url = encodeURI(CONTEXT_PREFIX + '/' + PUBLICATION_ID + PIPELINE_PATH + '?area='+area+'&path='+path+'&language='+CHOSEN_LANGUAGE+'&initial=true&areas='+ALL_AREAS+'&lenya.module=sitetree');
    
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
  newSite.href = langSuffix;
  newSite.langSuffix = langSuffix;
  newSite.label = site.getAttribute('label');
  
  return newSite;
}

NavTree.prototype.handleItemClick = function(item, event) {
    if (!item.isprotected && item.root!=item) {
        var itemhref = item.href.replace(/^\//, "");
        href = encodeURI(CONTEXT_PREFIX+'/'+PUBLICATION_ID+"/"+item.area+"/"+itemhref+"?lenya.usecase=tab.overview"); 
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
    
    var table = this.doc.createElement('table');
    div.appendChild(table);
    var tbody = this.doc.createElement('tbody');
    table.appendChild(tbody);
    var tr = this.doc.createElement('tr');
    tbody.appendChild(tr);

    // add the lines:
    this.addLines(item, tr);

    // add the opensign
    var td1 = this.doc.createElement('td');
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
    var td2 = this.doc.createElement('td');
    tr.appendChild(td2);
    td2.className = 'lenya-info-label-td';
    var line = this.createItemLine(item);
    td2.appendChild(line);
    
    // make areas not clickable
    if (item.depth>1) {
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

