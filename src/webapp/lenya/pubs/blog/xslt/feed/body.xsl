<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
>


<!-- FIXME: namspace problem -->
<!--
<xsl:import href="../entry/body.xsl"/>
-->

<xsl:template match="entry">
  <div class="dateline"><xsl:value-of select="issued"/></div>
  <div class="title"><a href="../../entries/{normalize-space(id)}/index.html"><xsl:value-of select="title"/></a></div>
  <xsl:apply-templates select="summary"/>

  <xsl:apply-templates select="content"/>
  <xsl:apply-templates select="echo:content"/>

  <p class="issued">
  <b>Posted <xsl:apply-templates select="author"/> at <xsl:value-of select="issued"/></b>&#160;|&#160;<a href="../../entries/{normalize-space(id)}/index.html">Permalink</a>
  </p>
</xsl:template>

<xsl:template match="summary">
<i>
  <xsl:apply-templates/>
</i>
</xsl:template>

<xsl:template match="content">
<p>
  <xsl:copy-of select="node()"/>
</p>
</xsl:template>

<xsl:template match="echo:content">
<p>
  <xsl:copy-of select="node()"/>
</p>
</xsl:template>

<xsl:template match="author">
by
<xsl:choose>
<xsl:when test="homepage">
<a href="{homepage}"><xsl:value-of select="name"/></a>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="name"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>
 
</xsl:stylesheet>  
