<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.0"> 

<xsl:param name="publicationName"/>

<xsl:template match="lenya:name">
  <lenya:name><xsl:value-of select="$publicationName"/></lenya:name>
</xsl:template>

<xsl:template match="lenya:description">
  <lenya:description>This publication is a clone of the default publication. No further description of this publication yet.</lenya:description>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet> 
