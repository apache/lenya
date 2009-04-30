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
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:cinclude="http://apache.org/cocoon/include/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:meta="http://apache.org/lenya/meta/1.0/"
  exclude-result-prefixes="cinclude search xhtml"
>

  <xsl:param name="url"/>
  <xsl:param name="area"/>
  <xsl:param name="pub"/>
  <xsl:param name="root"/>
  <xsl:param name="lenya.usecase"/>
  <xsl:param name="queryString"/>
  <xsl:param name="type"/>
  
  <xsl:variable name="start-index" select="/search:results/@start-index"/>
  <xsl:variable name="page-length" select="/search:results/@page-length"/>
  <xsl:variable name="query-string" select="/search:results/@query-string"/>
  
  <xsl:variable name="selectedType">
    <xsl:choose>
      <xsl:when test="$type = 'images' or $type = 'documents'">
        <xsl:value-of select="$type"/>
      </xsl:when>
      <xsl:otherwise>documents</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:variable name="usecaseParam">
    <xsl:if test="$lenya.usecase != ''">
      <xsl:text>lenya.usecase=</xsl:text>
      <xsl:value-of select="$lenya.usecase"/>
      <xsl:text>&amp;</xsl:text>
    </xsl:if>
  </xsl:variable>
  
  <xsl:template match="search:results">  
    <html>
      <body>
        <h1><i18n:text>Search</i18n:text></h1>
        <form class="search-results-form" action="" method="get">
          <p>
            <input name="queryString" type="text" style="width: 400px" value="{$queryString}"
            />&#160;<input type="submit" name="submit" value="Search" i18n:attr="value"/>
          </p>
          <p>
            <xsl:call-template name="type">
              <xsl:with-param name="searchType">documents</xsl:with-param>
            </xsl:call-template>
            &#160;&#160;&#160;
            <xsl:call-template name="type">
              <xsl:with-param name="searchType">images</xsl:with-param>
            </xsl:call-template>
          </p>
        </form>
        <xsl:apply-templates select="search:hits"/>
      </body>
    </html>
  </xsl:template>
  
  
  <xsl:template name="type">
    <xsl:param name="searchType"/>
    <xsl:param name="default"/>
    <input type="radio" name="type" value="{$searchType}">
      <xsl:if test="$selectedType = $searchType">
        <xsl:attribute name="checked">checked</xsl:attribute>
      </xsl:if>
    </input>
    <i18n:text><xsl:value-of select="$searchType"/></i18n:text>
  </xsl:template>
  

  <xsl:template match="search:hits">
    <!--
    <h1>
        <xsl:value-of select="@total-count"/> hit<xsl:if test="@total-count &gt; 1">s</xsl:if>
        <xsl:text>, </xsl:text>
        <xsl:value-of select="@count-of-pages"/> result page<xsl:if test="@count-of-pages &gt; 1">s</xsl:if> on query
        <em><xsl:value-of select="/search:results/@query-string"/></em>
    </h1>
    -->
    <h2>
      <xsl:choose>
        <xsl:when test="@total-count = 1">
          <i18n:text>1 hit</i18n:text>
        </xsl:when>
        <xsl:otherwise>
          <i18n:translate>
            <i18n:text>… hits</i18n:text>
            <i18n:param><xsl:value-of select="@total-count"/></i18n:param>
          </i18n:translate>
        </xsl:otherwise>
      </xsl:choose>
    </h2>
    <ul class="search-results {$selectedType}">
      <xsl:choose>
        <xsl:when test="$selectedType = 'images'">
          <xsl:apply-templates select="search:hit" mode="image"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="search:hit" mode="document"/>
        </xsl:otherwise>
      </xsl:choose>
    </ul>
    
    <hr style="clear: both; border: none;" size="0"/>
    
    <xsl:variable name="pages" select="/search:results/search:navigation/search:navigation-page"/>
    <xsl:if test="count($pages) &gt; 1">
      <p>
        <i18n:text>Pages</i18n:text><xsl:text>: </xsl:text>
  
        <xsl:variable name="has-previous" select="/search:results/search:navigation/@has-previous"/>
        <xsl:if test="$has-previous = 'true'">
          <xsl:variable name="previous-index" select="/search:results/search:navigation/@previous-index"/>
          <xsl:call-template name="navigation-link">
            <xsl:with-param name="page-length"><xsl:value-of select="$page-length"/></xsl:with-param>
            <xsl:with-param name="start-index"><xsl:value-of select="$previous-index"/></xsl:with-param>
            <xsl:with-param name="link-text">&lt;</xsl:with-param>
          </xsl:call-template>
          <xsl:text> </xsl:text>
        </xsl:if>
        
        <xsl:for-each select="$pages">
          <xsl:call-template name="navigation-link"> 
            <xsl:with-param name="start-index" select="@start-index"/>
            <xsl:with-param name="link-text" select="position()"/>
            <xsl:with-param name="isCurrent" select="@start-index = $start-index"/>
          </xsl:call-template>
        </xsl:for-each>
        
        <xsl:variable name="has-next" select="/search:results/search:navigation/@has-next"/>
        <xsl:if test="$has-next = 'true'">
          <xsl:text> </xsl:text>
          <xsl:variable name="next-index" select="/search:results/search:navigation/@next-index"/>
          <a href="?{$usecaseParam}startIndex={$next-index}&amp;queryString={$queryString}&amp;pageLength={$page-length}&amp;type={$selectedType}">
            <xsl:text>&gt;</xsl:text>
          </a>
        </xsl:if>
        
      </p>
    </xsl:if>
    
  </xsl:template>


  <xsl:template match="search:navigation"/>
  
  
  
  <xsl:template match="search:hit" mode="image">
    <li>
      <xsl:variable name="uuid" select="search:field[@name='uuid']"/>
      <xsl:variable name="language" select="search:field[@name='language']"/>
      <a href="lenya-document:{$uuid},lang={$language}?uuid2url.extension=html">
        <img src="lenya-document:{$uuid},lang={$language}?lenya.module=svg&amp;height=100">
          <meta:value element="title" ns="http://purl.org/dc/elements/1.1/" default="No Title"
            i18n:attr="default" uuid="{$uuid}" lang="{$language}"/>
        </img>
      </a>
      <div class="imageTitle">
        <meta:value element="title" ns="http://purl.org/dc/elements/1.1/" default="No Title"
          i18n:attr="default" uuid="{$uuid}" lang="{$language}"/>
      </div>
    </li>
  </xsl:template>
  
  
  <xsl:template match="search:hit" mode="document">
    <li>
      <div class="search-result-rank"><xsl:value-of select="@rank + 1"/>. </div>
      <xsl:variable name="uuid" select="search:field[@name='uuid']"/>
      <xsl:variable name="language" select="search:field[@name='language']"/>
      <div class="search-result-title">
        <a href="lenya-document:{$uuid},lang={$language}">
          <meta:value element="title" ns="http://purl.org/dc/elements/1.1/" default="No Title"
            i18n:attr="default" uuid="{$uuid}" lang="{$language}"/>
        </a>
        <span class="search-result-score"> (<i18n:text>Score</i18n:text>: <xsl:value-of select="format-number( @score, '### %' )"/>)</span>
      </div>
      <div class="search-result-description">
        <meta:value element="description" ns="http://purl.org/dc/elements/1.1/" default="" uuid="{$uuid}" lang="{$language}"/>
      </div>
    </li>
  </xsl:template>
  

  <xsl:template name="navigation-link">
    <xsl:param name="query-string"/>
    <xsl:param name="page-length"/>
    <xsl:param name="start-index"/>
    <xsl:param name="link-text"/>
    <xsl:param name="isCurrent" select="false()"/>
    <xsl:choose>
      <xsl:when test="$isCurrent">
        <strong><xsl:value-of select="$link-text"/></strong>
      </xsl:when>
      <xsl:otherwise>
        <a href="?{$usecaseParam}startIndex={$start-index}&amp;queryString={$queryString}&amp;pageLength={$page-length}&amp;type={$selectedType}">
          <xsl:value-of select="$link-text"/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text> </xsl:text>
  </xsl:template>
  

  <xsl:template match="@*|node()" priority="-2"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>
  <xsl:template match="text()" priority="-1"><xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>

