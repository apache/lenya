<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:param name="channel"/>
<xsl:param name="section"/>
<xsl:param name="year"/>                                                                                                   

<xsl:template match="Page">
  <xsl:apply-templates select="Content"/>
</xsl:template>

<xsl:template match="Content">
<xsl:variable name="sectiontext"><xsl:value-of select="FirstColumn/MainNavigation/Channels/Channel/Sections/Section[@highlighted='true']"/></xsl:variable>     
<html>
<head>
<title>unipublic - <xsl:value-of select="$sectiontext"/></title>

<xsl:call-template name="styles"/>

<xsl:call-template name="jscript"/>

</head>
<body text="black" link="#333399" alink="#CC0000" vlink="#666666" background="{$img-unipub}/bg.gif">
<div align="center">
<!--START kopf.html-->
<xsl:call-template name="Searchbox"/>
<!--ENDE kopf.html-->

<table border="0" cellpadding="0" cellspacing="0" width="585" bordercolor="green">

<tr>
<td width="135" height="21" align="right" valign="top"><img src="{$img-unipub}/t_magazin.gif" border="0" alt="Magazin"/></td>
<!-- Section title -->
<td colspan="2" width="450" height="21" valign="bottom"><img src="{$img-unipub}/r_{$section}.gif" border="0" alt="{$sectiontext}"/> 

<!-- Drawing dynamic sub-navigation for the years 2002 and after (according to tree.xml) -->
<xsl:for-each select="MainColumn/tree/branch/branch/branch[@relURI=$section]/branch">
	<xsl:choose>
		<xsl:when test="@relURI=$year"><img alt="{@relURI}" src="{$img-unipub}/jahr/{@relURI}_ein.gif" height="13" width="39" border="0" /></xsl:when>
		<xsl:otherwise><a href="../{@relURI}/"><img alt="{@relURI}" src="{$img-unipub}/jahr/{@relURI}_aus.gif" height="13" width="39" border="0" /></a></xsl:otherwise>
	</xsl:choose>
</xsl:for-each>

<!-- Static years 1999-2001  -->
<a href="http://www.unipublic.unizh.ch/{$channel}/{$section}/2001/"><img alt="2001" src="{$img-unipub}/jahr/2001_aus.gif" height="13" width="39" border="0" /></a>
<a href="http://www.unipublic.unizh.ch/{$channel}/{$section}/2000/"><img alt="2000" src="{$img-unipub}/jahr/2000_aus.gif" height="13" width="39" border="0" /></a>
<a href="http://www.unipublic.unizh.ch/{$channel}/{$section}/1999/"><img alt="1999" src="{$img-unipub}/jahr/1999_aus.gif" height="13" width="39" border="0" /></a>
</td>
</tr>

<tr>
<td width="135" align="right" valign="top">

<xsl:apply-templates select="FirstColumn/MainNavigation">
  <xsl:with-param name="is-section">true</xsl:with-param>
</xsl:apply-templates>

</td>
<td width="5" valign="top" bgcolor="white"><img src="{$img-unipub}/spacer.gif" width="5" alt=" " /></td>
<td width="445" bgcolor="white" valign="top" class="tsr-text">
<br />

<xsl:apply-templates select="MainColumn/section/articles" mode="articles"/>

</td>
</tr>

<tr>
<td width="135"></td>
<td width="5"></td>
<td width="445">

<!-- <xsl:apply-templates select="MainColumn/section/articles" mode="Section_copyright"/> -->
<xsl:call-template name="footer">
  <xsl:with-param name="footer_date" select="MainColumn/section/articles/article[1]/body.head/dateline/story.date/@norm" />
</xsl:call-template>

</td>
</tr>
</table>
</div>
</body>
</html>
</xsl:template>

<!-- Logik -->

<xsl:template match="section[@type='gesundheit']" mode="section-name">
Gesundheit
</xsl:template>

<xsl:template match="section[@type='gesellschaft']" mode="section-name">
Geist &#38; Gesellschaft
</xsl:template>

<xsl:template match="section[@type='umwelt']" mode="section-name">
Umwelt &#38; Technik
</xsl:template>

<xsl:template match="section[@type='wirtschaft']" mode="section-name">
Recht &#38; Wirtschaft
</xsl:template>

<xsl:template match="section[@type='uni-news']" mode="section-name">
Uni-News
</xsl:template>

<xsl:template match="section[@type='portraits']" mode="section-name">
Portraits
</xsl:template>

<xsl:template match="section[@type='lorbeeren']" mode="section-name">
Lorbeeren
</xsl:template>

<xsl:template match="section[@type='berufungen']" mode="section-name">
Berufungen
</xsl:template>

<xsl:template match="section[@type='publikationen']" mode="section-name">
Publikationen
</xsl:template>

<xsl:template match="section" mode="section-name">
Exception: Section has no name!
</xsl:template>

<xsl:template match="type[normalize-space(text())='gesellschaft']" mode="section-name">
Geist &#38; Gesellschaft
</xsl:template>

<xsl:template match="type[normalize-space(text())='gesundheit']" mode="section-name">
Gesundheit
</xsl:template>

<xsl:template match="type" mode="section-name">
Exception: Section has no name!
</xsl:template>

</xsl:stylesheet>
