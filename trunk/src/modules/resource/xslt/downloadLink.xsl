<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: xhtml-standard.xsl,v 1.44 2004/12/14 11:00:41 josias Exp $ -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:meta="http://apache.org/cocoon/lenya/metadata/1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:media="http://apache.org/lenya/pubs/default/media/1.0"
  xmlns:mediameta="http://apache.org/lenya/metadata/media/1.0"
  xmlns:docmeta="http://apache.org/lenya/metadata/document/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:dcterms="http://purl.org/dc/terms/"
  exclude-result-prefixes="xhtml meta dc">
  
  <xsl:import href="fallback://lenya/modules/resource/xslt/common/mimetype.xsl"/>
  
  
  <xsl:param name="documentUrl"/>
  <xsl:param name="sourceExtension"/>
  <xsl:param name="contentLength"/>
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  <xsl:param name="imageprefix"/>
  <xsl:param name="mimeType"/>
  
  <xsl:variable name="mediaUrl" select="concat(substring-before($documentUrl, '.html'), '.', $sourceExtension)"/>
  
  <xsl:template match="/">
    <xsl:apply-templates select="//meta:metadata" mode="media"/>
  </xsl:template>
    
  <xsl:template match="meta:metadata" mode="media">
  
    <xsl:variable name="title">
      <xsl:choose>
        <xsl:when test="normalize-space(dc:elements/dc:title) != ''">
          <xsl:value-of select="dc:elements/dc:title"/>
        </xsl:when>
        <xsl:otherwise>
          <i18n:text>No title</i18n:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="size">
      <xsl:value-of select="format-number($contentLength div 1024, '#,###.##')"/>
    </xsl:variable>
    
    <span class="asset">
      <a href="lenya-document:{$uuid},lang={$language}" title="{text()}">
        <xsl:call-template name="icon">
          <xsl:with-param name="mimetype" select="$mimeType"/>
          <xsl:with-param name="imageprefix" select="$imageprefix"/>
        </xsl:call-template>
      </a>
      &#160;
      <a href="lenya-document:{$uuid},lang={$language}" title="{text()}">
        <xsl:value-of select="$title"/>
      </a>
      (<xsl:value-of select="number($size)"/> KB)
    </span>

  </xsl:template>
  
</xsl:stylesheet>
