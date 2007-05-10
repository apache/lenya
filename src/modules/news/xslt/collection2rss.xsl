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
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
    xmlns:meta="http://apache.org/cocoon/lenya/metadata/1.0"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    exclude-result-prefixes="xhtml lenya col meta dc i18n"
    >
    
  <xsl:include href="fallback://lenya/modules/xhtml/xslt/helper-object.xsl"/>
  <xsl:include href="shared.xsl"/>
  
  <xsl:param name="rendertype" select="''"/>
  <xsl:param name="nodeid"/>
  <xsl:param name="language"/>
  <xsl:param name="area"/>
  <xsl:param name="baseUrl"/>
  
  
  <xsl:template match="/col:collection">
    <xsl:variable name="title" select="meta:metadata/dc:elements/dc:title"/>
    
    <rss version="2.0">
      
      <channel>
        <title><xsl:value-of select="$title"/></title>
        <link><xsl:value-of select="$baseUrl"/></link>
        <description></description>
        <language><xsl:value-of select="$language"/></language>
        <copyright></copyright>
        <pubDate><xsl:call-template name="date"/></pubDate>
        
        <!--
        <image>
          <url></url>
          <title></title>
          <link></link>
        </image>
        -->
        
        <xsl:apply-templates select="col:document">
          <xsl:sort order="descending" select="meta:metadata/dc:elements/dc:date"/>
        </xsl:apply-templates>
        
      </channel>
      
    </rss>
  </xsl:template>
  
  
  <xsl:template name="date">
    <xsl:for-each select="col:document">
      <xsl:sort select="meta:metadata/dc:elements/dc:date"/>
      <xsl:if test="position() = 1">
        <xsl:variable name="date" select="meta:metadata/dc:elements/dc:date"/>
        <i18n:date-time locale="en" src-pattern="yyyy-MM-dd hh:mm:ss" pattern="EEE, dd MMM yyyy HH:mm:ss Z" value="{$date}" />
      </xsl:if>
    </xsl:for-each>
  </xsl:template>


  <xsl:template match="col:document">
    <item>
      <title><xsl:value-of select="meta:metadata/dc:elements/dc:title"/></title>
      <description><xsl:value-of select="meta:metadata/dc:elements/dc:description"/></description>
      <xsl:variable name="href">
        <xsl:call-template name="getHref"/>
      </xsl:variable>
      <link><xhtml:a href="lenya-document:{$href}"/></link>
      <author><xsl:value-of select="meta:metadata/dc:elements/dc:creator"/></author>
      <xsl:variable name="date" select="meta:metadata/dc:elements/dc:date"/>
      <pubDate><i18n:date-time locale="en" src-pattern="yyyy-MM-dd hh:mm:ss" pattern="EEE, dd MMM yyyy HH:mm:ss Z" value="{$date}" /></pubDate>
    </item>
  </xsl:template>


  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  

</xsl:stylesheet> 
