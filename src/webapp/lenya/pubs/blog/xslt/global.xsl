<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
>

<xsl:param name="relative2root"/>

<xsl:template match="cmsbody">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="service.edit" type="application/x.atom+xml" href="introspection.xml" title="AtomAPI"/>
<link rel="stylesheet" type="text/css" href="css/styles.css" title="default css"/>
<title>
  <!-- FIXME: namespace -->
  <xsl:value-of select="feed/title"/>
  <xsl:value-of select="echo:feed/echo:title"/>
</title>
</head>

<body>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td colspan="2" id="title">
  <a href="{$relative2root}/index.html">
    <!-- FIXME: namespace -->
    <xsl:value-of select="feed/title"/>
    <xsl:value-of select="echo:feed/echo:title"/>
  </a>
</td>
</tr>
<tr>
<td colspan="2" id="subtitle">
    XML and beyond <!-- subtitle has been removed from Atom specification -->
</td>
</tr>

<tr>
<td valign="top" id="content" width="70%">
    <!-- FIXME: namespace -->
    <xsl:apply-templates select="feed/entry"/>
    <xsl:apply-templates select="echo:feed/echo:entry"/>
</td>

<td valign="top" id="sidebar" width="30%">

<!--
<div class="sidebar-title">Archives&#160;by&#160;Date</div>
<div class="sidebar-content">
<a href="{$relative2root}/2003/07/">July 2003</a>
</div>
-->

<xsl:apply-templates select="sidebar/block" mode="atom"/>
</td>
</tr>

<tr>
<td colspan="2" id="footer">
Copyright &#169; 2003 The Apache Software Foundation. All rights reserved.
</td>
</tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template match="block" mode="atom">
<div class="sidebar-title"><xsl:value-of select="title"/></div>
<xsl:apply-templates select="content" mode="sidebar"/>
</xsl:template>

<xsl:template match="content" mode="sidebar">
<div class="sidebar-content">
<xsl:copy-of select="@*|node()"/>
</div>
</xsl:template>
 
</xsl:stylesheet>
