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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:meta="http://apache.org/lenya/meta/1.0/"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:user="http://apache.org/lenya/userinfo/1.0"
  exclude-result-prefixes="xhtml lenya col meta dc i18n xml"
  >
  
  <xsl:include href="fallback://lenya/modules/xhtml/xslt/helper-object.xsl"/>
  <xsl:include href="shared.xsl"/>
  
  <xsl:param name="nodeid"/>
  <xsl:param name="language"/>
  <xsl:param name="area"/>
  <xsl:param name="baseUrl"/>
  
  
  <xsl:template match="/col:collection">
    
    <rss version="2.0">
      
      <channel>
        <title><meta:value ns="http://purl.org/dc/elements/1.1/" element="title"
          uuid="{@uuid}" lang="{$language}"/></title>
        <link><xhtml:a href="{$baseUrl}"/></link>
        <description><meta:value ns="http://purl.org/dc/elements/1.1/" element="description"
          uuid="{@uuid}" lang="{$language}"/></description>
        <language><xsl:value-of select="$language"/></language>
        <copyright><meta:value ns="http://purl.org/dc/elements/1.1/" element="rights"
        uuid="{@uuid}" lang="{$language}"/></copyright>
        <xsl:if test="col:document">
          <pubDate><i18n:date-time locale="en" src-pattern="yyyy-MM-dd hh:mm:ss" pattern="EEE, dd MMM yyyy HH:mm:ss Z" value="{col:document[1]/dc:date}"/></pubDate>
        </xsl:if>
        
        <!--
          <image>
          <url></url>
          <title></title>
          <link></link>
          </image>
        -->
        
        <xsl:apply-templates select="col:document"/>
        
      </channel>
      
    </rss>
  </xsl:template>
  
  
  <xsl:template match="col:document[xhtml:html]">
    <item>
      <title><meta:value element="title" ns="http://purl.org/dc/elements/1.1/" uuid="{@uuid}" lang="{@xml:lang}"/></title>
      <description><meta:value element="description" ns="http://purl.org/dc/elements/1.1/" uuid="{@uuid}" lang="{@xml:lang}"/></description>
      <xsl:variable name="href">
        <xsl:call-template name="getHref"/>
        </xsl:variable>
      <link><xhtml:a href="{$href}"/></link>
      <author><user:fullname><meta:value element="creator" ns="http://purl.org/dc/elements/1.1/" uuid="{@uuid}" lang="{@xml:lang}"/></user:fullname></author>
      <pubDate><i18n:date-time locale="en" src-pattern="yyyy-MM-dd hh:mm:ss" pattern="EEE, dd MMM yyyy HH:mm:ss Z" value="{dc:date}"/></pubDate>
    </item>
  </xsl:template>
  
  
  <xsl:template match="col:document[*[local-name() = 'item']]">
    <xsl:apply-templates mode="stripNamespace"/>
  </xsl:template>
  
  
  <xsl:template match="*" mode="stripNamespace">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()" mode="stripNamespace"/>
    </xsl:element>
  </xsl:template>
  
  
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet> 
