<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : page2xhtml.xsl
    Created on : 11. April 2003, 11:09
    Author     : andreas
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
    
<xsl:param name="root"/>
<xsl:param name="document-id"/>

<xsl:template match="cmsbody">
  <html>
    <head>
      <link rel="stylesheet" href="{$root}/css/page.css" mime-type="text/css"/>
    </head>	
    <body>
      <div class="project-logo">
        <img src="{$root}/images/project-logo.png"/>
      </div>
      <xsl:apply-templates select="xhtml:div[@id = 'tabs']"/>
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td valign="top">
            <xsl:apply-templates select="xhtml:div[@id = 'menu']"/>
          </td>
          <td valign="top">
            <div id="main">
              <xsl:apply-templates select="xhtml:div[@id = 'breadcrumb']"/>
              <xsl:apply-templates select="xhtml:div[@id = 'body']"/>
            </div>
          </td>
        </tr>
      </table>
    </body>
  </html>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
