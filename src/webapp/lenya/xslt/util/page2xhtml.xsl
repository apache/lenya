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
    >

<xsl:param name="contextprefix"/>

<xsl:template match="/page:page">
  <html>
    <head>
      <title><xsl:value-of select="page:title"/></title>
      <link rel="stylesheet" type="text/css"
        href="{$contextprefix}/lenya/css/default.css" title="default css"/>
    </head>
    <body>
    
      <table width="100%" border="0" cellpadding="10" cellspacing="0">
        <tr>
          <td class="lenya-header">
            <h1><xsl:value-of select="page:title"/></h1>
          </td>
          <td class="lenya-project-logo">
            <img src="{$contextprefix}/lenya/images/project-logo-small.png"/>
          </td>
        </tr>
      </table>
      <div class="lenya-page-subtitle">
        Open Source Content Management System
      </div>
      <table class="lenya-body" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="lenya-content">
            <xsl:copy-of select="page:body/node()[local-name() != 'div' or @class != 'lenya-sidebar']"/>
          </td>
          <td class="lenya-sidebar">
            <xsl:copy-of select="//xhtml:div[@class = 'lenya-sidebar']/node()"/>
          </td>
        </tr>
      </table>
<!--    
      <div>
        
      </div>
      <div class="lenya-header">
        
      </div>
      <div class="lenya-body">
        <xsl:copy-of select="page:body/*"/>
      </div>
-->      
    </body>
  </html>
</xsl:template>


</xsl:stylesheet> 
