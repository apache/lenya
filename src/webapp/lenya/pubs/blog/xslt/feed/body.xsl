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
  <h1><xsl:value-of select="title"/></h1>
  <h2><xsl:value-of select="subtitle"/></h2>
  <i><xsl:value-of select="summary"/></i>
  <br />
  <b>Posted by <a href="{author/homepage}"><xsl:value-of select="author/name"/></a> at <xsl:value-of select="issued"/></b> | <a href="../../entries/{id}/index.html">Permalink</a>
  <br />
  <xsl:apply-templates select="content"/>
</xsl:template>

<xsl:template match="content">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>
 
</xsl:stylesheet>  
