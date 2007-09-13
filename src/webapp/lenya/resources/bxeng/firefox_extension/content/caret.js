document.addEventListener("click", bxehelper_mouse, false);


function bxehelper_mouse(e) {
	try {
	if (e.target.ownerDocument.defaultView.BXENS) {
		var target = e.target.parentNode;
		if(target && target.userModifiable ) {
			if(e.target.tagName=="tabbrowser") {
				return;
			}
			// if the target of the click is one of these, then don't change the status of the toolbar
			var protect = new Array( 'toolbar', 'toolbarbutton', 'menuitem', 'menu', 'colorpicker');
			for(var i=0; i<protect.length; i++) {
				if(e.target.tagName==protect[i]) {
					return;
				}
			}
			
			
			bxehelper_setCaret();
		}
	} else {
		bxehelper_removeCaret();
	}
	} catch (e) {
		dump("bxehelper catched\n");
	}
	
	
}

function bxehelper_removeCaret() {
	prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefService).getBranch(null);
	prefs.setBoolPref('accessibility.browsewithcaret', false);
}

function bxehelper_setCaret() {
	prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefService).getBranch(null);
	prefs.setBoolPref('accessibility.browsewithcaret', true);
}
