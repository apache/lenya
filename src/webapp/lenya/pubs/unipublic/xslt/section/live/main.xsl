<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:variable name="unipublic">/wyona-cms/unipublic</xsl:variable>
<xsl:variable name="img-uni"><xsl:value-of select="$unipublic"/>/img_uni</xsl:variable>
<xsl:variable name="img-unipub"><xsl:value-of select="$unipublic"/>/img_unipublic</xsl:variable>

<xsl:template match="/">

<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=iso-8859-1"/>
<title>unipublic - Geist und Gesellschaft</title>
<link rel="stylesheet" type="text/css" href="{$unipublic}/unipublic.mac.css"/>
<script type="text/javascript" language="JavaScript">
<xsl:comment>
<!--antiframe-->
<!--
if (top.frames.length > 0) {top.location.href = self.location;}
-->
<!-- CSS Triage-->
<!--
if (navigator.appVersion.indexOf ('Win') >= 0) {
   seite = '{$unipublic}/unipublic.win.css';
   document.write('<link rel="stylesheet" type="text/css" href="'+seite+'">');
}
-->
</xsl:comment>
</script>

<meta http-equiv="content-type" content="text/html;charset=iso-8859-1"/>
<link href="{$unipublic}/unipublic.css" rel="styleSheet" type="text/css"/>
<meta http-equiv="content-type" content="text/html;charset=iso-8859-1"/>
<meta http-equiv="content-type" content="text/html;charset=iso-8859-1"/>
</head>
<body text="black" link="#333399" alink="#CC0000" vlink="#666666" background="{$img-unipub}/bg.gif">
<!--START kopf.html-->
<center>
<form action="http://www.unizh.ch/cgi-bin/unisearch" method="post">
<input type="hidden" value="www.unipublic.unizh.ch" name="url"/> <a name="topofpage">&#160;</a>
<table width="585" border="0" cellspacing="0" cellpadding="0" bgcolor="#666699">
<tr>
<td bgcolor="#999966" valign="middle" align="left"><img height="20" width="3" src="{$img-uni}/1.gif" alt=" "/><a href="http://www.unizh.ch/index.html"><img src="{$img-uni}/oliv_home.gif" alt="Home" border="0" height="17" width="31"/></a></td>
<td bgcolor="#999966" width="1">&#160;</td>
<td bgcolor="#999966" valign="middle" align="right"><a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">
<img src="{$img-uni}/oliv_kontakt.gif" alt="Kontakt" border="0" height="17" width="41" align="middle"/></a>
<img src="{$img-uni}/oliv_strich.gif" alt="|" height="17" width="7" align="middle"/>
<img src="{$img-uni}/oliv_suchen.gif" alt="Suchen" height="17" width="37" align="middle"/>
 <input type="text" name="keywords" size="18"/> <input src="{$img-uni}/oliv_go.gif" type="image" border="0" name="search" align="middle"/></td>
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
<td width="135" valign="bottom" align="right"><img height="21" width="120" src="{$img-unipub}/t_magazin.gif" alt="magazin"/></td>
<td width="315" valign="bottom"><img src="{$img-unipub}/r_geist.gif" width="138" height="13" border="0" alt="geist &#38; gesellschaft"/></td>

<td width="135" valign="bottom" align="right"></td>
</tr>

<tr>
<td width="135" align="right" valign="top">
<table border="0" cellpadding="0" cellspacing="0" width="115">
<tr>
<td><img height="25" src="{$img-unipub}/m_gesund.gif" border="0" alt="gesundheit" width="115"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" src="{$img-unipub}/m_d_geist.gif" border="0" name="geist" alt="geist &#38; gesellschaft" width= "115"/></td>
</tr>

<tr>
<td><img height="1" width="1" src= "{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src= "{$img-unipub}/m_umwelt.gif" border="0" alt="umwelt &#38; technik"/></td>
</tr>

<tr>
<td><img height="1" width="1" src= "{$img-unipub}/1.gif"/></td>

</tr>

<tr>
<td><img height="25" src="{$img-unipub}/m_recht.gif" border="0" alt="recht &#38; wirtschaft" width="115"/></td>
</tr>
</table>

<p>&#160;</p>

<table border="0" cellpadding="0" cellspacing="0" width="115">
<tr>
<td align="right"><img height="21" width="103" src="{$img-unipub}/t_camp.gif" alt="magazin"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/c_news.gif" border="0" alt="uni-news"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/c_portrait.gif" border="0" alt="Portraits"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/c_lorbeer.gif" border="0" alt="lorbeeren"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/c_beruf.gif" border="0" alt="berufungen"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/c_publik.gif" border="0" alt="publikationen"/></td>
</tr>
</table>

<p><img src="{$unipublic}/img/dossiers.gif" width="120" height="21" border="0"/></p>
</td>
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

<xsl:for-each select="/section/articles/article">
<p>
<a href="{@href}/"><span class="tsr-title"><xsl:apply-templates select="body.head/hedline/hl1"/></span></a><br />
<xsl:apply-templates select="body.head/abstract"/> (3.4.2002)
</p>
</xsl:for-each>

</td>
</tr>
</table>
</td>
</tr>

<tr>
<td width="135"></td>
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

</xsl:stylesheet>
