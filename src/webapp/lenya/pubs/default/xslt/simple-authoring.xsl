<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:include href="../../../xslt/menu/page2xhtml.xsl"/>
  
  <xsl:template match="cmsbody">
    <xsl:copy-of select="*"/>
  </xsl:template>
  
</xsl:stylesheet>

