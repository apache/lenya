<?xml version="1.0" encoding="iso-8859-1"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
  xmlns:blog="http://apache.org/cocoon/blog/1.0"
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
  <xsl:choose>
    <xsl:when test="not(echo:feed/echo:entry) and count(blog:overview)=1">
    <p>
    No entries yet!
    </p>
    <h3>Add new entry</h3>
    <p>
    To create a new entry click on the <strong>File</strong> menu above and select the menu item <strong>Add new entry</strong>. Enter <strong>id</strong> and <strong>title</strong> and a new entry will be created.
    </p>
    <h3>Edit entry</h3>
    <p>
    To edit an entry click on the title of the entry. On the entry page click on the <strong>File</strong> menu and select the menu item <strong>Edit with ...</strong>.
    </p>
    <h3>Publish entry</h3>
    <p>
    To publish an entry click on the <strong>File</strong> menu and select the menu item <strong>Publish</strong>.
    </p>
    </xsl:when>
    <xsl:when test="echo:feed/echo:entry">
      <xsl:apply-templates select="echo:feed/echo:entry"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="blog:overview[not(@structure)]" mode="overview"/>
    </xsl:otherwise>
  </xsl:choose>
</td>

<td valign="top" id="sidebar" width="30%">

<xsl:apply-templates select="sidebar/block" mode="atom"/>
<xsl:apply-templates select="blog:overview[@structure]" mode="sidebar"/>	
</td>
</tr>

<tr>
<td colspan="2" id="footer">
Copyright &#169; 2006 The Apache Software Foundation. All rights reserved.
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

<xsl:template match="blog:overview" mode="sidebar"> 
<div class="sidebar-title"><a href="{$relative2root}/overview.html">Archive</a></div>
<div class="sidebar-content">
<xsl:apply-templates select="blog:year" mode="sidebar"/>
</div>	
</xsl:template>

<xsl:template match="blog:year" mode="sidebar">
<div class="overview-year-sidebar">
<ul class="overview-year">
  <li class="overview-year-li"><a href="{$relative2root}/overview.html?year={@id}"><xsl:value-of select="@id"/></a></li>
  <ul class="overview-month">
    <xsl:apply-templates select="blog:month" mode="sidebar"/>
  </ul>
</ul>
</div>
</xsl:template>

<xsl:template match="blog:month" mode="sidebar">
<li class="overview-month">
  <a href="{$relative2root}/overview.html?year={../@id}&#38;month={@id}"><xsl:value-of select="@id"/></a>
  <ul class="overview-day">
    <xsl:apply-templates select="blog:day" mode="sidebar"/>
  </ul>
</li>
</xsl:template>

<xsl:template match="blog:day" mode="sidebar">
<li class="overview-day">
  <a href="{$relative2root}/overview.html?year={../../@id}&#38;month={../@id}&#38;day={@id}"><xsl:value-of select="@id"/></a>
</li>
</xsl:template>

</xsl:stylesheet>
