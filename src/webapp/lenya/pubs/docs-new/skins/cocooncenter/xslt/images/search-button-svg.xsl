<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="text"/>
<xsl:param name="background"/>
<xsl:param name="foreground"/>
<xsl:param name="menu-background"/>
<xsl:param name="menubar-background"/>

 <xsl:template match="/">
  <svg width="30" height="20">
    
    <rect
      style="fill:{$menubar-background}"
      x="0" y="0" width="30" height="20">
    </rect>
    
    <rect x="0" y="0" width="30" height="20" rx="5" ry="5" style="fill:{$menu-background};"/>
    
    <text text-anchor="middle"
        style="fill:#{$foreground};font-size:11;font-weight:bold;font-family:'Verdana';" x="15" y="14">Go!&#160;</text>
    
  </svg>
 </xsl:template>

</xsl:stylesheet>
