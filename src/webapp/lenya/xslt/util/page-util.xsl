<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : page-util.xsl.xsl
    Created on : November 18, 2002, 10:47 AM
    Author     : ah
    Description: Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

<!-- includes the default CSS stylesheet -->
<xsl:template name="include-css">
  <link rel="stylesheet" type="text/css"
    href="/lenya/lenya/css/default.css" title="default css"/>
</xsl:template>
    
<!-- prints a list of $separator-separated strings -->
<xsl:template name="print-list">
  <xsl:param name="list-string"/>
  <xsl:param name="separator" select="','"/>
  <xsl:choose>
    <xsl:when test="contains($list-string, $separator)">
      <li><xsl:value-of select="substring-before($list-string, $separator)"/></li>
      <xsl:call-template name="print-list">
        <xsl:with-param name="list-string" select="substring-after($list-string, $separator)"/>
        <xsl:with-param name="separator" select="$separator"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <li><xsl:value-of select="$list-string"/></li>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
