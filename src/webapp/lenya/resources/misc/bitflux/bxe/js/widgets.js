// +----------------------------------------------------------------------+
// | Bitflux Editor                                                       |
// +----------------------------------------------------------------------+
// | Copyright (c) 2001,2002 Bitflux GmbH                                 |
// +----------------------------------------------------------------------+
// | This software is published under the terms of the Apache Software    |
// | License a copy of which has been included with this distribution in  |
// | the LICENSE file and is available through the web at                 |
// | http://bitflux.ch/editor/license.html                                |
// +----------------------------------------------------------------------+
// | Author: Christian Stocker <chregu@bitflux.ch>                        |
// +----------------------------------------------------------------------+
//
// $Id: widgets.js,v 1.4 2002/11/23 11:47:33 felixcms Exp $

/**
 * @file
 * Implements some Widget Interfaces.
 *
 * It will certainly change
 *
 */


function BXE_widget() {

	function newObject (name,option) {
		return eval("new BXE_widget_"+name+"(option)");
	}
	BXE_widget.prototype.newObject = newObject;
}

function BXE_widget_loadMessage() {

	function set(text) {
	try {
 		 BX_innerHTML(document.getElementById("bxe_area"),"<br/><img hspace='5' width='314' height='34' src='"+ BX_root_dir + "img/bxe_logo.png'/><br/><span style='font-family: Arial; padding: 5px; background-color: #ffffff'>"+text.replace(/\n/g,"<br/><br/>")+"</span>"); 
		
	} catch (e) {
		alert(text);
	}
	
	}
	BXE_widget_loadMessage.prototype.set = set;
}

function BXE_widget_initAlert(object) {
	if (object) {
		this.set(object);
	}


	function set(e) {
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
	BXE_widget_initAlert.prototype.set = set;

}

// for whatever reason, jsdoc needs this line
