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

<!-- $Id: body.xsl,v 1.15 2004/04/24 20:59:15 gregor Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>

<xsl:template match="echo:entry">
  <div class="dateline"><xsl:value-of select="echo:issued"/></div>
  <xsl:apply-templates select="echo:title"/>
  <xsl:apply-templates select="echo:summary"/>
  <xsl:apply-templates select="echo:content"/>
  <br />
  <p class="issued">
  <strong>Posted <xsl:apply-templates select="echo:author"/> at <xsl:value-of select="echo:issued"/>&#160;|&#160;<xsl:call-template name="permalink"><xsl:with-param name="id" select="echo:id"/></xsl:call-template></strong>
  </p>
</xsl:template>


<xsl:template match="echo:title">
  <div class="title"><xsl:value-of select="."/></div>
</xsl:template>


<xsl:template match="echo:summary">
  <i><xsl:copy-of select="node()"/></i>
</xsl:template>

<xsl:template match="echo:content[@type='application/xhtml+xml']">
  <xsl:copy-of select="node()"/>
</xsl:template>

<xsl:template match="echo:content">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="echo:author">
by
<xsl:choose>
<xsl:when test="echo:homepage">
<a href="{echo:homepage}"><xsl:value-of select="echo:name"/></a>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="echo:name"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template name="permalink">
  <a href="index.html">Permalink</a>
</xsl:template>
 
</xsl:stylesheet>  
