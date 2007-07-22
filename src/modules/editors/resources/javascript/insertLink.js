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


/* this file implements the generic part of an LenyaInsertLink usecase. for the editor-specific glue,
   a callback function must be provided by the editor module. */

function LenyaInvokeInsertLink() { 
  // prepare linkData object (this is part of the callback API!):
  var linkData = {
    'href':  document.forms["LenyaInsertLink"].url.value,
    'text':  document.forms["LenyaInsertLink"].text.value,
    'title': document.forms["LenyaInsertLink"].title.value,
    'name':  "",
    'lang':  ""
  };

  // invoke callback:
  LenyaInsertLink(linkData);
}

/**
  * if the user selected some text in the editor, use it to fill the "text" field.
  */
function LenyaSetText() { 
    var selectionContent = window.opener.getSelection().getEditableRange().toString(); 
    if (selectionContent.length != 0) { 
        document.forms["LenyaInsertLink"].text.value = selectionContent;
    } 
    focus(); 
}

function LenyaLinkTree(doc, treeElement) {
    this.doc = doc;
    this.treeElement = treeElement;
    this.selected = null;
}

/**
  * FIXME: CHOSEN_LANGUAGE should not be a global variable!
  * a callback used by the LenyaLinkTree object.
  */
function LenyaSetLink(uuid) {
    var language = CHOSEN_LANGUAGE;
    document.forms["LenyaInsertLink"].url.value = "lenya-document:" + uuid + ",lang=" + language;
}

function LenyaBuildTree() {
    var placeholder = document.getElementById('tree');
    var tree = new LenyaLinkTree(document, placeholder);
    tree.init(PUBLICATION_ID);
    tree.render();
    tree.loadInitialTree(AREA, DOCUMENT_ID);
}

window.onload = LenyaSetText;

LenyaLinkTree.prototype = new NavTree;
LenyaLinkTree.prototype.handleItemClick = function(item, event) {
    LenyaSetLink(item.uuid);
}
