<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="xml"/>
<xsl:param name="xsd"/>
<xsl:param name="xslt"/>
<xsl:param name="publicationid"/>
<xsl:param name="completearea"/>
<xsl:param name="contextprefix"/>

<xsl:template match="body/div/xml/pipeline/@xml">
  <xsl:attribute name="xml"><xsl:value-of select="$xml"/></xsl:attribute>
</xsl:template>

<xsl:template match="body/div/xml/pipeline/@xsd">
  <xsl:attribute name="xsd"><xsl:value-of select="$xsd"/></xsl:attribute>
</xsl:template>

<xsl:template match="body/div/xml/pipeline/view[@id='defaultView']/transform[last()]/@xsl">
  <xsl:attribute name="xsl"><xsl:value-of select="$xslt"/></xsl:attribute>
</xsl:template>

<xsl:template match="body/div/xml/pipeline/view[@id='defaultView']/transform">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
    <param name="contextprefix"><xsl:value-of select="$contextprefix"/></param>
    <param name="publicationid"><xsl:value-of select="$publicationid"/></param>
    <param name="completearea"><xsl:value-of select="$completearea"/></param>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet>
