<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:variable name="unipublic">/wyona-cms/unipublic</xsl:variable>
<xsl:variable name="img-uni"><xsl:value-of select="$unipublic"/>/img_uni</xsl:variable>
<xsl:variable name="img-unipub"><xsl:value-of select="$unipublic"/>/img_unipublic</xsl:variable>
<xsl:variable name="section"><xsl:value-of select="/NewsML/NewsItem/NewsComponent/TopicSet/Topic/TopicType/FormalName"/></xsl:variable>

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">

<html>
<head>
<title>unipublic - <xsl:value-of select="$section"/></title>
<!--<title>unipublic - Uni-News</title>-->

<xsl:call-template name="styles"/>

<!--
   seite = 'http://www.unipublic.unizh.ch/unipublic.win.css';
-->

<script type="text/javascript" language="JavaScript">
<xsl:comment>
<!--antiframe-->
if (top.frames.length &#62; 0) {top.location.href = self.location;}

<!-- CSS Triage-->
if (navigator.appVersion.indexOf ('Win') &#62;= 0) {
   seite = '<xsl:value-of select="$unipublic"/>/unipublic.win.css';
   document.write('&#60;link rel="stylesheet" type="text/css" href="'+seite+'"&#62;');
}
</xsl:comment>
</script>

</head>
<body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">

<!--START kopf.html-->
<xsl:call-template name="Searchbox"/>
<!--ENDE kopf.html-->

<center>
<table cellspacing="0" cellpadding="0" border="0" width="585">
<tr height="16">
<td height="16" width="187" align="center" valign="top">
<center><a href="../../../../"><img height="52" width="108" src="{$img-unipub}/t_unipublic_ALT.gif" alt="Unipublic" border="0"/></a></center>
</td>
<td height="16" align="right" width="10"></td>
<td width="388" height="16"></td>
</tr>

<xsl:call-template name="slider_image"/>

<tr>
<td valign="top" width="187">
<table width="180" border="0" cellspacing="0" cellpadding="0">
<tr valign="top">
<td width="180">
<table width="180" border="0" cellspacing="0" cellpadding="0">
<tr valign="top">
<td width="180"><img height="19" width="187" src="{$img-unipub}/t_teil7.gif" alt="Muscheln1"/></td>
</tr>

<tr valign="top">
<td width="180" valign="middle" bgcolor="#CCCC99">
<xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.content/related.contents"/>
</td>
</tr>

<tr valign="top">
<td width="180"><img height="27" width="181" src="{$img-unipub}/t_teil8.gif" align="right"/></td>
</tr>
</table>
</td>
</tr>
</table>
</td>
<td width="10" bgcolor="white" valign="top">&#160; 

<p>&#160;</p>
</td>
<td valign="top" bgcolor="white" width="388" class="art-text">
<p class="art-date"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/dateline/story.date"/></p>

<p class="art-pretitle"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/head/hedline/dossier"/></p>

<p class="art-title1"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/hedline/hl1"/></p>

<p class="art-lead"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/abstract"/> </p>

<p class="art-author"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/byline"/></p>

<xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.content/block"/>

<p class="art-author"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.end/tagline"/></p>

</td>
</tr>

<tr>
<td width="187"></td>
<td width="10" bgcolor="white">&#160;</td>
<td bgcolor="white" width="388"><br />
  

<div align="left"><a href="#topofpage"><font size="1">zum Anfang<br /> <br />
</font></a> <img height="1" width="390" src="{$img-unipub}/999999.gif" alt=" "/><br />
 <font size="1">&#169; <xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/NewsLines/CopyrightLine"/>,
<xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/NewsLines/DateLine"/>, 
<a href="/ssi_unipublic/impressum.html">Impressum</a></font></div>
</td>
</tr>
</table>
</center>
</body>
</html>
</xsl:template>


<xsl:template name="slider_image">
<tr>
<td align="right" width="187"></td><td width="10"></td><td width="388"><a href="../">
<xsl:if test="contains($section, 'Geist und Gesellschaft')"><img height="13" width="138" src="{$img-unipub}/r_geist.gif" alt="{$section}" border="0"/></xsl:if>
<xsl:if test="contains($section, 'Gesundheit')"><img height="13" width="80" src="{$img-unipub}/r_gesund.gif" alt="{$section}" border="0"/></xsl:if>
<xsl:if test="contains($section, 'Umwelt und Technik')"><img height="13" width="97" src="{$img-unipub}/r_umwelt.gif" alt="{$section}" border="0"/></xsl:if>
<xsl:if test="contains($section, 'Recht und Wirtschaft')"><img height="13" width="133" src="{$img-unipub}/r_recht.gif" alt="{$section}" border="0"/></xsl:if>
</a>
</td>
</tr>
</xsl:template>

<xsl:template match="related.contents">
<xsl:for-each select="related.content">
<table border="0" cellpadding="0" cellspacing="8" width="100%">
<tr>
<td class="rel-title"><xsl:value-of select="@title"/></td>
</tr>
<xsl:apply-templates select="link"/>
</table>
</xsl:for-each>
</xsl:template>

<xsl:template match="link">
<tr>
<td class="rel-text">
<xsl:apply-templates/>
</td>
</tr>
</xsl:template>

<xsl:template match="link.title">
<a href="{@href}"><xsl:value-of select="."/></a><br />
</xsl:template>

<xsl:template match="link.text">
<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="block">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="p">
<p>
<xsl:if test="not(preceding-sibling::p)">
  <xsl:apply-templates select="../hl2" mode="block"/>
</xsl:if>
<xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="hl2" mode="block">
<span class="art-title3"><xsl:value-of select="."/></span><br />
</xsl:template>

<xsl:template match="hl2">
<!-- Comment -->
</xsl:template>

<!--<xsl:template match="media/@media-type[text()='image']">-->
<xsl:template match="media">
<p>&#160;</p>
<table border="0" cellpadding="0" cellspacing="0" width="250">
<tr>
<td>
<xsl:apply-templates select="media-reference" mode="image"/>
</td></tr>
<tr>
<td class="img-text"><xsl:value-of select="media-caption"/></td>
</tr>
<xsl:apply-templates select="up:authorline"/>
</table>
<p>&#160;</p>
</xsl:template>

<xsl:template match="media-reference" mode="image">
<img src="{@source}" alt="{@alternate-text}" />
</xsl:template>

<xsl:template match="up:authorline">
<tr>
<td class="img-author">(<xsl:value-of select="."/>)</td>
</tr>
</xsl:template>

<xsl:template name="Searchbox">
<center>
<form action="http://www.unizh.ch/cgi-bin/unisearch" method="post">
<input type="hidden" value="www.unipublic.unizh.ch" name="url"/>
 <a name="topofpage">&#160;</a>
<table width="585" border="0" cellspacing="0" cellpadding="0" bgcolor="#666699">
<tr>
<td bgcolor="#999966" valign="middle" align="left"><img height="20" width="3" src="{$img-uni}/1.gif" alt=" "/><a href="http://www.unizh.ch/index.html"><img src="{$img-uni}/oliv_home.gif" alt="Home" border="0" height="17" width="31"/></a></td>
<td bgcolor="#999966" width="1">&#160;</td>
<td bgcolor="#999966" valign="middle" align="right"><a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html"><img src="{$img-uni}/oliv_kontakt.gif" alt="Kontakt" border="0" height="17" width="41" align="middle" /></a><img src="{$img-uni}/oliv_strich.gif" alt="|" height="17" width="7" align="middle"/><img src="{$img-uni}/oliv_suchen.gif" alt="Suchen" height="17" width="37" align="middle" />
<input type="text" name="keywords" size="18"/> <input src="{$img-uni}/oliv_go.gif" type="image" border="0" name="search" align="middle"
/></td>
<td bgcolor="#F5F5F5" width="57">&#160;</td>
</tr>

<tr height="39">
<td align="right" height="39">&#160;</td>
<td align="right" height="39" valign="top" colspan="2"><a href="http://www.unizh.ch/index.html"><img height="29" width="235" src="{$img-uni}/unilogoklein.gif" alt="Universit&#228;t Z&#252;rich" border="0"/></a></td>
<td width="57" height="39">&#160;</td>
</tr>
</table>
</form>
</center>
</xsl:template>


<xsl:template name="styles">
<link type="text/css" rel="stylesheet" href="{$unipublic}/unipublic.css"/>
<link rel="stylesheet" type="text/css" href="{$unipublic}/unipublic.mac.css" />
</xsl:template>

</xsl:stylesheet>

