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


/* this file implements the generic part of an insertLink usecase.
   for the editor-specific glue, a callback function must be provided by the editor module.
   see http://wiki.apache.org/lenya/GenericEditorAPI .
 */

function lenyaInvokeInsertLink() { 
  // prepare linkData object (this is part of the callback API!):
  var form = document.forms["LenyaInsertLink"]
  var linkData = {
    'href':  form.url.value,
    'text':  form.text.value,
    'title': form.title.value,
    'name':  "",
    'lang':  ""
  };

  // invoke callback:
  window.opener.LenyaSetLinkData(linkData);
  window.close();
}

/**
  * fill the form with defaults.
  */
function lenyaSetDefaults(linkData) { 
    var linkData = window.opener.LenyaGetLinkData();
    var form = document.forms["LenyaInsertLink"];
    if (linkData['href'] !== undefined)
      form.url.value = linkData['href'];
    else
      form.url.disabled = true;
     if (linkData['text'] !== undefined)
      form.text.value = linkData['text'];
    else
      form.text.disabled = true;
    if (linkData['title'] !== undefined)
       form.title.value = linkData['title'];
    else
       form.title.disabled = true;
    window.focus();
}

function lenyaLinkTree(doc, treeElement) {
    this.doc = doc;
    this.treeElement = treeElement;
    this.selected = null;
}

/**
  * FIXME: CHOSEN_LANGUAGE should not be a global variable!
  * a callback used by the lenyaLinkTree object.
  */
function lenyaSetLink(uuid) {
    var language = CHOSEN_LANGUAGE;
    document.forms["LenyaInsertLink"].url.value = "lenya-document:" + uuid + ",lang=" + language;
}

function lenyaBuildTree() {
    var placeholder = document.getElementById('tree');
    var tree = new lenyaLinkTree(document, placeholder);
    tree.init(PUBLICATION_ID);
    tree.render();
    tree.loadInitialTree(AREA, DOCUMENT_ID);
}

lenyaLinkTree.prototype = new NavTree;
lenyaLinkTree.prototype.handleItemClick = function(item, event) {
    lenyaSetLink(item.uuid);
}

window.onload = function() {
  lenyaSetDefaults();
}