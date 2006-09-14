/*
* Copyright 1999-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

   ext = '';
   
   function insertAsset(nodeid) {
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value;
      var type = document.forms['image'].type.value;
      var size = document.forms['image'].assetSize.value;
      window.opener.bxe_insertContent('<asset xmlns="http://apache.org/cocoon/lenya/page-envelope/1.0" src="'+src+'" size="'+size+'" type="'+type+'">'+title+'</asset>',window.opener.BXE_SELECTION,window.opener.BXE_SPLIT_IF_INLINE);
      window.close();
    }
   
   function insertImage() { 
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value; 
      var type = document.forms['image'].type.value;
      var height = document.forms['image'].height.value;
      var width = document.forms['image'].width.value;
      window.opener.SetUrl( src, width, height, title );
      window.close();
   }

   function insertCaption(name, caption, type, size) { 
     document.forms['image'].assetName.value = name;
     document.forms['image'].caption.value = caption;
     document.forms['image'].type.value = type;
     document.forms['image'].assetSize.value = size;
     focus(); 
   } 

   function insertData(name, caption, type, size, height, width) { 
     var ratio = 1;
     if (width != 0) {
       ratio = height / width;
     }
     document.forms['image'].assetName.value = name;
     document.forms['image'].caption.value = caption;
     document.forms['image'].type.value = type;
     document.forms['image'].assetSize.value = size;
     document.forms['image'].height.value = height;
     document.forms['image'].width.value = width;
     document.forms['image'].ratio.value = ratio;
     focus(); 
   } 

   function scaleHeight(width) {
     var ratio = document.forms['image'].ratio.value;
     document.forms['image'].height.value = width * ratio;
     focus(); 
   } 
  
   function scaleWidth(height) {
     var ratio = document.forms['image'].ratio.value;
     document.forms['image'].width.value = height * ratio;
     focus(); 
   } 
  