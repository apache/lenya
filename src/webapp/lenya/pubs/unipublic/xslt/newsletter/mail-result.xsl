<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : mail-result.xsl
    Created on : November 19, 2002, 4:35 PM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:mail="http://cxa/cocoon/sendmail">

<xsl:include href="../../../../../../stylesheets/cms/util/page-util.xsl"/>
    
<xsl:param name="context-prefix"/>

<xsl:variable name="menu-separator" select="'&#160;&#160;|&#160;&#160;'"/>

<!-- template rule matching source root element -->
<xsl:template match="/">
  <html>
  <head>
    <title>Newsletter</title>
    <!-- FIXME -->
    <xsl:call-template name="include-css">
      <xsl:with-param name="context-prefix" select="$context-prefix"/>
    </xsl:call-template>
  </head>
  <body>
    <h1>Newsletter</h1>
    <xsl:apply-templates/>
    <div class="menu">
      <a href="{$context-prefix}/authoring/index.html">Back to Frontpage</a>
      <xsl:value-of select="$menu-separator"/>
      <a href="{$context-prefix}/authoring/newsletter/index.html">Back to Newsletter</a>
    </div>
  </body>
  </html>
</xsl:template>
  
<xsl:template match="document">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="mail:sendmail">
  <p>
    <xsl:apply-templates/>
  </p>
</xsl:template>

</xsl:stylesheet> 
