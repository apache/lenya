<?xml version="1.0" encoding="UTF-8" ?>

<!-- $Id: page-util.xsl,v 1.9 2004/02/08 13:09:19 andreas Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

<!-- includes the default CSS stylesheet -->
<xsl:template name="include-css">
  <xsl:param name="contextprefix"/>
  <link rel="stylesheet" type="text/css" href="{$contextprefix}/lenya/css/default.css" media="screen"/>
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

<!-- prints a list of $separator-separated strings -->
<xsl:template name="print-list-simple">
  <xsl:param name="list-string"/>
  <xsl:param name="separator" select="','"/>

  <xsl:choose>
    <xsl:when test="contains($list-string, $separator)">
      <xsl:value-of select="substring-before($list-string, $separator)"/><br />
      <xsl:call-template name="print-list-simple">
        <xsl:with-param name="list-string" select="substring-after($list-string, $separator)"/>
        <xsl:with-param name="separator" select="$separator"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$list-string"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
