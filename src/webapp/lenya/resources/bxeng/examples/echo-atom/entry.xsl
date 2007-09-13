<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: entry.xsl 687 2004-04-29 08:32:39Z chregu $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:xhtml="http://www.w3.org/1999/xhtml"
 xmlns:lenya="http://apache.org/cocoon/lenya/document/1.0"
 xmlns:dc="http://dc.org/2003/" 
 xmlns:echo="http://example.com/newformat#"
 xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>
<xsl:output method="xml" encoding="iso-8859-1" />

<xsl:template match="/">
    <div>Echo/Atom</div>
    <xsl:apply-templates select="/echo:entry"/>
</xsl:template>

<xsl:template match="*">
    <xsl:copy-of select="."/>
</xsl:template>

</xsl:stylesheet>
