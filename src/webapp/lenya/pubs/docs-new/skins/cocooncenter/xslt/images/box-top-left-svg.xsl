<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="framebox-title-background" select="'#E0E0F0'"/>
<xsl:param name="framebox-background" select="'White'"/>
<xsl:param name="border" select="'Navy'"/>

<xsl:template match="/">
  <svg width="5" height="5">
    <rect
        style="fill:{$framebox-title-background};"
        x="0" y="0" rx="5" ry="5" width="10" height="10"/>
  </svg>
</xsl:template>

</xsl:stylesheet>
