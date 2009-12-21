<?xml version="1.0"?>
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
<!-- CVS $Id: search2html.xsl 47285 2004-09-27 12:52:44Z cziegeler $ -->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:search="http://apache.org/cocoon/search/1.0"
  xmlns:openSearch="http://a9.com/-/spec/opensearchrss/1.0/"
  exclude-result-prefixes="openSearch"
>

<xsl:param name="language"/>
<xsl:param name="root"/>
<xsl:param name="publication-id"/>
<xsl:param name="page-length"/>

  <xsl:template match="search:results">  
    <rss version="2.0">
      <title><xsl:value-of select="$publication-id"/></title>
      <link><xsl:value-of select="$root"/></link>
      <description></description>
      <language><xsl:value-of select="$language"/></language>
      <xsl:apply-templates/>
    </rss>
  </xsl:template>


  <xsl:template match="search:hits">
      <openSearch:totalResults><xsl:value-of select="@total-count"/></openSearch:totalResults>
      <openSearch:startIndex><xsl:value-of select="@count-of-pages"/></openSearch:startIndex>
      <openSearch:itemsPerPage><xsl:value-of select="$page-length"/></openSearch:itemsPerPage>
      
      <xsl:for-each select="/search:results/search:navigation/search:navigation-page">
        <xsl:call-template name="navigation-link"> 
          <xsl:with-param name="query-string" select="/search:results/@query-string"/>
          <xsl:with-param name="page-length" select="/search:results/@page-length"/>
          <xsl:with-param name="start-index" select="@start-index"/>
          <xsl:with-param name="link-text" select="position()"/>
        </xsl:call-template>
      </xsl:for-each>
    
    <xsl:call-template name="navigation-paging-link">
      <xsl:with-param name="query-string" select="/search:results/@query-string"/>
      <xsl:with-param name="page-length" select="/search:results/@page-length"/>
      <xsl:with-param name="has-previous" select="/search:results/search:navigation/@has-previous"/>
      <xsl:with-param name="has-next" select="/search:results/search:navigation/@has-next"/>
      <xsl:with-param name="previous-index" select="/search:results/search:navigation/@previous-index"/>
      <xsl:with-param name="next-index" select="/search:results/search:navigation/@next-index"/>
    </xsl:call-template>
    
    <xsl:apply-templates/>
    
  </xsl:template>

  <xsl:template match="search:navigation">
    <xsl:call-template name="navigation-paging-form">
      <xsl:with-param name="query-string"><xsl:value-of select="/search:results/@query-string"/></xsl:with-param>
      <xsl:with-param name="page-length"><xsl:value-of select="/search:results/@page-length"/></xsl:with-param>
      <xsl:with-param name="has-previous"><xsl:value-of select="@has-previous"/></xsl:with-param>
      <xsl:with-param name="has-next"><xsl:value-of select="@has-next"/></xsl:with-param>
      <xsl:with-param name="previous-index"><xsl:value-of select="@previous-index"/></xsl:with-param>
      <xsl:with-param name="next-index"><xsl:value-of select="@next-index"/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="search:hit">
      <item>
        <title><xsl:value-of select="search:field[@name='title']"/></title>
        <link><xsl:value-of select="@uri"/></link>
        <description><xsl:value-of select="search:field[@name='description']"/></description>
      </item>
  </xsl:template>

  <xsl:template name="navigation-paging-form">
    <xsl:param name="query-string"/>
    <xsl:param name="page-length"/>
    <xsl:param name="has-previous"/>
    <xsl:param name="has-next"/>
    <xsl:param name="previous-index"/>
    <xsl:param name="next-index"/>

    <xsl:if test="$has-previous = 'true'">
      <form action="findIt" id="form-opensearch_previous">
        <input type="hidden" name="startIndex" value="{$previous-index}"/>
        <input type="hidden" name="queryString" value="{$query-string}"/>
        <input type="hidden" name="pageLength" value="{$page-length}"/>
        <input type="submit" name="previous" value="previous"/>
      </form>
    </xsl:if>
    
    <xsl:if test="$has-next = 'true'">
      <form action="findIt" id="form-opensearch_next">
        <input type="hidden" name="startIndex" value="{$next-index}"/>
        <input type="hidden" name="queryString" value="{$query-string}"/>
        <input type="hidden" name="pageLength" value="{$page-length}"/>
        <input type="submit" name="next" value="next"/>
      </form>
    </xsl:if>
    
  </xsl:template>

  <xsl:template name="navigation-paging-link">
    <xsl:param name="query-string"/>
    <xsl:param name="page-length"/>
    <xsl:param name="has-previous"/>
    <xsl:param name="has-next"/>
    <xsl:param name="previous-index"/>
    <xsl:param name="next-index"/>

    <xsl:if test="$has-previous = 'true'">
      
      <xsl:call-template name="navigation-link">
        <xsl:with-param name="query-string"><xsl:value-of select="$query-string"/></xsl:with-param>
        <xsl:with-param name="page-length"><xsl:value-of select="$page-length"/></xsl:with-param>
        <xsl:with-param name="start-index"><xsl:value-of select="$previous-index"/></xsl:with-param>
        <xsl:with-param name="link-text">Previous Page Of Hits</xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    &#160;
    <xsl:if test="$has-next = 'true'">
      <a href="findIt?startIndex={$next-index}&amp;queryString={$query-string}&amp;pageLength={$page-length}">
        Next Page Of Hits
      </a>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="navigation-link">
    <xsl:param name="query-string"/>
    <xsl:param name="page-length"/>
    <xsl:param name="start-index"/>
    <xsl:param name="link-text"/>

    <a href="findIt?startIndex={$start-index}&amp;queryString={$query-string}&amp;pageLength={$page-length}">
      <xsl:value-of select="$link-text"/>
    </a>
    &#160;
  </xsl:template>

  <xsl:template match="@*|node()" priority="-2"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>
  <xsl:template match="text()" priority="-1"><xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>

