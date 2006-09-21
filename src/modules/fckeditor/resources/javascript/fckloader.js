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

function fckloader(host, requesturi, contextPath)
{
  var oFCKeditor = new FCKeditor( 'content' ) ;
  oFCKeditor.BasePath	= contextPath + '/modules/fckeditor/FCKeditor/' ;
  oFCKeditor.Width="800";
  oFCKeditor.Height="700";
  oFCKeditor.Config[ "FullPage" ] = true ;
  oFCKeditor.Config[ "ProcessHTMLEntities" ] = true ;
  oFCKeditor.Config[ "BaseHref" ] = host + requesturi ;
  oFCKeditor.Config["CustomConfigurationsPath"] = contextPath + "/modules/fckeditor/javascript/fckconfig.js"  ;
  oFCKeditor.ToolbarSet = 'Lenya' ;
  oFCKeditor.Config[ "ImageBrowserURL" ] = host + requesturi +'?lenya.usecase=fckeditor.insertImage' ;
  oFCKeditor.Config[ "LinkBrowserURL" ] = host + requesturi +'?lenya.module=fckeditor&lenya.step=link-show&language=en' ;
  oFCKeditor.Config[ "EditorAreaCSS" ] = contextPath + '/default/authoring/css/page.css' ;
  oFCKeditor.ReplaceTextarea() ;
}