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


/*
 * toolbar for lenya
 */

FCKConfig.ToolbarSets["Lenya"] = [
	['Source','DocProps','-','Lenya_save','Preview','-','Templates'],
	['Cut','Copy','Paste','PasteText','PasteWord','-','Print'],
	['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
	['OrderedList','UnorderedList','-','Outdent','Indent'],
	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
	['Link', 'Lenya_insertLink','Unlink','Anchor'],
	['Image', 'Lenya_insertImage','Table','Rule','Smiley','SpecialChar','PageBreak'],
	['Form','Checkbox','Radio','TextField','Textarea','Select','Button','ImageButton','HiddenField'],
	'/',
	['Style','FontFormat','FontName','FontSize'],
	['TextColor','BGColor'],
	['About']
  ];  
  
 /*
 * plugin for save in lenya
 */  
FCKConfig.Plugins.Add( 'fck_lenya_glue', 'de,en', '../../plugins/' ) ;

/*
 * disable uploads since it needs to be done from within Lenya
 */
FCKConfig.ImageUpload = false;
FCKConfig.LinkUpload  = false;
FCKConfig.FlashUpload = false;

/*
 * disable server browsing since we now use plugins for that
 */
FCKConfig.ImageBrowser = false;
FCKConfig.LinkBrowser = false;

