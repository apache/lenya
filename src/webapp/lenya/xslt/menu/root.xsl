<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:variable name="context_prefix" select="/lenya/menu/context_prefix"/>

<xsl:template match="lenya">
  <div style="position:absolute;top:0px;left:0px;z-index:2">
      <xsl:apply-templates select="menu"/>
  </div>

  <div style="position:absolute;top:60px;left:0px;z-index:1">
      <xsl:apply-templates select="cmsbody"/>
  </div>
</xsl:template>

<xsl:include href="menu.xsl"/>
 
</xsl:stylesheet>  
