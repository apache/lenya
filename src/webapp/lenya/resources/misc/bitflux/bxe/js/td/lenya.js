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
// $Id: lenya.js,v 1.7 2003/03/06 20:47:09 gregor Exp $
/**
 * @file
 * Implements the lenya TransportDriver (only save for now)
 *
 */

/**
 * lenya TransportDriver
 * @ctor
 * The constructor
 * @tparam Object parent the "parent" Object (the loader)
 * @see BXE_TransportDriver
 * @todo implement load (maybe not needed, as httpget is enough for lenya)
 */


function BXE_TransportDriver_lenya(parent)
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
	* Save a file over the lenya protocol. 
	* It wraps the original xml around some additional tags.
	* @tparam String filename the filename (can be http://... or just a relative path)
	* @tparam Mixed options 
	* @treturn void Nothing
	*/
	function  save(filename,options)
	{
	   // lenya way
	   this.p.onload = this._responseXML;
	   this.p.options = options;
	   var LenyaRequest = BX_xml.doc.createElement("request");
	   LenyaRequest.setAttribute("type","checkin");
	   var dataEle = BX_xml.doc.createElement("data");
	   dataEle.setAttribute("type","xml");
	   dataEle.appendChild(BX_getResultXML().firstChild);

	   LenyaRequest.appendChild(dataEle);
	   this.p.open("POST",filename);
   	   // BX_show_xml(LenyaRequest);
	   this.p.send(calculateMarkup(LenyaRequest,true));
	}
	BXE_TransportDriver_lenya.prototype.save = save;
	
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
	BXE_TransportDriver_lenya.prototype._responseXML = _responseXML;
}


