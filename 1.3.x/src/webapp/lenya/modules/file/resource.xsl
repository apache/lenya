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

<xsl:param name="module"/>
<xsl:param name="publication"/>
<xsl:param name="publicationname"/>
<xsl:param name="languages"/>

<xsl:template match="/content">
  <html><head>
  <xsl:apply-templates select="resource" mode="head"/>
  </head><body>
<xsl:apply-templates/>
   </body></html>
</xsl:template>


<xsl:template match="resource" mode="head">
<link rel="stylesheet" href="/{$publication}/{$module}/edit.css" type="text/css"/>
<title><xsl:value-of select="$publicationname"/>&#160;<i18n:text>Resource</i18n:text>&#160;<xsl:value-of select="@unid"/></title>
</xsl:template>


<xsl:template match="resource">
<h1><xsl:value-of select="$publicationname"/>&#160;<i18n:text>File</i18n:text>&#160;<i18n:text>Resource</i18n:text>&#160;<xsl:value-of select="@unid"/></h1>
<form method="post" action="/{$publication}/{$module}/save/{@unid}" enctype="multipart/form-data">
<table>
<!-- Resource Info -->
<tr>
<th colspan="3"><i18n:text>ID</i18n:text></th>
<td colspan="3"><xsl:value-of select="@id"/></td>
<td colspan="2" class="dark">
<xsl:element name="input">
<xsl:attribute name="type">radio</xsl:attribute>
<xsl:attribute name="name">defaultlanguage</xsl:attribute>
<xsl:attribute name="value"></xsl:attribute>
<xsl:if test="string-length(@defaultlanguage) &lt; 1"><xsl:attribute name="checked"/></xsl:if>
</xsl:element><i18n:text>No Default Language</i18n:text>
</td>
</tr>
<tr>
<th colspan="3"><i18n:text>Change ID to</i18n:text></th>
<td colspan="5">
[Not implemented yet]
<!-- TODO: Must update Structures in FlatResource.setID()
<xsl:element name="input">
<xsl:attribute name="type">text</xsl:attribute>
<xsl:attribute name="name">new.id</xsl:attribute>
<xsl:attribute name="size">60</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
</xsl:element>
-->
</td>
</tr>
<!-- Spacer -->
<tr><td colspan="8" class="spacer">&#160;</td></tr>
<!-- Column Headers -->
<xsl:if test="translation">
<tr>
<xsl:if test="@doctype"><td colspan="2"><xsl:value-of select="@doctype"/></td></xsl:if>
</tr>
<tr>
<th class="dark"><i18n:text>Delete</i18n:text></th>
<th><i18n:text>Live</i18n:text></th>
<th><i18n:text>Edit</i18n:text></th>
<th><i18n:text>Extension</i18n:text></th>
<th><i18n:text>Title</i18n:text></th>
<th><i18n:text>Creator</i18n:text></th>
<th><i18n:text>When</i18n:text></th>
<th><i18n:text>Revision</i18n:text></th>
</tr>
</xsl:if>
<!-- Translations -->
<xsl:apply-templates select="translation"/>
<!-- Spacer -->
<tr><td colspan="8" class="spacer">&#160;</td></tr>

<!-- New Translation -->
<xsl:variable name="languageoptionstest"><xsl:call-template name="languageoptionstest"/></xsl:variable>
<xsl:if test="string-length($languageoptionstest) &gt; 0">
<tr>
<th colspan="2" rowspan="3" class="spacer"><i18n:text>New</i18n:text><br/><i18n:text>Translation</i18n:text></th>
<th><i18n:text>Language</i18n:text></th>
<td colspan="3">
<xsl:element name="SELECT">
<xsl:attribute name="name">new.language</xsl:attribute>
<OPTION VALUE=""><i18n:text>None</i18n:text></OPTION>
<xsl:call-template name="languageoptions"/>
</xsl:element>
</td>
<td colspan="2" class="dark">
<xsl:element name="input">
<xsl:attribute name="type">radio</xsl:attribute>
<xsl:attribute name="name">defaultlanguage</xsl:attribute>
<xsl:attribute name="value">new</xsl:attribute>
</xsl:element><i18n:text>Default Language</i18n:text>
</td>
</tr>

<tr>
<th><i18n:text>File</i18n:text></th>
<td colspan="5">
<xsl:element name="input">
<xsl:attribute name="type">file</xsl:attribute>
<xsl:attribute name="name">new.file</xsl:attribute>
<xsl:attribute name="size">50</xsl:attribute>
</xsl:element>
</td>
</tr>
<tr>
<th><i18n:text>Title</i18n:text></th>
<td colspan="5">
<xsl:element name="input">
<xsl:attribute name="type">text</xsl:attribute>
<xsl:attribute name="name">new.title</xsl:attribute>
<xsl:attribute name="size">60</xsl:attribute>
</xsl:element>
</td>
</tr>
</xsl:if>
<!-- Finish -->
<tr><td colspan="8" class="spacer">&#160;</td></tr>
<tr><th colspan="8" class="spacer"><input type="submit" value="Save" i18n:attribute="value"/></th></tr>
</table>
</form>
<br/>
</xsl:template>


