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
// $Id: http.js,v 1.3 2002/11/17 16:48:14 felixcms Exp $
/**
 * @file
 * Implements the http TransportDriver 
 *
 */

/**
 * http TransportDriver
 * @ctor
 * The constructor
 * @tparam Object parent the "parent" Object (the loader)
 * @see BXE_TransportDriver
 */
function BXE_TransportDriver_http (parent)
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
	* Loads a file over http get
	* @tparam String filename the filename (can be http://... or just a relative path
	* @tparam Function callback the function which is called after loading
	* @treturn XMLDocument newly created xml document
	*/
	function load(filename,callback)
	{
        var docu = document.implementation.createDocument("","",null);
		docu.loader = this.parent;
		if (callback) {
			docu.onload = callback;	
		}
		else {
			//this seems to be a static call, therefore we needed
			// the docu.loader aboive... have to check, how it's done in JS correctly
			docu.onload = this.parent.xmlloaded;  // set the callback when we are done loading
		}
		docu.load(filename);
        return docu;
	
	}
	BXE_TransportDriver_http.prototype.load = load;

	/**
	* Save a file over http post. It just posts the whole xml file without variable
	* assignment (in PHP you have to use $HTTP_RAW_POST_DATA for getting the content)
	* See php/save.php for an example how to implement it in PHP.
	* @tparam String filename the filename (can be http://... or just a relative path)
	* @tparam Mixed options Not used here
	* @treturn void Nothing
	*/

	function save(filename,options)
	{
		this.p.onload = this._responseXML;
		this.p.open("POST",filename );
		
		this.p.send(calculateMarkup(BX_getResultXML(),true));
	}
	BXE_TransportDriver_http.prototype.save = save;

	/**
	* Handles the response of the save method.
	* This method is called, when the POST request from save has finished
	* It displays a message, if it succeeded or failed
	*
	* @tparam Event e the event triggered after save
	* @treturn void Nothing
	*/

	function _responseXML(e) {

	   var p = e.target;
	   var alerttext="";

	   if (p.responseXML) {
		  if (p.responseXML.firstChild.nodeName == 'parsererror')
		   {
		   alerttext="Something went wrong during parsing of the response:\n\n";
		   alerttext+=BX_show_xml(p.responseXML);
		   }
		   else if (p.responseXML.documentElement.getAttribute("status") == "ok")
		   {
			   alerttext = "Document successfully saved";
		   }
		   else
		   {
		   alerttext="Something went wrong during saving:\n\n";
		   alerttext += (calculateMarkup(p.responseXML.documentElement,true));
		   }
		   alert(alerttext);
	   }
	   else {
		   alerttext="Something went wrong during saving:\n\n";
		   alert(alerttext + p.responseText) ;
	   }
	}	
	BXE_TransportDriver_http.prototype._responseXML = _responseXML;

}
// the following line is not true, but an example for declaring class hierarchy
// BXE_TransportDriver_http.fulfills( BXE_TransportDriver );	




