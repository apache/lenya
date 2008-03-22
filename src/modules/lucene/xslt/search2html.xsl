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
  exclude-result-prefixes="cinclude search xhtml"
>

  <xsl:param name="url"/>
  <xsl:param name="area"/>
  <xsl:param name="pub"/>
  <xsl:param name="root"/>
  <xsl:param name="lenya.usecase"/>
  <xsl:param name="queryString"/>
  
  <xsl:variable name="usecaseParam">
    <xsl:if test="$lenya.usecase != ''">
      <xsl:text>lenya.usecase=</xsl:text>
      <xsl:value-of select="$lenya.usecase"/>
      <xsl:text>&amp;</xsl:text>
    </xsl:if>
  </xsl:variable>
  
  <xsl:template match="search:results">  
    <div id="body">
      <h1><i18n:text>Search</i18n:text></h1>
      <form class="search-results-form" action="" method="get">
        <input name="queryString" type="text" style="width: 400px" value="{$queryString}"
        />&#160;<input type="submit" name="submit" value="Search" i18n:attr="value"/>
      </form>
      <xsl:apply-templates select="search:hits"/>
    </div>
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
    <ul class="search-results">
      <xsl:apply-templates/>
    </ul>
    
    <xsl:variable name="pages" select="/search:results/search:navigation/search:navigation-page"/>
    <xsl:if test="count($pages) &gt; 1">
      <p>
        <i18n:text>Pages</i18n:text><xsl:text>: </xsl:text>
  
        <xsl:for-each select="$pages">
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
      </p>
    </xsl:if>
    
  </xsl:template>

  <xsl:template match="search:navigation">
    <!--
    <p>
    <xsl:call-template name="navigation-paging-form">
      <xsl:with-param name="query-string"><xsl:value-of select="/search:results/@query-string"/></xsl:with-param>
      <xsl:with-param name="page-length"><xsl:value-of select="/search:results/@page-length"/></xsl:with-param>
      <xsl:with-param name="has-previous"><xsl:value-of select="@has-previous"/></xsl:with-param>
      <xsl:with-param name="has-next"><xsl:value-of select="@has-next"/></xsl:with-param>
      <xsl:with-param name="previous-index"><xsl:value-of select="@previous-index"/></xsl:with-param>
      <xsl:with-param name="next-index"><xsl:value-of select="@next-index"/></xsl:with-param>
    </xsl:call-template>
    </p>
    -->
  </xsl:template>
  
  <xsl:template match="search:hit">
    <li class="search-result">
      <div class="search-result-rank"><xsl:value-of select="@rank + 1"/>. </div>
      <div class="search-result-title">
        <xsl:variable name="titleField" select="search:field[attribute::name='title']"/>
        <xsl:variable name="title">
          <xsl:choose>
            <xsl:when test="normalize-space($titleField) != ''">
              <xsl:value-of select="$titleField"/>
            </xsl:when>
            <xsl:otherwise><i18n:text>No Title</i18n:text></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="normalize-space(search:field[@name = 'uid']) != ''">
            <a href="{$root}{search:field[attribute::name='uid']}"><xsl:value-of select="$title"/></a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$title"/> (<i18n:text>not in site structure</i18n:text>)
          </xsl:otherwise>
        </xsl:choose>
      <span class="search-result-score"> (<i18n:text>Score</i18n:text>: <xsl:value-of select="format-number( @score, '### %' )"/>)</span>
      </div>
      <div class="search-result-description"><xsl:value-of select="search:field[attribute::name='description']"/></div>
    </li>
  </xsl:template>

  <xsl:template name="navigation-paging-form">
    <xsl:param name="page-length"/>
    <xsl:param name="has-previous"/>
    <xsl:param name="has-next"/>
    <xsl:param name="previous-index"/>
    <xsl:param name="next-index"/>

    <xsl:if test="$has-previous = 'true'">
      <form action="" id="form-previous">
        <xsl:if test="$lenya.usecase != ''">
          <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
        </xsl:if>
        <input type="hidden" name="startIndex" value="{$previous-index}"/>
        <input type="hidden" name="queryString" value="{$queryString}"/>
        <input type="hidden" name="pageLength" value="{$page-length}"/>
        <input type="submit" name="previous" value="Previous" i18n:attr="value"/>
      </form>
    </xsl:if>
    
    <xsl:if test="$has-next = 'true'">
      <form action="" id="form-next">
        <xsl:if test="lenya.usecase != ''">
          <input type="hidden" name="lenya.usecase" value="{$lenya.usecase}"/>
        </xsl:if>
        <input type="hidden" name="startIndex" value="{$next-index}"/>
        <input type="hidden" name="queryString" value="{$queryString}"/>
        <input type="hidden" name="pageLength" value="{$page-length}"/>
        <input type="submit" name="next" value="Next" i18n:attr="value"/>
      </form>
    </xsl:if>
    
  </xsl:template>

  <xsl:template name="navigation-paging-link">
    <xsl:param name="page-length"/>
    <xsl:param name="has-previous"/>
    <xsl:param name="has-next"/>
    <xsl:param name="previous-index"/>
    <xsl:param name="next-index"/>

    <xsl:if test="$has-previous = 'true'">
      <xsl:call-template name="navigation-link">
        <xsl:with-param name="page-length"><xsl:value-of select="$page-length"/></xsl:with-param>
        <xsl:with-param name="start-index"><xsl:value-of select="$previous-index"/></xsl:with-param>
        <xsl:with-param name="link-text">&lt;</xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:text> </xsl:text>
    <xsl:if test="$has-next = 'true'">
      <a href="?{$usecaseParam}startIndex={$next-index}&amp;queryString={$queryString}&amp;pageLength={$page-length}">
        <xsl:text>&gt;</xsl:text>
      </a>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="navigation-link">
    <xsl:param name="query-string"/>
    <xsl:param name="page-length"/>
    <xsl:param name="start-index"/>
    <xsl:param name="link-text"/>

    <a href="?{$usecaseParam}startIndex={$start-index}&amp;queryString={$queryString}&amp;pageLength={$page-length}">
      <xsl:value-of select="$link-text"/>
    </a>
    <xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-2"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>
  <xsl:template match="text()" priority="-1"><xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>

