<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:oscom="http://www.oscom.org/2002/oscom">
 
<xsl:template match="oscom_navigation">
    <font face="verdana" size="-2">
      <xsl:apply-templates>
        <xsl:with-param name="offset">&#160;&#160;</xsl:with-param>
        <xsl:with-param name="index"></xsl:with-param>
      </xsl:apply-templates>
    </font>
</xsl:template>

<xsl:template match="branch">
  <xsl:param name="offset"/>
  <xsl:param name="index"/>

  <xsl:value-of select="$index"/>
  <xsl:choose>
    <xsl:when test="@href">
      <xsl:choose>
        <xsl:when test="contains(@href,'http://')">
          <a href="{@href}"><xsl:value-of select="@name"/></a>
        </xsl:when>
        <xsl:otherwise>
          <a href="{$CONTEXT_PREFIX}/{@href}"><xsl:value-of select="@name"/></a>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="@name"/>
    </xsl:otherwise>
  </xsl:choose>
  <br />

  <xsl:apply-templates>
    <xsl:with-param name="offset"><xsl:value-of select="$offset"/></xsl:with-param>
    <xsl:with-param name="index"><xsl:value-of select="concat($index,$offset)"/></xsl:with-param>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="leaf">
  <xsl:param name="offset"/>
  <xsl:param name="index"/>

  <xsl:value-of select="$index"/>
  <xsl:choose>
    <xsl:when test="@href">
      <xsl:choose>
        <xsl:when test="contains(@href,'http://')">
          <a href="{@href}"><xsl:value-of select="@name"/></a>
        </xsl:when>
        <xsl:otherwise>
          <a href="{$CONTEXT_PREFIX}/{@href}"><xsl:value-of select="@name"/></a>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="@name"/>
    </xsl:otherwise>
  </xsl:choose>
  <br />
</xsl:template>
 
</xsl:stylesheet>  
