<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:rng="http://relaxng.org/ns/structure/1.0">
  
  
  <xsl:param name="root"/>
  <xsl:param name="uri"/>
  
  
  <xsl:template name="stripLastStep">
    <xsl:param name="text"/>
    <xsl:if test="contains($text, '/')">
      <xsl:value-of select="substring-before($text, '/')"/>
      <xsl:text>/</xsl:text>
      <xsl:call-template name="stripLastStep">
        <xsl:with-param name="text" select="substring-after($text, '/')"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:variable name="baseUri">
    <xsl:call-template name="stripLastStep">
      <xsl:with-param name="text" select="$uri"/>
    </xsl:call-template>
  </xsl:variable>


  <xsl:template match="rng:include/@href[starts-with(., 'fallback://')]">
    <xsl:attribute name="href">
      <xsl:value-of select="$root"/>
      <xsl:text>fallback/</xsl:text>
      <xsl:value-of select="substring-after(., 'fallback://')"/>
    </xsl:attribute>
  </xsl:template>


  <xsl:template match="rng:include/@href[not(starts-with(., 'fallback://'))]">
    <xsl:attribute name="href">
      <xsl:value-of select="$baseUri"/>
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>