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
xmlns:publication="http://apache.org/lenya/1.3/publication"
xmlns:modules="http://apache.org/lenya/1.3/modules"
>

<xsl:param name="module"/>
<xsl:param name="publication"/>
<xsl:param name="publicationname"/>
<xsl:param name="languages"/>


<xsl:template match="/">
  <html><head>
  <xsl:call-template name="head"/>
  </head><body>
<h1><xsl:value-of select="$publicationname"/>&#160;<i18n:text>XML</i18n:text>&#160;<i18n:text>New Resource</i18n:text></h1>
<form method="post" action="/{$publication}/{$module}/save">
<table>
<tr><td>Resource Type</td><td>
  <xsl:call-template name="types"/>
</td></tr>
<tr><td>UNID</td><td>
<xsl:element name="input">
<xsl:attribute name="type">text</xsl:attribute>
<xsl:attribute name="name">unid</xsl:attribute>
<xsl:attribute name="value"></xsl:attribute>
</xsl:element><br/>Leave blank to generate UUID.</td></tr>
<tr><td>URL identifier (no spaces): </td><td><xsl:element name="input">
<xsl:attribute name="type">text</xsl:attribute>
<xsl:attribute name="name">id</xsl:attribute>
<xsl:attribute name="value"></xsl:attribute>
</xsl:element></td></tr>
</table>
<input type="submit"/>
</form>
   </body></html>
</xsl:template>


<xsl:template name="head">
<link rel="stylesheet" href="/{$publication}/{$module}/edit.css" type="text/css"/>
<title><xsl:value-of select="$publicationname"/>&#160;<i18n:text>New Resource</i18n:text></title>
</xsl:template>

<xsl:template name="types">
<table>
<xsl:apply-templates select="/data"/>
</table>
</xsl:template>

<xsl:template match="data">
<xsl:apply-templates select="publication:modules"/>
</xsl:template>

<xsl:template match="publication:modules">
<xsl:apply-templates select="publication:resource"/>
</xsl:template>


<xsl:template match="publication:resource">
<xsl:variable name="publication"><xsl:value-of select="@publication:publication"/></xsl:variable>
<xsl:variable name="module"><xsl:value-of select="@publication:module"/></xsl:variable>
<tr><td><xsl:element name="input">
<xsl:attribute name="type">radio</xsl:attribute>
<xsl:attribute name="name">type</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="@publication:resource"/></xsl:attribute>
</xsl:element><xsl:value-of select="@publication:name"/> (<xsl:value-of select="@publication:resource"/>)</td>
<td>
<xsl:apply-templates select="/data/modules:modules/modules:module[(@modules:publication = $publication) and (@modules:id = $module)]" mode="description"/>
</td></tr>
</xsl:template>

<xsl:template match="modules:modules" mode="description">
<xsl:value-of select="modules:description"/>
</xsl:template>

<!-- Copy -->
<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 
