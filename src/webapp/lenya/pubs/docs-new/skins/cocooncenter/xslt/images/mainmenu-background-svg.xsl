<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>
<xsl:param name="background"/>
<xsl:param name="foreground"/>
<xsl:param name="menu-background"/>
<xsl:param name="menu-background-current"/>

 <xsl:template match="/">
  <svg width="1" height="25">
    
    <rect x="0" y="0" width="1" height="25">
      <xsl:attribute name="style">
      fill:
      <xsl:choose>
        <xsl:when test="$text = 'current'"><xsl:value-of select="$menu-background-current"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="$menu-background"/></xsl:otherwise>
      </xsl:choose>
      </xsl:attribute>
    </rect>
    
   <defs>
    <filter id="blur1"><feGaussianBlur stdDeviation="2"/></filter>
      <linearGradient id="gradient" x1="0" y1="1" x2="0" y2="0">
        <stop offset="0%" style="stop-color:{$menu-background}" stop-opacity="1"/>
        <stop offset="30%" stop-opacity="0"/>
      </linearGradient>
   </defs>
   
   <xsl:if test="$text != 'current'">
    <rect x="0" y="0" rx="0" ry="0" width="5" height="25">
      <xsl:attribute name="style">
      fill:
      <xsl:choose>
        <xsl:otherwise>url(#gradient)</xsl:otherwise>
      </xsl:choose>
      </xsl:attribute>
    </rect>
  </xsl:if>
    
  </svg>
 </xsl:template>

</xsl:stylesheet>
