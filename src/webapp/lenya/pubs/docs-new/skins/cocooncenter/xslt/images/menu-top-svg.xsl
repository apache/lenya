<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>

 <xsl:template match="/">
  <svg width="201" height="20">

<!--    <xsl:variable name="background">#F0F0F0</xsl:variable>-->
    <xsl:variable name="background">#E0E0F0</xsl:variable>
    <xsl:variable name="foreground">Navy</xsl:variable>
    <xsl:variable name="border">Navy</xsl:variable>

	    <rect 
      style="fill:{$background};stroke:{$border};stroke-width:0.4;"
      x="0" y="" rx="3" ry="3" width="200" height="25"/>
<!--    <text style="fill:{$foreground};font-size:12;font-family:'MicrogrammaDMedExt';" letter-spacing="2" x="10" y="14.5">-->
    <text style="fill:Navy;font-size:12;font-family:Arial" letter-spacing="0.5" x="10" y="14.5">
      <xsl:value-of select="$text"/>
    </text>
  </svg>
 </xsl:template>

</xsl:stylesheet>
