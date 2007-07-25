// create a domain-based "package" to keep the global namespace clean
var org;
if (!org) org = {};
if (!org.apache) org.apache = {};
if (!org.apache.lenya) org.apache.lenya = {};
if (!org.apache.lenya.editors) org.apache.lenya.editors = {};


/**
  * defines the interface between generic editor usecases and editor implementations
  *
  * The idea is to use the same data structure for links, images and assets.
  * Thus, insertLink, insertImage and insertAsset can all share most of the javascript code.
  *
  * FIXME: objectData is an exceptionally stupid term. Please fix if you can think of 
  * something that encompasses "data for to-be-inserted links, images and assets in general".
  */
org.apache.lenya.editors.objectDataTemplate = {
  url    : "",  // href for links, src for assets and images
  title  : "",  // xhtml title attribute
  text   : "",  // element content for links and assets, alt text for images
  width  : "",  // width for images
  height : "",  // height for images
  type   : "",  // MIME Type for images and assets.
/* probably not necessary now that we have windowName...
  editorID : "" // an optional field that can be used by the calling editor instance
                // to identify the transaction in case multiple usecases are triggered
                // at the same time or multiple editors are active on one page.
*/
} 

/**
  * set objectData object in editor
  *
  * This callback must be implemented in the editor window.
  * @param objectData a data object as defined by objectDataTemplate
  * @param windowName the ID of the usecase window (window.name).
  * @see org.apache.lenya.editors.objectDataTemplate
  */
org.apache.lenya.editors.setObjectData = undefined;


/**
  * get objectData object from editor
  *
  * This callback must be implemented in the editor window.
  * @param windowName the ID of the usecase window (window.name).
  * @returns an objectData object.
  * @see org.apache.lenya.editors.objectDataTemplate
   */
org.apache.lenya.editors.getObjectData = undefined;


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
  for (var i in org.apache.lenya.editors.objectDataTemplate) {
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
  var objectData = new Object();
  for (var i in org.apache.lenya.editors.objectDataTemplate) {
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
  * a cross-browser helper to insert data at the selected position in a form field (textarea etc.)
  *
  * @param formElement a XHTML form element (document.forms[foo].bar)
  * @param beforeSelection the text snippet that should be inserted before the selected text
  * @param afterSelection the text snippet that should be inserted after the selected text
  * @param replaceSelection (optional) replaces the currently selected text (retained if absent)
  *
  * inspired by http://aktuell.de.selfhtml.org/artikel/javascript/bbcode/
  */
org.apache.lenya.editors.insertContent = function(
    formElement, 
    beforeSelection, 
    afterSelection, 
    replaceSelection) {

  /* alert("beforeSelection: '" + beforeSelection + "'\n"
      + "afterSelection: '" + afterSelection + "'\n"
      + "replaceSelection: '" + replaceSelection + "'\n"
  );*/
 
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
      + (beforeSelection ? beforeSelection : "")
      + (replaceSelection ? replaceSelection : selection)
      + (afterSelection ? afterSelection : "") 
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
    range.text = (beforeSelection ? beforeSelection : "")
      + (replaceSelection ? replaceSelection : selection)
      + (afterSelection ? afterSelection : "");
    range.select();
  } else {
  // for all other browsers, paste the stuff at the end...
    alert("Hey, what kind of browser is this? Please get in touch with dev@lenya.apache.org to make this feature work properly for you!");
    formElement.value = formElement.value
      + (beforeSelection ? beforeSelection : "")
      + (replaceSelection ? replaceSelection : selection)
      + (afterSelection ? afterSelection : "");
  }
}
