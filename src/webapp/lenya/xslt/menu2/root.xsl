<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:include href="menu.xsl"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:variable name="context_prefix" select="/lenya/bar/context_prefix"/>

<xsl:template match="lenya">
      <xsl:apply-templates select="bar"/>
<!--      <xsl:apply-templates select="cmsbody"/> -->
</xsl:template>

 
</xsl:stylesheet>  
