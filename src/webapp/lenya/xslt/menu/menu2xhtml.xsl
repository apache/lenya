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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:uc="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="area"/>
<xsl:param name="documenturl"/>
<xsl:param name="documentid"/>
<xsl:param name="userid"/>
<xsl:param name="servertime"/>
<xsl:param name="workflowstate"/>
<xsl:param name="islive"/>
<xsl:param name="usecase"/>

<xsl:variable name="currentTab">
  <xsl:choose>
    <xsl:when test="starts-with($usecase, 'admin.')">admin</xsl:when>
    <xsl:when test="starts-with($usecase, 'tab.')">site</xsl:when>
    <xsl:otherwise>authoring</xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:variable name="image-prefix"><xsl:value-of select="$contextprefix"/>/lenya/menu/images</xsl:variable>
 
<xsl:template match="menu:menu">
    
  <!-- Lenya graphic -->
  <div id="lenya-logo">
    <img src="{$image-prefix}/lenya-logo.gif" alt="Lenya" />
  </div>

  <div id="lenya-menus">

    <!-- The main tabs for the different areas of Lenya -->
    <div id="lenya-areas">
      <ul>
        
        <!-- ADMIN TAB -->
        <xsl:if test="not(menu:tabs/menu:tab[@label = 'admin']/@show = 'false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">admin</xsl:with-param>
            <xsl:with-param name="tabName">admin</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
        <xsl:variable name="info-area">
          <xsl:choose>
            <xsl:when test="$area = 'admin'">authoring</xsl:when>
            <xsl:otherwise><xsl:value-of select="$area"/></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
          
        <xsl:if test="not(menu:tabs/menu:tab[@label = 'info']/@show = 'false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area" select="$info-area"/>
            <xsl:with-param name="queryString">?lenya.usecase=tab.overview</xsl:with-param>
            <xsl:with-param name="tabName">site</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
        <!-- AUTHORING TAB -->
        <xsl:call-template name="area-tab">
          <xsl:with-param name="tab-area">authoring</xsl:with-param>
          <xsl:with-param name="tabName">authoring</xsl:with-param>
        </xsl:call-template>
          
        <!-- STAGING TAB -->
        <xsl:if test="menu:tabs/menu:tab[@label = 'staging']/@show = 'true'">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">staging</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
        <!-- LIVE TAB -->
        <xsl:if test="not(menu:tabs/menu:tab[@label = 'live']/@show = 'false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area">live</xsl:with-param>
            <xsl:with-param name="target">_blank</xsl:with-param>
            <xsl:with-param name="tabName">live</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
      </ul>
    </div>
 
    <!-- General information about the state  of the page, etc. -->
    <div id="lenya-info">
      <ul>
        <xsl:if test="$workflowstate != ''">
          <xsl:call-template name="workflow"/>
        </xsl:if>
        <li id="info-user"><i18n:text>User</i18n:text>: <span id="logged-user"><xsl:value-of select="$userid"/></span></li>
        <li id="info-time"><i18n:text>Server Time</i18n:text>: <span id="server-time"><xsl:value-of select="$servertime"/></span></li>
      </ul>
    </div>

    <!-- drop down menus for area options -->
    <div id="lenya-options">
      <ul>
        <xsl:apply-templates select="menu:menus/menu:menu" mode="nav"/>
      </ul>
    </div>

  </div>

</xsl:template>
  
  
  <xsl:template name="area-tab">
    <xsl:param name="tab-area"/>
    <xsl:param name="tab-area-prefix" select="$tab-area"/>
    <xsl:param name="target" select="'_self'"/>
    <xsl:param name="queryString"/>
    <xsl:param name="tabName"/>
    
    <xsl:variable name="tab-documenturl">
      <xsl:choose>
        <!-- index.html for link from/to admin area -->
        <xsl:when test="$tab-area = 'admin' or $area = 'admin'">/index.html</xsl:when>
        <xsl:when test="($currentTab = 'site') and $documentid = '/'">/index.html</xsl:when>
        <xsl:otherwise><xsl:value-of select="$documenturl"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$tabName = $currentTab">
        <li id="area-{$tab-area}-active"><a href="{$contextprefix}/{$publicationid}/{$tab-area}{normalize-space($tab-documenturl)}{$queryString}" target="{$target}"><span><i18n:text><xsl:value-of select="$tabName"/></i18n:text></span></a></li>
      </xsl:when>
      <xsl:otherwise>
        <li id="area-{$tab-area}"><a href="{$contextprefix}/{$publicationid}/{$tab-area}{normalize-space($tab-documenturl)}{$queryString}" target="{$target}"><span><i18n:text><xsl:value-of select="$tabName"/></i18n:text></span></a></li>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template name="workflow">
    <li id="info-state"><i18n:text>Workflow State</i18n:text>: <span id="workflow-state"><i18n:text><xsl:value-of select="$workflowstate"/></i18n:text></span></li>
    <li id="info-live">
      <xsl:choose>
        <xsl:when test="$islive = 'false'">
          <i18n:text>not live</i18n:text>
        </xsl:when>
        <xsl:otherwise>
          <i18n:text>live</i18n:text>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>
  
  <xsl:template match="menu:menu" mode="nav">
    <li id="nav{position()}"><xsl:value-of select="@name"/>
      <ul id="menu{position()}">
        <xsl:apply-templates select="menu:block[not(@info = 'false') and ($currentTab = 'site') or not(@*[local-name() = $currentTab] = 'false') and not($currentTab = 'site')]"/>
      </ul>
    </li>
  </xsl:template>

  <xsl:template match="menu:menu[not(*)]" mode="nav">
    <li id="nav{position()}" class="disabled">
      <xsl:value-of select="@name"/>
    </li>
  </xsl:template>
 
  <!-- match blocks with not area='false' -->
  <xsl:template match="menu:block">
    <xsl:apply-templates select="menu:title"/>
    <xsl:apply-templates select="menu:item[not(@info = 'false') and ($currentTab = 'site') or not(@*[local-name() = $currentTab] = 'false') and not($currentTab = 'site')]"/>
		
    <xsl:if test="position() != last()">
      <li class="lenya-menu-separator"></li>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="menu:title">
    <li class="lenya-menu-title">
      <xsl:apply-templates select="node()"/>
    </li>
  </xsl:template>
  	
  <!-- match items with not area='false' -->
  <xsl:template match="menu:item">
    <xsl:choose>
      <xsl:when test="@href">
        <li><a>
          <xsl:attribute name="href">
            <xsl:value-of select="@href"/>
            <xsl:apply-templates select="@*[local-name() != 'href']"/>
            <xsl:text/>
            <xsl:if test="$currentTab = 'site'">
              <xsl:choose>
                <xsl:when test="contains(@href, '?')">
                  <xsl:text>&amp;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>?</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:if>
          </xsl:attribute>
          <span><xsl:value-of select="."/></span>
        </a></li>
      </xsl:when>
      <xsl:otherwise>
        <li class="disabled">
          <a>
            <xsl:for-each select="menu:message">
              <xsl:copy>
                <i18n:translate>
                  <i18n:text><xsl:value-of select="text()"/></i18n:text>
                  <xsl:for-each select="menu:parameter">
                    <i18n:param><xsl:value-of select="text()"/></i18n:param>
                  </xsl:for-each>
                </i18n:translate>
              </xsl:copy>
            </xsl:for-each>
            <span><xsl:value-of select="text()"/></span>
          </a>
        </li>
      </xsl:otherwise>
    </xsl:choose>
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
