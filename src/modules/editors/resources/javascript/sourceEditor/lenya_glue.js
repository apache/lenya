org.apache.lenya.editors.setObjectData = function(objectData, windowName) {
  var currentUsecase = usecaseMap[windowName];
  var snippet = org.apache.lenya.editors.generateContentSnippet(currentUsecase, objectData);
  org.apache.lenya.editors.insertContent(
    document.forms['oneform'].elements['content'], 
    snippet
  );
  usecaseMap[windowName] = undefined; // we're done!
  objectData[windowName] = undefined; // we're done!
}

org.apache.lenya.editors.getObjectData = function(windowName) {
  return objectData[windowName];
}

function triggerUsecase(usecase) {
  var windowName = org.apache.lenya.editors.generateUniqueWindowName();
  var selectedText = org.apache.lenya.editors.getSelectedText(document.forms[0].elements['content']);
  switch (usecase) {

    case org.apache.lenya.editors.USECASE_INSERTLINK:
      objectData[windowName] = new org.apache.lenya.editors.ObjectData({
        url   : "",
        text  : selectedText,
        title : ""
      });
      break;

    case org.apache.lenya.editors.USECASE_INSERTIMAGE:
      objectData[windowName] = new org.apache.lenya.editors.ObjectData({
        url   : "",
        text  : selectedText,
        title : "",
        width : "",
        height: ""
      });
      break;

    case org.apache.lenya.editors.USECASE_INSERTASSET:
      objectData[windowName] = new org.apache.lenya.editors.ObjectData({
        url   : "",
        text  : selectedText,
        title : ""
      })
      break;
  }
  org.apache.lenya.editors.openUsecaseWindow(usecase, windowName);
  usecaseMap[windowName] = usecase;
  /*  alert("Stored values for new window " + windowName + ":\n"
      + "objectData[windowName] = '" + objectData[windowName] + "'\n"
      + "usecaseMap[windowName] = '" + usecaseMap[windowName] + "'"
  ); */ 
}

var objectData = new Array();
var usecaseMap = new Array();
