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

tinyMCE.importPluginLanguagePack('simplebrowser','en');

var TinyMCE_SimpleBrowserPlugin={options:{},getInfo:function(){
return{longname:'Simple Browser plugin',
       author:'',
       authorurl:'',
       infourl:'',
       version:""};
},initInstance:function(inst){
    tinyMCE.settings['file_browser_callback']="TinyMCE_SimpleBrowserPlugin_browse";
    TinyMCE_SimpleBrowserPlugin.options={width:tinyMCE.getParam("plugin_simplebrowser_width",'800'),height:tinyMCE.getParam("plugin_simplebrowser_height",'600'),browseimageurl:tinyMCE.getParam("plugin_simplebrowser_browseimageurl",false),browselinkurl:tinyMCE.getParam("plugin_simplebrowser_browselinkurl",false),browseflashurl:tinyMCE.getParam("plugin_simplebrowser_browseflashurl",false)}},browse:function(field_name,current_url,type,win){switch(type.toLowerCase()){case'image':if(TinyMCE_SimpleBrowserPlugin.options['browseimageurl']){TinyMCE_SimpleBrowserPlugin.openServerBrowser(field_name,current_url,type,win,TinyMCE_SimpleBrowserPlugin.options['browseimageurl']);}else{alert("Image browser URL not set.");}break;case'flash':if(TinyMCE_SimpleBrowserPlugin.options['browseflashurl']){TinyMCE_SimpleBrowserPlugin.openServerBrowser(field_name,current_url,type,win,TinyMCE_SimpleBrowserPlugin.options['browseflashurl']);
    }else{
    alert("Flash browser URL not set.");}break;default:if(TinyMCE_SimpleBrowserPlugin.options['browselinkurl']){TinyMCE_SimpleBrowserPlugin.openServerBrowser(field_name,current_url,type,win,TinyMCE_SimpleBrowserPlugin.options['browselinkurl']);}else{alert("Link browser URL not set.");}}},openServerBrowser:function(field_name,current_url,link_type,win,browse_url){TinyMCE_SimpleBrowserPlugin.options['field']=field_name;TinyMCE_SimpleBrowserPlugin.options['curl']=current_url;TinyMCE_SimpleBrowserPlugin.options['type']=link_type;TinyMCE_SimpleBrowserPlugin.options['target']=win;var sOptions="toolbar=no,status=no,resizable=yes,dependent=yes";sOptions+=",width="+TinyMCE_SimpleBrowserPlugin.options['width'];sOptions+=",height="+TinyMCE_SimpleBrowserPlugin.options['height'];if(tinyMCE.isMSIE){var oWindow=window.open(browse_url,"TinyMCESimpleBrowserWindow",sOptions);oWindow.opener=window;}else{window.open(browse_url,"TinyMCESimpleBrowserWindow",sOptions);}},browserCallback:function(returnValue){if(!returnValue)return;TinyMCE_SimpleBrowserPlugin.options['target'].document.forms[0].elements[TinyMCE_SimpleBrowserPlugin.options['field']].value=returnValue;}};function TinyMCE_SimpleBrowserPlugin_browse(field_name,current_url,type,win){TinyMCE_SimpleBrowserPlugin.browse(field_name,current_url,type,win)};tinyMCE.addPlugin("simplebrowser",TinyMCE_SimpleBrowserPlugin);
    