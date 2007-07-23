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



function LenyaSaveContent(element_id, html, body) {
  // Do some custom HTML cleanup
  // this is necessary since tinymce inserts &nbsp; entities into
  // empty <p>s and <td>s regardless of the "entity_encoding" setting,
  // and these will break Lenya as they are not defined by default.
  html = html.replace(/&nbsp;/g, "&#160;");
  return html;
}

/**
  * This function is called by the editors.insertLink view to
  * get default values and to determine which fields will be enabled.
  * @returns a linkData hash map.
  */
function LenyaGetLinkData() {
  var currentURL = TinyMCE_SimpleBrowserPlugin.options['curl'];
  return {
    'href' : currentURL ? currentURL : ""
  };
}

/**
  * This function is called by the editors.insertLink view to
  * update the editor with the new user-supplied values.
  */
function LenyaSetLinkData(linkData) { 
  TinyMCE_SimpleBrowserPlugin.browserCallback(linkData);
}

var lenyaLinkData;
