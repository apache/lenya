<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:variable name="unipublic">/wyona-cms/unipublic</xsl:variable>
<xsl:variable name="img-uni"><xsl:value-of select="$unipublic"/>/img_uni</xsl:variable>
<xsl:variable name="img-unipub"><xsl:value-of select="$unipublic"/>/img_unipublic</xsl:variable>

<xsl:template match="/">

<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=iso-8859-1"/>
<title>unipublic - Das Online-Magazin der Universit&#228;t Z&#252;rich</title>
<script type="text/javascript" language="JavaScript1.2">
<xsl:comment>
function aboWindow() {
    newWind = open("newsletter.html","Display", "toolbar='no', statusbar='no', height='280', width='225'" );
}
</xsl:comment>        
</script>

<link rel="stylesheet" type="text/css" href="{$unipublic}/unipublic.mac.css"/>
<script type="text/javascript" language="JavaScript">
<xsl:comment>
<!--
//antiframe
if (top.frames.length > 0) {top.location.href = self.location;}
 //CSS Triage
if (navigator.appVersion.indexOf ('Win') >= 0) {
   seite = '{$unipublic}/unipublic.win.css';
   document.write('<link rel="stylesheet" type="text/css" href="'+seite+'">');
}
-->
</xsl:comment>
</script>

<meta http-equiv="content-type" content="text/html;charset=iso-8859-1"/>
<style type="text/css">
<xsl:comment>

