<?xml version="1.0" encoding="UTF-8" ?>
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
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  exclude-result-prefixes="page xhtml dc lenya"
  >
  
  <!-- {context-prefix}/{publication-id}/{area} -->
  <xsl:param name="root"/>
  <xsl:param name="context-prefix"/>
  
  <!-- i.e. doctypes/xhtml-document -->
  <xsl:param name="document-type"/>
  
  <xsl:param name="document-id"/>
  
  <!-- The request url i.e. /lenya/doctypes/xhtml-document_en.html -->
  <xsl:param name="url"/>
  <xsl:param name="language"/>
  <xsl:param name="title"/>
  
  <xsl:param name="lastPublishedUser"/>
  <xsl:param name="lastPublishedDate"/>
  
  <!--Following is a show off to explain lenya.properties.xml -->
  <xsl:param name="author"/>
  
  <xsl:template match="cmsbody">
    <html>
      <head>
        <link rel="neutron-introspection" type="application/neutron+xml" href="{$root}{$document-id}/introspection.xml"/>
        <link rel="stylesheet" href="{$root}/css/page.css" type="text/css"/>
        <!-- Load doctype-specific CSS -->
        <xsl:choose>
          <xsl:when test="$document-type">
            <!-- Looking into the pub e.g. {$yourPub}/resources/shared/css/{$document-type}.css -->
            <link rel="stylesheet" href="{$root}/css/{$document-type}.css" type="text/css"/>
            <xsl:copy-of select="xhtml:html/xhtml:head/*"/>
          </xsl:when>
          <xsl:otherwise>
            <!-- do nothing -->
          </xsl:otherwise>
        </xsl:choose>
        <meta content="Apache Lenya" name="generator"/>
        <title><xsl:value-of select="$title"/></title>
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
      </head>
      <body>
        <div id="page">
          <table width="100%" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td id="publication-title">
                <xsl:choose>
                  <xsl:when test="$language = 'de'">
                    Willkommen zur Default Publikation von <xsl:value-of select="$author"/>!
                  </xsl:when>
                  <xsl:otherwise>
                    Welcome to the Default Publication from <xsl:value-of select="$author"/>!
                  </xsl:otherwise>
                </xsl:choose>
              </td>
              <td id="project-logo"><img src="{$root}/images/project-logo.png" alt="project logo"/></td>
            </tr>
          </table>
          <xsl:apply-templates select="xhtml:div[@id = 'tabs']"/>
          <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td valign="top" style="width: 230px">
                <xsl:apply-templates select="xhtml:div[@id = 'menu']"/>
              </td>
              <td valign="top">
                <div id="main">
                  <xsl:apply-templates select="xhtml:div[@id = 'breadcrumb']"/>
                  <xsl:apply-templates select="xhtml:div[@id = 'search']"/>
                  <xsl:apply-templates select="xhtml:div[@id = 'body']"/>
                  <xsl:if test="xhtml:html">
                    <div id="body">
                      <xsl:apply-templates select="xhtml:html/xhtml:body/*"/>
                    </div>
                  </xsl:if>
                  <xsl:call-template name="footer"/>
                </div>
              </td>
            </tr>
          </table>
        </div>
      </body>
    </html>
  </xsl:template>
  
  
  <xsl:template name="footer">
    <div id="footer">
      <p>
        <xsl:choose>
          <xsl:when test="$lastPublishedUser != ''">
            <i18n:translate>
              <i18n:text>last-published</i18n:text>
              <i18n:param><i18n:date-time src-pattern="yyyy-MM-dd hh:mm:ss"><xsl:value-of select="$lastPublishedDate"/></i18n:date-time></i18n:param>
              <i18n:param><xsl:value-of select="$lastPublishedUser"/></i18n:param>
            </i18n:translate>
          </xsl:when>
          <xsl:otherwise>
            <i18n:text>never-published</i18n:text>
          </xsl:otherwise>
        </xsl:choose>
      </p>
    </div>
  </xsl:template>
  
  
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet> 
