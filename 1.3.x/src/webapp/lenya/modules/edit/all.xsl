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
<xsl:param name="module"/>
<xsl:param name="publicationname"/>
<xsl:param name="publicationlanguages"/>
<xsl:variable name="language"><xsl:value-of select="/index/@language"/></xsl:variable>

<xsl:template match="/index">
  <html>
    <head>
      <title><xsl:value-of select="$publication"/>&#160;<i18n:text>Resources</i18n:text></title>
    </head>	
    <body>
<h1><xsl:value-of select="$publication"/>&#160;<i18n:text>Resources</i18n:text></h1>
<table border="1">
<tr>
<th><i18n:text>UNID</i18n:text></th>
<th><i18n:text>Type</i18n:text></th>
<th><i18n:text>ID</i18n:text></th>
</tr>
<xsl:apply-templates select="resource"/>
</table></body></html>
</xsl:template>

<xsl:template match="resource">
<tr><td><xsl:element name="a">
<xsl:attribute name="href">/<xsl:value-of select="$publication"/>/<xsl:value-of select="$module"/>/<xsl:value-of select="@unid"/></xsl:attribute><xsl:value-of select="@unid"/></xsl:element></td>
<td><xsl:value-of select="@type"/><xsl:if test="@doctype">/<xsl:value-of select="@doctype"/></xsl:if></td>
<td><xsl:value-of select="@id"/></td>
</tr>
</xsl:template>

<!-- OBSOLETE
<xsl:template match="resource">
<tr><td><xsl:element name="a">
<xsl:attribute name="href">/<xsl:value-of select="$publication"/>/<xsl:value-of select="$module"/>/<xsl:value-of select="@type"/><xsl:if test="@doctype">/<xsl:value-of select="@doctype"/></xsl:if>/<xsl:value-of select="@unid"/></xsl:attribute><xsl:value-of select="@unid"/></xsl:element></td>
<td><xsl:value-of select="@id"/></td>
<td><xsl:value-of select="@title"/></td>
</tr>
</xsl:template>
-->

</xsl:stylesheet> 
