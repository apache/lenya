   ext = '';
   
   function insertAsset(nodeid) {
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value;
      window.opener.bxe_insertContent('<asset xmlns="http://apache.org/cocoon/lenya/page-envelope/1.0" src="'
          + src + '">'+title+'</asset>',window.opener.BXE_SELECTION,window.opener.BXE_SPLIT_IF_INLINE);
      window.close();
    }
   
   function insertImage(nodeid) { 
      // var link = document.forms['image'].link.value;
      var link = '';
      var src = document.forms['image'].assetName.value;
      var title = document.forms['image'].caption.value;
      var type = document.forms['image'].type.value;
      var height = document.forms['image'].height.value;
      var width = document.forms['image'].width.value;
      var content = '<object xmlns="'+window.opener.XHTMLNS+'" href="'+link+'" title="'+title+'" type="'+type+'" data="'+src+'" height="'+height+'" width="'+width+'">'+title+'</object>'; 
      window.opener.bxe_insertContent(content,window.opener.bxe_ContextNode); 
      window.close();
   }

