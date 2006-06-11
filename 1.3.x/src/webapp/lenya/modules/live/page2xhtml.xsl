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

<!-- $Id: page2xhtml.xsl 170255 2005-05-15 19:58:26Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    exclude-result-prefixes="page xhtml"
    >
    
    
<!-- {context-prefix}/{publication-id}/{area} -->
<xsl:param name="root"/>

<xsl:param name="document-id"/>

<!-- i.e. doctypes/xhtml-document -->
<xsl:param name="document-type"/>

<!-- The rquest url i.e. /lenya/doctypes/xhtml-document_en.html -->
<xsl:param name="url"/>

<xsl:param name="language"/>


<xsl:template match="cmsbody">
  <html>
    <head>
      <link rel="stylesheet" href="{$root}/css/page.css" type="text/css"/>
      <meta content="Apache Lenya" name="generator"/>
      <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
      <title><xsl:value-of select="//lenya:meta/dc:title"/></title>
    </head>	
    <body>
      <div id="page">
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td id="publication-title">
            <xsl:choose>
              <xsl:when test="$language = 'de'">
	        Willkommen zur Default Publikation!
              </xsl:when>
              <xsl:otherwise>
	        Welcome to the Default Publication!
              </xsl:otherwise>
	    </xsl:choose>
          </td>
          <td id="project-logo"><img src="{$root}/images/project-logo.png" alt="project logo"/></td>
        </tr>
      </table>
      <xsl:apply-templates select="xhtml:div[@id = 'tabs']"/>
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td valign="top">
            <xsl:apply-templates select="xhtml:div[@id = 'menu']"/>
          </td>
          <td valign="top">
            <div id="main">
              <xsl:apply-templates select="xhtml:div[@id = 'breadcrumb']"/>
              <xsl:apply-templates select="xhtml:div[@id = 'search']"/>
              <xsl:apply-templates select="xhtml:div[@id = 'body']"/>
            </div>
          </td>
        </tr>
        <tr>
          <td colspan="2" valign="top">
            <div id="footer">
              <xsl:apply-templates select="lenya:meta/dcterms:modified"/>
              <xsl:apply-templates select="lenya:meta/dc:publisher"/>
            </div>
          </td>
        </tr>
      </table>
      </div>
    </body>
  </html>
</xsl:template>

<xsl:template match="dcterms:modified">
  <xsl:variable name="date"><xsl:value-of select="."/></xsl:variable>
  <i18n:text>last_published</i18n:text>:
    <xsl:if test="$date!=''">
    <i18n:date-time src-pattern="yyyy-MM-dd HH:mm:ss" pattern="EEE, d MMM yyyy HH:mm:ss z" value="{$date}"/> 
  </xsl:if>
</xsl:template>

<xsl:template match="dc:publisher">
  <xsl:variable name="user"><xsl:value-of select="."/></xsl:variable>
  <xsl:variable name="user-id"><xsl:value-of select="substring-before($user,'|')"/></xsl:variable>
  <xsl:variable name="rest"><xsl:value-of select="substring-after($user,'|')"/></xsl:variable>
  <xsl:variable name="user-name"><xsl:value-of select="substring-before($rest,'|')"/></xsl:variable>
  <xsl:variable name="user-email"><xsl:value-of select="substring-after($rest,'|')"/></xsl:variable>

  <xsl:if test="$user != ''">
    <xsl:choose>
      <xsl:when test="$user-email != ''">
      / <a>
          <xsl:attribute name="href"><xsl:value-of select="concat('mailto:',$user-email)"/></xsl:attribute> 
          <xsl:value-of select="$user-name"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
       / <xsl:value-of select="$user-name"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
