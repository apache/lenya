/* 
these are the configuration settings for TinyMCE. a complete list 
of options can be found in the excellent manual at
http://tinymce.moxiecode.com/tinymce/docs/reference_configuration.html
*/
  
    config = {

    /* enable customizable theme */
    theme    : "advanced",

    /* only the element whose ID is listed under "elements" will be editable */
    mode     : "exact",
    elements : "tinymce.content",

    /* FIXME: how do I get the user's language preference for authoring? */
    language : "en",

    /* enable plugins for custom save function, fullscreen editing
      and search-and-replace. */
    plugins  : "save,fullscreen,searchreplace,table,contextmenu",

    /* grey out the "save" button unless there are unsaved changes: */
    save_enablewhendirty : true,

    /* some special settings for fullscreen mode (they override the 
      settings further down) */
    fullscreen_settings : {
            auto_resize : false,
            theme_advanced_toolbar_location : "top",
            theme_advanced_statusbar_location : "bottom"
    },

    /* auto-resize the editing area. docs say this is "experimental"! */
    auto_resize : true,

    /* characters that should be replaced by named XHTML entities.
       cocoon does not define entities by default, so we use none. */
    entities        : "",

    /* do not make unicode references, output all special characters unchanged. */
    entity_encoding : "raw",

    /* keep linebreaks, don't put everything on one line. this is 
       important for advanced users who want to use the source editor. */
    remove_linebreaks       : false,

    /* prettyprint sourcecode on saving FIXME: test. does this do anything? */
    apply_source_formatting : true,

    /* if the user inserts a table into a p, split p to create correct code.
       for nested lists, the inner list is correctly placed in a &lt;li/&gt;
    fix_list_elements: true;
    fix_table_elements: true;

    /* let tinymce do the layout by itself */
    theme_advanced_layout_manager     : "SimpleLayout",

    /* the toolbar is placed at the top of the editable area. */
    theme_advanced_toolbar_location   : "top",

    /* the statusbar is placed at the bottom. it displays the element path. */
    theme_advanced_statusbar_location : "bottom",

    /* the following items define the function buttons your users get to see.
      for a complete list, see
      http://tinymce.moxiecode.com/tinymce/docs/reference_buttons.html */

    theme_advanced_buttons1 :
            "save,undo,redo,search,replace,separator," + 
            "cleanup,code,charmap,visualaid,fullscreen,separator," + 
            "formatselect,styleselect",

    theme_advanced_buttons2 : 
            "bold,italic,underline,strikethrough,sub,sup,removeformat,separator," +
            "bullist,numlist,table,separator," + 
            "indent,outdent,separator," + 
            "link,unlink,image,separator," +
            "help",

    /* three button rows are active by default. override. */
    theme_advanced_buttons3 : "",

    /* the following items define the allowed elements and attributes on your site. 
      the default setting is very restrictive to enforce a corporate design and clean
      code at the expense of flexibility. */
    theme_advanced_blockformats       : "p,h1,h2,h3,h4",


/*
Lenya/TinyMCE glue code
*/

    /* this callback can override arbitrary tinymce commands. neato!
       currently used hooks are "insert image", "insert link" and "save". 
       the handler sits in tiny_lenya_glue.js. */
    execcommand_callback : "LenyaExecCommandHandler",

    /* insert custom save handler to do some extra clean-up. */
    save_callback : "LenyaSaveContent"
    };

