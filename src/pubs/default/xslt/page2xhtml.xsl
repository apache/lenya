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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:meta="http://apache.org/lenya/meta/1.0/"
  exclude-result-prefixes="xhtml lenya"
  >
  
  <xsl:import href="context://lenya/xslt/util/string-functions.xsl"/>
  
  <xsl:param name="publication-id"/>
  <xsl:param name="area"/>
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  
  <!-- i.e. doctypes/xhtml-document -->
  <xsl:param name="document-type"/>
  
  <xsl:param name="document-path"/>
  
  <xsl:param name="lastPublishedUser"/>
  <xsl:param name="lastPublishedDate"/>
  
  <!--Following is a show off to explain lenya.properties.xml -->
  <xsl:param name="author"/>
  <xsl:param name="lenyaVersion"/>
  
  <xsl:variable name="root" select="concat('/', $publication-id, '/', $area)"/>
  
  <xsl:template match="cmsbody">
    <html>
      <head>
        <link rel="neutron-introspection" type="application/neutron+xml"
          href="{$root}{$document-path}.xml?lenya.module=neutron&amp;lenya.action=introspect"/>
        <link rel="stylesheet" href="{$root}/css/page.css" type="text/css"/>
        <link rel="SHORTCUT ICON" type="image/ico" href="/lenya/images/lenya.ico"/>
        <!-- Load doctype-specific CSS -->
        <xsl:if test="$document-type">
          <!-- Looking into the pub e.g. {$yourPub}/resources/shared/css/{$document-type}.css -->
          <link rel="stylesheet" href="{$root}/css/{$document-type}.css" type="text/css"/>
          <xsl:copy-of select="xhtml:html/xhtml:head/*"/>
        </xsl:if>
        <meta content="Apache Lenya {$lenyaVersion}" name="generator"/>
        
        <title><meta:value element="title" ns="http://purl.org/dc/elements/1.1/" default="error-404"
          i18n:attr="default" uuid="{$uuid}" lang="{$language}"/></title>
        
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
      </head>
      <body>
        <div id="page">
          <table width="100%" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td id="publication-title">
                <xsl:variable name="pubTitle">
                  <xsl:call-template name="capitalize">
                    <xsl:with-param name="text" select="$publication-id"/>
                  </xsl:call-template>
                </xsl:variable>
                <i18n:translate>
                  <i18n:text>publication-title</i18n:text>
                  <i18n:param><xsl:value-of select="$pubTitle"/></i18n:param>
                  <i18n:param><xsl:value-of select="$author"/></i18n:param>
                </i18n:translate>
              </td>
              <td id="project-logo"><img src="{$root}/images/project-logo.png" alt="project logo"/></td>
            </tr>
          </table>
          <xsl:apply-templates select="xhtml:div[@id = 'tabs']"/>
          <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td valign="top" style="width: 230px">
                <xsl:apply-templates select="xhtml:div[@id = 'menu']"/>
                <xsl:apply-templates select="xhtml:div[@id = 'languageselector']"/>
              </td>
              <td valign="top">
                <div id="main">
                  <xsl:apply-templates select="xhtml:div[@id = 'breadcrumb']"/>
                  <xsl:apply-templates select="xhtml:div[@id = 'search']"/>
                  <xsl:apply-templates select="xhtml:html/xhtml:body"/>
                  <xsl:call-template name="footer"/>
                </div>
              </td>
            </tr>
          </table>
        </div>
      </body>
    </html>
  </xsl:template>
  
  
  <xsl:template match="xhtml:body">
    <xsl:choose>
      <xsl:when test="xhtml:div[@id = 'body']">
        <xsl:apply-templates select="node()"/>
      </xsl:when>
      <xsl:otherwise>
        <div id="body">
          <xsl:apply-templates select="node()"/>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="xhtml:div[@id = 'breadcrumb']">
    <xsl:if test="*">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template name="footer">
    <div id="footer">
      <p>
        <xsl:choose>
          <xsl:when test="$lastPublishedUser != ''">
            <i18n:translate>
              <i18n:text>last-published</i18n:text>
              <i18n:param><i18n:date-time src-pattern="yyyy-MM-dd HH:mm:ss"><xsl:value-of select="$lastPublishedDate"/></i18n:date-time></i18n:param>
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
