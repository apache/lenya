<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="oscom">
  <xsl:apply-templates select="html"/>
</xsl:template>

<xsl:template match="html">
  <xsl:apply-templates/>
<!--
  HALLO
  <xsl:copy-of select="body/*"/>
  LEVI
-->
</xsl:template>

<xsl:template match="body">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="font">
  <font><xsl:apply-templates/></font>
</xsl:template>

<xsl:template match="h3">
  <h3><xsl:apply-templates/></h3>
</xsl:template>

<xsl:template match="h4">
  <h4><xsl:apply-templates/></h4>
</xsl:template>

<xsl:template match="p">
  <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="b">
  <b><xsl:apply-templates/></b>
</xsl:template>

<xsl:template match="text()|@*">
<xsl:value-of select="."/>
</xsl:template>
 
</xsl:stylesheet>
