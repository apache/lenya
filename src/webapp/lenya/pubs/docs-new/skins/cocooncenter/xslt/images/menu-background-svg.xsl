<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>

 <xsl:template match="/">
  <svg width="1" height="1">

    <xsl:variable name="background">#F9F9FF</xsl:variable>
    <xsl:variable name="foreground">Navy</xsl:variable>
    <xsl:variable name="border">Navy</xsl:variable>

	    <rect 
      style="fill:{$background};"
      x="0" y="0" rx="3" ry="3" width="1" height="1"/>
<!--    <text style="fill:{$foreground};font-size:12;font-family:'MicrogrammaDMedExt';" letter-spacing="2" x="10" y="14.5">-->
  </svg>
 </xsl:template>

</xsl:stylesheet>
