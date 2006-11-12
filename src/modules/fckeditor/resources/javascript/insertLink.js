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

function insertLink() { 
    var title = document.forms["link"].title.value;
    var prefix = '/' + PUBLICATION_ID + '/' + AREA;
    var url = document.forms["link"].url.value;
    if (url.charAt(0) == "/") {
     // prepend hostname etc for internal links
     url = prefix + url;
    }
    window.opener.SetUrl(url); 
    window.opener.document.getElementById('txtAttTitle').value = title ; 
    window.close();
}

function setLink(src) { 
    url = src;
    document.forms["link"].url.value = url;
}

function LinkTree(doc, treeElement) {
    this.doc = doc;
    this.treeElement = treeElement;
    this.selected = null;
}

LinkTree.prototype = new NavTree;

LinkTree.prototype.handleItemClick = function(item, event) {
    setLink(item.href);
}

function buildTree() {
    var placeholder = document.getElementById('tree');
    var tree = new LinkTree(document, placeholder);
    tree.init(PUBLICATION_ID);
    tree.render();
    tree.loadInitialTree(AREA, DOCUMENT_ID);
}
