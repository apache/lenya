/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/**
  * updates the hidden form data whenever the user selects a new asset radiobutton.
  */
function updateData(url, title, height, width, type) { 
  
  var text = document.forms["insertAsset"].text.value;
  if (text && text != document.forms["insertAsset"].title.value) {
    // if the user has provided an entry for "text" 
    //    (other than a copy of "title"), use that:
    text  = document.forms["insertAsset"].text.value;
  } else {
    // otherwise just copy the title value:
    text = title;
  }
  // we store the ratio with every image for correct re-scaling.
  var ratio = 1;
  if (width != 0) {
    ratio = height / width;
  }
  document.forms["insertAsset"].ratio.value = ratio;

  var objectData = new org.apache.lenya.editors.ObjectData({
    url    : url,
    title  : title,
    text   : text,
    height : height,
    width  : width,
    type   : type
  });
  //alert("Setting form data:" + objectData.toString());
  org.apache.lenya.editors.setFormValues("insertAsset", objectData);
}

/**
  * updates the height to maintain correct ratio when the user changes the width
  */
function scaleHeight(width) {
  var ratio = document.forms['insertAsset'].ratio.value;
  document.forms['insertAsset'].height.value = Math.round(width * ratio);
  focus(); 
} 

/**
  * updates the width to maintain correct ratio when the user changes the height
  */
function scaleWidth(height) {
  var ratio = document.forms['insertAsset'].ratio.value;
  document.forms['insertAsset'].width.value = Math.round(height * 1.0 / ratio);
  focus(); 
} 

window.onload = function() {
  org.apache.lenya.editors.handleFormLoad("insertAsset");
};
