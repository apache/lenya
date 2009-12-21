<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:param name="pub"/>
  <xsl:param name="area"/>
  
  <xsl:template name="getHref">
    <xsl:choose>
      <xsl:when test="@uuid">lenya-document:<xsl:value-of select="@uuid"/>,pub=<xsl:value-of select="$pub"/>,area=<xsl:value-of select="$area"/>,lang=<xsl:value-of select="@xml:lang"/></xsl:when>
      <xsl:when test="@href"><xsl:value-of select="@href"/></xsl:when>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>