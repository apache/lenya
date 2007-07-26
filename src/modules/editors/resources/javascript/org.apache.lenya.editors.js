// create a domain-based "package" to keep the global namespace clean
var org;
if (!org) org = new Object();
if (!org.apache) org.apache = new Object();
if (!org.apache.lenya) org.apache.lenya = new Object();
if (!org.apache.lenya.editors) org.apache.lenya.editors = new Object();


/**
  * ObjectData constructor, the interface between generic editor usecases
  * and editor modules.
  *
  * The idea is to use the same data structure for links, images and assets.
  * Thus, insertLink, insertImage and insertAsset can all share most of the javascript code.
  *
  * FIXME: objectData is an exceptionally stupid term. Please fix if you can think of 
  * something that encompasses "data for to-be-inserted links, images and assets in general".
  *
  * @param an optional hash map of initial values
  */
org.apache.lenya.editors.ObjectData = function(init) {
  if (init) {
    for (var i in this) {
      if (typeof this[i] == "function") continue; // skip the methods!
      //alert("Checking this[" + i + "], init[" + i + "] is '" + init[i] + "'.");
      this[i] = init[i];
    }
  //alert("Created new ObjectData = " + this.toString());
  }
}


/**
  * href for links, src for assets and images
  */
org.apache.lenya.editors.ObjectData.prototype.url = undefined;

/**
  * XHTML title attribute:
  */
org.apache.lenya.editors.ObjectData.prototype.title = undefined;

/**
  * element content for links and assets, alt text for images: 
  */
org.apache.lenya.editors.ObjectData.prototype.text = undefined;

/**
  * width for images
  */
org.apache.lenya.editors.ObjectData.prototype.width = undefined;

/**
  * height for images
  */
org.apache.lenya.editors.ObjectData.prototype.height = undefined;

/** 
  * MIME Type for images and assets.
  */
org.apache.lenya.editors.ObjectData.prototype.type = undefined;


/**
  * Utility function to ease debugging. Will dump all fields in
  * a human-readable fashion, including their types.
  */
org.apache.lenya.editors.ObjectData.prototype.toString = function() {
  var s = "\n";
  for (var i in this) {
    if (typeof this[i] != "function") {
      s += "\t" + i + ": [" + this[i] + "] (" + typeof this[i] + ")\n";
    }
  } 
  return s;
}


/**
  * set objectData object in editor
  *
  * This callback must be implemented in the editor window.
  * It will be called when the usecase has completed successfully 
  * and make the obtained data available to your editor.
  * Here you must implement the necessary code to either add the
  * data to your editor area directly (you may want to use 
  * org.apache.lenya.editors.generateContentSnippet  and 
  * org.apache.lenya.editors.insertContent as helpers), or
  * to fill the values into some editor-specific dialog.
  * 
  * @param objectData a data object as defined by objectDataTemplate
  * @param windowName the ID of the usecase window (window.name).
  * @see org.apache.lenya.editors.objectDataTemplate
  */
org.apache.lenya.editors.setObjectData = function(objectData, windowName) {
  alert("Programming error:\n  You must override org.apache.lenya.editors.setObjectData(objectData, windowName)!");
};


/**
  * get objectData object from editor
  *
  * This callback must be implemented in the editor window. 
  * The usecase will query your editor for an objectData object, which
  * it will use to fill form fields with default values (if provided).
  * All form fields whose values in objectData are undefined will be 
  * deactivated, so that your editor can handle them.
  *
  * Usually, default values are based on selected text or user settings.
  * 
  * @param windowName the ID of the usecase window (window.name).
  * @returns an objectData object.
  * @see org.apache.lenya.editors.objectDataTemplate
   */
org.apache.lenya.editors.getObjectData = function(windowName) {
  alert("Programming error:\n  You must override org.apache.lenya.editors.getObjectData(windowName)!");
};


/**
  * sets default values of the usecase form
  *
  * The form field names must correspond to the properties of
  * objectDataTemplate.
  * Note: if a value in objectData is undefined (as opposed to ""),
  * the corresponding form field will be disabled (greyed out).
  * Editors should use this to deactivate properties they wish
  * to handle themselves.
  * @param formName the "name" attribute of the form
  * @param objectData 
  * @see org.apache.lenya.editors.objectDataTemplate
  */
