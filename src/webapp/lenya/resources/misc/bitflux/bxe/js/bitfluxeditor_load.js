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
// $Id: bitfluxeditor_load.js,v 1.1 2002/09/13 20:26:50 michicms Exp $


//the following to functions are for doing a transport independent interface
// now it's only http, but xmlrpc and more should be possible.

function BX_load_document(filename,method)
{
		if (method)
		{
			try {
				return eval("BX_"+method+"_load('"+filename+"');");
			}
			catch(e) {
				BX_init_alert(e);
			}
		}
		else
		{
			return BX_http_load(filename);
		}
}

function BX_save_document(filename,method)
{
		if (method)
		{
			try {
				return eval("BX_"+method+"_save('"+filename+"');");
			}
			catch(e) {
				BX_init_alert(e);
			}
		}
		else
		{
			return BX_http_save(filename);
		}
}
      
       
function BX_xmlloaded()
{
    BX_xmldone++;
	//we need all 5 xml documents (xmltransformback, xmltransform, xsltransform, xml and xsl) 
	// then we can start the transformation
    if (BX_xmldone > 4)
    {
		BX_transformDoc();
	}
}


function BX_submit()
{
	BX_save_document(BX_posturl,BX_posturl_method);
}
