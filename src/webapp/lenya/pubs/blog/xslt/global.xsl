<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://example.com/newformat#"
>

<xsl:template match="cmsbody">
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/styles.css" title="default css"/>
<title>
  <xsl:value-of select="feed/title"/><xsl:value-of select="feed/echo:title"/> - <xsl:value-of select="feed/subtitle"/><xsl:value-of select="feed/echo:subtitle"/>
</title>
</head>

<body>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td colspan="2" id="title">
  <a>
    <xsl:attribute name="href">
      <xsl:choose>
        <xsl:when test="$doctype='entry'">../../../../../index.html</xsl:when>
        <xsl:otherwise>../../index.html</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
    <!-- FIXME: namespace -->
    <xsl:value-of select="feed/title"/><xsl:value-of select="feed/echo:title"/>
  </a>
</td>
</tr>
<tr>
<td colspan="2" id="subtitle">
  <!-- FIXME: namespace -->
  <xsl:value-of select="feed/subtitle"/><xsl:value-of select="feed/echo:subtitle"/>
</td>
</tr>

<tr>
<td valign="top" id="content" width="70%">
    <!-- FIXME: namespace -->
    <xsl:apply-templates select="feed/entry"/>
    <xsl:apply-templates select="feed/echo:entry"/>
</td>

<td valign="top" id="sidebar" width="30%">

<!--
<div class="sidebar-title">Feeds</div>
<div class="sidebar-content">
<a href="index.xml">Echo Feed</a>
<br />RSS Feed
</div>

<div class="sidebar-title">Archives&#160;by&#160;Date</div>
<div class="sidebar-content">
August 2003
<br />July 2003
<br />June 2003
<br />May 2003
<br />April 2003
<br />March 2003
<br />February 2003
<br />January 2003
<br />December 2002
</div>
-->

<xsl:apply-templates select="sidebar/block"/>
</td>
</tr>

<!--
<tr>
<td colspan="2" id="footer">
Copyright &#169; Apache Software Foundation
</td>
</tr>
-->
</table>
</body>
</html>
</xsl:template>

<xsl:template match="block">
<div class="sidebar-title"><xsl:value-of select="title"/></div>
<xsl:apply-templates select="content"/>
</xsl:template>

<xsl:template match="content">
<div class="sidebar-content">
<!-- FIXME: Don't copy "content" tag, only children -->
<xsl:copy-of select="."/>
<xsl:copy/>
</div>
</xsl:template>
 
</xsl:stylesheet>
