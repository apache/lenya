<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="@*|node()"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template> 
  
<xsl:template match="/">
  <html>
    <head>
      <title><xsl:value-of select="/system/system_name"/></title>
    </head>
    <body bgcolor="white">
      <xsl:apply-templates select="system" />
    </body>
  </html>
</xsl:template>
  
<xsl:template match="system">
  <xsl:apply-templates select="system_name" />
  <xsl:apply-templates select="description" />
</xsl:template>
  
<xsl:template match="system_name">
  <h1><xsl:apply-templates/></h1>
</xsl:template>
  
<xsl:template match="description">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>

</xsl:stylesheet>
