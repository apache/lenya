// document lookup for links

objLookupWindow = null;
objTextArea = null;
objReferenceFormat = null;
objAppendValue = false;

function openObjectLookupWindow(url_to_open, folder_context) {
  // open the lookup window, will be called by getObjectReference
  url_to_open = url_to_open + '?folder_context=' + folder_context;
  width = 760;
  height = 500;
  leftPos = (screen.width - width) / 2;
  topPos = (screen.height - height) / 2;
  aspects = 'toolbar=yes,status=yes,scrollbars=yes,resizable=yes,width=' + width + ',height=' + height + ',left=' + leftPos + ',top=' + topPos;
  objLookupWindow = window.open(url_to_open, 'ObjectLookupWindow', aspects);
  objLookupWindow.focus();
}

function getObjectReference(url, format, folder_context) {
  // should be called by pagetemplate
  referenceFormat = format;
  openObjectLookupWindow(url, folder_context);
}

function insertObjectReference(id, reference, wndw) {
  // Is called by the window if the user selected an object
  var text = referenceFormat.replace('_id_', id).replace('_reference_', reference);
  var linktool = kupu.getTool('linktool');
  linktool.createLink(text);
  wndw.close();
}

// Image asset lookup

lookupWindow = null;
textArea = null;
referenceFormat = null;
appendValue = false;
// Called from getAssetReference
function openAssetLookupWindow(url_to_open, folder_context) {
  // Do the opening
  url_to_open = url_to_open + '&folder_context=' + folder_context;
  width = 760;
  height = 500;
  leftPos = (screen.width - width) / 2;
  topPos = (screen.height - height) / 2;
  aspects = 'toolbar=yes,status=yes,scrollbars=yes,resizable=yes,width='+width+',height='+height+',left='+leftPos+',top='+topPos;
  lookupWindow = window.open(
    url_to_open, 'AssetLookupWindow', aspects);
  lookupWindow.focus();
}
// Called from "asset lookup window"
function insertAssetReference(id, reference, wndw) {
  // for Kupu we need to place the image as well
  var imagetool = kupu.getTool('imagetool');

  imagetool.createImage(reference);
  // Close LookupWindow
  wndw.close();
}

