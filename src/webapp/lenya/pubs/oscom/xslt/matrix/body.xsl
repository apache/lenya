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

<!-- $Id: body.xsl,v 1.15 2004/03/13 12:42:07 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template name="html-title">
<xsl:value-of select="/lenya/cmsbody/oscom/system/system_name"/> - CMS Matrix
</xsl:template>

<xsl:template name="admin-url">
<xsl:param name="prefix"/>
<a class="breadcrumb"><xsl:attribute name="href"><xsl:value-of select="$prefix"/>/matrix/<xsl:value-of select="/lenya/cmsbody/oscom/system/id"/>.html</xsl:attribute>Edit</a> - Apache Lenya
</xsl:template>
 
<xsl:template name="body">
  <xsl:apply-templates select="system"/>
</xsl:template>

<xsl:template match="system">
 <font face="verdana">

 <h3>
<xsl:choose>
  <xsl:when test="@type='cms'">
Content Management System
  </xsl:when>
  <xsl:when test="@type='framework'">
Content Management Framework
  </xsl:when>
  <xsl:when test="@type='editor'">
TTW WYSIWYG Editor
  </xsl:when>
  <xsl:otherwise>
No such type: <xsl:value-of select="@type"/>
  </xsl:otherwise>
</xsl:choose>
</h3>

 <h2><xsl:value-of select="system_name"/></h2>
 <xsl:apply-templates select="editor"/>
 <xsl:apply-templates select="description"/>
 <ul>
   <li><b>Home:</b><xsl:text> </xsl:text><a href="{main_url}" target="_blank"><xsl:apply-templates select="main_url"/></a></li>
   <li><xsl:apply-templates select="license"/></li>
   <xsl:apply-templates select="programming-language"/>
 </ul>
 <xsl:apply-templates select="related-info" />
 <xsl:apply-templates select="features" />
 </font>
</xsl:template>

<xsl:template match="license">
<b>License:</b><xsl:text> </xsl:text><a href="{license_url}" target="_blank"><xsl:apply-templates select="license_name"/></a>
</xsl:template>

<xsl:template match="programming-language">
 <li><b>Programming Language:</b><xsl:text> </xsl:text><xsl:value-of select="."/></li>
</xsl:template>

<xsl:template match="description">
  <p><xsl:copy-of select="."/></p>
</xsl:template>

<xsl:template match="editor">
<p>
<font size="-1">
Data maintained by <i><a href="mailto:{email}?subject=OSCOM CMS Matrix: {../system_name}"><xsl:value-of select="name"/></a></i>
</font>
</p>
</xsl:template>

<xsl:template match="related-info">
  <b>Related Information:</b>
  <ul>
  <xsl:for-each select="info-item">
    <li><a href="{uri}" target="_blank"><xsl:value-of select="title"/></a></li>
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
