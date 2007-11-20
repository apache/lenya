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

function fckloader(requesturi, contextPath)
{
  var oFCKeditor = new FCKeditor( 'content' ) ;
  oFCKeditor.BasePath	= contextPath + '/modules/fckeditor/fckeditor/' ;
  oFCKeditor.Width="800";
  oFCKeditor.Height="700";
  oFCKeditor.Config[ "FullPage" ] = true ;
  oFCKeditor.Config[ "ProcessHTMLEntities" ] = true ;
  oFCKeditor.Config[ "ProcessNumericEntities" ] = true ;
  oFCKeditor.Config["CustomConfigurationsPath"] = contextPath + "/modules/fckeditor/javascript/fckconfig.js"  ;
  oFCKeditor.ToolbarSet = 'Lenya' ;
// the current API does not support this anymore. there is a plugin for that job now.
// with an extra html page, this function could probably be restored easily, which would be
// nice because it plugs into the rather comfortable image and link dialogs of FCK...
//  oFCKeditor.Config[ "ImageBrowserURL" ] = requesturi +'?lenya.usecase=editors.insertImage' ;
//  oFCKeditor.Config[ "LinkBrowserURL" ] = requesturi +'?lenya.usecase=editors.insertLink' ;
  oFCKeditor.ReplaceTextarea() ;
}
