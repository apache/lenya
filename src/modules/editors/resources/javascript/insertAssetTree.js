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

/* Constructor for LinkTree object */
function LinkTree(doc, treeElement) {
    this.doc = doc;
    this.treeElement = treeElement;
    this.selected = null;
}

/**
  * FIXME: CHOSEN_LANGUAGE should not be a global variable!
  * a callback used by the LinkTree object.
  */
function setLink(uuid) {
    var language = CHOSEN_LANGUAGE;
    document.forms.insertAsset.url.value = "lenya-document:" + uuid + ",lang=" + language;
}

function updateInfos(url){
    var param = [this,url];
    var callback = function(xml, param) {
        var root = xml.documentElement;
        var children = root.childNodes;
        var extension;
        var height;
        var width;
        var mimeType;
        var i;    
        var j;
        for (i = 0; i < children.length; i++) {
           if (children[i].tagName === "elements") {
              // now get the extension
              var childrenElement =children[i].childNodes;
              for (j = 0; j < childrenElement.length; j++) {
                 if (childrenElement[j].tagName === "extension") {
                     extension=childrenElement[j].textContent;
                 }
                 if (childrenElement[j].tagName === "height") {
                     height=childrenElement[j].textContent;
                 }
                 if (childrenElement[j].tagName === "width") {
                     width=childrenElement[j].textContent;
                 }
                 if (childrenElement[j].tagName === "mimeType") {
                     mimeType=childrenElement[j].textContent;
                 }
              }
           }
        }
       if (MODE_INSERT!=='Asset') {
           document.forms.insertAsset.width.value = width;
        document.forms.insertAsset.height.value = height;
        var result = returnObjById('preview');
        result.innerHTML = "";
        var newImg = document.createElement('img');
        newImg.setAttribute('src', param[1].replace('.html.meta','.'+extension));
        if (width>450) {
         newImg.setAttribute('width','450');
        }
        result.appendChild(newImg);
       } else {
            $('#preview').html(mimeType);
       }

    };
    loadAsyncXML(url, callback, param);
}

function buildTree() {
    var placeholder = document.getElementById('tree');
    var tree = new LinkTree(document, placeholder);
    tree.init(PUBLICATION_ID);
    tree.render();
    tree.loadInitialTree(AREA, DOCUMENT_ID);
}

LinkTree.prototype = new NavTree();
LinkTree.prototype.handleItemClick = function(item, event) {
	var className = item.getStyle();
	/* 
	 * Here we disable all onclick events for disabled nodes
	 * (early exist)
	 */
	if(className!=='treenode_label'){
		return;
	}
    setLink(item.uuid);
    document.forms.insertAsset.title.value = item.label;
    document.forms.insertAsset.text.value = item.label;
    var url = encodeURI(item.href+'.meta');
    updateInfos(url);
};

window.onload = function() {
  buildTree();
  org.apache.lenya.editors.handleFormLoad('insertAsset');
};

/* OVERRIDE creates the item name and any icons and such */
LenyaTree.prototype.createItemLine = function(item) {
    var div = this.createElement('div');
    var span = this.createElement('span');
    var icon = this.getIcon(item);
    if (icon.nodeType === 1) {
        icon.className = 'treenode_icon';
    }
    div.appendChild(icon);
    var text = this.doc.createTextNode(item.label ? item.label : item.id);
    var isAsset = item.mimetype;
    span.className = item.getStyle();
    span.appendChild(text);
    div.appendChild(span);
    
    return div;
};
NavNode.prototype.getStyle = function() {
    if (this.tree.root === this) {
        return 'lenya-info-root';
    } else if (this.parent === this.tree.root) {
        return 'lenya-info-area';
    } else if (!this.existsChosenLanguage || !this.isresource) {
        return 'lenya-info-nolanguage';
    } else {
		if(MODE_INSERT!=='Asset' && this.mimetype.indexOf('image')){
			return 'lenya-info-nolanguage';
		}else{
            return 'treenode_label';
		}   
    }
};

function returnObjById(id){
   var returnVar;
   if (document.getElementById) {
      returnVar = document.getElementById(id);
   }else if (document.all) {
      returnVar = document.all[id];
   }else if (document.layers) {
      returnVar = document.layers[id];
   }
   return returnVar;
}