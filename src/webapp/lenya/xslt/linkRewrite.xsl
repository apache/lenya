<?xml version="1.0" encoding="UTF-8" ?>

<!-- $Id: linkRewrite.xsl,v 1.1 2003/11/24 17:25:49 egli Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xhtml"
  >

  <xsl:param name="idbefore"/>
  <xsl:param name="idafter"/>
  
  <xsl:template match="xhtml:a">
    <xsl:variable name="href">
      <xsl:choose>
	<xsl:when test="@href=$idbefore">
	  <xsl:value-of select="$idafter"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="@href"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <a href="{$href}">
      <xsl:apply-templates select="@*[not(local-name()='href')]|node()"/>
    </a>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
