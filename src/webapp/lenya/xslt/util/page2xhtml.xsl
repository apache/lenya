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
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
  exclude-result-prefixes="page xhtml i18n"
  >
  <xsl:param name="contextprefix"/>
  
  <xsl:include href="fallback://lenya/xslt/util/toggle.xsl"/>
  
  <xsl:template match="page:page">
    <html>
      <head>
        <title><xsl:apply-templates select="page:title/node()"/></title>
        <link rel="stylesheet" type="text/css" href="{$contextprefix}/lenya/css/default.css" title="default css"/>
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
        <xsl:apply-templates select="xhtml:script"/>
        <xsl:copy-of select="page:head/*"/>
      </head>
      <body>
        <xsl:copy-of select="page:body/@*"/>
        <xsl:if test="page:body//xhtml:div[@class = 'lenya-box-toggled']">
          <xsl:call-template name="toggle-script"/>
        </xsl:if>
          
        <xsl:apply-templates select="page:title"/>
        <table class="lenya-body" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <xsl:if test="//xhtml:div[@class = 'lenya-sidebar']">
              <td class="lenya-sidebar">
                <xsl:apply-templates select="//xhtml:div[@class = 'lenya-sidebar']/node()"/>
              </td>
            </xsl:if>
            <td class="lenya-content">
              <xsl:apply-templates select="page:body/node()[local-name() != 'div' or not(@class = 'lenya-sidebar')]"/>
            </td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>
  
  
  <xsl:template match="page:title">
    <table width="100%" border="0" cellpadding="10" cellspacing="0">
      <tr>
        <td class="lenya-header">
          <h1><xsl:apply-templates select="node()"/></h1>
        </td>
        <td class="lenya-project-logo">
          <img src="{$contextprefix}/lenya/images/project-logo-small.png" alt="Apache Lenya Project Logo"/>
        </td>
      </tr>
    </table>
    <div class="lenya-page-subtitle">
      Open Source Content Management System
    </div>
  </xsl:template>
  
  
  <xsl:template match="xhtml:div[@class = 'lenya-box-toggled']">
    <xsl:variable name="number" select="count(preceding::xhtml:div[@class = 'lenya-box-toggled']) + 1"/>
    <div class="lenya-box">
      <xsl:apply-templates select="@*[local-name() != 'class']|node()">
        <xsl:with-param name="number" select="$number"/>
      </xsl:apply-templates>
    </div>
  </xsl:template>
  
  
  <xsl:template match="xhtml:div[@class = 'lenya-box-toggled']/xhtml:div[@class = 'lenya-box-title']">
    <xsl:param name="number"/>
    <div class="lenya-box">
      <xsl:apply-templates select="@*|node()"/>
      <xsl:text> </xsl:text>
      <span class="switch" onclick="toggle('{$number}')" id="{$number}-switch">[<i18n:text i18n:key="show" />]</span>
    </div>
  </xsl:template>
  
  
  <xsl:template match="xhtml:div[@class = 'lenya-box-toggled']/xhtml:div[@class = 'lenya-box-body']">
    <xsl:param name="number"/>
    <div id="{$number}" style="display: none">
      <xsl:apply-templates select="@*|node()"/>
    </div>
  </xsl:template>
  
  
  <xsl:template match="@*|node()" priority="-2">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet> 
