function insertLink() { 
    var text = document.forms["link"].text.value;
    var title = document.forms["link"].title.value;
    var prefix = '/' + PUBLICATION_ID + '/' + AREA;
    var url = document.forms["link"].url.value;
    if (url.charAt(0) == "/") {
     // prepend hostname etc for internal links
     url = prefix + url;
    }
    var content = '<a xmlns="'+window.opener.XHTMLNS+'" href="'+url+'" title="'+title+'">'+text+'</a>'; 
    window.opener.bxe_insertContent(content,window.opener.bxe_ContextNode); 
    window.close();
}

