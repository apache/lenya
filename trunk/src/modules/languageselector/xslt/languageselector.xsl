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
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  exclude-result-prefixes="xhtml"
  >

  <xsl:param name="currentLanguage"/>
  <xsl:param name="publication-languages-csv"/>
  <xsl:param name="document-languages-csv"/>
  <xsl:param name="text"/>
  <xsl:param name="flagsize"/>
  <xsl:param name="context"/>

  <xsl:template match="/">
    <div id="languageselector">
      <ul>
        <xsl:call-template name="enumLangs">
          <xsl:with-param name="list" select="$publication-languages-csv"/>
        </xsl:call-template>
      </ul>
    </div>
  </xsl:template>

  <!-- parse a comma-separated list of languages and call handleLanguage for each language found. -->
  <xsl:template name="enumLangs">
    <xsl:param name="list"/>

    <!-- get the first item in the list: -->
    <xsl:variable name="head">
      <xsl:choose>
        <!-- all but last item: -->
        <xsl:when test="substring-before($list, ',')">
          <xsl:value-of select="substring-before($list, ',')"/>
        </xsl:when>
        <!-- last item: -->
        <xsl:when test="$list">
          <xsl:value-of select="$list"/>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:variable>

    <!-- get the rest of the list: -->
    <xsl:variable name="tail">
      <xsl:value-of select="substring-after($list, ',')"/>
    </xsl:variable>

    <!-- handle the first item in the list: -->
    <xsl:if test="$head">
      <xsl:call-template name="handleLanguage">
        <xsl:with-param name="lang" select="$head"/>
      </xsl:call-template>
    </xsl:if>

    <!-- if there are more items in the list, handle them recursively: -->
    <xsl:if test="$tail">
      <xsl:call-template name="enumLangs">
        <xsl:with-param name="list" select="$tail"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="handleLanguage">
    <xsl:param name="lang"/>
    <li>
      <xsl:choose>
  
        <xsl:when test="$lang = $currentLanguage">
          <a class="lenya-language-isCurrent" 
             i18n:attr="title"
             title="{$lang}-isCurrent"
          >
            <xsl:call-template name="text">
              <xsl:with-param name="lang" select="$lang"/>
            </xsl:call-template>
            <xsl:call-template name="flag">
              <xsl:with-param name="lang" select="$lang"/>
              <xsl:with-param name="flagsize" select="$flagsize"/>
            </xsl:call-template>
          </a>
        </xsl:when>
  
        <!-- this test is a little sloppy and can lead to spurious substring matches if 
             both two and three letter language codes (ISO 639-2) are being used. -->
    
        <xsl:when test="contains($document-languages-csv, $lang)">
          <!-- aren't these new lenya-document links lovely? relative links to languages! -->
          <a class="lenya-language-isAvailable" 
             i18n:attr="title"
             title="{$lang}-isAvailable"
             href="lenya-document:,lang={$lang}"
          >
            <xsl:call-template name="text">
              <xsl:with-param name="lang" select="$lang"/>
            </xsl:call-template>
            <xsl:call-template name="flag">
              <xsl:with-param name="lang" select="$lang"/>
              <xsl:with-param name="flagsize" select="$flagsize"/>
            </xsl:call-template>
          </a>
        </xsl:when>
  
        <xsl:otherwise>
          <a class="lenya-language-isUnavailable"
             i18n:attr="title"
             title="{$lang}-isUnavailable"
          >
            <xsl:call-template name="text">
              <xsl:with-param name="lang" select="$lang"/>
            </xsl:call-template>
            <xsl:call-template name="flag">
              <xsl:with-param name="lang" select="$lang"/>
              <xsl:with-param name="flagsize" select="$flagsize"/>
            </xsl:call-template>
          </a>
        </xsl:otherwise>
  
      </xsl:choose>
    </li>
  </xsl:template>

  <xsl:template name="text">
    <xsl:param name="lang"/>
    <xsl:choose>
      <xsl:when test="$text = 'abbr'">
        <xsl:value-of select="$lang"/>
      </xsl:when>
      <xsl:when test="$text = 'long'">
        <i18n:text><xsl:value-of select="$lang"/></i18n:text>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="flag">
    <xsl:param name="lang"/>
    <xsl:param name="flagsize"/>
    <xsl:choose>
      <xsl:when test="$flagsize = 'none'"/>
      <xsl:when test="number($flagsize) &gt; 0">
        <img src="{$context}/modules/languageselector/flag-{$lang}-{$flagsize}.png" alt="{$lang}"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
