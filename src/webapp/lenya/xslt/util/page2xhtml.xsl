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

<!-- $Id: page2xhtml.xsl,v 1.18 2004/03/22 16:42:08 gregor Exp $ -->

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
      <script><xsl:value-of select="xhtml:script" />&#160;</script>
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
