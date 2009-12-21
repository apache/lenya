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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="uc menu"
  >
  
  <xsl:param name="contextprefix"/>
  <xsl:param name="usecase"/>
  <xsl:param name="position"/>
  
  
  <xsl:template match="/menu:menu">
    <ul id="menu{$position}">
      <xsl:apply-templates select="menu:menus/menu:menu/menu:block"/>
    </ul>
  </xsl:template>
  
  
  <xsl:template match="menu:block">
    <xsl:apply-templates select="menu:title"/>
    <xsl:apply-templates select="menu:menu | menu:item"/>
    
    <xsl:if test="position() != last()">
      <li class="lenya-menu-separator"></li>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="menu:title">
    <li class="lenya-menu-title">
      <xsl:apply-templates select="node()"/>
    </li>
  </xsl:template>
  
  
  <xsl:template name="checkItem">
    <xsl:if test="@checked">
      <xsl:variable name="image">
        <xsl:choose>
          <xsl:when test="@checked = 'true'">
            <xsl:text>checked.png</xsl:text>
          </xsl:when>
          <xsl:when test="@checked = 'false'">
            <xsl:text>unchecked.png</xsl:text>
          </xsl:when>
        </xsl:choose>
      </xsl:variable>
      <xsl:attribute name="style">
        <xsl:text>background: url('</xsl:text>
        <xsl:value-of select="$contextprefix"/><xsl:text>/lenya/menu/images/</xsl:text>
        <xsl:value-of select="$image"/>
        <xsl:text>') left 2px no-repeat;</xsl:text>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="menu:item">
    <xsl:choose>
      <xsl:when test="@href">
        <li>
          <xsl:call-template name="checkItem"/>
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="@href"/>
              <xsl:apply-templates select="@*[local-name() != 'href']"/>
              <xsl:text/>
            </xsl:attribute>
            <xsl:if test="@target = '_blank'">
              <xsl:attribute name="onclick">window.open(this.href); return false;</xsl:attribute>
            </xsl:if>
            <span><xsl:apply-templates select="i18n:*|text()"/></span>
          </a></li>
      </xsl:when>
      <xsl:otherwise>
        <li class="disabled">
          <xsl:call-template name="checkItem"/>
          <a>
            <xsl:for-each select="menu:message">
              <xsl:copy>
                <i18n:translate>
                  <i18n:text><xsl:value-of select="text()"/></i18n:text>
                  <xsl:for-each select="menu:parameter">
                    <i18n:param><xsl:value-of select="normalize-space(text())"/></i18n:param>
                  </xsl:for-each>
                </i18n:translate>
              </xsl:copy>
            </xsl:for-each>
            <span><xsl:apply-templates select="i18n:*|text()"/></span>
          </a>
        </li>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="menu:item/text()">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>
  
  
  <xsl:template match="menu:item/i18n:*">
    <xsl:copy-of select="."/>
  </xsl:template>
  
  
  <xsl:template match="menu:item/@uc:usecase">
    <xsl:text/>
    <xsl:choose>
      <xsl:when test="contains(../@href, '?')">&amp;</xsl:when>
      <xsl:otherwise>?</xsl:otherwise>
    </xsl:choose>
    <xsl:text/>lenya.usecase=<xsl:value-of select="normalize-space(.)"/><xsl:text/>
    <xsl:if test="$usecase != ''">
      <xsl:text>&amp;lenya.exitUsecase=</xsl:text><xsl:value-of select="$usecase"/><xsl:text/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="menu:item/@uc:step">
    <xsl:text/>&amp;lenya.step=<xsl:value-of select="normalize-space(.)"/><xsl:text/>
  </xsl:template>
  
  <xsl:template match="menu:item/@*[not(namespace-uri() = 'http://apache.org/cocoon/lenya/usecase/1.0')]"><xsl:copy-of select="."/></xsl:template>
  
  
</xsl:stylesheet>
