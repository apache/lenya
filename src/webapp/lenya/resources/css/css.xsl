<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:text="http://chaperon.sourceforge.net/schema/text/1.0">
  
  <xsl:param name="root"/>
  
  <xsl:template match="css">
    <text:text>
      <xsl:apply-templates/>
    </text:text>
  </xsl:template>
  
  <xsl:template match="context-prefix"/>
  
  <xsl:template match="root">
    <xsl:value-of select="$root"/>
  </xsl:template>
  
</xsl:stylesheet>