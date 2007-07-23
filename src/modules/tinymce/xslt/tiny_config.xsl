<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="locale"/>

<xsl:template match="/*">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="locale">
  <xsl:value-of select="$locale"/>
</xsl:template>

</xsl:stylesheet>
