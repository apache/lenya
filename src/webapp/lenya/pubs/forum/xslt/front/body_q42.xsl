<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="front">
  <xsl:apply-templates select="articles"/>
</xsl:template>

<xsl:template match="articles">
  <xsl:if test="not(article)">
    No articles published yet!
  </xsl:if>

    <xsl:for-each select="article">
        <xsl:apply-templates select="article"/>
    </xsl:for-each>
</xsl:template>

<xsl:template match="article">
<div limeid="1024493390011_0_q42_NI3" limetype="news item"
limeaccess="*">
<table border="0" cellspacing="0" cellpadding="0">
<tr>
<td><img src="http://www.q42.nl/media/nix.gif" width="460" height="1"
alt="" /><br />
</td>
</tr>

<tr>
<td background="http://www.q42.nl/media/entry_top.gif" valign="top"><br />
<span class="head"><span limeid="1024493390011_1_q42_NI3_title"
limetype="title" limeaccess="*"><xsl:apply-templates select="head/title"/></span></span><br />
<div class="small" align="right"><font color="#ffffff">(<span limeid="1024493390011_2_q42_NI3_date" limetype="date" limeaccess="*"><xsl:value-of select="meta/date/day"/>-<xsl:value-of select="meta/date/month"/>-<xsl:value-of select="meta/date/year"/></span>)</font></div>

<br />
 </td>
</tr>

<tr>
<td bgcolor="#FFFFFF">
<xsl:for-each select="body/p">
  <xsl:apply-templates/>
  <xsl:if test="position() != last()">
    <font size="2"><br /><br /></font>
  </xsl:if>
</xsl:for-each>
</td>
</tr>

<tr>
<td background="http://www.q42.nl/media/entry_bottom.gif">&#160;</td>
</tr>
</table>

<br />
<br />
</div>
</xsl:template>

<xsl:template match="p">
<font size="2">
<xsl:apply-templates/>

<!--
<a href="http://www.globereisburo.nl/">
<img height="136" alt="" hspace="3" src="http://www.globereisburo.nl/images/globek.gif" width="151" align="right" border="0" ori_width="151" ori_height="136" />
</a>

Vanaf vandaag kunt u online reizen boeken&#160;met het systeem
van de <a href="http://www.globereisburo.nl/">Globe Reisburo
Groep</a>.
-->

<br />
<br />
</font>
</xsl:template>

</xsl:stylesheet>
