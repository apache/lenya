<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="@*|node()"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template> 
  
<xsl:template match="/">
  <html>
    <head>
      <title>Atom Entry Title</title>
    </head>
    <body bgcolor="white">
      Atom Entry
      <xsl:apply-templates select="entry" />
    </body>
  </html>
</xsl:template>
  
<xsl:template match="entry">
  <xsl:apply-templates />
</xsl:template>

</xsl:stylesheet>
