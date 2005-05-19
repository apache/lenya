/*
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

var xmlhttp;

function NavRoot(doc, rootElement) {
    this.doc = doc;
    this.rootElement = rootElement;
    this.selected = null;
};

NavRoot.prototype = new Root;

NavRoot.prototype.getItems = function(item, handler, allow_cache) {
  if (item.root == item) {
    alert('getTtems() of root called. This should not happen, loadInitialTree should be called first.');
  } else {
    item.loadSubTree(handler);
  }
};

NavRoot.prototype.loadInitialTree = function(area, documentid) {
  var url = CONTEXT_PREFIX + '/' + PUBLICATION_ID + PIPELINE_PATH + '?area='+area+'&documentid='+documentid+'&language='+CHOSEN_LANGUAGE+'&initial=true&areas='+ALL_AREAS;  
  var fragment = loadSitetreeFragment(url);
  if (fragment!=null) {
    this.initialTreeLoaded(fragment);
    this.select(this.getItemByPath(PUBLICATION_ID+'/'+area+documentid)); // FIXME: is the path always correct?
  }
};

NavRoot.prototype.addItems = function(items) {
    this.items = {};
    this.itemids = [];
    for (var i = items.length - 1; i >= 0; i--) {
        var item = items[i];
        this.items[item.id] = item;
        this.itemids.unshift(item.id);
    };
};

NavRoot.prototype.initialTreeLoaded = function(xml)
{
  var root = xml.documentElement;
  var children = root.childNodes;
  var items=[];
  var item;
  for (var i = 0; i < children.length; i++) {
     if (children[i].tagName == "nav:site") {
        item = this.addLoadedSite(children[i]);
        items.push(item);
        item.addNodesRec(children[i]);
     }
  }
  this.addItems(items);
  this.open();
}

NavRoot.prototype.addLoadedSite = function(site)
{
  var langSuffix = '';
  if (CHOSEN_LANGUAGE!=DEFAULT_LANGUAGE) langSuffix = '_'+CHOSEN_LANGUAGE;

  var siteArea = site.getAttribute('area');
  var newSite = new NavNode(siteArea, this);
  newSite.init();
  
  newSite.isfolder = isNodeFolder(site);
  newSite.area = siteArea;
  newSite.documentid = '/';
  newSite.isprotected = isNodeProtected(site);
  newSite.href = langSuffix;
  newSite.label = site.getAttribute('label');
  
  return newSite;
}

/* NavRoot.prototype.getIcon = function(item) {
    // don't use an icon for root and area nodes
    if (item.depth<2) return this.doc.createTextNode('');

    var img = this.doc.createElement('img');
    img.setAttribute('src', IMAGE_PATH + 'document.gif');
    return img;
}; */

/* return an img object that represents the file type */
NavRoot.prototype.getIcon = function(item) {
    return this.doc.createTextNode('');
};

/* creates the item name and any icons and such */
NavRoot.prototype.createItemLine = function(item) {
    var span = this.doc.createElement('span');
    var icon = this.getIcon(item);
    if (icon.nodeType == 1) {
        icon.className = 'treenode_icon';
    };
    span.appendChild(icon);
    
    var text = this.doc.createTextNode(item.label ? item.label : item.id);
    
    if (item.root==item) {
        span.className = 'lenya-info-root';
    } else {
        if (item.isprotected) {
            span.className = 'lenya-info-protected';
        } else if (item.documentid == CUT_DOCUMENT_ID) {
            span.className = 'lenya-info-cut';
        } else if (!item.existsChosenLanguage) {
            span.className = 'lenya-info-nolanguage';
        } else {
            span.className = 'treenode_label';
        }
    }
    span.appendChild(text);
    
    return span;
};

