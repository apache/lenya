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
    xmlns:page="http://www.lenya.org/2003/cms-page"
    >

<xsl:param name="context-prefix"/>

<xsl:template match="/page:page">
  <html>
    <head>
      <title><xsl:value-of select="page:title"/></title>
      <link rel="stylesheet" type="text/css"
        href="{$context-prefix}/lenya/css/default.css" title="default css"/>
    </head>
    <body>
      <h1><xsl:value-of select="page:title"/></h1>
      <xsl:copy-of select="page:body/*"/>
    </body>
  </html>
</xsl:template>

</xsl:stylesheet> 
