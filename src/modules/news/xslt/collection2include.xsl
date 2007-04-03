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
    xmlns:meta="http://apache.org/cocoon/lenya/metadata/1.0"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    exclude-result-prefixes="xhtml lenya col meta dc i18n"
    >
    
  <xsl:import href="fallback://lenya/modules/news/xslt/collection2xhtml.xsl"/>
  
  <xsl:param name="rendertype" select="''"/>
  <xsl:param name="nodeid"/>
  <xsl:param name="language"/>
  <xsl:param name="uuid"/>
  
  
  <xsl:template match="/col:collection">
    <xsl:variable name="title" select="meta:metadata/dc:elements/dc:title"/>
    <div id="news">
      <div class="rsslink">
        <a type="application/rss+xml" href="lenya-document:{$uuid}?uuid2url.extension=rss">RSS 2.0</a>
      </div>
      <h1><xsl:value-of select="$title"/></h1>
      <xsl:apply-templates select="col:document">
        <xsl:sort order="descending" select="meta:metadata/dc:elements/dc:date"/>
      </xsl:apply-templates>
    </div>
  </xsl:template>


</xsl:stylesheet> 
