<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://example.com/newformat#"
>


<!-- FIXME: namspace problem -->
<!--
<xsl:import href="../entry/body.xsl"/>
-->

<xsl:template match="entry">
  <div class="dateline"><xsl:value-of select="issued"/></div>
  <div class="title"><a href="../../entries/{id}/index.html"><xsl:value-of select="title"/></a></div>
  <div class="subtitle"><xsl:value-of select="subtitle"/></div>
  <i><xsl:value-of select="summary"/></i>
  <xsl:apply-templates select="content"/>
  <xsl:apply-templates select="echo:content"/>
  <p class="issued">
  <b>Posted by <a href="{author/homepage}"><xsl:value-of select="author/name"/></a> at <xsl:value-of select="issued"/></b>&#160;|&#160;<a href="../../entries/{id}/index.html">Permalink</a>
  </p>
</xsl:template>

<xsl:template match="content">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="echo:content">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>
 
</xsl:stylesheet>  
