/* Show a preview of an image to be uploaded */
function imagepreview(obj) {
   prev = document.getElementById('preview');
   prev.style.visibility = 'hidden';
   var i = 0;
   var delimiter = ' '; 
   var imageext = 'gif jpg jpeg png';
   var isimage = false;
   var _tempArray = new Array();
   _tempArray = imageext.split(delimiter);
   for(i in _tempArray) { 
     if(obj.value.indexOf('.' + _tempArray[i]) != -1) { // file is an image. 
       isimage = true; 
     } 
   } 
   if (isimage) { 
     prev.setAttribute('src',obj.value);
     prev.style.visibility = 'visible';
   }
}
            
