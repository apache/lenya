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
// $Id: bitfluxeditor_sc.js,v 1.3 2002/10/25 10:12:22 felixcms Exp $

/**
 * @file
 * Implements the special chars plugin
 *
 * we need some kind of plugin-interface. to be defined yet
 */
 
function BX_sc_popup ()
{

	window.open("./"+BX_root_dir+"/specialcharacters/sc_generated.html","sc",'toolbar=no,width=380,height=450,scrollbars=no,resizable=yes','articleinfo_keywordset');
}    
    
