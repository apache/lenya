function bxe_InsertLink() {
	
	var sel = window.getSelection();
	var object = document.createElementNS("http://www.w3.org/1999/xhtml","a");
	var cb = bxe_getCallback("a","http://www.w3.org/1999/xhtml");
	if (cb ) {
		bxe_doCallback(cb, object);
	} 
	else {
	
		sel.insertNode(object);
	}
}