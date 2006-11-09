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

function setLink(uuid) {
    var language = CHOSEN_LANGUAGE;
    document.forms["link"].url.value = "lenya-document:" + uuid + ",lang=" + language;
}

function insertText() { 
    var selectionContent = window.opener.getSelection().getEditableRange().toString(); 
    if (selectionContent.length != 0) { 
        document.forms["link"].text.value = selectionContent;
    } 
    focus(); 
}

function LinkTree(doc, treeElement) {
    this.doc = doc;
    this.treeElement = treeElement;
    this.selected = null;
}

LinkTree.prototype = new NavTree;

LinkTree.prototype.handleItemClick = function(item, event) {
    setLink(item.uuid);
}

function buildTree() {
    var placeholder = document.getElementById('tree');
    var tree = new LinkTree(document, placeholder);
    tree.init(PUBLICATION_ID);
    tree.render();
    tree.loadInitialTree(AREA, DOCUMENT_ID);
}

var url;
window.onload = insertText
