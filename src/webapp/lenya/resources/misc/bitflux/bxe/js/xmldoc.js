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
// $Id: xmldoc.js,v 1.2 2002/10/25 10:12:22 felixcms Exp $

/**
 * @file
 * Implements the BXE_XmlDocument Interface
 *
 */
/**
 * BXE_XmlDocument Interface
 * @ctor
 * The constructor
 * @tparam String xpath to the place in the config file
 */
 
function BXE_XmlDocument(xpath)
{
	/**
	* The filename of the Document.
	* 
	* Usually, this is taken from the config file, if an xpath is provided
	*  otherwise it's null
	* @type String
	*/
	this.filename = null;
	
	/**
	* The method (like http) which is used to get the Document with load
	* 
	* Usually, this is taken from the config file, if an xpath is provided
	*  otherwise it's null and we can't use this.load()
	* @type String
	* @see  load
	*/	
	this.method = null;

	if (xpath) {
		this.filename = BX_config_getContent(xpath)
		this.method = BX_config_getContent(xpath + "/@method");
	}

	/**
	* Loads the xml file
	*
	* @tparam Function callback the function which is called after loading
	* @treturn void Nothing
	*/
	function load(callback) {
		this.doc = BXE_loader.load(this.filename,this.method,callback)
	}
	BXE_XmlDocument.prototype.load = load;

	/**
	* Shows the XML in a seperate Window in MSIE style
	* @treturn void Nothing
	*/	
	function showXml() {
		BX_xml_source= this.doc;
		var BX_source_window = window.open(BX_root_dir+"showsource/index.html","_blank","");
	}
	BXE_XmlDocument.prototype.showXml = showXml;
	

}

/**
 * BXE_XsltDocument Interface
 * It is extended from BXE_XmlDocument and provides some methods
 *  only needed in xslt-documents
 * @ctor
 * The constructor
 * @tparam String xpath to the place in the config file
 */
function BXE_XsltDocument(xpath)
{

	/**
	* An Array of all xsl:includes.
	* 
	* Usually, this is taken from the config file.
	* includes are always get by http-get, since it's done by the browser
	*
	* @type Array
	* @see  includeXsltIncludes
	*/	
	this.includes = null;

	/**
	* An associative Array of all xsl:params.
	* 
	* Usually, this is taken from the config file.
	*
	* @type Array
	* @see  includeXsltParams
	*/
	this.parameters = null;

	/*
	* this code is doubled from XmlDocument. has to change
	*  when i'm more literate in JS OO Programming
	*/

	if (xpath) {
		this.filename = BX_config_getContent(xpath)
		this.method = BX_config_getContent(xpath + "/@method");
	}
	
	/**
	* Includes the Xslt Params defined in Array params
	* @treturn void Nothing
	*/	
	function includeXsltParams() {
	   /* the following is to set parameters in the xsl-stylesheet according to url querystrings
          there seems to be no other way to do that ... */
       /* mozilla does not work with namespaces on xpath as of RC2... maybe this will change...
               see http://bugzilla.mozilla.org/show_bug.cgi?id=113611 for details (it will be in 1.1)
       until then we need this more complicated xpath string..
       var nsResolver = BX_xsl.createNSResolver(BX_xsl);
       */
	   /* in Mozilla 1.2 this can be done with setParameter and getParameter (not tested yet...)
	   see http://lxr.mozilla.org/mozilla/source/content/xsl/public/nsIXSLTProcessor.idl
	   
	   */
	   
	   
	   if ((this.xsltParams) ) {
	   		var node;
			var paramName;
	   		for (paramName in this.xsltParams ) {
	    		var result = this.doc.evaluate("/*/*[name() = 'xsl:param' and @name='"+ paramName+"']", this.doc, null, 0, null);				
	    		if (node = result.iterateNext()) {
					//there is already such a xsl:param, replace it
					node.childNodes[0].nodeValue = this.xsltParams[paramName];
				} else {
					//there was no such xsl:param, create a new node
		    		var result = this.doc.evaluate("/*[name() = 'xsl:stylesheet']", this.doc, null, 0, null);															
					node = result.iterateNext();
					var newNode = this.doc.createElementNS("http://www.w3.org/1999/XSL/Transform","param");
					newNode.setAttribute("name",paramName);
					newNode.appendChild(this.doc.createTextNode(this.xsltParams[paramName]));
					node.insertBefore(newNode,node.firstChild);
				}
			}
		}
	}
	BXE_XsltDocument.prototype.includeXsltParams = includeXsltParams;

	/**
	* Includes the Xslt Documents defined in Array includes.
	* 
	* If there is an xsl:param with the same namein the xsl document, it is replaced, 
	*  if not, it as added
	*
	* @treturn void Nothing
	*/	
	function includeXsltIncludes() {
	
			for (var i=0; i < this.includes.length;i++)
			{
				var incl = this.doc.createElementNS("http://www.w3.org/1999/XSL/Transform","include");
				incl.setAttribute("href",this.includes[i])
				this.doc.documentElement.insertBefore(incl,this.doc.documentElement.firstChild);
			}
	}
	BXE_XsltDocument.prototype.includeXsltIncludes = includeXsltIncludes;

}
BXE_XsltDocument.prototype = new BXE_XmlDocument;

