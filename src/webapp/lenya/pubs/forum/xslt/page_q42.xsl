<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:variable name="context_prefix">/lenya/forum</xsl:variable>
<xsl:variable name="image_path"><xsl:value-of select="$context_prefix"/>/images</xsl:variable>

<xsl:template name="page">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org" />
<title>Q42</title>
<meta name="description" content="The official website of Q42." />
<meta name="keywords" content="Q42, Lon Boonen, Kars Veling, SPI, Single Page, Xopus, Lime, pro2type" />
<link rel="stylesheet" type="text/css" href="http://www.q42.nl/style_ie.css" />
</head>
<body bgcolor="#84A6BD">
<br />
<!-- Selects CSS -->
<!--
<script type="text/javascript" language="javascript"
src="http://www.q42.nl/general.js">
</script>
-->

<center><!-- top navigatie -->
<table width="620" border="0" cellspacing="0" cellpadding="4">
<tr>
<td colspan="2" width="620" valign="top"
background="http://www.q42.nl/media/page_header_bg.gif">
<center><img src="http://www.q42.nl/media/page_header_logo.gif" width="44"
height="65" alt="Q42" vspace="4" /><br />
</center>

<table border="0" width="100%" cellspacing="0" cellpadding="5">
<tr>
<td width="4%"></td>
<td width="15%" colspan="1" background="http://www.q42.nl/media/nix.gif"
valign="middle"><span limeid="link1" limetype="link"
limeaccess="*"><font color="#ffffff"><b>Home</b></font></span></td>
<td width="15%" colspan="1" background="http://www.q42.nl/media/nix.gif"
valign="middle"><span limeid="link2" limetype="link"
limeaccess="*"><a href="/products/"><b>Products</b></a></span></td>
<td width="15%" colspan="1" background="http://www.q42.nl/media/nix.gif"
valign="middle"><span limeid="link3" limetype="link"
limeaccess="*"><a href="/projects/"><b>Projects</b></a></span></td>
<td width="15%" colspan="1" background="http://www.q42.nl/media/nix.gif"
valign="middle"><span limeid="link4" limetype="link"
limeaccess="*"><a href="/research/"><b>Research</b></a></span></td>
<td width="15%" colspan="1" background="http://www.q42.nl/media/nix.gif"
valign="middle"><span limeid="link5" limetype="link"
limeaccess="*"><a href="/demos/"><b>Demos</b></a></span></td>
<td width="15%" colspan="1" background="http://www.q42.nl/media/nix.gif"
valign="middle"><span limeid="link6" limetype="link"
limeaccess="*"><a href="/contact/"
target=""><b>Contact</b></a></span></td>
</tr>
</table>
</td>
</tr>
</table>

<br />
 

<table width="620" border="0" cellspacing="0" cellpadding="0">
<tr><!-- linker kolom -->
<td width="140" valign="top">
<table border="0" cellspacing="0" cellpadding="0">
<tr>
<td background="http://www.q42.nl/media/leftcol_top.gif"><img src="http://www.q42.nl/media/nix.gif"
width="140" height="1" alt="" /><br />
<p><b>Q42</b> is a small&#160;and innovative internet agency that
focuses on the creation of friendly internet applications and
tools.<br />
We give our best in both projects and products.</p>

<br />
<br />
 </td>
</tr>

<tr>
<td style="background-color:#CDD8DE"><img src="http://www.q42.nl/media/nix.gif"
width="140" height="1" alt="" /><br />
<p>Most parts of our website are in english. Some parts, however,
are in dutch: skip them if you want.<br />
<br />
This page contains some recent and interesting news bits.<br />
At the moment there is a lot about <strong>Quek</strong> and
<strong>Lime</strong> and <strong>Xopus</strong>&#160;on
it...<br />
<br />
For some interesting old news visit our <a
href="/archive/">Archive</a></p>

<br />
<br />
 </td>
</tr>

<tr>
<td><img src="http://www.q42.nl/media/leftcol_bottom.gif" alt="" /></td>
</tr>
</table>
</td>


<td width="20"><img src="http://www.q42.nl/media/nix.gif" width="20" height="1"
alt="" /><br />
</td>


<!-- rechter kolom -->
<td width="460" valign="top">
<xsl:call-template name="cmsbody"/>
</td>
<!-- /rechter kolom -->
</tr>
</table>
</center>

<!-- Maintained by Lime, version 1.04 (Official Release, 15 november 2001). -->
</body>
</html>
</xsl:template>

</xsl:stylesheet>
