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
  <html><head>
<link rel="stylesheet" href="/{$publication}/{$module}/resource.css" type="text/css"/>
<title><xsl:value-of select="$publicationname"/>&#160;<i18n:text>Resource</i18n:text>&#160;<xsl:value-of select="$unid"/></title>
 <script type="text/javascript">
    _editor_url  = "/<xsl:value-of select="$publication"/>/xinha/"  // (preferably absolute) URL (including trailing slash) where Xinha is installed
    _editor_lang = "<xsl:value-of select="$lang"/>";      // And the language we need to use in the editor.
  </script>
  <script type="text/javascript" src="/{$publication}/xinha/XinhaCore.js"></script>
<script type="text/javascript" src="/{$publication}/xinha/my_config.js"></script>
  </head><body>
<h1><xsl:value-of select="$publicationname"/>&#160;<i18n:text>XML</i18n:text>&#160;<i18n:text>Resource</i18n:text>&#160;<xsl:value-of select="@unid"/></h1>

<form method="post" action="/{$publication}/{$module}/saverevision/{$unid}_{$lang}">

<table>
<xsl:apply-templates mode="root"/>
</table>
<xsl:apply-templates mode="richtextroot"/>
<br/><br/><input i18n:attribute="value" value="Save" type="submit"/>
</form>
<br/><br/></body></html>
</xsl:template>

<xsl:template match="*" mode="root">
<tr><th align="right"><i18n:text>Title</i18n:text></th><td>
<xsl:element name="input">
<xsl:attribute name="type">text</xsl:attribute>
<xsl:attribute name="name">lenya-title</xsl:attribute>
<xsl:attribute name="size">50</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="@title"/></xsl:attribute>
</xsl:element>
</td></tr>
<xsl:apply-templates mode="fields"/>
</xsl:template>


<xsl:template match="lenya:meta" mode="fields" priority="2">
<xsl:apply-templates select="*" mode="fields"/>
</xsl:template>

<xsl:template match="xhtml:head" mode="fields" priority="2"/>
<xsl:template match="dc:title" mode="fields" priority="2"/>
<xsl:template match="dc:creator" mode="fields" priority="2"/>
<xsl:template match="dc:language" mode="fields" priority="2"/>

<xsl:template match="*" mode="fields">
<xsl:variable name="name"><xsl:value-of select="name(.)"/></xsl:variable>
<xsl:variable name="local"><xsl:value-of select="local-name(.)"/></xsl:variable>
<xsl:if test="string-length($name) &gt; 0">
<xsl:choose>
<xsl:when test="*[1]"/>
<xsl:otherwise>
<tr>
<th align="right"><xsl:value-of select="$local"/></th>
<td>
<xsl:element name="input">
<xsl:attribute name="type">text</xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
<xsl:attribute name="size">50</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
</xsl:element>
</td>
</tr>
</xsl:otherwise>
</xsl:choose>
</xsl:if>
</xsl:template>

<xsl:template match="*" mode="richtextroot">
<xsl:apply-templates mode="richtext"/>
</xsl:template>

<xsl:template match="lenya:meta" mode="richtext" priority="2"/>
<xsl:template match="*[local-name()='head']" mode="richtext" priority="2"/>

<!-- TODO: Must handle more than one richtext field by changing the textarea id. -->
<xsl:template match="*" mode="richtext">
<xsl:if test="*[1]">
<xsl:variable name="local"><xsl:value-of select="local-name(.)"/></xsl:variable>
<h2><xsl:value-of select="$local"/></h2>
<textarea id="editfield1" name="{$local}" rows="20" cols="50" style="width: 100%; height: 70%;"><xsl:apply-templates/></textarea>
</xsl:if>
</xsl:template>

<!-- Copy -->
<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 