NavRoot.prototype.createItemHtml = function(item) {
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
Root.prototype.getSignType = function(item) {
    if (item.root != item) {
        if (isLastChild(item.parent, item)) {
            return 'L';
        } else {
            return 'T';
        }
    }
    return 'X';  // invalid type
};

/* get the [+] sign for a collection or resource */
Root.prototype.getCloseSign = function(item) {
    var opensign = this.doc.createElement('img');
    opensign.className = 'treenode_sign';
    
    if (item.root == item) {
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
Root.prototype.getOpenSign = function(item) {
    var opensign = this.doc.createElement('img');
    opensign.className = 'treenode_sign';

    if (item.root == item) {
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
NavRoot.prototype.addLines = function(item, parentElement) {
    var linesStr = this.computeLinesString(item);
    // linesStr is in the reverse order
    for (var i=linesStr.length-1; i>0; i--) {
        var td = this.doc.createElement('td');
        var img = this.doc.createElement('img');
        
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
NavRoot.prototype.computeLinesString = function(item) {
    var lines = ''
    
    if (item.root == item) return lines;
    
    // first level, decide T or L
    if (isLastChild(item.parent, item)) {
        lines += 'L';
    } else {
        lines += 'T';
    }
    item = item.parent;

    // subsequent levels, decide I or E
    while (item.root != item) {
        if (isLastChild(item.parent, item)) {
            lines += 'E'; // empty
        } else {
            lines += 'I'; // vertical line
        }
        item = item.parent;
    }
    
    return lines;
}

function isLastChild(parent, child) {
    // this should be a method of Node
    for (var i=0; i<parent.itemids.length-1; i++) {
        if (parent.itemids[i] == child.id) return false;
    };
    return true;
}

NavRoot.prototype.handleItemClick = function(item, event) {
    if (!item.isprotected && item.root!=item) {
    	var itemhref = item.href.replace(/^\//, "");
        href = CONTEXT_PREFIX+'/'+PUBLICATION_ID+"/info-"+item.area+"/"+itemhref+"?lenya.usecase=info-overview&lenya.step=showscreen"; 
        window.location = href;
    }
};

/******************************************
 *  NavNode 
 ******************************************/

function NavNode(id, parent) {
    this.id = id;
    this.parent = parent;
    this.items = {};
    this.isfolder = false;
    this.href = '';
    this.label = '';
    this.area = '';
    this.documentid = '';
    this.isprotected = true;
    this.existsChosenLanguage = true;
};

NavNode.prototype = new Node;

NavNode.prototype.addItems = function(items) {
    this.items = {};
    this.itemids = [];
    for (var i = items.length - 1; i >= 0; i--) {
        var item = items[i];
        this.items[item.id] = item;
        this.itemids.unshift(item.id);
    };
};

NavNode.prototype.isCollection = function() {
    return this.isfolder;
};

NavNode.prototype.loadSubTree = function(handler) 
{
  area = this.area;
  documentid = this.documentid;  
  
  var url = CONTEXT_PREFIX + '/' + PUBLICATION_ID + PIPELINE_PATH + '?area='+area+'&documentid='+documentid+'&language='+CHOSEN_LANGUAGE+'&areas='+ALL_AREAS;

  var xml = loadSitetreeFragment(url);
  if (xml!=null) this.subTreeLoaded(xml, handler);
}

NavNode.prototype.subTreeLoaded = function(xml, handler)
{
  var root = xml.documentElement;

  var children = root.childNodes;
  var items=[];
  for (var i = 0; i < children.length; i++) {
     if (children[i].tagName == "nav:node") {
        items.push(this.addLoadedNode(children[i]));
     } 
  }
  //handler(items);
  this._continueOpen(items);

}

NavNode.prototype.addNodesRec = function(parentNode)
{
    var children = parentNode.childNodes;
    var items = [];
    var nodes = [];
    var item;
    for (var i = 0; i < children.length; i++) {
       if (children[i].tagName == "nav:node") {
          this.reopen = true; // this causes the parent to unfold
          item = this.addLoadedNode(children[i]);
          items.push(item);
          item.addNodesRec(children[i]); 
       } 
    }
    this.addItems(items);
}

NavNode.prototype.addLoadedNode = function(node)
{
    var newItem = new NavNode(node.getAttribute('id'), this);
    newItem.init();
  
    newItem.isfolder = isNodeFolder(node);
    newItem.area = this.area;
    newItem.documentid = '/' + node.getAttribute('basic-url');
    newItem.isprotected = isNodeProtected(node);
    newItem.href = node.getAttribute('href');
    newItem.label = getLabel(node);
    newItem.existsChosenLanguage = existsChosenLanguage(node);
  
    return newItem;
}



/******************************************
 *  Dynamic loading and helper functions 
 ******************************************/


function loadSitetreeFragment(url)
{
  if (xmlhttp==null) createXMLHttp();
  
  //alert('load subtree for '+url);
  // do synchronous loading 
  xmlhttp.open("GET",url,false);  
  xmlhttp.setRequestHeader('Accept','text/xml');
  xmlhttp.send(null);
  
  //alert('result: '+xmlhttp.responseText);
  
  var xml = xmlhttp.responseXML;
  if( xml == null || xml.documentElement == null) {
    alert('Error: could not load sitetree xml');
    return null;
  } 
  return xml;
}

function getLabel(node) 
{
    var cs = node.childNodes;
    var l = cs.length;
    // lenya generates the xml and is responsible to insert the label
    // of the correct language
    for (var i = 0; i < l; i++) {
       if (cs[i].tagName=='nav:label') {
          return cs[i].firstChild.nodeValue;
       } 
    }
    return '';
}

function isNodeProtected(node) 
{
  var prot = node.getAttribute('protected');
  if (prot == 'true') return true;
  return false;
}

function isNodeFolder(node) 
{
  var isfolder = node.getAttribute('folder');
  if (isfolder == 'true') return true;
  return false;
}

// check if the node has a label of the chosen language
function existsChosenLanguage(node) 
{
    var children = node.childNodes;
    for (var i = 0; i < children.length; i++) {
       if (children[i].tagName=='nav:label' && children[i].getAttribute('xml:lang')==CHOSEN_LANGUAGE) {
          return true;
       } 
    }
    return false;
}

// create the xmlhttp object
function createXMLHttp() 
{
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
