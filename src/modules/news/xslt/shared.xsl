<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:template name="getHref">
    <xsl:choose>
      <xsl:when test="@uuid">lenya-document:<xsl:value-of select="@uuid"/>,lang=<xsl:value-of select="@xml:lang"/></xsl:when>
      <xsl:when test="@href"><xsl:value-of select="@href"/></xsl:when>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>