org.apache.lenya.editors.setFormValues = function(formName,objectData) {
  var form = document.forms[formName];
  for (var i in org.apache.lenya.editors.ObjectData.prototype) {
    if (form[i] !== undefined) {
      if (objectData[i] !== undefined) {
        form[i].value = objectData[i];
      } else {
        form[i].disabled = true;
        form[i].title = "disabled by editor";
      }
    }
  } 
}


/**
  * reads the values from the usecase form
  *
  * The form field names must correspond to the properties of
  * objectDataTemplate.
  * @param formName the "name" attribute of the form
  * @returns objectData
  */
org.apache.lenya.editors.getFormValues = function(formName) {
  var form = document.forms[formName];
  var objectData = new org.apache.lenya.editors.ObjectData();
  for (var i in org.apache.lenya.editors.ObjectData.prototype) {
    if (form[i] !== undefined) {
      objectData[i] = form[i].value;
    }
  }
  return objectData;
}


/**
  * handle the submit event of the form
  *
  * @param formName the "name" attribute of the form
  */
org.apache.lenya.editors.handleFormSubmit = function(formName) {
  var objectData = org.apache.lenya.editors.getFormValues(formName);
  window.opener.org.apache.lenya.editors.setObjectData(objectData, window.name);
  window.close();
}


/**
  * handle the load event of the form
  *
  * @param formName the "name" attribute of the form
  */
org.apache.lenya.editors.handleFormLoad = function(formName) {
  var objectData = window.opener.org.apache.lenya.editors.getObjectData(window.name);
  org.apache.lenya.editors.setFormValues(formName, objectData);
}


/**
  * default attributes for usecase windows (window.open()...)
  */
org.apache.lenya.editors.usecaseWindowOptions = 
      "toolbar=no,"
    + "scrollbars=yes,"
    + "status=no,"
    + "resizable=yes,"
    + "dependent=yes,"
    + "width=600,"
    + "height=700";

org.apache.lenya.editors.generateUniqueWindowName = function() {
  return new String("windowName-" + Math.random().toString().substr(2));
}


/**
  * a helper function to open new usecase windows.
  *
  * If everyone used this, we'd save some maintenance work 
  * in the long run and can ensure consistent
  * behaviour across different editors.
  * @param usecase the name of the usecase to invoke, one of
  *   ("insertLink" | "insertImage" | "insertAsset")
  * @param windowName the name of the new window, in case the editor needs 
  *   that info later on.
  * @returns the new window object
  */
org.apache.lenya.editors.openUsecaseWindow = function(usecase, windowName) {
  var currentBaseURL;
  var usecaseWindow;

  switch (usecase) {
    case "insertLink":
    case "insertAsset": 
    case "insertImage":
      currentBaseURL = window.location.href.replace(/\?.*$/,"");
      usecaseWindow = window.open(
        currentBaseURL + "?lenya.usecase=editors." + usecase,
        windowName,
        org.apache.lenya.editors.usecaseWindowOptions
      );
      break;
    default:
      alert("openUsecaseWindow: Unknown usecase '" + usecase + "'. This is likely a programming error.");
  }
  return usecaseWindow;
}


/**
  * this data structure helps with the insertion of generated tags 
  */
org.apache.lenya.editors.ContentSnippet = function(
    beforeSelection, 
    afterSelection, 
    replaceSelection
) {
  this.beforeSelection = beforeSelection,
  this.afterSelection = afterSelection,
  this.replaceSelection = replaceSelection
}


/**
  * the characters to be inserted before the selected text
  */
org.apache.lenya.editors.ContentSnippet.prototype.beforeSelection = "";

/**
  * the characters to be inserted after the selected text
  */
org.apache.lenya.editors.ContentSnippet.prototype.afterSelection = "";

/**
  * the text to replace the currently selected area (if any)
  */
org.apache.lenya.editors.ContentSnippet.prototype.replaceSelection = undefined;

/**
  * @see org.apache.lenya.editors.ObjectData.prototype.toString
  */
org.apache.lenya.editors.ContentSnippet.prototype.toString =  
    org.apache.lenya.editors.ObjectData.prototype.toString;


/**
  * generates a content snippet to be inserted into the editor area
  * 
  * @param usecase the usecase for which the snippet should be generated
  * @param objectData an objectData object for the contents
  * @param namespace an optional namespace URI (usually http://www.w3.org/1999/xhtml)
  * @returns an object of type ContentSnippet 
  * @see org.apache.lenya.editors.ContentSnippet
  */
