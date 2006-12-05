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


function LenyaExecCommandHandler(editor_id, elm, command, user_interface, value) {
var linkElm, imageElm, inst;

/*
  switch (command) {

    case "mceSave":
      
      alert("mceSave callback invoked. This does not yet do anything.");
      return true;
      
  }
*/
  return false; // Pass to next handler in chain
}


// this function will be called by the generic insertAsset
// usecase.
function insertCallback(content, editor_id) {
  // FIXME: this is not tested and not finished. How are internal links handled atm?
  alert("insertCallback called.\n" 
         + "content   : " + content + "\n"
         + "editor_id : " + editor_id
  );
  tinyMCE.execCommand('mceInsertContent', decodeURI(editor_id), content);
}

function LenyaSaveContent(element_id, html, body) {
  // Do some custom HTML cleanup
  alert("LenyaSaveContent callback invoked. I'll do some clean-up now (\"&nbsp;\" -> \"&#160;\").");

  // this is necessary since tinymce inserts &nbsp; entities into
  // empty <p>s and <td>s regardless of the "entity_encoding" setting,
  // and these will break Lenya as they are not defined by default.
  html = html.replace(/&nbsp;/g, "&#160;");
  return html;
}


