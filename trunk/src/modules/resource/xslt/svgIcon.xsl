<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:svg="http://www.w3.org/2000/svg"
  xmlns:xlink="http://www.w3.org/1999/xlink">
  
  <xsl:param name="basePath"/>
  <xsl:param name="extension"/>
  
  <xsl:template match="/*">
    
    <svg:svg width="33" height="40">
      
      <svg:image x="0" y="0" width="33" height="40"
        xlink:href="file://{$basePath}/lenya/resources/images/icons/empty-icon.png"/>
        
      <svg:text x="16" y="24"
        style="text-anchor: middle; font-size: 11; font-weight: bold; fill: #000000;">
        <xsl:value-of select="translate(substring($extension, 1, 3),
          'abcdefghijklmnopqrstuvwxyz',
          'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
      </svg:text>
      
    </svg:svg>
    
  </xsl:template>

</xsl:stylesheet>