<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>
<xsl:param name="title-background" select="'#E0E0F0'"/>
<xsl:param name="background" select="'White'"/>
<xsl:param name="text-color" select="'Navy'"/>
<xsl:param name="border" select="'Navy'"/>

<xsl:template match="/">
  <svg width="1" height="1">

    <rect 
      style="fill:{$background};stroke:{$border};stroke-width:0.4;"
      x="0" y="-4" rx="3" ry="3" width="3" height="10"/>
  </svg>
 </xsl:template>

</xsl:stylesheet>
