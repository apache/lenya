   ext = '';
   
   function insertAsset(nodeid) {
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value;
      var type = document.forms['image'].type.value;
      var size = document.forms['image'].assetSize.value;
      window.top.opener.TinyMCE_SimpleBrowserPlugin.browserCallback(src) ;
	  window.top.close() ;
    }
   
   function insertImage(nodeid) { 
      // var link = document.forms['image'].link.value;
      var link = '';
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value;
      var type = document.forms['image'].type.value;
      var height = document.forms['image'].height.value;
      var width = document.forms['image'].width.value;
      var content = '<object xmlns="'+window.opener.XHTMLNS+'" href="'+link+'" title="'+title+'" type="'+type+'" data="'+src+'" height="'+height+'" width="'+width+'">'+src+'</object>'; 
      window.top.opener.TinyMCE_SimpleBrowserPlugin.browserCallback(src) ;
	  window.top.close() ;
   }

   function insertCaption(name, caption, type, size) { 
     document.forms['image'].assetName.value = name;
     document.forms['image'].caption.value = caption;
     document.forms['image'].type.value = type;
     document.forms['image'].assetSize.value = size;
     focus(); 
   } 

   function insertData(name, caption, type, size, height, width) { 
     var ratio = 1;
     if (width != 0) {
       ratio = height / width;
     }
     document.forms['image'].assetName.value = name;
     document.forms['image'].caption.value = caption;
     document.forms['image'].type.value = type;
     document.forms['image'].assetSize.value = size;
     document.forms['image'].height.value = height;
     document.forms['image'].width.value = width;
     document.forms['image'].ratio.value = ratio;
     focus(); 
   } 

   function scaleHeight(width) {
     var ratio = document.forms['image'].ratio.value;
     document.forms['image'].height.value = width * ratio;
     focus(); 
   } 
  
   function scaleWidth(height) {
     var ratio = document.forms['image'].ratio.value;
     document.forms['image'].width.value = height * ratio;
     focus(); 
   } 
  