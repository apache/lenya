<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 

  <xsl:param name="publicationName"/>
  <xsl:param name="templatePublication"/>

  <xsl:template match="parameter">
    <xsl:apply-templates select="@value"/>
  </xsl:template>

  <xsl:template match="@value">
     <xsl:variable name="paraValue">
       <xsl:value-of select="substring-before(.,$templatePublication)"/>
       <xsl:value-of select="$publicationName"/>
       <xsl:value-of select="substring-after(.,$templatePublication)"/>
     </xsl:variable>
     <parameter  value="{$paraValue}" name="{../@name}"/>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet> 
