<?xml version="1.0" encoding="iso-8859-1"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: global.xsl,v 1.17 2004/03/13 12:31:34 gregor Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
>

<xsl:param name="relative2root"/>
<xsl:param name="contextprefix"/>

<xsl:template match="cmsbody">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="service.edit" type="application/x.atom+xml" href="introspection.xml" title="AtomAPI"/>
<link rel="stylesheet" type="text/css" href="{$contextprefix}/blog/live/css/styles.css" title="default css"/>
<title>
  <xsl:value-of select="echo:feed/echo:title"/>
</title>
</head>

<body>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td colspan="2" id="title">
  <a href="{$relative2root}/index.html">
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
    <xsl:if test="not(echo:feed/echo:entry)">
    <p>
    No entries yet!
    </p>
    <h3>Add new entry</h3>
    <p>
    To create a new entry click on the <b>File</b> menu above and select the menu item <b>Add new entry</b>. Enter <b>id</b> and <b>title</b> and a new entry will be created.
    </p>
    <h3>Edit entry</h3>
    <p>
    To edit an entry click on the title of the entry. On the entry page click on the <b>File</b> menu and select the menu item <b>Edit with ...</b>.
    </p>
    <h3>Publish entry</h3>
    <p>
    To publish an entry click on the <b>File</b> menu and select the menu item <b>Publish</b>.
    </p>
    </xsl:if>

    <xsl:apply-templates select="echo:feed/echo:entry"/>
</td>

<td valign="top" id="sidebar" width="30%">

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
