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
<xsl:param name="type"/>

<xsl:template match="/content">
  <html><head>
  <xsl:apply-templates select="resource" mode="head"/>
  </head><body> 
<xsl:apply-templates/>
   </body></html>
</xsl:template>

<xsl:template match="resource" mode="head">
<title><xsl:value-of select="$publicationname"/>&#160;<i18n:text>Resource</i18n:text>&#160;<xsl:value-of select="@unid"/></title>
</xsl:template>

<xsl:template match="resource">
<h1><xsl:value-of select="$publicationname"/>&#160;<i18n:text>Resource</i18n:text>&#160;<xsl:value-of select="@unid"/></h1>
<table border="1">
<tr>
<th><i18n:text>ID</i18n:text></th>
<th><i18n:text>Type</i18n:text></th>
<xsl:if test="@doctype"><th><i18n:text>DocumentType</i18n:text></th></xsl:if>
</tr>
<tr>
<td><xsl:value-of select="@type"/></td>
<td><xsl:value-of select="@id"/></td>
<xsl:if test="@doctype"><td colspan="2"><xsl:value-of select="@doctype"/></td></xsl:if>
</tr>
<xsl:apply-templates select="translation"/>
</table><br/>
</xsl:template>

<xsl:template match="translation">
<tr><td colspan="3"><i18n:text>Translation</i18n:text>&#160;<xsl:value-of select="@language"/>
<xsl:if test="@language = ../@defaultlanguage">&#160;(Default)</xsl:if></td>
</tr>
<xsl:apply-templates select="revision"/>
</xsl:template>

<xsl:template match="revision">
<tr><td>
<xsl:element name="a">
<xsl:attribute name="href">/<xsl:value-of select="$publication"/>/<xsl:value-of select="$module"/>/<xsl:value-of select="../../@unid"/>_<xsl:value-of select="../@language"/>!<xsl:value-of select="@revision"/></xsl:attribute><xsl:value-of select="@revision"/></xsl:element>
<xsl:if test="@revision = ../@live">&#160;LIVE</xsl:if>
<xsl:if test="@revision = ../@edit">&#160;EDIT</xsl:if>
</td>
<td>
<xsl:if test="@extension"><xsl:value-of select="@extension"/></xsl:if>
<xsl:if test="@href"><xsl:value-of select="@href"/></xsl:if></td>
<td><xsl:value-of select="@title"/></td>
</tr>
</xsl:template>

<!-- Copy -->
<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 
