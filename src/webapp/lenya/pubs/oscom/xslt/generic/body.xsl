<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <xsl:apply-templates select="html"/>
</xsl:template>

<xsl:template match="html">
  <xsl:copy-of select="body/*"/>
</xsl:template>
 
</xsl:stylesheet>  
