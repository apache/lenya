<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>
<xsl:param name="background"/>

 <xsl:template match="/">
  <svg width="1" height="1">
    <xsl:variable name="background">#8686BD</xsl:variable>

    <rect 
      style="fill:{$background};"
      x="0" y="0" width="1" height="1"/>
  </svg>
 </xsl:template>

</xsl:stylesheet>
