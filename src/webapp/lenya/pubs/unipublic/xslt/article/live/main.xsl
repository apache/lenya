<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:variable name="unipublic">/wyona-cms/unipublic</xsl:variable>
<xsl:variable name="img-uni"><xsl:value-of select="$unipublic"/>/img_uni</xsl:variable>
<xsl:variable name="img-unipub"><xsl:value-of select="$unipublic"/>/img_unipublic</xsl:variable>

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">

<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=iso-8859-1" />
<title>unipublic - Uni-News</title>
<link rel="stylesheet" type="text/css" href="{$unipublic}/unipublic.mac.css" />
<script type="text/javascript" language="JavaScript">
<xsl:comment>
<!--antiframe-->
<!--if (top.frames.length > 0) {top.location.href = self.location;}-->

<!-- CSS Triage-->
<!--
if (navigator.appVersion.indexOf ('Win') >= 0) {
   seite = '{$unipublic}/unipublic.win.css';
   document.write('<link rel="stylesheet" type="text/css" href="'+seite+'">');
}
-->
</xsl:comment>
</script>

<meta http-equiv="content-type" content="text/html;charset=iso-8859-1" />
<link type="text/css" rel="stylesheet" href="{$unipublic}/unipublic.css"/>
<meta http-equiv="content-type" content="text/html;charset=iso-8859-1" />
<meta http-equiv="content-type" content="text/html;charset=iso-8859-1" />

</head>
<body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">
<!--START kopf.html-->
<center>
<form action="http://www.unizh.ch/cgi-bin/unisearch" method="post">
<input type="hidden" value="www.unipublic.unizh.ch" name="url"/>
 <a name="topofpage">&#160;</a>
<table width="585" border="0" cellspacing="0" cellpadding="0" bgcolor="#666699">
<tr>
<td bgcolor="#999966" valign="middle" align="left"><img height="20" width="3" src="{$img-uni}/1.gif" alt=" "/><a href="http://www.unizh.ch/index.html"><img src="{$img-uni}/oliv_home.gif" alt="Home" border="0" height="17" width="31"/></a></td>
<td bgcolor="#999966" width="1">&#160;</td>
<td bgcolor="#999966" valign="middle" align="right"><a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html"><img src="{$img-uni}/oliv_kontakt.gif" alt="Kontakt" border="0" height="17" width="41" align="middle" /></a><img src="{$img-uni}/oliv_strich.gif" alt="|" height="17" width="7" align="middle"/><img src="{$img-uni}/oliv_suchen.gif" alt="Suchen" height="17" width="37" align="middle" /> 
<input type="text" name="keywords" size="18"/> <input src="{$img-uni}/oliv_go.gif" type="image" border="0" name="search" align="middle" /></td>
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

<tr>
<td align="right" width="187"></td>
<td width="10"></td>
<td width="388"><a href="../"><img height="13" width="138" src="{$img-unipub}/r_geist.gif" alt="Geist &#38; Gesellschaft" border="0"/></a></td>
</tr>

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
<table border="0" cellpadding="0" cellspacing="8" width="100%">
<tr>
<td class="rel-title">Link</td>
</tr>

<tr>
<td class="rel-text"><a href="programm.html">&#171;Inventur im
Museum. Musikalische Meisterwerke neu geh&#246;rt&#187; - Das
Programm</a></td>
</tr>
</table>
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
<p class="art-date">3.4.2002</p>

<p class="art-title1"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/hedline/hl1"/></p>

<p class="art-lead"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/abstract"/> </p>

<p class="art-author"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/byline"/></p>

<xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.content/block"/>

<!--<p><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.content/block/p"/></p>-->

<p class="art-author">Sabine Witt ist Redaktorin des
&#171;unijournals&#187; und freie Journalistin.</p>
</td>
</tr>

<tr>
<td width="187"></td>
<td width="10" bgcolor="white">&#160;</td>
<td bgcolor="white" width="388"><br />
  

<div align="left"><a href="#topofpage"><font size="1">zum
Anfang<br />
<br />
</font></a> <img height="1" width="390" src="{$img-unipub}/999999.gif" alt=" "/><br />
 <font size="1">&#169; Universit&#228;t Z&#252;rich, 05.04.2002 ,
<a href="/ssi_unipublic/impressum.html">Impressum</a></font></div>
</td>
</tr>
</table>
</center>
</body>
</html>
</xsl:template>

<xsl:template match="block">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="hl2">
<span class="art-title3"><xsl:value-of select="."/></span><br />
</xsl:template>

<xsl:template match="p">
<p><xsl:apply-templates/></p>
</xsl:template>

<!--<xsl:template match="media/@media-type[text()='image']">-->
<xsl:template match="media">
<table border="0" cellpadding="0" cellspacing="0" width="250">
<tr>
<td>
<xsl:apply-templates select="media-reference" mode="image"/>
</td></tr>
<tr>
<td class="img-text"><xsl:value-of select="media-caption"/></td>
</tr>
<tr>
<td class="img-author">Bilder: Sabine Witt</td>
</tr>
</table>
</xsl:template>

<xsl:template match="media-reference" mode="image">
<img src="{@source}" alt="{@alternate-text}" />
</xsl:template>

</xsl:stylesheet>

