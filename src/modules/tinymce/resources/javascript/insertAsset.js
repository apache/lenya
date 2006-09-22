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

   function insertAsset(editorInstanceID) {
      var src = document.forms['asset'].assetName.value;
      var title = document.forms['asset'].caption.value;
      var type = document.forms['asset'].type.value;
      var size = document.forms['asset'].assetSize.value;
      var content = '<asset xmlns="http://apache.org/cocoon/lenya/page-envelope/1.0" '
          + 'src="' + src
          + '" size="' + size 
          + '" type="' + type
          + '">' + title + '</asset>';
      window.opener.insertCallback(content, editorInstanceID);
   }
   
   function insertImage(editorInstanceID) { 
      // var link = document.forms['asset'].link.value;
      var link = '';
      var src = document.forms['asset'].assetName.value;
      var title = document.forms['asset'].caption.value;
      var type = document.forms['asset'].type.value;
      var height = document.forms['asset'].height.value;
      var width = document.forms['asset'].width.value;
      var content = '<object xmlns="http://www.w3.org/1999/xhtml" '
          + 'href="' + link
          + '" title="' + title
          + '" type="' + type
          + '" data="' + src
          + '" height="' + height
          + '" width="' + width
          + '">' + src + '</object>'; 
      window.opener.insertCallback(content, editorInstanceID); 
   }

   function insertCaption(name, caption, type, size) { 
     document.forms['asset'].assetName.value = name;
     document.forms['asset'].caption.value = caption;
     document.forms['asset'].type.value = type;
     document.forms['asset'].assetSize.value = size;
     focus(); 
   } 

   function insertData(name, caption, type, size, height, width) { 
     var ratio = 1;
     if (width != 0) {
       ratio = height / width;
     }
     document.forms['asset'].assetName.value = name;
     document.forms['asset'].caption.value = caption;
     document.forms['asset'].type.value = type;
     document.forms['asset'].assetSize.value = size;
     document.forms['asset'].height.value = height;
     document.forms['asset'].width.value = width;
     document.forms['asset'].ratio.value = ratio;
     focus(); 
   } 

   // just for debugging:
   function printFormData() {
     alert(
         'assetName = ' + document.forms['asset'].assetName.value + '\n'
       + 'caption   = ' + document.forms['asset'].caption.value + '\n'
       + 'type      = ' + document.forms['asset'].type.value + '\n'
       + 'assetSize = ' + document.forms['asset'].assetSize.value + '\n'
// FIXME: the whole function will fail if these are not present:
//       + 'height    = ' + document.forms['asset'].height.value + '\n'
//       + 'width     = ' + document.forms['asset'].width.value + '\n'
//       + 'ratio     = ' + document.forms['asset'].ratio.value + '\n'
     )
   }

   function scaleHeight(width) {
     var ratio = document.forms['asset'].ratio.value;
     document.forms['asset'].height.value = width * ratio;
     focus(); 
   } 
  
   function scaleWidth(height) {
     var ratio = document.forms['asset'].ratio.value;
     document.forms['asset'].width.value = height * ratio;
     focus(); 
   } 
  
