<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>
  
<xsl:template match="array">
 <xsl:apply-templates select="tocTab"/>
</xsl:template>
  
<xsl:template match="tocTab">
  <xsl:variable name="hrefWithRemovedContextPrefix"><xsl:value-of select="substring-after(@href,'/docs/')"/></xsl:variable>
  <xsl:choose>
    <xsl:when test="contains($hrefWithRemovedContextPrefix,'#')">
      <xsl:text> </xsl:text><xsl:value-of select="substring-before($hrefWithRemovedContextPrefix,'#')"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text> </xsl:text><xsl:value-of select="$hrefWithRemovedContextPrefix"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>

