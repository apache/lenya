<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes"/>

<xsl:template match="navigation">
<html>
<body>
  <xsl:apply-templates select="main-navigation"/>
</body>
</html>
</xsl:template>

<xsl:template match="main-navigation">
    <font face="verdana" size="-1">
      <xsl:apply-templates mode="node">
<!--
        <xsl:with-param name="offset">&#160;&#160;</xsl:with-param>
        <xsl:with-param name="index"></xsl:with-param>
-->
        <xsl:with-param name="offset">..</xsl:with-param>
        <xsl:with-param name="index">+</xsl:with-param>
      </xsl:apply-templates>
    </font>
</xsl:template>

<xsl:template match="item" mode="node">
  <xsl:param name="offset"/>
  <xsl:param name="index"/>

  <xsl:value-of select="$index"/>
  <xsl:choose>
    <xsl:when test="@href">
      <xsl:choose>
        <xsl:when test="contains(@href,'http://')">
          <a href="{@href}"><xsl:value-of select="name"/></a> (external link)
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="@target">
              <a href="CONTEXT_PREFI{@href}"><font color="red"><xsl:value-of select="name"/></font></a> (internal link)
            </xsl:when>
            <xsl:otherwise>
              <a href="CONTEXT_PREFI{@href}"><xsl:value-of select="name"/></a> (internal link)
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="name"/> (no link)
    </xsl:otherwise>
  </xsl:choose>
  <br />

  <xsl:apply-templates mode="node">
    <xsl:with-param name="offset"><xsl:value-of select="$offset"/></xsl:with-param>
    <xsl:with-param name="index"><xsl:value-of select="concat($index,$offset)"/></xsl:with-param>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="name" mode="node"></xsl:template>
 
</xsl:stylesheet>
