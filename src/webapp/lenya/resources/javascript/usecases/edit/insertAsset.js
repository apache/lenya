   ext = '';
   
   function insertImage(nodeid) { 
      // var link = document.forms['image'].link.value;
      var link = '';
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value;
      var type = document.forms['image'].type.value;
      var content = '<object xmlns="'+window.opener.XHTMLNS+'" href="'+link+'" title="'+title+'" type="'+type+'" data="'+nodeid + '/' + src+'">'+src+'</object>'; 
      window.opener.bxe_insertContent(content,window.opener.bxe_ContextNode); 
      window.close();
   }

   function insertCaption(name, caption, type) { 
     document.forms['image'].assetName.value = name;
     document.forms['image'].caption.value = caption;
     document.forms['image'].type.value = type;
     focus(); 
   } 
