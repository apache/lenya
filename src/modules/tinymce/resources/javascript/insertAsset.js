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

   ext = '';
   
   function insertAsset(nodeid) {
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value;
      var type = document.forms['image'].type.value;
      var size = document.forms['image'].assetSize.value;
      window.top.opener.TinyMCE_SimpleBrowserPlugin.browserCallback(src) ;
	  window.top.close() ;
    }
   
   function insertImage(nodeid) { 
      // var link = document.forms['image'].link.value;
      var link = '';
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value;
      var type = document.forms['image'].type.value;
      var height = document.forms['image'].height.value;
      var width = document.forms['image'].width.value;
      var content = '<object xmlns="'+window.opener.XHTMLNS+'" href="'+link+'" title="'+title+'" type="'+type+'" data="'+src+'" height="'+height+'" width="'+width+'">'+src+'</object>'; 
      window.top.opener.TinyMCE_SimpleBrowserPlugin.browserCallback(src) ;
	  window.top.close() ;
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
  