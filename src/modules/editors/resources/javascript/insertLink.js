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
    document.forms["insertLink"].url.value = "lenya-document:" + uuid + ",lang=" + language;
}


function buildTree() {
    var placeholder = document.getElementById('tree');
    var tree = new LinkTree(document, placeholder);
    tree.init(PUBLICATION_ID);
    tree.render();
    tree.loadInitialTree(AREA, DOCUMENT_ID);
}

LinkTree.prototype = new NavTree;
LinkTree.prototype.handleItemClick = function(item, event) {
    setLink(item.uuid);
}

window.onload = function() {
  buildTree();
  org.apache.lenya.editors.handleFormLoad('insertLink');
};
