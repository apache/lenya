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
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:meta="http://apache.org/lenya/meta/1.0/"
  exclude-result-prefixes="xhtml col meta"
  >
  
  <xsl:import href="fallback://lenya/modules/news/xslt/collection2xhtml.xsl"/>
  
  <xsl:param name="nodeid"/>
  <xsl:param name="pub"/>
  <xsl:param name="area"/>
  <xsl:param name="language"/>
  <xsl:param name="uuid"/>
  
  <xsl:variable name="includeItems" select="/col:collection/@includeItems"/>
  
  <xsl:template match="/col:collection">
    <div id="news">
      <div class="rsslink">
        <a type="application/rss+xml" href="lenya-document:{$uuid},pub={$pub},area={$area},lang={$language}?uuid2url.extension=rss">RSS 2.0</a>
      </div>
      <h1><meta:value element="title" ns="http://purl.org/dc/elements/1.1/"
        uuid="{$uuid}" lang="{$language}"/></h1>
      <xsl:for-each select="col:document">
        <xsl:if test="position() &lt;= number($includeItems)">
          <xsl:apply-templates select="."/>
        </xsl:if>
      </xsl:for-each>
    </div>
  </xsl:template>
  
  
</xsl:stylesheet> 
