/**
  * updates the hidden form data whenever the user selects a new asset radiobutton.
  */
function updateData(url, text, type, size, height, width) { 
  // we store the ratio with every image to allow preview re-scaling.
  var ratio = 1;
  if (width != 0) {
    ratio = height / width;
  }
  document.forms["insertAsset"].ratio.value = ratio;

  org.apache.lenya.editors.setFormValues("insertAsset", {
    url : url,
    text : text,
    size : size,
    height : height,
    width : width,
    type : type
  });
  /*  alert("Setting hidden form data:\n"
      + "url    : " + url + "\n"
      + "text   : " + text + "\n"
      + "size   : " + size + "\n"
      + "height : " + height + "\n"
      + "width  : " + width + "\n"
      + "type   : " + type);
  */
}

/**
  * updates the height to maintain correct ratio when the user changes the width
  */
function scaleHeight(width) {
  var ratio = document.forms['insertAsset'].ratio.value;
  document.forms['insertAsset'].height.value = width * ratio;
  focus(); 
} 

/**
  * updates the width to maintain correct ratio when the user changes the height
  */
function scaleWidth(height) {
  var ratio = document.forms['insertAsset'].ratio.value;
  document.forms['insertAsset'].width.value = height * ratio;
  focus(); 
} 

window.onload = function() {
  org.apache.lenya.editors.handleFormLoad("insertAsset");
};
