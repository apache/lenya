<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="span[@id = 'preview']" >
    <!-- Insert magazine preview here... -->
    <xsl:apply-templates select="/wyona/preview"  />
</xsl:template>

<xsl:template match="preview" >
<!-- AKTUELLE AUSGABE TABLE BEGINS HERE -->

<table border="0" cellpadding="0" cellspacing="0" width="440">
<tr>
<td width="440" height="5" colspan="3"><img
src="/img/layout/trans1x1.gif" width="1" height="5" /></td>
</tr>

<tr bgcolor="#000000">
<td width="325" height="21" valign="middle"><span
class="txt-m-white"><b><i>&#160;jetzt am
Kiosk...</i></b></span></td>
<td width="115" height="21" valign="middle" align="right"
colspan="2"><span
class="txt-s-white"><b><xsl:value-of select="date"/> Nr. <xsl:value-of select="edition"/></b></span>
</td>
</tr>

<tr><!-- TITEL -->
<td width="325" valign="middle" height="21" bgcolor="#EFEFE7"><span
class="txt-s-black"><b><xsl:value-of select="item[1]/title"/></b></span></td>
<!-- VERTICAL DOT...LINE BEGINS HERE -->
<td width="1" rowspan="6" valign="top"
style="background-image: url('/img/layout/dot1x3-black.gif')">
<img src="/img/layout/trans1x1.gif" width="1" height="1" /></td>
<td width="114" rowspan="6" valign="top" align="center">
<!-- TITELBILD AKTUELLE AUSGABE -->
<br/>
                                        <a 
href="index.html?usecase=uploadimage&amp;step=showscreen&amp;documentid=preview.xml&amp;xpath=/preview/*[1]">
                                                <img src="/images/wyona/cms/util/reddot.gif" alt="Insert 
Image" border="0"/>
                                        </a>
                                        <br/>

<img border="0" src="/img/{media/media-reference/@source}"
alt="Aktuelle Ausgabe jetzt am Kiosk" /></td>
</tr>

<tr bgcolor="#EFEFE7"><!-- LEAD -->
<td width="325" valign="middle"><span class="txt-s-black"><xsl:apply-templates select="item[1]/p" /><!-- 
WEITER BUTTON -->&#160;
<!--
<a href="/print/index.html"
class="txt-link-red"><img border="0" src="/img/layout/arrow-red.gif"
width="9" height="7" alt="&#187;" />weiter</a>
-->
</span></td>
</tr>

<tr bgcolor="#EFEFE7">
<td width="325" valign="middle" height="3"
style="background-image: url('/img/layout/lines/linecontent440x3.gif')">
<img src="/img/layout/trans1x1.gif" width="1" height="3" /></td>
</tr>

<tr bgcolor="#FFFFFF"><!-- TITEL -->
<td width="325" valign="middle" height="21"><span
class="txt-s-black"><b><xsl:value-of select="item[2]/title"/></b></span></td>
</tr>

<tr bgcolor="#FFFFFF"><!-- LEAD -->
<td width="325" valign="middle"><span class="txt-s-black"><xsl:apply-templates select="item[2]/p" /><!-- 
WEITER BUTTON -->&#160;
<!--
<a href="/print/index.html"
class="txt-link-red"><img border="0" src="/img/layout/arrow-red.gif"
width="9" height="7" alt="&#187;" />weiter</a>
-->
</span></td>
</tr>

<tr bgcolor="#FFFFFF">
<td width="440" height="3" colspan="3"
style="background-image: url('/img/layout/lines/linecontent440x3.gif')">
<img src="/img/layout/trans1x1.gif" width="1" height="3" /></td>
</tr>
</table>

<!-- AKTUELLE AUSGABE TABLE ENDS HERE -->
</xsl:template>
</xsl:stylesheet>

