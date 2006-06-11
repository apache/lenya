<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- solprovider.com -->
<!-- Adds language to ACTION attributes -->
<xsl:param name="language"/>
<xsl:template match="/">
    <xsl:apply-templates/>
</xsl:template>
<xsl:template match="@action">
    <xsl:attribute name="action"><xsl:value-of select="substring-before(. ,'.')"/>_<xsl:value-of select="$language"/>.<xsl:value-of select="substring-after(. ,'.')"/></xsl:attribute>
</xsl:template>
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
</xsl:stylesheet> 
