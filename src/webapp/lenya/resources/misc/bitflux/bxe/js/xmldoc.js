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
// $Id: xmldoc.js,v 1.1 2002/10/24 14:44:31 felixcms Exp $


function BXE_XmlDocument(xpath)
{
	if (xpath) {
		this.filename = BX_config_getContent(xpath)
		this.method = BX_config_getContent(xpath + "/@method");
	}
	else {
		this.method = null;
		this.filename = null;
	}
	this.includes = null;
	this.parameters = null;

}




BXE_XmlDocument.prototype={

	load:function(callback) {
		this.doc = BXE_loader.load(this.filename,this.method,callback)
	},
	
	includeXsltIncludes:function() {
	
			for (var i=0; i < this.includes.length;i++)
			{
				var incl = this.doc.createElementNS("http://www.w3.org/1999/XSL/Transform","include");
				incl.setAttribute("href",this.includes[i])
				this.doc.documentElement.insertBefore(incl,this.doc.documentElement.firstChild);
			}
	},
	
	showXml:function() {
		BX_xml_source= this.doc;
		var BX_source_window = window.open(BX_root_dir+"showsource/index.html","_blank","");
	},
	
	includeXsltParams: function() {
	   /* the following is to set parameters in the xsl-stylesheet according to url querystrings
          there seems to be no other way to do that ... */
       /* mozilla does not work with namespaces on xpath as of RC2... maybe this will change...
               see http://bugzilla.mozilla.org/show_bug.cgi?id=113611 for details (it will be in 1.1)
       until then we need this more complicated xpath string..
       var nsResolver = BX_xsl.createNSResolver(BX_xsl);
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
}


// for whatever reason, jsdoc needs this line
