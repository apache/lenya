<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" version="1.0">
  <xsl:template match="lenya:object/@data">
    <xsl:variable name="url">
      <xsl:value-of select="." />
    </xsl:variable>
    <xsl:attribute name="data">
      <xsl:value-of select="substring-after($url, '/')" />
    </xsl:attribute>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

