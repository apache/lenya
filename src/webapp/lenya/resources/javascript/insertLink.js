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

function setLink(src) { 
    url = src;
    document.forms["link"].url.value = url;
}

function insertText() { 
    var selectionContent = window.opener.getSelection().getEditableRange().toString(); 
    if (selectionContent.length != 0) { 
        document.forms["link"].text.value = selectionContent;
    } 
    focus(); 
}

function LinkRoot(doc, rootElement) {
    this.doc = doc;
    this.rootElement = rootElement;
    this.selected = null;
}

LinkRoot.prototype = new NavRoot;

LinkRoot.prototype.handleItemClick = function(item, event) {
    setLink('/' + item.href);
}

function buildTree() {
    var placeholder = document.getElementById('tree');
    var root = new LinkRoot(document, placeholder);
    root.init(PUBLICATION_ID);
    root.render();
    root.loadInitialTree(AREA, DOCUMENT_ID);
}

var url;
window.onload = insertText
