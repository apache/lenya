function BXE_widget() {
}

BXE_widget.prototype={
	newObject:function(name,option) {
		return eval("new BXE_widget_"+name+"(option)");
	},
}


function BXE_widget_loadMessage() {
}

BXE_widget_loadMessage.prototype={
	set:function(text) {
 		BX_innerHTML(document.getElementById("bxe_area"),"<br/><img hspace='5' width='314' height='34' src='./bxe/img/bxe_logo.png'/><br/><span style='font-family: Arial; padding: 5px; background-color: #ffffff'>"+text.replace(/\n/g,"<br/><br/>")+"</span>");
	}
}

function BXE_widget_initAlert(object) {
	if (object) {
		this.set(object);
	}
}

BXE_widget_initAlert.prototype={

	set:function(e) {
	    var mes = "ERROR in initialising Bitflux Editor:\n"+e.message +"\n";
        try
        {
            mes += "In File: " + e.filename +"\n";
        }
        catch (e)
        {
            mes += "In File: " + le.fileName +"\n";
        }
        try
        {
            mes += "Linenumber: " + e.lineNumber + "\n";
        }
        catch(e) {}
        
        mes += "Type: " + e.name + "\n";
        mes += "Stack:" + e.stack + "\n";
		BXEui.lm.set(mes.replace(/\n/g,"<br /><br />"));
		alert(mes);
	}

}

// for whatever reason, jsdoc needs this line
