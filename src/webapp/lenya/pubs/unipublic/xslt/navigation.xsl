<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<xsl:template name="navigation">
<table border="0" cellpadding="0" cellspacing="0" width="115">
<tr>
<td><a href="{$unipublic}/magazin/gesundheit/"><img height="25" src="{$img-unipub}/m_gesund.gif" border="0" name="gesund" alt="Gesundheit" width="115"/></a></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/magazin/geist/"><img height="25" src="{$img-unipub}/m_geist.gif" border="0" alt="geist" width="115"/></a></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/magazin/umwelt/"><img height="25" width="115" src="{$img-unipub}/m_umwelt.gif" border="0" alt="umwelt &#38; technik"/></a></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/magazin/recht/"><img height="25" src="{$img-unipub}/m_recht.gif" border="0" alt="recht &#38; wirtschaft" width="115"/></a></td> </tr>

<tr>
<td align="right"><img height="19" width="100" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td align="right"><img height="21" width="103" src="{$img-unipub}/t_camp.gif" border="0" alt="campus"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/campus/uni-news/"><img height="25" width="115" src="{$img-unipub}/c_news.gif" border="0" alt="uni-news"/></a></td>

</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/campus/portrait/"><img height="25" width="115" src="{$img-unipub}/c_portrait.gif" border="0" alt="Portraits"/></a></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/campus/lorbeer/"><img height="25" width="115" src="{$img-unipub}/c_lorbeer.gif" border="0" alt="lorbeeren"/></a></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/campus/beruf/"><img height="25" width="115" src="{$img-unipub}/c_beruf.gif" border="0" alt="berufungen"/></a></td>
</tr>

<tr>
<td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><a href="{$unipublic}/campus/publik/"><img height="25" width="115" src="{$img-unipub}/c_publik.gif" border="0" alt="publikationen"/></a></td>
</tr>

<tr>
<td><img height="19" width="100" src="{$img-unipub}/1.gif"/></td>
</tr>
<!--
<tr>
<td><a href="dossiers/2002/"><img src="{$unipublic}/img/dossiers.gif" width="120" height="21" border="0"/></a></td>
</tr>

<tr>
<td class="tsr-text"><span class="tsr-title"><a href="dossiers/2002/brainfair/">BrainFair 2002</a></span><br />
 Hirn im Focus</td>
</tr>
-->
<xsl:call-template name="dossier"/>

</table>
</xsl:template>

<xsl:template name="dossier">
<tr>
<td><a href="dossiers/2002/"><img src="{$unipublic}/img/dossiers.gif" width="120" height="21" border="0"/></a></td>
</tr>

<tr>
<td class="tsr-text"><span class="tsr-title"><a href="dossiers/2002/brainfair/">BrainFair 2002</a></span><br />
 Hirn im Focus</td>
</tr>
</xsl:template>

</xsl:stylesheet>
