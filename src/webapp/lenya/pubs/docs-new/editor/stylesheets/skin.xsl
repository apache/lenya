<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:template match="/">
   <html>
   <head>
    <title>Forrest editor</title>
    
    <link type="text/css" rel="stylesheet" href="style"/>

    </head>
    <body> 
			<xsl:apply-templates/>
	</body>   
    

   </html>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-2"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>
  <xsl:template match="text()" priority="-1"><xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>
