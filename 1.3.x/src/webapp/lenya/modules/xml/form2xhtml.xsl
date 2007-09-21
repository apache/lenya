<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    exclude-result-prefixes="page xhtml"
>
<xsl:param name="publication"/>
<xsl:param name="publicationname"/>
<xsl:param name="module"/>
<xsl:param name="unid"/>
<xsl:param name="lang"/>

<xsl:template match="/">
  <html>
<xsl:apply-templates select="form/field[@name = 'lenya-title']" mode="title"/>
<xsl:apply-templates select="form"/>
  </html>
</xsl:template>

<xsl:template match="field" mode="title">
<xsl:attribute name="title"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>

<xsl:template match="form">
<lenya:meta>
<xsl:apply-templates select="field" mode="meta"/>
</lenya:meta>
<xsl:apply-templates select="field" mode="rest"/>
</xsl:template>

<xsl:template match="field" mode="meta">
<xsl:if test="starts-with(@name, 'dc')">
<xsl:element name="{@name}">
<xsl:value-of select="."/>
</xsl:element>
</xsl:if>
</xsl:template>

<xsl:template match="field[@name = 'lenya-title']" mode="rest" priority="2"/>

<xsl:template match="field" mode="rest">
<xsl:if test="not(starts-with(@name, 'dc'))">
<xsl:element name="{@name}">
     <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()" mode="rest"/>
</xsl:element>
</xsl:if>
</xsl:template>

<!-- Unescape HTML entities -->
<xsl:template match="text()" mode="rest">
    <xsl:value-of disable-output-escaping="yes" select="."/>
</xsl:template>

<!-- Copy -->
<xsl:template match="@*|node()" mode="copy" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="copy"/>
  </xsl:copy>
</xsl:template>
<xsl:template match="@*|node()" mode="rest" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="rest"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 
