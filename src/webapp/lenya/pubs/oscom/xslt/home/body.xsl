<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
                xmlns:error="http://apache.org/cocoon/error/2.0"
>
 
<xsl:output version="1.0" indent="yes"/>

<xsl:template name="body">
  <xsl:apply-templates select="html/body" mode="xhtml"/>
</xsl:template>

<xsl:template match="body">
  <xsl:copy-of select="*"/>
</xsl:template>

<xsl:template name="html-title">
Home - OSCOM - Open Source Content Management
</xsl:template>

<xsl:template name="admin-url">
<xsl:param name="prefix"/>
<a class="breadcrumb"><xsl:attribute name="href"><xsl:value-of select="$prefix"/>/index.html</xsl:attribute>Apache Lenya
</a>
</xsl:template>


<xsl:template match="p">
 <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="a">
<a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="error:notify">
  EXCEPTION
</xsl:template>
 
</xsl:stylesheet>  
