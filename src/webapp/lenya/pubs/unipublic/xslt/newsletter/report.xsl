<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : report.xsl
    Created on : November 20, 2002, 2:59 PM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:session="http://www.lenya.org/2002/session"
    >

<xsl:param name="result"/>

<xsl:variable name="menu-separator" select="'&#160;&#160;|&#160;&#160;'"/>
<xsl:variable name="context-prefix"
    select="concat(/session:session/session:context, '/', /session:session/session:publication-id)"/>

<xsl:template match="/">
  <page>
    <title>Newsletter Report</title>
    <context><xsl:value-of select="/session:session/session:context"/></context>
    <publication-id><xsl:value-of select="/session:session/session:publication-id"/></publication-id>
  
    <body>
      <xsl:call-template name="message"/>
      <div class="menu">
        <a href="{$context-prefix}/authoring/index.html">Back to Frontpage</a>
        <xsl:value-of select="$menu-separator"/>
        <a href="{$context-prefix}/authoring/newsletter/index.html">Back to Newsletter</a>
      </div>
    </body>
  </page>
</xsl:template>

<xsl:template name="message">
  <xsl:choose>
    <xsl:when test="$result = 'success'">
      <xsl:call-template name="success"/>
    </xsl:when>
    <xsl:when test="$result = 'failure'">
      <xsl:call-template name="failure"/>
    </xsl:when>
  </xsl:choose>
  
</xsl:template>

<xsl:template name="success">
  <p>
    The newsletter was successfully sent.
  </p>
</xsl:template>

<xsl:template name="failure">
  <h2>Error!</h2>
  <p>
    The newsletter has not been sent. Consult the log files for further information.
  </p>
</xsl:template>

</xsl:stylesheet> 
