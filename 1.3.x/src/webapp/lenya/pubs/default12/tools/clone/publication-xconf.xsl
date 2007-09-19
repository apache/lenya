<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 

  <xsl:param name="publicationName"/>
  <xsl:param name="templatePublication"/>

  <xsl:template match="proxy">
    <xsl:apply-templates select="@url"/>
  </xsl:template>

  <xsl:template match="@url">
     <xsl:variable name="urlValue">
       <xsl:value-of select="substring-before(.,$templatePublication)"/>
       <xsl:value-of select="$publicationName"/>
       <xsl:value-of select="substring-after(.,$templatePublication)"/>
     </xsl:variable>
     <proxy area="{../@area}" ssl="{../@ssl}" url="{$urlValue}"/>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet> 
