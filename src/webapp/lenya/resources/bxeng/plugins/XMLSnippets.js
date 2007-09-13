function BxeXMLSnippets() {
	var snippets;
	
	this.init = function (options) {
		bxe_config.XMLSnippets = this;
		this.snippets = options;
		this.snippetsNames = new Array();
		
		for (var name in options) {
			
			this.snippetsNames.push(name);
		}
		// add button
		var b = bxe_config.getButtons();
		var tmpArray = new Array();
		tmpArray['col'] = 0;
		tmpArray['row'] = 3;
		tmpArray['action'] = "InsertXMLSnippet";
		tmpArray['type'] = "action";
		tmpArray['ns'] = "";
		tmpArray['data'] = "foobar";
		b["XMLSnippets"] = tmpArray;
		
		//register event
		
		document.eDOMaddEventListener( "InsertXMLSnippet" , BxeXMLSnippets_OpenDialog , false);
		
	}
	
	this.getCss = function() {
		return new Array();
	}
	
	this.getScripts = function() {
		return new Array();
	}
	

}

function BxeXMLSnippets_OpenDialog(e) {
	var mod = mozilla.getWidgetModalBox("Choose an XML Snippet", function(values) {
		bxe_history_snapshot();
		bxe_insertContent_async(values.snippet,BXE_SELECTION,BXE_SPLIT_IF_INLINE);
		var sel = window.getSelection();
		
		if (!sel.anchorNode.parentNode.XMLNode.parentNode.isNodeValid(false, 2)) {
			bxe_history_undo();
		}
		bxe_history_snapshot_async();
	});
	
	
	mod.addItem("snippet", "", "select","", bxe_config.XMLSnippets.snippets, bxe_config.XMLSnippets.snippetsNames);
	mod.show(100,50,"fixed");
	
}
