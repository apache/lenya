<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>
<xsl:param name="background"/>

 <xsl:template match="/">
  <svg width="5" height="21">
    <xsl:variable name="background">#E0E0F0</xsl:variable>

    <rect 
      style="fill:{$background};stroke:Navy;stroke-width:0.5;"
      x="-6" y="" rx="3" ry="3" width="10" height="20"/>
  </svg>
 </xsl:template>

</xsl:stylesheet>
