<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>
<xsl:param name="background"/>
<xsl:param name="foreground"/>

 <xsl:template match="/">
  <svg width="82" height="20">
    
    <rect 
      style="fill:#8686BD;"
      x="0" y="0" width="82" height="25">
    </rect>
    
    <rect 
      style="fill:#CCCCE0;"
      x="0" y="0" rx="5" ry="5" width="82" height="25">
    </rect>
    
    <text text-anchor="middle" style="fill:{$foreground};font-size:12;font-family:'Arial';" x="40" y="15">
      <xsl:value-of select="$text"/>
    </text>
  </svg>
 </xsl:template>

</xsl:stylesheet>
