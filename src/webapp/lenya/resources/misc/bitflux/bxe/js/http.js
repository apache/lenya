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
// $Id: http.js,v 1.1 2002/09/13 20:26:50 michicms Exp $


//the following to functions are for doing a transport independent interface
// now it's only http, but xmlrpc and more should be possible.

function BX_http_load(filename)
{
        var docu = document.implementation.createDocument("","",null);
        docu.onload = BX_xmlloaded;  // set the callback when we are done loading
		docu.load(filename);
        return docu;
}

      
