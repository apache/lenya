<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://example.com/newformat#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>

<xsl:template match="echo:entry">
  <h1><xsl:value-of select="echo:title"/></h1>
  <h2><xsl:value-of select="echo:subtitle"/></h2>
  <i><xsl:value-of select="echo:summary"/></i>
  <xsl:apply-templates select="echo:content"/>
  <br />
  <p class="issued">
  <b>Posted by <a href="{echo:author/echo:homepage}"><xsl:value-of select="echo:author/echo:name"/></a> at <xsl:value-of select="echo:issued"/></b>
  </p>
</xsl:template>

<xsl:template match="echo:content">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>
 
</xsl:stylesheet>  
