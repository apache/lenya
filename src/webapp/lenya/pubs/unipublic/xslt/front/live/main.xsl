<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="../../head.xsl"/>
<xsl:include href="../../HTMLhead.xsl"/>
<xsl:include href="../../variables.xsl"/>
<xsl:include href="../../navigation.xsl"/>

<xsl:template match="/">
<html>
<head>
<title>unipublic - Das Online-Magazin der Universit&#228;t Z&#252;rich</title>

<script type="text/javascript" language="JavaScript1.2">
<xsl:comment>
function aboWindow() {
    newWind = open("newsletter.html","Display", "toolbar='no', statusbar='no', height='280', width='225'" );
}
</xsl:comment>        
</script>

<xsl:call-template name="styles"/>

<xsl:call-template name="jscript"/>

<style type="text/css">
<xsl:comment>
.tsr-title { font-weight: bold; font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular"; text-transform: uppercase }
.tsr-text { font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular" }
.webperlen { font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular" }
.top-title { font-size: 18px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular"; text-transform: uppercase }
</xsl:comment>
</style>

</head>	
<body text="#333333" link="#333399" alink="#993300" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">

<center>

<!--START kopf.html-->
<xsl:call-template name="Searchbox"/>
<!--ENDE kopf.html-->

<table border="0" cellpadding="0" cellspacing="0" width="585">
<tr>
<td width="135" valign="bottom" align="right"><img height="21" width="120" src="{$img-unipub}/t_magazin.gif" alt="magazin"/></td>
<td width="315">
<table border="0" cellpadding="0" cellspacing="0" width="315">
<tr>
<td valign="bottom" width="19"><img height="9" width="19" src="{$img-unipub}/eck.gif"/></td>
<td width="150" align="center" valign="bottom"></td>
<td width="108"><img height="63" width="108" src="{$img-unipub}/t_publogo.gif" alt="unipublic"/></td>
<td width="38"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>
</table>
</td>
<td width="135" valign="bottom" align="left"><img height="21" width="120" src="{$img-unipub}/t_service.gif" alt= "service"/></td>
</tr>

<tr>
<td width="135" align="right" valign="top">

<xsl:call-template name="navigation"/>

</td>
<td width="315" valign="top">
<table border="0" cellpadding="0" cellspacing="0" width="315">
<tr height="5">
<td width="5" bgcolor="#CCCC99" height="5"></td>
<td width="150" bgcolor="#CCCC99" height="5"><img src="{$img-unipub}/1.gif" width="10" height="5" border="0"/></td>
<td width="5" bgcolor="#CCCC99" height="5"></td>
<td width="1" bgcolor="#CCCC99" height="5"></td>
<td align="right" width="150" bgcolor="#CCCC99" height="5"></td>
<td width="5" bgcolor="#CCCC99" height="5"></td>
</tr>

<tr>
<td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td align="right" width="150" bgcolor="#CCCC99" valign="bottom"><a href="magazin/gesellschaft/2002/0515/"><img src="{$unipublic}/magazin/gesellschaft/2002/0515/bild-headline.gif" width="80" height="60" border="0" alt="Bruckner"/></a></td>
<td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="1" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td align="right" width="150" bgcolor="#CCCC99"><img src="{$unipublic}/campus/uni-news/2002/0520/bild-headline.jpg" width="80" height="60" border="0" alt="ZB"/></td>
<td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td width="5" bgcolor="white"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="150" valign="top" bgcolor="white" class="tsr-text">



<p><a href="{articles/article[1]/@href}/"><span class="tsr-title"><xsl:apply-templates select="articles/article[1]/body.head/hedline/hl1"/></span></a><br />
<xsl:apply-templates select="articles/article[1]/body.head/abstract"/>(<xsl:apply-templates select="articles/article[1]/body.head/dateline/story.date/@norm"/>)</p>


</td>
<td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="1" bgcolor="white"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="150" valign="top" bgcolor="white" class="tsr-text">

<p><a href="{articles/article[2]/@href}/"><span class="tsr-title"><xsl:apply-templates select="articles/article[2]/body.head/hedline/hl1"/> </span></a><br />

<xsl:apply-templates select="articles/article[2]/body.head/abstract"/>(<xsl:apply-templates select="articles/article[2]/body.head/dateline/story.date/@norm"/>)</p>

</td>
<td width="5" bgcolor="white"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td width="5"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="150"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="1"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="150"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="5"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>
</table>

<br />
 
<xsl:for-each select="articles/article">
<xsl:if test="position()>=3">
<xsl:variable name="section"><xsl:value-of select="@section"/></xsl:variable>

<table border="0" cellpadding="0" cellspacing="0" width="316">
<tr>
<td colspan="3">
<table border="0" cellpadding="0" cellspacing="0" width="316">
<tr>
<td colspan="3"><img src="{$img-unipub}/t_{$section}.gif" width="316" height="13" border="0" alt="{$section}"/></td>
</tr>

<tr>
<td bgcolor="white" colspan="3">&#160;</td>
</tr>

<tr>
<td width="4" bgcolor="white">&#160;</td>
<td bgcolor="white" class="tsr-text">


<p><a href="{@href}/"><img src="{$unipublic}/campus/uni-news/2002/0513/bild-headline.jpg" width="80" height="60" border="0" alt="unijournal" align="right"/></a><span class="tsr-title"><a href="{@href}/"><xsl:apply-templates select="body.head/hedline/hl1"/></a> </span><br />
<xsl:apply-templates select="body.head/abstract"/></p>

</td>
<td width="4" bgcolor="white">&#160;</td>
</tr>

<tr>
<td bgcolor="white" colspan="3">&#160;</td>
</tr>
</table>
</td>
</tr>
</table>
</xsl:if>
</xsl:for-each>

</td>
<td width="135" valign="top">
<table border="0" cellpadding="0" cellspacing="0" width="126">
<tr>
<td><img height="25" width="115" src="{$img-unipub}/s_agenda.gif" border="0" alt="agenda"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/s_mediadesk.gif" border="0" name="media" alt="mediadesk"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/s_neuepro.gif" border="0" alt="neue produkte"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/s_unimus.gif" border="0" alt="uni-museen"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/s_weiter.gif" border="0" alt="weiterbildung"/></td>
</tr>

<tr>
<td><br />
 <img height="18" width="126" src="{$img-unipub}/newslett.gif" border="0" alt="newsletter abo"/></td>
</tr>

<tr>
<td><br />
 <a href="../webperlen/"><img height="40" width="83" src="{$img-unipub}/t_webperlen.gif" alt="webperlen" border="0"/>
</a></td>
</tr>
</table>

<table border="0" cellpadding="0" cellspacing="0" width="135">
<tr>
<td bgcolor="#CCCCFF" width="5"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td bgcolor="#CCCCFF" width="125" class="webperlen"><font color="#333333">HANDVERLESENE PERLEN AUS DEM WEB DER UNI</font> 

<p><img height="1" width="125" src="{$img-unipub}/white.gif"/></p>

<p><font color="#333333"><b><img height="7" width="7" src="{$img-unipub}/t_perle.gif"/></b></font> Die Matrikel der<br />
 Universit&#228;t Z&#252;rich<br />
 1833 bis 1912 (-1914)</p>

<p><img height="1" width="125" src="{$img-unipub}/white.gif"/></p>

<p><font color="#333333"><b><img height="7" width="7" src="{$img-unipub}/t_perle.gif"/></b></font> Die BSE-Homepage:
Fachwissen zum Thema BSE - mit Video!</p>

<p><img height="1" width="125" src="{$img-unipub}/white.gif"/></p>

<p><font color="#333333"><b><img height="7" width="7" src="{$img-unipub}/t_perle.gif"/></b></font> Johann Caspar Lavater:
Ausgew&#228;hlte Werke in historisch-kritischer Ausgabe</p>

<p><img height="1" width="125" src="{$img-unipub}/white.gif"/></p>

<p><font color="#333333"><b><img height="7" width="7" src="{$img-unipub}/t_perle.gif"/> <img height="7" width="7" src="{$img-unipub}/t_perle.gif"/> <img height="7" width="7" src="{$img-unipub}/t_perle.gif"/></b></font> weitere Perlen ...</p>

</td>
<td bgcolor="#CCCCFF" width="5"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>
</table>
</td>
</tr>

<tr>
<td width="135"></td>
<td width="315"></td>
<td width="135"></td>
</tr>

<tr>
<td width="135"></td>
<td colspan="2">[an error occurred while processing this
directive]</td>
</tr>
</table>

</center>
</body>
</html>

</xsl:template>
</xsl:stylesheet>
