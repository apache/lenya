<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:variable name="sectiontext"><xsl:apply-templates select="/up:Page/Content/MainColumn/section" mode="section-name"/></xsl:variable>

<!--
<xsl:template match="up:Page">
-->
<xsl:template match="Page">
  <xsl:apply-templates select="Content"/>
</xsl:template>

<xsl:template match="Content">
<html>
<head>
<title>unipublic - <xsl:value-of select="$sectiontext"/> (<xsl:value-of select="MainColumn/section/@type"/>)</title>

<xsl:call-template name="styles"/>

<xsl:call-template name="jscript"/>

</head>
<body text="black" link="#333399" alink="#CC0000" vlink="#666666" background="{$img-unipub}/bg.gif">

<!--START kopf.html-->
<xsl:call-template name="Searchbox"/>
<!--ENDE kopf.html-->

<center>
<table border="0" cellpadding="0" cellspacing="0" width="585">
<tr>
<td width="135" valign="bottom"></td>
<td width="315">

<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td width="169">&#160;</td>
<td><a href="../../../"><img height="63" width="108" src="{$img-unipub}/t_publogo.gif" alt="unipublic" border="0"/></a></td>
<td width="38">&#160;</td>
</tr>
</table>

</td>
<td width="135" valign="bottom"></td>
</tr>

<tr>
<td width="135" align="right" valign="top" rowspan="3">

<xsl:apply-templates select="FirstColumn/MainNavigation"/>

</td>
<td width="315" height="21" valign="bottom"><img src="{$img-unipub}/r_{MainColumn/section/@type}.gif" border="0" alt="{$sectiontext}"/></td>
<td width="135" valign="bottom" align="right"></td>
</tr>

<tr>
<td valign="top" colspan="2">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td width="5" valign="top" bgcolor="white">&#160;</td>
<td width="295" bgcolor="white"><font size="1" face="Geneva,Helvetica,Arial,Swiss,Nu Sans Regular"><br />
 <b>2002</b> | 2001 | 2000 | 1999</font></td>
</tr>

<xsl:apply-templates select="MainColumn/section/articles" mode="articles"/>

</table>
</td>
</tr>

<xsl:apply-templates select="MainColumn/section/articles" mode="Section_copyright"/>

</table>
</center>
</body>
</html>
</xsl:template>

<!-- Logik -->

<xsl:template match="section[@type='gesundheit']" mode="section-name">
Gesundheit
</xsl:template>

<xsl:template match="section[@type='geist']" mode="section-name">
Geist &#38; Gesellschaft
</xsl:template>

<xsl:template match="section[@type='umwelt']" mode="section-name">
Umwelt &#38; Technik
</xsl:template>

<xsl:template match="section[@type='recht']" mode="section-name">
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

<xsl:template match="type[normalize-space(text())='geist']" mode="section-name">
Geist &#38; Gesellschaft
</xsl:template>

<xsl:template match="type[normalize-space(text())='gesundheit']" mode="section-name">
Gesundheit
</xsl:template>

<xsl:template match="type" mode="section-name">
Exception: Section has no name!
</xsl:template>

</xsl:stylesheet>
