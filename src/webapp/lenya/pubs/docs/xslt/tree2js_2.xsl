<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="array">
   var tocTab = new Array();
   tocTab[0] = new Array("0", "Documentation","/wyona-cms/docs/welcome.html");
   <xsl:apply-templates select="tocTab"/>
   var nCols = 5;
  </xsl:template>
  
  <xsl:template match="tocTab">
  tocTab[<xsl:value-of select="position()"/>]=<xsl:value-of select="."/>
  <!--
  tocTab[<xsl:value-of select="position()"/>]=<xsl:apply-templates/>
  -->
  </xsl:template>

</xsl:stylesheet>

