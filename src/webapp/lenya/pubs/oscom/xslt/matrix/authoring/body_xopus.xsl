<?xml version="1.0"?>
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

<!-- $Id: body_xopus.xsl,v 1.4 2004/03/13 12:42:13 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="text()">
  <xsl:copy />
</xsl:template>

<xsl:template match="system">
<div>
 <font face="verdana">

 <h3>Content Management
<xsl:choose>
  <xsl:when test="@type='cms'">
System
  </xsl:when>
  <xsl:when test="@type='framework'">
Framework
  </xsl:when>
  <xsl:otherwise>
<xsl:value-of select="."/>
  </xsl:otherwise>
</xsl:choose>
</h3>

 <h2><xsl:value-of select="system_name"/></h2>
 <xsl:apply-templates select="editor"/>
 <xsl:apply-templates select="description"/>
 <ul>
   <li>Home: <xsl:apply-templates select="main_url"/></li>
   <li><xsl:apply-templates select="license"/></li>
   <xsl:apply-templates select="programming-language"/>
 </ul>
 <xsl:apply-templates select="related-info"/>
 <xsl:apply-templates select="features"/>
 </font>
</div>
</xsl:template>

<xsl:template match="license">
License Name: <xsl:apply-templates select="license_name"/>
<br />License URI: <xsl:apply-templates select="license_url"/>
</xsl:template>

<xsl:template match="programming-language">
<li>Programming Language: <xsl:value-of select="."/></li>
</xsl:template>

<xsl:template match="description">
  <p><xsl:copy-of select="."/></p>
</xsl:template>

<xsl:template match="description" mode="feature">
<xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="editor">
<p>
<font size="-1">
Data maintained by <xsl:value-of select="name"/> (<xsl:value-of select="email"/>)
</font>
</p>
</xsl:template>

<xsl:template match="related-info">
  <b>Related Information:</b>
  <ul>
  <xsl:for-each select="info-item">
    <li><xsl:value-of select="title"/> (<xsl:value-of select="uri"/>)</li>
  </xsl:for-each>
  </ul>
</xsl:template>

<xsl:template match="features">
  <b>Features:</b>
  <ul>
  <xsl:for-each select="feature">
    <li><b><xsl:value-of select="title"/>:</b><xsl:text> </xsl:text><xsl:apply-templates select="description" mode="feature"/></li>
  </xsl:for-each>
  </ul>
</xsl:template>
 
</xsl:stylesheet>  
