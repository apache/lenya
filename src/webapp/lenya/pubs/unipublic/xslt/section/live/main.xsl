<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="../../head.xsl"/>
<xsl:include href="../../HTMLhead.xsl"/>
<xsl:include href="../../navigation.xsl"/>
<xsl:include href="../../variables.xsl"/>

<xsl:variable name="sectiontext"><xsl:apply-templates select="/Page/Content/MainColumn/section" mode="section-name"/></xsl:variable>

<xsl:template match="/Page/Content">
<html>
<head>
<!--
<title>unipublic - TEXT: <xsl:value-of select="$sectiontext"/> ELEMENT:<xsl:apply-templates select="MainColumn/section/type" mode="section-name"/> ATTRIBUTE:<xsl:apply-templates select="MainColumn/section" mode="section-name"/> (<xsl:value-of select="MainColumn/section/@type"/>)</title>
-->
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
<!--
<xsl:apply-templates select="MainNavigation"/>
-->
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

<tr>
<td width="5" bgcolor="white" valign="top">&#160;</td>
<td width="295" bgcolor="white" class="tsr-text"><br />

<xsl:for-each select="MainColumn/section/articles/article">
<p>
<a href="{@href}/"><span class="tsr-title"><xsl:apply-templates select="body.head/hedline/hl1"/></span></a><br />
<xsl:apply-templates select="body.head/abstract"/> (<xsl:apply-templates select="body.head/dateline/story.date/@norm"/>)
</p>
</xsl:for-each>

</td>
</tr>
</table>
</td>
</tr>

<tr>
<td colspan="2" bgcolor="white"><br />
  

<div align="left"><a href="#topofpage"><font size="1">zum
Anfang<br />
<br />
</font></a> <img height="1" width="390" src="{$img-unipub}/999999.gif" alt=" "/><br />
 <font size="1">&#169; Universit&#228;t Z&#252;rich, 05.04.2002 ,
<a href="/ssi_unipublic/impressum.html">Impressum</a></font></div>

<br />
</td>
</tr>
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
