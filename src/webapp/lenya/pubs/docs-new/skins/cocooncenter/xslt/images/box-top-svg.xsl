<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>
<xsl:param name="title-background" select="'#E0E0F0'"/>
<xsl:param name="background" select="'White'"/>
<xsl:param name="text-color" select="'Navy'"/>
<xsl:param name="border" select="'Navy'"/>

 <xsl:template match="/">
  <svg width="1" height="20">

	    <rect 
      style="fill:{$title-background};stroke:{$border};stroke-width:0.4;"
      x="-5" y="0" rx="3" ry="3" width="10" height="25"/>
  </svg>
 </xsl:template>

</xsl:stylesheet>
