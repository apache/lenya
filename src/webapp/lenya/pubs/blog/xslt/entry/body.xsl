<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>

<xsl:template match="echo:entry">
  <div class="dateline"><xsl:value-of select="echo:issued"/></div>
  <div class="title"><xsl:value-of select="echo:title"/></div>
  <i><xsl:copy-of select="echo:summary/node()"/></i>
  <xsl:apply-templates select="echo:content"/>
  <br />
  <p class="issued">
  <b>Posted by <a href="{echo:author/echo:homepage}"><xsl:value-of select="echo:author/echo:name"/></a> at <xsl:value-of select="echo:issued"/>&#160;|&#160;<a href="index.html">Permalink</a></b>
  </p>
</xsl:template>

<xsl:template match="echo:content[@type='text/xhtml']">
  <xsl:copy-of select="node()"/>
</xsl:template>

<xsl:template match="echo:content">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>
 
</xsl:stylesheet>  
