<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:template match="/">
<xsl:apply-templates select="feed"/>
</xsl:template>

<xsl:template match="feed">
<feed xmlns:echo="http://example.com/newformat#" version="{echo:feed/@version}">
<!--
<feed xmlns:echo="http://purl.org/atom/ns#" version="{echo:feed/@version}">
-->
<xsl:copy-of select="echo:feed/echo:title"/>
<xsl:copy-of select="echo:feed/echo:subtitle"/>
<xsl:copy-of select="echo:feed/echo:link"/>
<xsl:copy-of select="echo:feed/echo:modified"/>
<xsl:copy-of select="echo:entry"/>
</feed>
</xsl:template>
 
</xsl:stylesheet>  
