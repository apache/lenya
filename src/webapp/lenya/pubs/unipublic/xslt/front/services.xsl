<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="../variables.xsl"/>

<xsl:template match="Services">
  <table border="0" cellpadding="0" cellspacing="0" width="126">
    <xsl:for-each select="Service">
      <tr>
        <td><a href="{@href}"><img height="25" width="115" src="{$img-unipub}/s_{@id}.gif" border="0" alt="{@id}"/></a></td>
      </tr>

      <tr>
        <td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
      </tr>
    </xsl:for-each>
<!--
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
td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
</tr>

<tr>
<td><img height="25" width="115" src="{$img-unipub}/s_weiter.gif" border="0" alt="weiterbildung"/></td>
</tr>
-->

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

</xsl:template>

</xsl:stylesheet>