.tsr-title { font-weight: bold; font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular"; text-transform: uppercase }
.tsr-text { font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular" }
.webperlen { font-size: 10px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular" }
.top-title { font-size: 18px; font-family: Geneva, Verdana, Helvetica, Arial, Swiss, "Nu Sans Regular"; text-transform: uppercase }

</xsl:comment>
</style>

<meta http-equiv="content-type" content="text/html;charset=iso-8859-1"/>
</head>	
<body text="#333333" link="#333399" alink="#993300" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">

<center>

<!--START kopf.html-->
<center>
<form action="http://www.unizh.ch/cgi-bin/unisearch" method="post">
<input type="hidden" value="www.unipublic.unizh.ch" name="url"/>
 <a name="topofpage">&#160;</a>

<table width="585" border="0" cellspacing="0" cellpadding="0" bgcolor="#666699">
<tr>
<td bgcolor="#999966" valign="middle" align="left">
<img height="20" width="3" src="{$img-unipub}/1.gif" alt= " "/>
<a href="http://www.unizh.ch/index.html">
<img src="{$img-uni}/oliv_home.gif" alt="Home" border="0" height="17" width="31"/></a></td>
<td bgcolor="#999966" width="1">&#160;</td>
<td bgcolor="#999966" valign="middle" align="right">
<a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">
<img src="{$img-uni}/oliv_kontakt.gif" alt="Kontakt" border="0" height="17" width="41" align="middle"/></a>
<img src="{$img-uni}/oliv_strich.gif" alt="|" height="17" width="7" align="middle"/>
<img src="{$img-uni}/oliv_suchen.gif" alt="Suchen" height="17" width="37" align="middle"/>
 <input type="text" name="keywords" size="18"/> <input src="{$img-uni}/oliv_go.gif" type="image" border="0" name="search" align="middle"/>

</td>
<td bgcolor="#F5F5F5" width="57">&#160;</td>
</tr>
<tr height="39">
<td align="right" height="39">&#160;</td>
<td align="right" height="39" valign="top" colspan="2">
<a href="http://www.unizh.ch/index.html">
<img height="29" width="235" src="{$img-uni}/unilogoklein.gif" alt="Universit&#228;t Z&#252;rich" border="0"/></a></td>
<td width="57" height="39">&#160;</td>
</tr>
</table>

</form>
</center>

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
<table border="0" cellpadding="0" cellspacing="0" width="115">
<tr>
<td><img height="25" src="{$img-unipub}/m_gesund.gif" border="0" alt="gesundheit" width="115"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/magazin/gesellschaft/2002/"><img height="25" src="{$img-unipub}/m_geist.gif" border="0" name="geist" alt="geist &#38; gesellschaft" width="115"/></a></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/m_umwelt.gif" border="0" alt="umwelt &#38; technik"/></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" src="{$img-unipub}/m_recht.gif" border="0" alt="recht &#38; wirtschaft" width="115"/></td> </tr>

<tr>
<td align="right"><img height="19" width="100" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td align="right"><img height="21" width="103" src="{$img-unipub}/t_camp.gif" border="0" alt="campus"/></td>
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

<tr>
<td><img height="19" width="100" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="dossiers/2002/"><img src="{$unipublic}/img/dossiers.gif" width="120" height="21" border="0"/></a></td>
</tr>

<tr>
<td class="tsr-text"><span class="tsr-title"><a href="dossiers/2002/brainfair/">BrainFair 2002</a></span><br />
 Hirn im Focus</td>
</tr>
</table>
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
<p><a href="magazin/gesellschaft/2002/0515/"><span class="tsr-title">Rauschen im musikalischen Museum</span></a><br />
 In der Vorlesungsreihe &#171;Inventur im Museum&#187; des
Musikwissenschaftlichen Seminars wird unser musikalischer Kanon
hinterfragt. Gastreferenten aus Deutschland und der Schweiz
sezieren jeweils ein &#171;grosses&#187; Werk. Zum Auftakt
konfrontierte Hans-Joachim Hinrichsen gewohnte H&#246;rweisen von
Bruckners &#171;Achter&#187; mit neuen T&#246;nen. (3.4.2002)</p>
</td>
<td width="5" bgcolor="#CCCC99"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="1" bgcolor="white"><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
<td width="150" valign="top" bgcolor="white" class="tsr-text">
<p><span class="tsr-title">Universit&#228;t Z&#252;rich zu
ZB-Pl&#228;nen</span><br />
 Die geplante Abspaltung der ZB vom gemeinsamen Katalog mit der
ETH-Bibliothek hat negative Folgen auf mehreren Ebenen. Eine
Erg&#228;nzung zum gestern hier ver&#246;ffentlichten Artikel.
(5.4.2002)</p>
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
 

<table border="0" cellpadding="0" cellspacing="0" width="316">
<tr>
<td colspan="3">
<table border="0" cellpadding="0" cellspacing="0" width="316">
<tr>
<td colspan="3"><img src="{$img-unipub}/t_uninews.gif" width="316" height="13" border="0" alt="uni-news"/></td>
</tr>

<tr>
<td bgcolor="white" colspan="3">&#160;</td>
</tr>

<tr>
<td width="4" bgcolor="white">&#160;</td>
<td bgcolor="white" class="tsr-text">
<p><a href="../campus/uni-news/2002/0513/"><img src="{$unipublic}/campus/uni-news/2002/0513/bild-headline.jpg" width="80" height="60" border="0" alt="unijournal" align="right"/></a><span class="tsr-title">unijournal 2/2002 erschienen</span><br />
 Welche Muse den &#171;Heldenverehrer&#187; Thomas Hirschhorn
gek&#252;sst hat, wieso Doktorierende ihr Schneckenhaus verlassen
und was Jahresringe mit Frauenf&#246;rderung zu tun haben, erfahren
Sie im neuen unijournal. (3.4.2002)</p>
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

<table border="0" cellpadding="0" cellspacing="0" width="316">
<tr>
<td colspan="3"><img src="{$img-unipub}/t_umwelt.gif" width="316" height="13" border="0" alt="Umwelt &#38; Technik"/></td>
</tr>

<tr>
<td bgcolor="white" colspan="3">&#160;</td>
</tr>

<tr>
<td width="4" bgcolor="white">&#160;</td>
<td bgcolor="white" class="tsr-text">
<p><a href="../magazin/umwelt/2002/0385/"><img src="{$unipublic}/magazin/umwelt/2002/0385/bild-headline.jpg" width="80" height="60" border="0" alt="Kuppel" align="right"/></a><span class="tsr-title">Spaziergang in die Vergangenheit</span><br />
 Der Botanische Garten ist sicher einer der sch&#246;nsten Orte in
Z&#252;rich. Aber auch mit seiner Geschichte hat dieser in seiner
Anlage ganz besondere Park einiges zu bieten. Am vergangenen
Samstag trafen ndash; 25 Jahre nach der Er&#246;ffnung des Gartens
im Z&#252;rcher Quartier Riesbach ndash; die Architekten von
damals zusammen zu einer Veranstaltung &#252;ber Konzept und Anlage
des Parks. (26.3.2002)</p>
</td>
<td width="4" bgcolor="white">&#160;</td>
</tr>

<tr>
<td bgcolor="white" colspan="3">&#160;</td>
</tr>
</table>

<table border="0" cellpadding="0" cellspacing="0" width="316">
<tr>
<td colspan="3">
<table border="0" cellpadding="0" cellspacing="0" width="316">
<tr>
<td colspan="3"><img src="{$img-unipub}/t_uninews.gif" width="316" height="13" border="0" alt="uni-news"/></td>
</tr>

<tr>
<td bgcolor="white" colspan="3">&#160;</td>
</tr>

<tr>
<td width="4" bgcolor="white">&#160;</td>
<td bgcolor="white" class="tsr-text">
<p><a href="../campus/uni-news/2002/0511/"><img src="{$unipublic}/campus/uni-news/2002/0511/bild-headline.jpg" width="80" height="60" border="0" alt="Charly Schneiter" align="right"/></a><span class="tsr-title">Tod st&#228;rker als Vitalit&#228;t</span><br />
 Charly Schneiter, Gr&#252;nder und langj&#228;hriger Direktor des
ASVZ starb vergangenen Samstag im Alter von 91 Jahren. Der Initiant
und Promotor zahlreicher Sportanl&#228;sse und -anlagen, wie z.B.
des Uni - Poly Rudermatches oder der Sportanlage Fluntern und der
Dreifachsporthalle auf der Politerrasse blieb dem Sport bis zuletzt
verbunden. Ein Nachruf. (26.3.2002)</p>
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
