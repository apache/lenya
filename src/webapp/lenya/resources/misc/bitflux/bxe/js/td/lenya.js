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
// $Id: lenya.js,v 1.5 2002/11/23 11:47:33 felixcms Exp $
/**
 * @file
 * Implements the wyona TransportDriver (only save for now)
 *
 */

/**
 * wyona TransportDriver
 * @ctor
 * The constructor
 * @tparam Object parent the "parent" Object (the loader)
 * @see BXE_TransportDriver
 * @todo implement load (maybe not needed, as httpget is enough for wyona)
 */


function BXE_TransportDriver_wyona(parent)
{
	/**
	* XMLHttpRequest Object
	*
	* We use the same XMLHttpRequest in the whole instance
	* @type Object
	*/
	this.p = new XMLHttpRequest();

	/**
	* Parent Object
	*
	* This is normally the BXE_loader class
	* this has to be implemented with xbBrowser some day:
	* @type Object
	*/
	//check doku, if we can access parent otherwise
	this.parent = parent;

	/**
	* Save a file over the wyona protocol. 
	* It wraps the original xml around some additional tags.
	* @tparam String filename the filename (can be http://... or just a relative path)
	* @tparam Mixed options 
	* @treturn void Nothing
	*/
	function  save(filename,options)
	{
	   // wyona way
	   this.p.onload = this._responseXML;
	   this.p.options = options;
	   var WyonaRequest = BX_xml.doc.createElement("request");
	   WyonaRequest.setAttribute("type","checkin");
	   var dataEle = BX_xml.doc.createElement("data");
	   dataEle.setAttribute("type","xml");
	   dataEle.appendChild(BX_getResultXML().firstChild);

	   WyonaRequest.appendChild(dataEle);
	   this.p.open("POST",filename);
   	   // BX_show_xml(WyonaRequest);
	   this.p.send(calculateMarkup(WyonaRequest,true));
	}
	BXE_TransportDriver_wyona.prototype.save = save;
	
	/**
	* Handles the response of the save method.
	* This method is called, when the POST request from save has finished
	* It displays a message, if it succeeded or failed
	*
	* @tparam Event e the event triggered after save
	* @treturn void Nothing
	*/
	function _responseXML (e) {
		var alerttext="";
		var p = e.target;		
		if (p.responseXML) {
	   		if (p.responseXML.firstChild.nodeName == 'parsererror') {
				alerttext="Something went wrong during parsing of the response:\n\n";
				alerttext+=BX_show_xml(p.responseXML);
	    		}
			else if (p.responseXML.documentElement.getAttribute("status") == "ok") {
				alerttext = "Document successfully saved";
				if (p.options == 1)
				{
					alert(alerttext);
					window.location = BX_backurl;
					return true;
				}
			}
			else {
				alerttext="Something went wrong during saving:\n\n";
				alerttext += (calculateMarkup(p.responseXML.documentElement,true));
			}
			alert(alerttext );
		}
		else {
			alerttext="Something went wrong during saving:\n\n";
			alert(alerttext + p.responseText) ;
		}
	}
	BXE_TransportDriver_wyona.prototype._responseXML = _responseXML;
}


