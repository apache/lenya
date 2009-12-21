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

<!-- $Id: xhtml2xhtml.xsl 201776 2005-06-25 18:25:26Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:meta="http://apache.org/lenya/meta/1.0/"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  exclude-result-prefixes="xhtml lenya col meta dc i18n"
  >
  
  <xsl:include href="fallback://lenya/modules/xhtml/xslt/helper-object.xsl"/>
  <xsl:include href="shared.xsl"/>
  
  <xsl:param name="nodeid"/>
  <xsl:param name="language"/>
  <xsl:param name="pub"/>
  <xsl:param name="area"/>
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  
  <xsl:variable name="maxChars">100</xsl:variable>
  
  
  <xsl:template match="/col:collection">
    <html>
      <body>
        <div id="body">
          <h1>
            <div class="rsslink">
              <a type="application/rss+xml" href="lenya-document:{$uuid},pub={$pub},area={$area},lang={$language}?uuid2url.extension=rss">RSS 2.0</a>
            </div>
            <meta:value ns="http://purl.org/dc/elements/1.1/" element="title"
            uuid="{$uuid}" lang="{$language}"/></h1>
          
          <xsl:if test="$area = 'authoring'">
            <p style="color: #999999;">
              <i18n:text>new-news-message-hint</i18n:text>
            </p>
          </xsl:if>
          
          <xsl:apply-templates select="col:document"/>
        </div>
      </body>
    </html>
  </xsl:template>
  
  
  <xsl:template match="col:document">
    <div class="newsItem">
      <h2>
        <span class="newsDate">
          <i18n:date-time src-pattern="yyyy-MM-dd HH:mm:ss" locale="{$language}" value="{dc:date}" />
        </span><br />
        <xsl:variable name="href">
          <xsl:call-template name="getHref"/>
        </xsl:variable>
        <a href="{$href}" style="text-decoration: none">
          <xsl:choose>
            <xsl:when test="dc:title">
              <xsl:value-of select="dc:title"/>
            </xsl:when>
            <xsl:otherwise>
              <meta:value element="title" ns="http://purl.org/dc/elements/1.1/" uuid="{@uuid}" lang="{@xml:lang}"/>
            </xsl:otherwise>
          </xsl:choose>
        </a>
      </h2>
      <xsl:apply-templates select="xhtml:html/xhtml:body//xhtml:p[1]" mode="excerpt"/>
    </div>
  </xsl:template>
  
  
  <xsl:template match="xhtml:p" mode="excerpt">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:variable name="content" select="."/>
      <xsl:value-of select="substring($content, 1, $maxChars)"/>
      <xsl:if test="string-length($content) &gt; $maxChars">
        <xsl:text>&#x2026;</xsl:text>
      </xsl:if>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet> 
