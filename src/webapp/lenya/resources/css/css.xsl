<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
<xsl:param name="contextprefix"/>

<xsl:output method="text"/>

<xsl:template match="css">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="context-prefix">
  <xsl:value-of select="$contextprefix"/>
</xsl:template>
  
</xsl:stylesheet>