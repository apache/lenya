<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: test.xsl,v 1.1 2003/08/18 09:49:06 michi Exp $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:xhtml="http://www.w3.org/1999/xhtml"
 xmlns:lenya="http://apache.org/cocoon/lenya/document/1.0"
 xmlns:dc="http://dc.org/2003/" 
 xmlns:unizh="http://unizh.ch/2003/"
>
<xsl:output method="xml" encoding="iso-8859-1" />

<xsl:template match="/">
    <div>test div</div>
    <xsl:apply-templates select="/unizh:content-document/xhtml:body"/>
    <h1>h1 test</h1>
    
    <div >hhhh</div>
</xsl:template>
<xsl:template match="*">
    <xsl:copy-of select="."/>
</xsl:template>

</xsl:stylesheet>
