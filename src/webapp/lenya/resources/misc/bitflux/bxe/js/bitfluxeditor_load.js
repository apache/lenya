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
// $Id: bitfluxeditor_load.js,v 1.4 2002/11/17 16:48:14 felixcms Exp $

/**
 * @file
 * Implements the TransportDriver Factory Class
 *
 */

//the following to functions are for doing a transport independent interface
// now it's only http, but xmlrpc and more should be possible.

/**
 * TransportDriver Factory Methode
 * @ctor
 */
function BXE_TransportDriver()
{
	this.methods = new Array;
	this.xmldone = 0;	

	function load(filename,method,callback) {
		if (!method)	{
			method = "http";
		}
		if (! this.methods[method]) {
			this.methods[method] = eval( "new BXE_TransportDriver_" + method + "(this)");
		}
		
		try {
				return this.methods[method].load(filename,callback);
			}
		catch(e) {
				BXEui.newObject("initAlert",e);
		}
		
	}
	BXE_TransportDriver.prototype.load = load;
	
	function save(filename,method,options)
    {
		if (!method) {
			method = "http";
		}
		if (! this.methods[method]) {
			this.methods[method] = eval( "new BXE_TransportDriver_" + method + "(this)");
		}
		
		try {
			return this.methods[method].save(filename,options);
		}
		catch(e) {
			BXEui.newObject("initAlert",e);
		}
    }
	BXE_TransportDriver.prototype.save = save;
	
	function xmlloaded(e)
	{
		if (!e.currentTarget.documentElement ){ 
			alert ("The document "+e.currentTarget.baseURI + " seems not to be a valid XML document. Most probably it couldn't be found or you didn't set the mime-type for it to text/xml in your webserver configuration");
			return false;
		}
		e.currentTarget.loader.xmldone++;

	
	//we need all 5 xml documents (xmltransformback, xmltransform, xsltransform, xml. xsl and xsd) 
	// then we can start the transformation
	    if (e.currentTarget.loader.xmldone > 5)
    	{
			BX_transformDoc();
			window.defaultStatus = null;
		}
		else {
			window.defaultStatus = e.currentTarget.loader.xmldone + " of 6 documents loaded";
		}
	}
	BXE_TransportDriver.prototype.xmlloaded = xmlloaded;
}      
       
function BX_submit(options) {

	BXE_loader.save(BX_posturl.filename,BX_posturl.method,options);
}


function BX_xmlTRBack_loaded (e) {
	/* include includes*/
	BX_xmlTRBack.includeXsltIncludes();
	BXE_loader.xmlloaded(e);
}			

// for whatever reason, jsdoc needs this line
