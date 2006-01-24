<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:rng="http://relaxng.org/ns/structure/1.0">
  
  
  <xsl:template match="rng:include/@href[starts-with(., 'fallback://')]">
    <xsl:attribute name="href">
      <xsl:text>/fallback/</xsl:text>
      <xsl:value-of select="substring-after(., 'fallback://')"/>
    </xsl:attribute>
  </xsl:template>


  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>