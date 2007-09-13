function ImageToolBxe() {
	/* Image tool to add images */
	
	this.initialize = function(editor) {
		/* attach the event handlers */
		this.editor = editor;
		this.editor.logMessage('Image tool initialized');
	
    };
	
	this.createImageHandler = function(event) {
		/* create an image according to a url entered in a popup */
		var imageWindow = openPopup('kupupopups/image.html', 300, 200);
		imageWindow.imagetool = this;
		imageWindow.focus();
	};
	
	this.createImage = function(url) {
		var xml = "<img xmlns='"+ XHTMLNS + "' alt='' src='" + url + "'/>";
		var sel = window.getSelection();
		sel.selectEditableRange(drawertool.cssr);
		return bxe_insertContent_async(xml, BXE_SELECTION);
		
	};
	
	
	this.createContextMenuElements = function(selNode, event) {
		return new Array(new ContextMenuElement('Create image', this.createImageHandler, this));
	};
}



function LinkToolBxe() {
    /* Add and update hyperlinks */
    
    this.initialize = function(editor) {
        this.editor = editor;
        this.editor.logMessage('Link tool initialized');
    };
    
    this.createLinkHandler = function(event) {
        /* create a link according to a url entered in a popup */
        var linkWindow = openPopup('kupupopups/link.html', 300, 200);
        linkWindow.linktool = this;
        linkWindow.focus();
    };
    
    // the order of the arguments is a bit odd here because of backward compatibility
    this.createLink = function(url, type, name, target) {
        
        var sel = window.getSelection();
        sel.selectEditableRange(drawertool.cssr);
        //make string
		var te = "" + sel;
		te = te.replace(/</,"&lt;");
		if (typeof target == "object") {
			var xml = "<a xmlns='"+ XHTMLNS + "' href='"+url+"'>"+te+"</a>";
		} else {
			var xml = "<a xmlns='"+ XHTMLNS + "' href='"+url+"' target='"+target+"'>"+te+"</a>";
		}
        
        return bxe_insertContent(xml, BXE_SELECTION);
        
       
    };
    
    this.deleteLink = function() {
        /* delete the current link */
        var currnode = this.editor.getSelectedNode();
        var linkel = this.editor.getNearestParentOfType(currnode, 'a');
        if (!linkel) {
            this.editor.logMessage('Not inside link');
            return;
        };
        while (linkel.childNodes.length) {
            linkel.parentNode.insertBefore(linkel.childNodes[0], linkel);
        };
        linkel.parentNode.removeChild(linkel);
    };
    
    this.createContextMenuElements = function(selNode, event) {
        /* create the 'Create link' or 'Remove link' menu elements */
        var ret = new Array();
        var link = this.editor.getNearestParentOfType(selNode, 'a');
        if (link) {
            ret.push(new ContextMenuElement('Delete link', this.deleteLink, this));
        } else {
            ret.push(new ContextMenuElement('Create link', this.createLinkHandler, this));
        };
        return ret;
    };
}


function AssetToolBxe() {
    
    this.initialize = function(editor) {
        this.editor = editor;
        this.editor.logMessage('Asset tool initialized');
       
    };

    this.createAsset =  function(src, type, lang, title, cssClass, target) {
        
        var sel = window.getSelection();
        sel.selectEditableRange(drawertool.cssr);
        
        if (sel != "") {
            title = sel;
        }
         
        var xml = "<asset xmlns=\"http://bitflux.org/doctypes/bx\" src=\""+src+"\" lang=\""+lang+"\" type=\""+type+"\" ";
        xml = xml + "target=\""+target+"\" cssclass=\""+cssClass+"\" >"+title+"</asset>";
        
        

        return bxe_insertContent(xml, BXE_SELECTION);
    };
}

function DrawerToolBxe() {
	this.closeDrawer = function() {
		if (!this.current_drawer) {
			return;
		};
		bxe_registerKeyHandlers();
		
		this.current_drawer.hide();
		this.current_drawer = null;
	};
	
	this.openDrawer = function(id) {
		/* open a drawer */
		if (this.current_drawer) {
			this.closeDrawer();
		};
		bxe_deregisterKeyHandlers();
		var drawer = this.drawers[id];
		drawer.createContent();
		this.current_drawer = drawer;
	};
	
}
DrawerToolBxe.prototype = new DrawerTool;