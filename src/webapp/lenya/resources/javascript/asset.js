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

/* $Id$ */

/* Show a preview of an image to be uploaded */
function imagepreview(obj) {
   prev = document.getElementById('preview');
   prev.style.visibility = 'hidden';
   var i = 0;
   var delimiter = ' '; 
   var imageext = 'gif jpg jpeg png';
   var isimage = false;
   var _tempArray = new Array();
   _tempArray = imageext.split(delimiter);
   for(i in _tempArray) { 
     if(obj.value.indexOf('.' + _tempArray[i]) != -1) { // file is an image. 
       isimage = true; 
     } 
   } 
   if (isimage) { 
     prev.setAttribute('src','file://' + obj.value);
     prev.style.visibility = 'visible';
   }
}
            
