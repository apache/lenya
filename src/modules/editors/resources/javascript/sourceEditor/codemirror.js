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

var editor;

function insertEditor() {
    editor = CodeMirror.fromTextArea('editorContent', {
        path: "/modules/editors/codemirror/0.60/js/",
        parserfile: "parsexml.js",
        stylesheet: "/modules/editors/css/codemirror.css",
        height: "400px",
        onChange: saveContent
    });
}

function saveContent() {
    if (editor) {
        // Firefox
        document.forms.oneform.content.innerHTML = editor.getCode();
        // Safari
        document.forms.oneform.content.value = editor.getCode();
        
        document.getElementById("save1").disabled = null;
        document.getElementById("save2").disabled = null;
    }
}

function indent() {
    editor.reindent();
}
