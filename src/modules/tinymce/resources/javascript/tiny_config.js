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

/* 
these are the configuration settings for TinyMCE. a complete list 
of options can be found in the excellent manual at
http://tinymce.moxiecode.com/tinymce/docs/reference_configuration.html
*/


currentURL = location.href.replace(/\?.*$/,"");
  
 function cleanup(type, value) {
   switch (type) {
       case "get_from_editor":
           value = value.replace(/<a (class="mceItemAnchor" )?name="([^"]+)"/gi,'<a $1id="$2"');
           break;
       case "insert_to_editor":
           value = value.replace(/<a (class="mceItemAnchor" )?id="([^"]+)"/gi,'<a $1name="$2"');
           break;
       case "submit_content":
           // Do custom cleanup code here
           break;
   }
 
   return value;
}
 
config = {
  
    cleanup_callback : "cleanup",
    /* enable customizable theme */
    theme : "advanced",
    
    /* only the element whose ID is listed under "elements" will be editable */
    mode : "exact",
    elements : "tinymce.content",
    
    /* enable plugins for custom save function, fullscreen editing
       and search-and-replace. */
    //plugins : "save,fullscreen,searchreplace,table,contextmenu,autoresize,simplebrowser,xhtmlxtras",
    plugins : "searchreplace,table,contextmenu,autoresize,simplebrowser,unloadhandler",
        
    /* grey out the "save" button unless there are unsaved changes: */
    //save_enablewhendirty : false,
    
    /* disable editing of attributes forbidden by lenya schema */
    /* TyniMCE 3.x */
    popup_css_add : "../modules/tinymce/css/lenya_dialog.css",

    /* some special settings for fullscreen mode (they override the 
       settings further down) */
/*
    fullscreen_new_window : true,

    fullscreen_settings : {
        auto_resize : false,
        theme_advanced_toolbar_location : "top",
        theme_advanced_statusbar_location : "bottom"
    },
*/    
    /* auto-resize the editing area. docs say this is "experimental"! */
    auto_resize : true,
    
    /* characters that should be replaced by named XHTML entities.
       cocoon does not define entities by default, so we use none. */
    entities : "",
    
    /* do not make unicode references, output all special characters unchanged. */
    entity_encoding : "raw",
    
    /* keep linebreaks, don't put everything on one line. this is 
       important for advanced users who want to use the source editor. */
    remove_linebreaks : false,
    
    /* prettyprint sourcecode on saving FIXME: test. does this do anything? */
    apply_source_formatting : true,
    
    /* if the user inserts a table into a p, split p to create correct code.
       for nested lists, the inner list is correctly placed in a &lt;li/&gt; */
    fix_list_elements: true,
    fix_table_elements: true,
    
    /* force all stray text nodes into a <p/> element. */
    forced_root_block: "p",
    
    /* let tinymce do the layout by itself */
    theme_advanced_layout_manager : "SimpleLayout",
    
    /* the toolbar is placed at the top of the editable area. */
    theme_advanced_toolbar_location : "top",
    
    /* the statusbar is placed at the bottom. it displays the element path. */
    theme_advanced_statusbar_location : "bottom",
    
    /* the following items define the function buttons your users get to see.
       for a complete list, see
       http://wiki.moxiecode.com/index.php/TinyMCE:Control_reference */
    
    theme_advanced_buttons1 :
            "save,undo,redo,search,replace,separator," + 
            "cleanup,code,charmap,visualaid,fullscreen,separator," + 
            "formatselect,styleselect",
    
    theme_advanced_buttons2 : 
            "bold,italic,underline,strikethrough,sub,sup,removeformat,separator," +
            "bullist,numlist,table,separator," + 
            "indent,outdent,separator," + 
            "anchor,link,unlink,image,separator," +
            "help",
    
    /* three button rows are active by default. override. */
    theme_advanced_buttons3 : "",
/*
    //for xhtmlxtras, use this:
    theme_advanced_buttons3 : "cite,ins,del,abbr,acronym,attribs",
*/    
    /* the following items define the elements presented in the "Format" dropdown. 
       the default setting is very restrictive to enforce a corporate design and clean
       code at the expense of flexibility. */
    theme_advanced_blockformats : "p,h1,h2,h3,h4,h5,h6",
    
    
    /*    Lenya/TinyMCE glue code    */

    /* the simplebrowser plugin is used to wire the generic editors.insertFOO usecases into TinyMCE */
    plugin_simplebrowser_browselinkurl : currentURL + '?lenya.usecase=editors.insertLink',
    plugin_simplebrowser_browseimageurl : currentURL + '?lenya.usecase=editors.insertImage',
    plugin_simplebrowser_browseflashurl : '',
    
    /* this callback can override arbitrary tinymce commands. neato!
       currently used hooks are "insert image", "insert link" and "save". 
       the handler sits in tiny_lenya_glue.js. */
    execcommand_callback : "LenyaExecCommandHandler",
    
    /* insert custom save handler to do some extra clean-up. */
    save_callback : "LenyaSaveContent"
};
