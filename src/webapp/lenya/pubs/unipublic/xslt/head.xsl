<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template name="Searchbox">
<center>
<form action="http://www.unizh.ch/cgi-bin/unisearch" method="post">
<input type="hidden" value="www.unipublic.unizh.ch" name="url"/>
 <a name="topofpage">&#160;</a>
<table width="585" border="0" cellspacing="0" cellpadding="0" bgcolor="#666699">
<tr>
<td bgcolor="#999966" valign="middle" align="left"><img height="20" width="3" src="{$img-uni}/1.gif" alt=" "/><a href="http://www.unizh.ch/index.html"><img src="{$img-uni}/oliv_home.gif" alt="Home" border="0" height="17" width="31"/></a></td><td bgcolor="#999966" width="1">&#160;</td>
<td bgcolor="#999966" valign="middle" align="right"><a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html"><img src="{$img-uni}/oliv_kontakt.gif" alt="Kontakt" border="0" height="17" width="41" align="middle" /></a><img src="{$img-uni}/oliv_strich.gif" alt="|" height="17" width="7" align="middle"/><img src="{$img-uni}/oliv_suchen.gif" alt="Suchen" height="17" width="37" align="middle" />
<input type="text" name="keywords" size="18"/> <input src="{$img-uni}/oliv_go.gif" type="image" border="0" name="search"
align="middle"
/></td>
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
</xsl:template>

</xsl:stylesheet>
