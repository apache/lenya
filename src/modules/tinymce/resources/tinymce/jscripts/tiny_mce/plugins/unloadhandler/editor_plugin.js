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

/* Import plugin specific language pack 
 * this could be written in a more generic way (possibly in the editors module) */
tinyMCE.importPluginLanguagePack('unloadhandler');

var TinyMCE_UnloadHandlerPlugin = {
  getInfo : function() {
    return {
      longname : 'Unload Handler Plugin for Lenya / TinyMCE',
      author : 'lenya',
      authorurl : 'http://lenya.apache.org',
      infourl : 'http://lenya.apache.org',
      version : tinyMCE.majorVersion + "." + tinyMCE.minorVersion
    };
  },

  // Private plugin internal methods

  _beforeUnloadHandler : function() {
    var msg = tinyMCE.getLang("lang_unloadhandler_unload_msg");
    return msg;
  }
};

window.onbeforeunload = TinyMCE_UnloadHandlerPlugin._beforeUnloadHandler;

tinyMCE.addPlugin("unloadhandler", TinyMCE_UnloadHandlerPlugin);
