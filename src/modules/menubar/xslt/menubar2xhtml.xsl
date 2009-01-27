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

<!-- $Id: menu2xhtml.xsl 568636 2007-08-22 14:54:40Z andreas $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >

  <xsl:import href="menu2xhtml.xsl"/>
      
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
<xsl:param name="tabGroup"/>
<xsl:param name="newMessages"/>
  
<xsl:variable name="currentTab">
  <xsl:choose>
    <xsl:when test="$tabGroup != ''"><xsl:value-of select="$tabGroup"/></xsl:when>
    <xsl:otherwise>authoring</xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:variable name="image-prefix"><xsl:value-of select="$contextprefix"/>/lenya/menu/images</xsl:variable>
 
<xsl:template match="/menu:menu">
  
  <div>
    
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
            <xsl:with-param name="tab-area"><xsl:value-of select="$area"/></xsl:with-param>
            <xsl:with-param name="tabName">admin</xsl:with-param>
            <xsl:with-param name="queryString">?lenya.usecase=admin.users</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
        <xsl:if test="not(menu:tabs/menu:tab[@label = 'info']/@show = 'false')">
          <xsl:call-template name="area-tab">
            <xsl:with-param name="tab-area"><xsl:value-of select="$area"/></xsl:with-param>
            <xsl:with-param name="queryString">?lenya.usecase=tab.overview</xsl:with-param>
            <xsl:with-param name="tabName">site</xsl:with-param>
          </xsl:call-template>
        </xsl:if>
          
        <!-- AUTHORING TAB -->
        <xsl:call-template name="area-tab">
          <xsl:with-param name="tab-area"><xsl:value-of select="$area"/></xsl:with-param>
          <xsl:with-param name="tabName"><xsl:value-of select="$area"/></xsl:with-param>
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
        <li id="info-messages">
          <a href="?lenya.usecase=notification.inbox">
            <xsl:choose>
              <xsl:when test="$newMessages = '0'">
                <i18n:text>Inbox</i18n:text>
              </xsl:when>
              <xsl:when test="$newMessages = '1'">
                <i18n:text>unread-message</i18n:text>
              </xsl:when>
              <xsl:otherwise>
                <i18n:translate>
                  <i18n:text>unread-messages</i18n:text>
                  <i18n:param><xsl:value-of select="$newMessages"/></i18n:param>
                </i18n:translate>
              </xsl:otherwise>
            </xsl:choose>
          </a>
        </li>
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
        <!-- 
           Document URLs are not meaningful in some areas. In that case, set the URL to
           "/". The publication sitemap currently takes care of mapping that to a default page (index.html).
           FIXME: that solution is sub-optimal, because it breaks when a user deletes the /index page.
           There should be a mapper from "/" to "first entry in sitetree", and if that does not exist, to a 
           "create document?" message.
        -->
        <!-- from or to the admin area, there's no concept of "document" (it's all usecases) -->
        <xsl:when test="$tab-area = 'admin' or $area = 'admin'">/</xsl:when>
        <!-- FIXME: what is documentid? -->
        <xsl:when test="($currentTab = 'site') and $documentid = '/'">/</xsl:when>
        <!-- catch missing trailing slash in urls with just the area: -->
        <xsl:when test="not($documenturl)">/</xsl:when>
        <!-- the default case is: use the current $documenturl for the new tab link. -->
        <xsl:otherwise><xsl:value-of select="$documenturl"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="prefix">
      <xsl:if test="$tabName != $currentTab">in</xsl:if>
    </xsl:variable>
    <!-- 
       FIXME: why do we normalize-space here? fiddling with uris is none of our business. 
       Looks like a workaround for a real bug that should be fixed. Sure, we don't allow spaces
       in document URLs, but that policy decision is made elsewhere.
    -->
    <li class="area-{$prefix}active">
      <a href="/{$publicationid}/{$tab-area}{normalize-space($tab-documenturl)}{$queryString}">
        <xsl:if test="$target = '_blank'">
          <xsl:attribute name="onclick">window.open(this.href); return false;</xsl:attribute>
        </xsl:if>
        <span><i18n:text><xsl:value-of select="$tabName"/></i18n:text></span>
      </a>
    </li>
  </xsl:template>
  
  
  <xsl:template name="workflow">
    <li id="info-state">
      <i18n:text>Workflow State</i18n:text>:
      <span id="workflow-state"><i18n:text><xsl:value-of select="$workflowstate"/></i18n:text></span>
    </li>
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
  
  <!--
  <xsl:template match="menu:menu//menu:menu">
    <li class="lenya-menu-title"><xsl:value-of select="@name"/></li>
    <xsl:apply-templates select="*"/>
  </xsl:template>
  -->
  
  <xsl:template match="menu:menu" mode="nav">
    <li>
      <xsl:choose>
        <xsl:when test="*">
          <xsl:attribute name="id">nav<xsl:value-of select="position()"/></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class">disabled</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <i18n:text><xsl:value-of select="@name"/></i18n:text>
      <xsl:if test="*">
        <ul id="menu{position()}">
          <xsl:apply-templates select="menu:block"/>
        </ul>
      </xsl:if>
    </li>
  </xsl:template>
  
  
</xsl:stylesheet>
