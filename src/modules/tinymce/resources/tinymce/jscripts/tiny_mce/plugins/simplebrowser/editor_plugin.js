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

(function() {
    tinymce.create('tinymce.plugins.SimpleBrowserPlugin',{
	options : {},

        init : function(ed, url) {
		var t = this;
		TinyMCE_SimpleBrowserPlugin = t;
		t.editor = ed;

		ed.settings.file_browser_callback = this.browse;

		// settings
		tinymce.each({
			openServerBrowser : this.openServerBrowser,
			width : ed.getParam("plugin_simplebrowser_width", '800'),
			height : ed.getParam("plugin_simplebrowser_height", '600'),
			browseimageurl : ed.getParam("plugin_simplebrowser_browseimageurl", false),
			browselinkurl : ed.getParam("plugin_simplebrowser_browselinkurl", false),
			browseflashurl : ed.getParam("plugin_simplebrowser_browseflashurl", false),
		      }, function(value, key) {
			key = 'simplebrowser_' + key;
			
			if (ed.settings[key] === undefined)
			    ed.settings[key] = value;
		 });

	    },

	openServerBrowser : function(field_name, current_url, link_type, win, browse_url)
		{
			this.options['field'] = field_name;
			this.options['curl'] = current_url;
			this.options['type'] = link_type;
			this.options['target'] = win;

			var sOptions = "toolbar=no,scrollbars=yes,status=no,resizable=yes,dependent=yes";
			sOptions += ",width=" + this.options['width'];
			sOptions += ",height=" + this.options['height'];
			if (tinymce.isIE)	{
			    // The following change has been made otherwise IE will open the file 
			    // browser on a different server session (on some cases):
			    // http://support.microsoft.com/default.aspx?scid=kb;en-us;831678
			    // by Simone Chiaretta.
			    var oWindow = window.open(browse_url, "TinyMCESimpleBrowserWindow", sOptions ) ;
			    oWindow.opener = window;
			} else {
			    window.open(browse_url, "TinyMCESimpleBrowserWindow", sOptions );
			}
		},

	browse : function(field_name, current_url, type, win) {

		var ed = tinyMCE.activeEditor;
		var pl = ed.plugins['simplebrowser'];

		switch(type.toLowerCase()) {
			case 'image':
				if(ed.settings['simplebrowser_browseimageurl']) {
					pl.openServerBrowser(field_name, current_url, type, win, ed.settings['simplebrowser_browseimageurl']);
				} else {
					alert("Image browser URL not set.");
				}
				break;
			case 'flash':
				if(ed.settings['simplebrowser_browseflashurl']) {
					pl.openServerBrowser(field_name, current_url, type, win, ed.settings['simplebrowser_browseflashurl']);
				} else {
					alert("Flash browser URL not set.");
				}
				break;
			default:
				if(ed.settings['simplebrowser_browselinkurl']) {
					pl.openServerBrowser(field_name, current_url, type, win, ed.settings['simplebrowser_browselinkurl']);
				} else {
					alert("Link browser URL not set.");
				}
		}
	},

	browserCallback : function(returnValue) {
		if(!returnValue) return;
		TinyMCE_SimpleBrowserPlugin.options['target'].document.forms[0].elements[TinyMCE_SimpleBrowserPlugin.options['field']].value = returnValue.url;
		
		// the following checks are necessary because this script is used
		// for image insertion AND link insertion (where the returnValue.width (height and title)  are null)
		if (returnValue.width != null) {
		    TinyMCE_SimpleBrowserPlugin.options['target'].document.forms[0].elements.width.value = returnValue.width;
		}
		if (returnValue.height != null) {
		    TinyMCE_SimpleBrowserPlugin.options['target'].document.forms[0].elements.height.value = returnValue.height;
		}
		if ((returnValue.title != null) && (TinyMCE_SimpleBrowserPlugin.options['target'].document.forms[0].elements.alt != null)) {    		
    		TinyMCE_SimpleBrowserPlugin.options['target'].document.forms[0].elements.alt.value = returnValue.title;
		}

	},

	getInfo : function() {
		return {
		    longname : 'Simple Browser Plugin for Lenya / TinyMCE',
		    author : 'lenya',
		    authorurl : 'http://lenya.apache.org',
		    infourl : 'http://lenya.apache.org',
		    version : '2.0.X'
		       };
	}

	});

    tinymce.PluginManager.add('simplebrowser', tinymce.plugins.SimpleBrowserPlugin);

})();

