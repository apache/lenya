<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : page2html.xsl
    Created on : November 20, 2002, 4:17 PM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    exclude-result-prefixes="page xhtml"
    >

<xsl:param name="contextprefix"/>

<xsl:template match="page:page">
  <html>
    <head>
      <title><xsl:value-of select="page:title"/></title>
      <link rel="stylesheet" type="text/css"
        href="{$contextprefix}/lenya/css/default.css" title="default css"/>
      <xsl:apply-templates select="script" />
    </head>
    <body>
    
      <table width="100%" border="0" cellpadding="10" cellspacing="0">
        <tr>
          <td class="lenya-header">
            <h1><xsl:value-of select="page:title"/></h1>
          </td>
          <td class="lenya-project-logo">
            <img src="{$contextprefix}/lenya/images/project-logo-small.png" alt="Apache Lenya Project Logo"/>
          </td>
        </tr>
      </table>
      <div class="lenya-page-subtitle">
        Open Source Content Management System
      </div>
      <table class="lenya-body" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <xsl:if test="//xhtml:div[@class = 'lenya-sidebar']">
            <td class="lenya-sidebar">
              <xsl:copy-of select="//xhtml:div[@class = 'lenya-sidebar']/node()"/>
            </td>
          </xsl:if>
          <td class="lenya-content">
            <xsl:copy-of select="page:body/node()[local-name() != 'div' or not(@class = 'lenya-sidebar')]"/>
          </td>
        </tr>
      </table>
    </body>
  </html>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
