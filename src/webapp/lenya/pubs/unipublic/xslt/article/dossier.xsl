<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="dossier">

<table width="187" border="0" cellspacing="0" cellpadding="0">
<tr>
<td align="right"><a href="{$unipublic}{$view}/dossiers/"><img
src="{$img-unipub}/dossiers/doss_rub_title.gif" alt="Dossiers"
height="28" width="112" border="0" /></a></td>
</tr>

<tr>
<td bgcolor="{head/color}">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
<td valign="top" width="80"><a
href="{$unipublic}{$view}/dossiers/{@id}/index.html"><img
src="{$unipublic}{$view}/dossiers/{@id}/{head/media/media-reference/@source}" alt="" width="80"
height="60" border="0" /></a></td>
<td valign="top" width="8"><img src="{$img-unipub}/spacer.gif"
alt=" " width="8" height="10" border="0" /></td>
<td class="dos-title1" valign="top"></td>
</tr>
</table>
</td>
</tr>

<tr>
<td bgcolor="white">
<table border="0" cellspacing="8" cellpadding="0" bgcolor="white">
<tr>
<td class="rel-text"><span class="rel-title"><a
href="{$unipublic}{$view}/dossiers/{@id}/index.html"><xsl:value-of select="head/title" /></a></span><br />
<xsl:choose>
  <xsl:when test="head/teasertext!=''">
    <xsl:value-of select="head/teasertext" />
  </xsl:when>
  <xsl:otherwise>
    <xsl:value-of select="head/abstract" />
  </xsl:otherwise>
</xsl:choose>
</td>
</tr>
</table>
</td>
</tr>
</table>

<br />
 <img src="{$img-unipub}/spacer.gif" alt=" " width="50"
height="15" border="0" /><br />

</xsl:template>

</xsl:stylesheet>
