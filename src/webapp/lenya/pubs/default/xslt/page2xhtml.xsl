<?xml version="1.0" encoding="UTF-8" ?>

<!--
$Id: page2xhtml.xsl,v 1.14 2004/03/07 19:15:28 gregor Exp $
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    exclude-result-prefixes="page xhtml"
    >
    
    
<!-- servlet context prefix i.e. /lenya/ -->
<xsl:param name="root"/>

<!-- i.e. doctypes/xhtml-document -->
<xsl:param name="document-id"/>

<!-- The rquest url i.e. /lenya/doctypes/xhtml-document_en.html -->
<xsl:param name="url"/>


<xsl:template match="cmsbody">
  <html>
    <head>
      <link rel="stylesheet" href="{$root}/css/page.css" type="text/css"/>
    </head>	
    <body>
      <div id="page">
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td id="publication-title">Welcome to the Default Publication!</td>
          <td id="project-logo"><img src="{$root}/images/project-logo.png"/></td>
        </tr>
      </table>
      <xsl:apply-templates select="xhtml:div[@id = 'tabs']"/>
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td valign="top" width="230px">
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
      </div>
    </body>
  </html>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