org.apache.lenya.editors.generateContentSnippet = function(usecase, objectData, namespace) {
  var snippet = new org.apache.lenya.editors.ContentSnippet();

  switch (usecase) {

    case "insertLink":
      snippet.beforeSelection = '<a'
        + (namespace ? ' xmlns="' + namespace + '"' : '')
        + (objectData.url ? ' href="' + objectData.url + '"' : '')
        + (objectData.title ? ' title="' + objectData.title + '"' : '')
        + '>';
      snippet.afterSelection = '</a>';
      snippet.replaceSelection =
        objectData.text ? objectData.text : undefined;
      break;

    case "insertAsset":
      snippet.beforeSelection = '<a'
        + (namespace ? ' xmlns="' + namespace + '"' : '')
        + (objectData.url ? ' href="' + objectData.url + '"' : '')
        + (objectData.title ? ' title="' + objectData.title + '"' : '')
        + ' class="asset">';
      snippet.afterSelection = '</a>';
      snippet.replaceSelection =
        objectData.text ? objectData.text : undefined;
      break;

    case "insertImage":
      snippet.beforeSelection = '<img'
        + (namespace ? ' xmlns="' + namespace + '"' : '')
        + (objectData.url ? ' src="' + objectData.url + '"' : '')
        + (objectData.title ? ' title="' + objectData.title + '"' : '')
        + (objectData.text ? ' alt="' + objectData.text + '"' : '')
        + (objectData.width ? ' width="' + objectData.width + '"' : '')
        + (objectData.height ? ' height="' + objectData.height + '"' : '')
        + '/>';
      snippet.afterSelection = "";
      snippet.replaceSelection = undefined;
      break;

    default:
      alert("setObjectData: Unknown usecase " + currentUsecase + ". This is likely a programming error.");
      return undefined;
  }
  return snippet;
}


/**
  * a cross-browser helper to obtain selected text in form elements
  *
  * @param formElement a XHTML form element (document.forms[foo].bar)
  * In IE, this parameter is disregarded, since IE uses a document-wide 
  * selection mechanism.
  * @returns the selected text or the empty string.
  */
org.apache.lenya.editors.getSelectedText = function(formElement) {
  if (formElement.selectionStart !== undefined) {
    return formElement.value.substr(
         formElement.selectionStart, 
         formElement.selectionEnd - formElement.selectionStart);
  } else 
  if (document.selection !== undefined) {
    return document.selection.createRange().text;
  } else {
    alert("Sorry, your browser apparently doesn't support text selection via javascript.");
    return "";
  }
}


/**
  * a cross-browser helper to insert data at the selected position in a form field (textarea etc.)
  *
  * @param formElement a XHTML form element (document.forms[foo].bar)
  * @param contentSnippet a org.apache.lenya.editors.ContentSnippet with the text to insert
  *
  * inspired by http://aktuell.de.selfhtml.org/artikel/javascript/bbcode/
  */
org.apache.lenya.editors.insertContent = function(formElement, snippet) {

  alert("snippet: '" + snippet.toString() + "'\n");
 
  // Danger, Will Robinson: you are leaving the w3c sector!
  // Selections are not properly standardized yet...
  formElement.focus();
  // Firefox and friends will support this for textareas etc.
  if (formElement.selectionStart !== undefined) {
    var begin = formElement.selectionStart;
    var end = formElement.selectionEnd;
    var content = formElement.value; 
    var selection = content.substring(begin, end);
    // alert("Found selection beginning at [" + begin + "], ending at [" + end + "].");
    formElement.value = content.substr(0, begin)
      + snippet.beforeSelection
      + (snippet.replaceSelection ? snippet.replaceSelection : selection)
      + snippet.afterSelection
      + content.substr(end);
    // update cursor position:
    formElement.selectionStart = begin;
    formElement.selectionEnd = begin;
  } else 
  // IE does it thusly:
  if (document.selection !== undefined) {
    alert("Hey, you are using IE, right? Please get in touch with dev@lenya.apache.org to test this feature!");
    var range = document.selection.createRange();
    var selection = range.text;
    range.text = snippet.beforeSelection
      + (snippet.replaceSelection ? snippet.replaceSelection : selection)
      + snippet.afterSelection;
    range.select();
  } else {
  // for all other browsers, paste the stuff at the end...
    alert("Hey, what kind of browser is this? Please get in touch with dev@lenya.apache.org to make this feature work properly for you!");
    formElement.value = formElement.value
      + snippet.beforeSelection
      + (snippet.replaceSelection ? snippet.replaceSelection : selection)
      + snippet.afterSelection;
  }
}
