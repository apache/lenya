/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


/**
  * implements a "save" button that works with Lenya
  *
  * All it does is trigger a submit event, and then the normal 
  * usecase handler takes over, as if the user had clicked 
  * the XHTML submit button.
  */
function LenyaSaveCommand() {  this.Name = 'Lenya_save'; }

/**
  * an FCKeditor callback that is run when the command is invoked
  */
LenyaSaveCommand.prototype.Execute = function() {
  var oForm = FCK.LinkedField.form ;
  oForm.submit.click()
}
/**
  * an FCKeditor callback that takes care of button state
  */ 
LenyaSaveCommand.prototype.GetState = function() { 
  return FCK_TRISTATE_OFF;
}


/**
  * implements an "insert link" button that works with Lenya
  *
  * It will open the editors.insertLink usecase in a new window.
  */
function LenyaInsertLinkCommand() { this.Name = 'Lenya_insertLink'; }

/**
  * an FCKeditor callback that is run when the command is invoked
  */
LenyaInsertLinkCommand.prototype.Execute = function() {
  //alert("Lenya_insertLink executed.");
  var windowName = org.apache.lenya.editors.generateUniqueWindowName();
  var selection = org.apache.lenya.editors.getSelectedText(FCK.EditorWindow);
  usecaseMap[windowName] = org.apache.lenya.editors.USECASE_INSERTLINK;
  objectData[windowName] = new org.apache.lenya.editors.ObjectData({
    url   : "",
    title : "",
    text  : selection
  });
  org.apache.lenya.editors.openUsecaseWindow(
    usecaseMap[windowName],
    windowName
  );
}

/**
  * an FCKeditor callback that takes care of button state
  */ 
LenyaInsertLinkCommand.prototype.GetState = function() { 
  return FCK_TRISTATE_OFF; 
}


/**
  * implements an "insert image" button that works with Lenya
  *
  * It will open the editors.insertImage usecase in a new window.
  */
function LenyaInsertImageCommand() { this.Name = 'Lenya_insertLink'; }

/**
  * an FCKeditor callback that is run when the command is invoked
  */
LenyaInsertImageCommand.prototype.Execute = function() {
  //alert("Lenya_insertImage executed.");
  var windowName = org.apache.lenya.editors.generateUniqueWindowName();
  var selection = org.apache.lenya.editors.getSelectedText(FCK.EditorWindow);
  usecaseMap[windowName] = org.apache.lenya.editors.USECASE_INSERTIMAGE;
  objectData[windowName] = new org.apache.lenya.editors.ObjectData({
    url   : "",
    title : "",
    text  : selection,
    height: "",
    width : ""
  });
  org.apache.lenya.editors.openUsecaseWindow(
    usecaseMap[windowName],
    windowName
  );
}

/**
  * an FCKeditor callback that takes care of button state
  */ 
LenyaInsertImageCommand.prototype.GetState = function() { 
  return FCK_TRISTATE_OFF;
}


/***************** main *******************************************************/  

/**
  * FCKeditor uses frames heavily, which means that our helper library
  * will not be in the current scope. So we define it to point to the
  * "top" window, which hopefully has the library included.
  */
var org;
if (!org) {
  org = window.top.org;
} else if (!org.apache) {
  org.apache = window.top.org.apache;
} else if (!org.apache.lenya) {
  org.apache.lenya = window.top.org.apache.lenya;
} else if (!org.apache.lenya.editors) {
  org.apache.lenya.editors = window.top.org.apache.lenya.editors;
}

/**
  * to store usecase data per window
  */
var objectData = new Array();

/**
  * to map usecase names to window names
  */
var usecaseMap = new Array();


/* FCKCommands.RegisterCommand(commandName, command)
       commandName - Command name, referenced by the Toolbar, etc...
       command - Command object (must provide an Execute() function).
*/

// Register Lenya-specific commands

FCKCommands.RegisterCommand('Lenya_save', new LenyaSaveCommand());
FCKCommands.RegisterCommand('Lenya_insertLink', new LenyaInsertLinkCommand());
FCKCommands.RegisterCommand('Lenya_insertImage', new LenyaInsertImageCommand());



// Create Lenya-specific toolbar buttons:
 
var oLenya_saveItem = new FCKToolbarButton('Lenya_save', FCKLang['DlgLenya_saveTitle']);
oLenya_saveItem.IconPath= [FCKConfig.SkinPath + 'fck_strip.gif', 16, 3]; // use FCKEditor's icons
FCKToolbarItems.RegisterItem( 'Lenya_save', oLenya_saveItem ) ;

var oLenya_insertLinkItem = new FCKToolbarButton('Lenya_insertLink', FCKLang['DlgLenya_insertLinkTitle']);
oLenya_insertLinkItem.IconPath = FCKPlugins.Items['fck_lenya_glue'].Path + 'insertLink.gif' ;
FCKToolbarItems.RegisterItem( 'Lenya_insertLink', oLenya_insertLinkItem ) ;

var oLenya_insertImageItem = new FCKToolbarButton( 'Lenya_insertImage', FCKLang['DlgLenya_insertImageTitle']);
oLenya_insertImageItem.IconPath = FCKPlugins.Items['fck_lenya_glue'].Path + 'insertImage.gif';
FCKToolbarItems.RegisterItem('Lenya_insertImage', oLenya_insertImageItem);


/******************** Lenya editor usecase callbacks **********************/


org.apache.lenya.editors.setObjectData = function(objectData, windowName) {
  var usecase = usecaseMap[windowName];
  var snippet = org.apache.lenya.editors.generateContentSnippet(usecase, objectData);
  var selection = org.apache.lenya.editors.getSelectedText(FCK.EditorWindow);
  
  FCK.InsertHtml(
    snippet.beforeSelection 
    + (snippet.replaceSelection ? snippet.replaceSelection : selection)
    + snippet.afterSelection
  );

  usecaseMap[windowName] = undefined; // we're done!
  objectData[windowName] = undefined; // we're done!
}

org.apache.lenya.editors.getObjectData = function(windowName, usecase) {
  return objectData[windowName];
}
