<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="../../head.xsl"/>
<xsl:include href="../../HTMLhead.xsl"/>
<xsl:include href="../../variables.xsl"/>
<!--
<xsl:variable name="unipublic">/wyona-cms/unipublic</xsl:variable>
<xsl:variable name="img-uni"><xsl:value-of select="$unipublic"/>/img_uni</xsl:variable>
<xsl:variable name="img-unipub"><xsl:value-of select="$unipublic"/>/img_unipublic</xsl:variable>
-->
<xsl:template match="/">

<html>
<head>
<title>unipublic - Geist und Gesellschaft</title>

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
<xsl:apply-templates select="body.head/abstract"/> (<xsl:apply-templates select="body.head/dateline/story.date/@norm"/>)
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
