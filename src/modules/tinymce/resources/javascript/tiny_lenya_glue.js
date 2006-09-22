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

  switch (command) {

    case "mceLink":

      inst = tinyMCE.getInstanceById(editor_id);
      linkElm = tinyMCE.getParentElement(inst.selection.getFocusElement(), "a");
      if (linkElm) {
        alert("Link dialog has been overriden. Found link href: " + tinyMCE.getAttrib(linkElm, "href"));
      } else {
        var newwindow = '';
        IUurl = '?lenya.module=tinymce&amp;lenya.step=link-show&amp;language=en';
        newwindow = self.open(decodeURLWithAmp(IUurl),'hello','location=1,resizable=1,height=600,width=500,scrollbars=1'); 
      }
      return true;
/*
      var inst = tinyMCE.getInstanceById(editor_id);
      var doc = inst.getDoc();
      var selectedText = "";
  
      if (tinyMCE.isMSIE) {
              var rng = doc.selection.createRange();
              selectedText = rng.text;
      } else
              selectedText = inst.getSel().toString();
  
      if (!tinyMCE.linkElement) {
              if ((tinyMCE.selectedElement.nodeName.toLowerCase() != "img") && (selectedText.length <= 0))
                      return true;
      }
  
      var href = "", target = "", title = "", onclick = "", action = "insert", style_class = "";
  
      if (tinyMCE.selectedElement.nodeName.toLowerCase() == "a")
              tinyMCE.linkElement = tinyMCE.selectedElement;
  
      // Is anchor not a link
      if (tinyMCE.linkElement != null && tinyMCE.getAttrib(tinyMCE.linkElement, 'href') == "")
              tinyMCE.linkElement = null;
  
      if (tinyMCE.linkElement) {
              href = tinyMCE.getAttrib(tinyMCE.linkElement, 'href');
              target = tinyMCE.getAttrib(tinyMCE.linkElement, 'target');
              title = tinyMCE.getAttrib(tinyMCE.linkElement, 'title');
              onclick = tinyMCE.getAttrib(tinyMCE.linkElement, 'onclick');
              style_class = tinyMCE.getAttrib(tinyMCE.linkElement, 'class');
  
              // Try old onclick to if copy/pasted content
              if (onclick == "")
                      onclick = tinyMCE.getAttrib(tinyMCE.linkElement, 'onclick');
  
              onclick = tinyMCE.cleanupEventStr(onclick);
  
              href = eval(tinyMCE.settings['urlconverter_callback'] + "(href, tinyMCE.linkElement, true);");
  
              // Use mce_href if defined
              mceRealHref = tinyMCE.getAttrib(tinyMCE.linkElement, 'mce_href');
              if (mceRealHref != "") {
                      href = mceRealHref;
  
                      if (tinyMCE.getParam('convert_urls'))
                              href = eval(tinyMCE.settings['urlconverter_callback'] + "(href, tinyMCE.linkElement, true);");
              }
  
              action = "update";
      }
  
      var template = new Array();
  
      template['file'] = 'link.htm';
      template['width'] = 310;
      template['height'] = 200;
  
      // Language specific width and height addons
      template['width'] += tinyMCE.getLang('lang_insert_link_delta_width', 0);
      template['height'] += tinyMCE.getLang('lang_insert_link_delta_height', 0);
  
      if (inst.settings['insertlink_callback']) {
              var returnVal = eval(inst.settings['insertlink_callback'] + "(href, target, title, onclick, action, style_class);");
              if (returnVal && returnVal['href'])
                      TinyMCE_AdvancedTheme._insertLink(returnVal['href'], returnVal['target'], returnVal['title'], returnVal['onclick'], returnVal['style_class']);
      } else {
              tinyMCE.openWindow(template, {href : href, target : target, title : title, onclick : onclick, action : action, className : style_class, inline : "yes"});
      }
  
      return true;
*/

    case "mceImage":

      inst = tinyMCE.getInstanceById(editor_id);
      imageElm = tinyMCE.getParentElement(inst.selection.getFocusElement(), "img");
      // is the currently selected element an image?
      if (imageElm) {
        // yes. the user wants to edit it. return false to pass 
        // control back to the standard tinymce image dialog.
        return false;
      } else {
        // no. open a lenya insertAsset dialog.
        alert("mceImage callback invoked.");
        imageDialog = window.open(
            "?lenya.usecase=tinymce.insertImage" +
            "&editors.instance=" + encodeURI(editor_id),
            "tinymce_insertImage",
            "dependent=yes," +
            "height=700," + 
            "width=580," + 
            "top=100," + 
            "width=100," +
            "location=yes," + 
            "menubar=yes," +
            "resizable=yes," + 
            "scrollbars=yes"
    
        );
      }
      return true;

    case "mceSave":
      
      alert("mceSave callback invoked. This does not yet do anything.");
      return true;
      
  }
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





/*

currently unused:

function decodeURLWithAmp(str) {
  str = str.replace(/&amp;/g, "&");
  return str;
}

function decodeChars(str) {
  str = str.replace(/&lt;/g, "<");
  str = str.replace(/&gt;/g, ">");
return str;
}

// some AJAX helpers 

// browser abstraction 
function createXMLHttpRequest() {
  // Firefox and friends, Opera, Konqueror, Safari:
  if (window.XMLHttpRequest) return new XMLHttpRequest();
  // the evil one:
  if (window.ActiveXObject)  return new ActiveXObject("Microsoft.XMLHTTP");
  alert(
      "The Lenya TinyMCE module needs XMLHttpRequest support (aka AJAX).\n" +
      "It seems like your browser does not provide it. Sorry."
  );
  return null;
}

// some constants to avoid magic numbers in XMLHttpRequest methods 
const XHR_ASYNC         = true;

const XHR_UNINITIALIZED = "0";
const XHR_LOADING       = "1";
const XHR_LOADED        = "2";
const XHR_INTERACTIVE   = "3";
const XHR_COMPLETE      = "4";

*/