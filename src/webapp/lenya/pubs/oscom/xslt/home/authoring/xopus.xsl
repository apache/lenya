<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="oscom">
  <xsl:apply-templates select="about"/>
  <xsl:apply-templates select="features"/>
</xsl:template>

<xsl:template match="about">
 <font face="verdana">
 <xsl:apply-templates/>
 </font>
</xsl:template>

<xsl:template match="features">
 <font face="verdana">
 <xsl:apply-templates/>
 </font>
</xsl:template>

<xsl:template match="feature">
 <h3><xsl:apply-templates select="title"/></h3>
 <xsl:apply-templates select="p"/>
</xsl:template>

<xsl:template match="p">
 <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="a">
<a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>
 
</xsl:stylesheet>  
