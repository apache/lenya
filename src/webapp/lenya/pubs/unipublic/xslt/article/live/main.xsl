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

<p class="art-title1">Rauschen im musikalischen Museum</p>

<p class="art-lead">In der Vorlesungsreihe &#171;Inventur im
Museum&#187; des Musikwissenschaftlichen Seminars wird unser
musikalischer Kanon hinterfragt. Gastreferenten aus Deutschland und
der Schweiz sezieren jeweils ein &#171;grosses&#187; Werk. Zum
Auftakt konfrontierte Hans-Joachim Hinrichsen gewohnte
H&#246;rweisen von Bruckners &#171;Achter&#187; mit neuen
T&#246;nen.</p>

<p class="art-author">Von Sabine Witt</p>

<table border="0" cellpadding="0" cellspacing="0" width="250">
<tr>
<td><img src="{$unipublic}/magazin/gesellschaft/2002/0515/notizen.jpg" width="250" height="166" border="0" alt="Notizen"/></td>
</tr>

<tr>
<td class="img-author">Bilder: Sabine Witt</td>
</tr>
</table>

<p>&#220;ber Bruckner l&#228;sst sich gut n&#246;rgeln: zu schwer,
pathetisch und noch schlimmer ndash; zum musikgeschichtlichen
Kanon geh&#246;rend. Dass da kein Aufschrei der Emp&#246;rung von
Fachkolleginnen und -kollegen oder vom Publikum erfolgte, erstaunte
Musikprofessor Hans-Joachim Hinrichsen einigermassen, als er am
Dienstagabend in der Universit&#228;t &#252;ber Bruckners Achte
Sinfonie referierte. Er bestritt den Auftakt zur &#246;ffentlichen
Ringvorlesung &#171;Inventur im Museum. Musikalische Meisterwerke
neu geh&#246;rt&#187;, die er gemeinsam mit seinem Kollegen
Professor Laurenz L&#252;tteken organisiert hat. Das Konzept, den
Kanon zu hinterfragen und ihn gleichzeitig zu reproduzieren, ist
eine beabsichtigte Provokation.</p>

<p>Laurenz L&#252;tteken sprach in der Einleitung vom Dilemma der
aus dem Angels&#228;chsischen her&#252;bergeschwappten Debatte, die
den Kanon, anstatt ihn zu durchbrechen noch forciert hat. Der
hartn&#228;ckige Versuch, Franz Schubert Homosexualit&#228;t
nachzuweisen, sei beispielsweise nur noch Ausdruck methodischer
Trostlosigkeit.</p>

<table border="0" cellpadding="0" cellspacing="0" width="354">
<tr>
<td valign="bottom"><img src="{$unipublic}/magazin/gesellschaft/2002/0515/bruckner.gif" width="154" height="300" border="0" alt="Bruckner"/></td>
<td class="img-text" valign="bottom">&#160;</td>
<td class="img-text" valign="bottom">Anton Bruckner <span class= "img-author">(Silhouette von Otto Boehler, zVg)</span></td>
</tr>
</table>

<p><span class="art-title3">Bruckner, ein Minimalist</span><br />
 Hinrichsens Vortrag zielte darauf ab, Bruckners sichere
Verankerung in unserem &#171;germanozentrischen&#187; Kanon zu
hinterfragen und neu zu begr&#252;nden. Mit Noten- und
H&#246;rbeispielen f&#252;hrte er das Typische an Bruckners
Sinfonik vor und an Besonderheiten deren Zuspitzung in der
&#171;Achten&#187;: Transformationen ziehen sich als roter Faden
durch die Sinfonie und sind als solche pr&#228;zise kalkuliert.
Gegen alle Voreingenommenheit, bedingt durch die
Rezeptionsgeschichte und Auff&#252;hrungspraxis, demonstrierte
Hinrichsen Bruckners Musik als souver&#228;ne und sogar
minimalistische Inszenierung. Er wies besonders auf Stellen hin,
derentwegen Bruckners Musik traditionell zur Projektionsfl&#228;che
f&#252;r Sehns&#252;chte, &#220;bersinnliches und Naturmystisches
gemacht wird.</p>

<table border="0" cellpadding="0" cellspacing="0" width="250">
<tr>
<td><img src="{$unipublic}/magazin/gesellschaft/2002/0515/hinrichsen.jpg" width="250" height="188" border="0" alt="Hinrichsen"/></td>
</tr>

<tr>
<td class="img-text">Professor Hans-Joachim Hinrichsen</td>
</tr>
</table>

<p><span class="art-title3">Bedenkliche
Auff&#252;hrungspraxis</span><br />
Der Vortrag und die anschliessende kurze Diskussion gaben nicht nur
neue H&#246;rimpulse, sondern auch einen Einblick in die
musikwissenschaftliche Arbeitsweise. Aus dem Vergleich der beiden
Fassungen der Achten Sinfonie  Bruckner fertigte eine
zweite, weil er die Dirigenten nicht zur Auff&#252;hrung der ersten
bewegen konnte  leitete der Musikphilologe Hinrichsen
Bruckners Innovation ab. Gegen&#252;ber der heutigen
Auff&#252;hrungspraxis meldeten sowohl Hinrichsen als auch
L&#252;tteken philologische Bedenken an. Denn heute wird die so
genannte Originalfassung von Robert Haas aus dem Jahre 1927
favorisiert, die eine Mischung aus Bruckners beiden Fassungen ist.
Massgeblich dazu beigetragen haben die bedeutenden
Bruckner-Interpreten G&#252;nter Wand und Pierre Boulez.</p>

<p>Die Vorlesungsreihe richtet sich ausdr&#252;cklich an ein
&#246;ffentliches Publikum, wie Hinrichsen und L&#252;tteken
versicherten. Das haben sie auch den Gastreferenten aus der Schweiz
und Deutschland so vermittelt.  Falls man dennoch nicht
jedes &#252;ber die Musik gemachte Wort versteht, so spricht doch
die Musik zumindest ihre eigene Sprache.</p>

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

</xsl:stylesheet>

