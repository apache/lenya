<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

<xsl:template match="img/@src[starts-with(., '/')]">
	<xsl:attribute name="src">/wyona-cms/computerworld<xsl:value-of select="."/></xsl:attribute>
</xsl:template>

<xsl:template match="link">
<link rel="stylesheet" type="text/css">  
<xsl:attribute name="href">/wyona-cms/computerworld<xsl:value-of select="@href"/></xsl:attribute>
</link>
</xsl:template>

<xsl:template match="@style[starts-with(.,'background-image')]">
 		<xsl:attribute name="style">background-image:url(/wyona-cms/computerworld<xsl:value-of select="substring(., 22)"/></xsl:attribute>
</xsl:template>

<xsl:template match="@*|*">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
