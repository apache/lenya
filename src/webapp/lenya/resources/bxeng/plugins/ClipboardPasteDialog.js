function BxeClipboardPasteDialog() {
	
	this.init = function (options) {
		
	}
	
	this.start = function() {
		//register event
		document.eDOMaddEventListener( "ClipboardPasteDialog" , BxeTextClipboard_OpenDialog , false);
		
	}
	this.getCss = function() {
		return new Array();
	}
	
	this.getScripts = function() {
		return new Array();
	}
	
	
}

function BxeTextClipboard_OpenDialog(e) {
	
	var sel = window.getSelection();
	if (bxe_checkForSourceMode(sel)) {
		return false;
	}
	
	var mod = mozilla.getWidgetModalBox("Paste here", function(values) {
		
		var clipboard = mozilla.getClipboard();
		var content = clipboard.setData(values.clipboard);
		clipboard._system = true;
		window.getSelection().paste();
	});
	
	
	mod.addItem("clipboard", "", "textarea");
	mod.show(100,50,"fixed");
	
}
