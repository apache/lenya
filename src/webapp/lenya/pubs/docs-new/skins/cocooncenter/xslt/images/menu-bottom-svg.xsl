<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>

 <xsl:template match="/">
  <svg width="201" height="5">

    <xsl:variable name="background">#F9F9FF</xsl:variable>
    <xsl:variable name="foreground">Navy</xsl:variable>
    <xsl:variable name="border">Navy</xsl:variable>

	    <rect 
      style="fill:{$background};stroke:{$border};stroke-width:0.4;"
      x="0" y="-21" rx="3" ry="3" width="200" height="25"/>
  </svg>
 </xsl:template>

</xsl:stylesheet>
