<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:param name="configfile"/>

<xsl:template match="xhtml:head/xhtml:title">
  <title><xsl:value-of select="$configfile"/></title>
</xsl:template>

<xsl:template match="xhtml:body/@onload">
  <xsl:attribute name="onload">bxe_start('<xsl:value-of select="$configfile"/>')</xsl:attribute>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet> 
