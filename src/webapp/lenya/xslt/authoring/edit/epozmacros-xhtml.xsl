<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:param name="contentfile"/>

<xsl:template match="xhtml:body/xhtml:div/xhtml:div/xhtml:div/xhtml:iframe/@src">
  <xsl:attribute name="src"><xsl:value-of select="$contentfile"/></xsl:attribute>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet> 
