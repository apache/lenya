<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="BX_xmlfile"/>
<xsl:param name="BX_xslfile"/>
<xsl:param name="css"/>
<xsl:param name="BX_exitdestination"/>

<xsl:template match="files/input/file[@name = 'BX_xmlfile']">
  <file name="BX_xmlfile"><xsl:value-of select="$BX_xmlfile"/></file>
</xsl:template>

<xsl:template match="files/input/file[@name = 'BX_xslfile']">
  <file name="BX_xslfile"><xsl:value-of select="$BX_xslfile"/></file>
</xsl:template>

<xsl:template match="files/output/file[@name = 'BX_exitdestination']">
  <file name="BX_exitdestination"><xsl:value-of select="$BX_exitdestination"/></file>
</xsl:template>

<xsl:template match="files/css/file">
  <file><xsl:value-of select="$css"/></file>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet> 
