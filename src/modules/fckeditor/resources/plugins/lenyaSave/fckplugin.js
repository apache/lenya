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

/* FCKCommands.RegisterCommand(commandName, command)
       commandName - Command name, referenced by the Toolbar, etc...
       command - Command object (must provide an Execute() function).
*/
// Register the related commands.
FCKCommands.RegisterCommand(
   'lenyaSave',
    new LenyaSaveCommand()
);

function LenyaSaveCommand()
{
  this.Name = 'lenyaSave' ;
}

LenyaSaveCommand.prototype.Execute = function()
{
  var oForm = FCK.LinkedField.form ;
  oForm.submit.click()
}

LenyaSaveCommand.prototype.GetState = function()
{
	return 0;
}

// Create the "Find" toolbar button. 
var oFindItem = new FCKToolbarButton('lenyaSave', FCKLang['lenyaSaveDlgToolbarName']);
oFindItem.IconPath = '/modules/fckeditor/FCKeditor/editor/skins/default/toolbar/save.gif' ;

// 'My_Find' is the name used in the Toolbar config.
FCKToolbarItems.RegisterItem( 'lenyaSave', oFindItem ) ;

