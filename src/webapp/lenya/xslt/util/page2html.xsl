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
    >

<xsl:import href="page-util.xsl"/>

<xsl:template match="/">
  <html>
    <head>
      <title><xsl:value-of select="page/title"/></title>
      <xsl:call-template name="include-css">
        <xsl:with-param name="context-prefix"
            select="concat(page/context, '/', page/publication-id)"/>
      </xsl:call-template>
    </head>
    <body>
      <h1><xsl:value-of select="page/title"/></h1>
      <xsl:copy-of select="page/body/*"/>
    </body>
  </html>
</xsl:template>

</xsl:stylesheet> 