<xsl:template match="translation">
<xsl:variable name="edit"><xsl:value-of select="@edit"/></xsl:variable>
<!-- Spacer -->
<tr><td colspan="8" class="spacer">&#160;</td></tr>
<!-- Header -->
<tr>
<td class="dark">
<xsl:if test="@language != ../@defaultlanguage">
<xsl:element name="input">
<xsl:attribute name="type">checkbox</xsl:attribute>
<xsl:attribute name="name">delete</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="@language"/></xsl:attribute>
</xsl:element>
</xsl:if>
</td>
<td>
<xsl:element name="input">
<xsl:attribute name="type">radio</xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@language"/>.live</xsl:attribute>
<xsl:attribute name="value"/>
<xsl:if test="string-length(@live) &lt; 1"><xsl:attribute name="checked"/></xsl:if>
</xsl:element>
</td>
<th colspan="4"><i18n:text>Translation</i18n:text>&#160;<xsl:value-of select="@language"/></th>
<td colspan="2" class="dark">
<xsl:element name="input">
<xsl:attribute name="type">radio</xsl:attribute>
<xsl:attribute name="name">defaultlanguage</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="@language"/></xsl:attribute>
<xsl:if test="@language = ../@defaultlanguage"><xsl:attribute name="checked"/></xsl:if>
</xsl:element><i18n:text>Default Language</i18n:text>
</td>
</tr>
<!-- Revisions -->
<xsl:apply-templates select="revision"/>
<!-- New Revision -->
<tr>
<th rowspan="2"><i18n:text>New</i18n:text></th>
<td rowspan="2">
<xsl:element name="input">
<xsl:attribute name="type">radio</xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@language"/>.live</xsl:attribute>
<xsl:attribute name="value">new</xsl:attribute>
</xsl:element>
</td>

<th><i18n:text>File</i18n:text></th>
<td colspan="5">
<xsl:element name="input">
<xsl:attribute name="type">file</xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@language"/>.file</xsl:attribute>
<xsl:attribute name="size">50</xsl:attribute>
</xsl:element>
</td>
</tr>
<tr>
<th><i18n:text>Title</i18n:text></th>
<td colspan="5">
<xsl:element name="input">
<xsl:attribute name="type">text</xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@language"/>.title</xsl:attribute>
<xsl:attribute name="size">60</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="revision[@revision= $edit]/@title"/></xsl:attribute>
</xsl:element>
</td>
</tr>
</xsl:template>


<xsl:template match="revision">
<tr>
<td class="dark">
<xsl:if test="(@revision != ../@live) and (@revision != ../@edit)">
<xsl:element name="input">
<xsl:attribute name="type">checkbox</xsl:attribute>
<xsl:attribute name="name">delete</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="../@language"/>.<xsl:value-of select="@revision"/></xsl:attribute>
</xsl:element>
</xsl:if>
</td>

<td>
<xsl:element name="input">
<xsl:attribute name="type">radio</xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="../@language"/>.live</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="@revision"/></xsl:attribute>
<xsl:if test="@revision = ../@live"><xsl:attribute name="checked"/></xsl:if>
</xsl:element>
</td>

<td>
<xsl:element name="input">
<xsl:attribute name="type">radio</xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="../@language"/>.edit</xsl:attribute>
<xsl:attribute name="value"><xsl:value-of select="@revision"/></xsl:attribute>
<xsl:if test="@revision = ../@edit"><xsl:attribute name="checked"/></xsl:if>
</xsl:element>
</td>

<td>
<xsl:value-of select="@extension"/></td>
<td><xsl:value-of select="@title"/></td>
<td><xsl:value-of select="@creator"/></td>
<td><xsl:value-of select="@when"/></td>
<td><xsl:value-of select="@revision"/></td>
</tr>
</xsl:template>


<xsl:template name="languageoption">
<xsl:param name="lang"/>
<xsl:if test="not(/content/resource/translation[@language = $lang])">
<option value="{$lang}"><i18n:text key="language-{$lang}"><xsl:value-of select="$lang"/></i18n:text></option>
</xsl:if>
</xsl:template>

<xsl:template name="languageoptions">
<xsl:param name="langs" select="$languages"/>
<xsl:variable name="more"><xsl:value-of select="substring-after($langs,';')"/></xsl:variable>
<xsl:choose>
<xsl:when test="string-length($more) &gt; 0">
<xsl:call-template name="languageoption">
   <xsl:with-param name="lang" select="substring-before($langs,';')"/>
</xsl:call-template>
<xsl:call-template name="languageoptions">
   <xsl:with-param name="langs" select="$more"/>
</xsl:call-template>
</xsl:when>
<xsl:otherwise>
<xsl:call-template name="languageoption">
   <xsl:with-param name="lang" select="$langs"/>
</xsl:call-template>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template name="languagetest">
<xsl:param name="language"/>
<xsl:if test="not(/content/resource/translation[@language = $language])"><xsl:value-of select="$language"/></xsl:if>
</xsl:template>

<xsl:template name="languageoptionstest">
<xsl:param name="langs" select="$languages"/>
<xsl:variable name="more"><xsl:value-of select="substring-after($langs,';')"/></xsl:variable>
<xsl:choose>
<xsl:when test="string-length($more) &gt; 0">
<xsl:call-template name="languagetest">
   <xsl:with-param name="langs" select="substring-before($langs,';')"/>
</xsl:call-template>
<xsl:call-template name="languageoptionstest">
   <xsl:with-param name="langs" select="$more"/>
</xsl:call-template>
</xsl:when>
<xsl:otherwise>
<xsl:call-template name="languagetest">
   <xsl:with-param name="language" select="$langs"/>
</xsl:call-template>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<!-- Copy -->
<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 
