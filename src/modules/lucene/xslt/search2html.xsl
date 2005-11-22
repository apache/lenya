<?xml version="1.0"?>
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
<!-- CVS $Id: search2html.xsl 47285 2004-09-27 12:52:44Z cziegeler $ -->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:search="http://apache.org/cocoon/search/1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:cinclude="http://apache.org/cocoon/include/1.0"
  exclude-result-prefixes="cinclude search xhtml"
>

<xsl:param name="url"/>
<xsl:param name="area"/>
<xsl:param name="pub"/>
<xsl:param name="root"/>

  <xsl:template match="search:results">  
    <cmsbody>
      <cinclude:include src="cocoon:/modules/sitetree/{$pub}/{$area}/breadcrumb{$url}.xml"/>
      <cinclude:include src="cocoon:/modules/sitetree/{$pub}/{$area}/menu{$url}.xml"/>
      <cinclude:include src="cocoon:/modules/sitetree/{$pub}/{$area}/tabs{$url}.xml"/>
      <cinclude:include src="cocoon:/modules/sitetree/{$pub}/{$area}/search{$url}.xml"/>      
      <xhtml:div id="body">
        <xsl:apply-templates/>
      </xhtml:div>
    </cmsbody>
  </xsl:template>

  <xsl:template match="search:hits">
    <h1>
        <xsl:value-of select="@total-count"/> hit(s)
        <xsl:value-of select="@count-of-pages"/> page(s) on query
        <em><xsl:value-of select="/search:results/@query-string"/></em>
    </h1>
    
    <ul>
      <xsl:apply-templates/>
    </ul>
    
    <p>
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
    </p>
    
    <p>
        <a href="{$root}{$url}">Return to document</a>	
    </p>
    
  </xsl:template>

  <xsl:template match="search:navigation">
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
  </xsl:template>
  
  <xsl:template match="search:hit">
    <li>
        <h2>Score: <xsl:value-of select="format-number( @score, '### %' )"/> Rank: <xsl:value-of select="@rank"/></h2>
        <p><a href="{$root}{search:field[attribute::name='uid']}"><xsl:value-of select="search:field[attribute::name='title']"/></a></p>
    </li>
  </xsl:template>

  <xsl:template name="navigation-paging-form">
    <xsl:param name="query-string"/>
    <xsl:param name="page-length"/>
    <xsl:param name="has-previous"/>
    <xsl:param name="has-next"/>
    <xsl:param name="previous-index"/>
    <xsl:param name="next-index"/>

    <xsl:if test="$has-previous = 'true'">
      <form action="">
        <input type="hidden" name="lenya.usecase" value="search"/>
        <input type="hidden" name="startIndex" value="{$previous-index}"/>
        <input type="hidden" name="queryString" value="{$query-string}"/>
        <input type="hidden" name="pageLength" value="{$page-length}"/>
        <input type="submit" name="previous" value="previous"/>
      </form>
    </xsl:if>
    
    <xsl:if test="$has-next = 'true'">
      <form action="">
        <input type="hidden" name="lenya.usecase" value="search"/>
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
      <a href="index.html?lenya.usecase=search&amp;startIndex={$next-index}&amp;queryString={$query-string}&amp;pageLength={$page-length}">
        Next Page Of Hits
      </a>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="navigation-link">
    <xsl:param name="query-string"/>
    <xsl:param name="page-length"/>
    <xsl:param name="start-index"/>
    <xsl:param name="link-text"/>

    <a href="index.html?lenya.usecase=search&amp;startIndex={$start-index}&amp;queryString={$query-string}&amp;pageLength={$page-length}">
      <xsl:value-of select="$link-text"/>
    </a>
    &#160;
  </xsl:template>

  <xsl:template match="@*|node()" priority="-2"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>
  <xsl:template match="text()" priority="-1"><xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>

