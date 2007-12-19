/**
  * updates the hidden form data whenever the user selects a new asset radiobutton.
  */
function updateData(url, title, height, width, type) { 
  
  var text = document.forms["insertAsset"].text.value;
  if (text && text != document.forms["insertAsset"].title.value) {
    // if the user has provided an entry for "text" 
    //    (other than a copy of "title"), use that:
    text  = document.forms["insertAsset"].text.value;
  } else {
    // otherwise just copy the title value:
    text = title;
  }
  // we store the ratio with every image for correct re-scaling.
  var ratio = 1;
  if (width != 0) {
    ratio = height / width;
  }
  document.forms["insertAsset"].ratio.value = ratio;

  var objectData = new org.apache.lenya.editors.ObjectData({
    url    : url,
    title  : title,
    text   : text,
    height : height,
    width  : width,
    type   : type
  });
  //alert("Setting form data:" + objectData.toString());
  org.apache.lenya.editors.setFormValues("insertAsset", objectData);
}

/**
  * updates the height to maintain correct ratio when the user changes the width
  */
function scaleHeight(width) {
  var ratio = document.forms['insertAsset'].ratio.value;
  document.forms['insertAsset'].height.value = Math.round(width * ratio);
  focus(); 
} 

/**
  * updates the width to maintain correct ratio when the user changes the height
  */
function scaleWidth(height) {
  var ratio = document.forms['insertAsset'].ratio.value;
  document.forms['insertAsset'].width.value = Math.round(height * 1.0 / ratio);
  focus(); 
} 

window.onload = function() {
  org.apache.lenya.editors.handleFormLoad("insertAsset");
};
