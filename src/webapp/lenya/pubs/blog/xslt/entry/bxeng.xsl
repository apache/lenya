<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: bxeng.xsl,v 1.3 2003/09/10 18:55:08 gregor Exp $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 xmlns:echo="http://purl.org/atom/ns#"
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
