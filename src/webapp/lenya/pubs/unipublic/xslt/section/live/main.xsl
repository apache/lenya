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
 <a href="0515/"><span class="tsr-title">Rauschen im musikalischen
Museum</span></a><br />
 In der Vorlesungsreihe &#171;Inventur im Museum&#187; des
Musikwissenschaftlichen Seminars wird unser musikalischer Kanon
hinterfragt. Gastreferenten aus Deutschland und der Schweiz
sezieren jeweils ein &#171;grosses&#187; Werk. Zum Auftakt
konfrontierte Hans-Joachim Hinrichsen gewohnte H&#246;rweisen von
Bruckners &#171;Achter&#187; mit neuen T&#246;nen. (3.4.2002) 

<p><span class="tsr-title">Heilige und Huren im Alten
Testament</span><br />
 Eva, Maria Magdalena, Rahab und Tamar: In der Bibel gibt es nicht
nur die jungfr&#228;uliche Gottesmutter, sondern auch einige
verworfene Frauengestalten. Die Doktorandin und Pfarrerin Christine
Stark will es genauer wissen. Es besteht der &#171;dringende
Verdacht&#187;, dass nicht alles &#171;hurte&#187;, was danach
t&#246;nt. Dieses Forschungsprojekt wird durch den Forschungskredit
der Universit&#228;t Z&#252;rich finanziert. unipublic hat mit
Christine Stark gesprochen. (15.3.2002)</p>

<p><span class="tsr-title">Vorbilder und Vorw&#228;nde</span><br />
 Thomas Hirschhorn hat den Kunstkiosk an der Universit&#228;t
Z&#252;rich Irchel neu gestaltet. Im Zentrum der siebten Auflage,
welche bis Ende August zu besichtigen ist, steht die russische
K&#252;nstlerin Ljubov Popova (1889 bis 1924). In einem
Gespr&#228;ch mit unipublic gab der in Paris lebende Schweizer
K&#252;nstler zu Hintergr&#252;nden seines Projekt Auskunft.
(4.3.2002)</p>

<p><span class="tsr-title">Unruhige Jugend</span><br />
 &#171;Wir wollen alles, und zwar subito!&#187; heisst ein
multimedialer Dokumentarband, der die Jugendunruhen der fr&#252;hen
1980er Jahre in der Schweiz und ihre Folgen beleuchtet.
Herausgegeben hat ihn Heinz Nigg. In einem Interview &#228;ussert
sich der freie Kulturschaffende und Lehrbeauftragte am
Ethnologischen Seminar zu Hintergr&#252;nden der Publikation.
(22.2.2002)</p>

<p><span class="tsr-title">Die Chancen eines verg&#228;nglichen
Nationalereignisses<br />
</span> Die Universit&#228;t Z&#252;rich und die ETH werden mit dem
&#171;intelligenten Raum Ada&#187; an der Expo.02 pr&#228;sent
sein. Anders als ein Grossteil der Expo-Bauten wird
&#171;Ada&#187; nach der Landesausstellung nicht abgerissen und
entsorgt, sondern am Institut f&#252;r Neuroinformatik weiter
entwickelt werden. Weshalb wird jedoch gerade diese
Landesausstellung so hartn&#228;ckig kritisiert, verg&#228;nglich
zu sein? fragte Expo-Pr&#228;sidentin Nelly Wenger diesen Montag,
18. Februar 2002, vor nicht allzu zahlreich erschienenem Publikum.
Das Ephemere birgt doch zahlreiche Vorteile. (21.2.2002)</p>

<p><span class="tsr-title">Erfahrung zwischen Authentizit&#228;t
und Sprachspiel</span><br />
 An der 11. Schweizerischen HistorikerInnentagung in Z&#252;rich
vom 15./16. Februar wurde der Erfahrungsbegriff ins Zentrum
ger&#252;ckt. Erfahrungsberichte sind wichtige Quellen in der
Geschichtswissenschaft, sie sind jedoch nicht unproblematisch.
(18.2.2002)</p>

<p><span class="tsr-title">&#171;A whole way of
life&#187;</span><br />
 Cultural Studies werden in der Schweiz zwar seit l&#228;ngerem
betrieben, doch fehlte bisher eine zentrale Stelle, die sich
daf&#252;r einsetzte. Vor kurzem wurde dieser Missstand nun behoben
und offiziell die Schweizerische Gesellschaft f&#252;r
Kulturwissenschaften (SGKW) gegr&#252;ndet. Die Initiative dazu kam
von Mittelbau-Leuten der Universit&#228;t Z&#252;rich.
(14.2.2002)</p>

<p><span class="tsr-title">Neue K&#228;mpfe um Troia</span><br />
 Der T&#252;binger Arch&#228;ologieprofessor Manfred Korfmann steht
im Mittelpunkt einer heftigen Debatte um Troia. Darin geht es um
den Streit zweier Disziplinen, das Verh&#228;ltnis von
dichterischer Fiktion zu arch&#228;ologischen Funden und nicht
zuletzt um die Wurzeln europ&#228;ischer Kultur. Professor Korfmann
h&#228;lt am 4. Februar an der Universit&#228;t Z&#252;rich einen
Vortrag, der schon lange vor der Debatte vereinbart worden ist. Aus
diesem Anlass gew&#228;hrte er &#171;unipublic&#187; ein
Exklusiv-Interview. (31.1.2002)</p>

<p><span class="tsr-title">Avantgarde und Tradition im
Einklang</span><a href="../../../../magazin/gesellschaft/2002/0396/"><br />
</a> Am 30. Januar 2002 kommt J&#252;rg Baur, einer der
interessantesten deutschen Komponisten der Nachkriegszeit, auf
Einladung des Musikwissenschaftlichen Seminars f&#252;r ein
Gespr&#228;chskonzert nach Z&#252;rich. Die Pianistin Sona Shaboian
wird Kostproben aus Baurs Schaffen erklingen lassen.
(24.1.2002)</p>

<p><span class="tsr-title">Arbeit am Untergrund</span><br />
 Das interdisziplin&#228;re Forum &#171;Philosophie der Geistes-
und Sozialwissenschaften&#187; will die F&#228;cher der
gleichnamigen Fakult&#228;t in einen Dialog &#252;ber ihre
gemeinsamen Grundlagen bringen. Damit erh&#228;lt die vor zwei
Jahren von einer Gruppe von Mitgliedern der Philosophischen
Fakult&#228;t unter F&#252;hrung des Philosophischen Seminars
gestartete Initiative eine institutionelle Basis. (14.1.2002)</p>
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
