org.apache.lenya.editors.setObjectData = function(objectData, windowName) {
  var currentUsecase = usecaseMap[windowName];
  // alert("windowName: "+ windowName);
  var beforeSelection, afterSelection, replaceSelection;
  
  switch (currentUsecase) {
    case "insertLink":
      beforeSelection = '<a'
        + (objectData.url ? ' href="' + objectData.url + '"' : '')
        + (objectData.title ? ' title="' + objectData.title + '"' : '')
        + '>';
      afterSelection = '</a>';
      replaceSelection =
        objectData.text ? objectData.text : undefined;
      break;
    case "insertImage":
      beforeSelection = '<img'
        + (objectData.url ? ' src="' + objectData.url + '"' : '')
        + (objectData.title ? ' title="' + objectData.title + '"' : '')
        + (objectData.text ? ' alt="' + objectData.title + '"' : '')
        + '/>';
      afterSelection = undefined;
      replaceSelection = undefined;
      break;
    case "insertAsset":
      beforeSelection = '<a'
        + (objectData.url ? ' href="' + objectData.url + '"' : '')
        + (objectData.title ? ' title="' + objectData.title + '"' : '')
        + ' class="asset">';
      afterSelection = '</a>';
      replaceSelection =
        objectData.text ? objectData.text : undefined;
      break;
    default:
      alert("setObjectData: Unknown usecase " + currentUsecase + ". This is likely a programming error.");
      return;
  }
  org.apache.lenya.editors.insertContent(
    document.forms['oneform'].elements['content'], 
    beforeSelection, 
    afterSelection, 
    replaceSelection
  );
  usecaseMap[windowName] = undefined;
  objectData[windowName] = undefined;
}

org.apache.lenya.editors.getObjectData = function(windowName) {
  return objectData[windowName];
}

function triggerUsecase(usecase) {
  var windowName = org.apache.lenya.editors.generateUniqueWindowName();
  org.apache.lenya.editors.openUsecaseWindow(usecase, windowName);
  objectData[windowName] = {
    url   : "",
    text  : "",
    title : ""
  } 
  usecaseMap[windowName] = usecase;
  /* alert("Stored values:" 
      + "\n windowName = '" + windowName 
      + "'\n objectData[windowName] = '" + objectData[windowName] 
      + "'\n usecaseMap[windowName] = '" + usecaseMap[windowName] 
      + "'"); 
  */
}

var objectData = new Array();
var usecaseMap = new Array();
