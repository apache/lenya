<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="framebox-title-background" select="'White'"/>

<xsl:param name="text"/>

 <xsl:template match="/">
  <svg width="1" height="1">

    <rect 
      style="fill:{$framebox-title-background}"
      x="0" y="0" width="1" height="1"/>
  </svg>
 </xsl:template>

</xsl:stylesheet>
