org.apache.lenya.editors.setObjectData = function(objectData, windowName) {
  var usecase = usecaseMap[windowName];
  var namespace;

  switch (usecase) {
    case org.apache.lenya.editors.USECASE_INSERTLINK:
    case org.apache.lenya.editors.USECASE_INSERTIMAGE:
      namespace = "http://www.w3.org/1999/xhtml";
      break;
    // FIXME: that is a shotgun approach to getting the broken <asset> thing to run.
    // We get an asset, but it throws validation errors.
    // it needs to be thrown out and replaced by <a href="" class="lenya.asset"/> anyways.
    case org.apache.lenya.editors.USECASE_INSERTASSET:
      namespace = "http://apache.org/cocoon/lenya/page-envelope/1.0";
      break;
  }

  var snippet = org.apache.lenya.editors.generateContentSnippet(usecase, objectData, namespace);
  var selection = window.getSelection(); // this works cross-browser, because bxe's editable area is not a form field
  var content = snippet.beforeSelection 
    + (snippet.replaceSelection ? snippet.replaceSelection : selection)
    + snippet.afterSelection;
  
  // FIXME: someone with better bxe insights might want to check the options below...
  switch (usecase) {
    case org.apache.lenya.editors.USECASE_INSERTLINK:
    case org.apache.lenya.editors.USECASE_INSERTIMAGE:
      // If something was selected, it ends up in the alt attribute only, and is lost from view.
      // better to keep it in the text as well (cf. replace behaviour...)
      window.bxe_insertContent(content, window.BXE_SELECTION,window.BXE_SPLIT_IF_INLINE);
      break;
    case org.apache.lenya.editors.USECASE_INSERTASSET:
      window.bxe_insertContent(content, window.BXE_SELECTION);
      break;
  }
  usecaseMap[windowName] = undefined; // we're done!
  objectData[windowName] = undefined; // we're done!
}


org.apache.lenya.editors.getObjectData = function(windowName) {
  return objectData[windowName];
}

// 3 functions are needed because bxe's config file accepts only function names,
// no parameters.


function triggerInsertLink() {
  var objectData = new org.apache.lenya.editors.ObjectData({
    url   : "",
    text  : "",
    title : ""
  });
  triggerUsecase(org.apache.lenya.editors.USECASE_INSERTLINK, objectData);
}


function triggerInsertImage() {
  var objectData = new org.apache.lenya.editors.ObjectData({
    url   : "",
    text  : "",
    title : "",
    width : "",
    height: ""
  });
  triggerUsecase(org.apache.lenya.editors.USECASE_INSERTIMAGE, objectData);
}


function triggerInsertAsset() {
 var objectData = new org.apache.lenya.editors.ObjectData({
    url   : "",
    text  : "",
    title : ""
  });
  triggerUsecase(org.apache.lenya.editors.USECASE_INSERTASSET, objectData);

}


function triggerUsecase(usecase, data) {
  var windowName = org.apache.lenya.editors.generateUniqueWindowName();
  data.text = window.getSelection();// this works cross-browser, because bxe's editable area is not a form field 
  org.apache.lenya.editors.openUsecaseWindow(usecase, windowName);
  //store some information about the new window for later:
  usecaseMap[windowName] = usecase;
  objectData[windowName] = data;
  /* alert("Stored values for new window " + windowName + ":\n"
      + "objectData[windowName] = '" + objectData[windowName] + "'\n"
      + "usecaseMap[windowName] = '" + usecaseMap[windowName] + "'"
  ); */ 
}


var objectData = new Array();
var usecaseMap = new Array();


