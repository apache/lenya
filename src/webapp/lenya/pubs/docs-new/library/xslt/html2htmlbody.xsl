<?xml version="1.0"?>
<!--
Stylesheet which strips everything outside the <body> and replaces it with <div
class="content">, making raw HTML suitable for merging with the Forrest tabs
and menu.
-->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <xsl:import href="copyover.xsl"/>
  <xsl:template match="/html">
    <xsl:apply-templates select="body"/>
  </xsl:template>

  <xsl:template match="/html/body">
    <div class="content">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

</xsl:stylesheet>

