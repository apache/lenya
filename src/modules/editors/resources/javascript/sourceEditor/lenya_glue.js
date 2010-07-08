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

org.apache.lenya.editors.setObjectData = function(objectData, windowName) {
  var currentUsecase = usecaseMap[windowName];
  var snippet = org.apache.lenya.editors.generateContentSnippet(currentUsecase, objectData);
  var newText = (snippet.beforeSelection || '') + (snippet.replaceSelection || '') + (snippet.afterSelection || '');
  editor.replaceSelection(newText);
  usecaseMap[windowName] = undefined; // we're done!
  objectData[windowName] = undefined; // we're done!
  saveContent();
}

org.apache.lenya.editors.getObjectData = function(windowName) {
  return objectData[windowName];
}

function triggerUsecase(usecase) {
  var windowName = org.apache.lenya.editors.generateUniqueWindowName();
  var selectedText = editor.selection();
  switch (usecase) {

    case org.apache.lenya.editors.USECASE_INSERTLINK:
      objectData[windowName] = new org.apache.lenya.editors.ObjectData({
        url   : "",
        text  : selectedText,
        title : ""
      });
      break;

    case org.apache.lenya.editors.USECASE_INSERTIMAGE:
      objectData[windowName] = new org.apache.lenya.editors.ObjectData({
        url   : "",
        text  : selectedText,
        title : "",
        width : "",
        height: ""
      });
      break;

    case org.apache.lenya.editors.USECASE_INSERTASSET:
      objectData[windowName] = new org.apache.lenya.editors.ObjectData({
        url   : "",
        text  : selectedText,
        title : ""
      })
      break;
  }
  org.apache.lenya.editors.openUsecaseWindow(usecase, windowName);
  usecaseMap[windowName] = usecase;
  /*  alert("Stored values for new window " + windowName + ":\n"
      + "objectData[windowName] = '" + objectData[windowName] + "'\n"
      + "usecaseMap[windowName] = '" + usecaseMap[windowName] + "'"
  ); */ 
}

var objectData = new Array();
var usecaseMap = new Array();
