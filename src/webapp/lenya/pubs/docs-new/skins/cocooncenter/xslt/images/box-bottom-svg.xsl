<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="framebox-background" select="'White'"/>
<xsl:param name="border" select="'Navy'"/>

<xsl:template match="/">

  <svg width="1" height="5">
    <rect 
      style="fill:{$framebox-background};stroke:{$border};stroke-width:0.0;"
      x="0" y="0" width="1" height="5"/>
  </svg>
  
</xsl:template>

</xsl:stylesheet>
