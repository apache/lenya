
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:include href="../../head.xsl"/>
<xsl:include href="../../HTMLhead.xsl"/>

<xsl:variable name="unipublic">/wyona-cms/unipublic</xsl:variable>
<xsl:variable name="img-uni"><xsl:value-of select="$unipublic"/>/img_uni</xsl:variable>
<xsl:variable name="img-unipub"><xsl:value-of select="$unipublic"/>/img_unipublic</xsl:variable>
<xsl:variable name="section"><xsl:value-of select="/NewsML/NewsItem/NewsComponent/TopicSet/Topic/TopicType/FormalName"/></xsl:variable>

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
<html>
<head>
<title>unipublic - <xsl:value-of select="$section"/></title>

<xsl:call-template name="styles"/>

<xsl:call-template name="jscript"/>

</head>
<body text="black" link="#333399" alink="#CC0000" vlink="#666666" bgcolor="#F5F5F5" background="{$img-unipub}/bg.gif">

<!--START kopf.html-->
<xsl:call-template name="Searchbox"/>
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

<xsl:call-template name="slider_image"/>

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
<xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/NewsComponent" mode="RelatedContent"/>
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
<p class="art-date"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/NewsLines/DateLine"/></p>

<p class="art-pretitle"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/head/hedline/dossier"/></p>

<p class="art-title1"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/hedline/hl1"/></p>

<p class="art-lead"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/abstract"/> </p>

<p class="art-author"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head/byline"/></p>

<xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.content/block"/>

<p class="art-author"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.end/tagline"/></p>

</td>
</tr>

<tr>
<td width="187"></td>
<td width="10" bgcolor="white">&#160;</td>
<td bgcolor="white" width="388"><br />
  

<div align="left"><a href="#topofpage"><font size="1">zum Anfang<br /> <br />
</font></a> <img height="1" width="390" src="{$img-unipub}/999999.gif" alt=" "/><br />
 <font size="1"><xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/NewsLines/CopyrightLine" mode="copyright"/>
<xsl:apply-templates select="/NewsML/NewsItem/NewsComponent/NewsLines/DateLine" mode="copyright"/>
<xsl:apply-templates select="NewsLines/DateLine" mode="copyright"/> 
<a href="/ssi_unipublic/impressum.html">Impressum</a></font></div>
</td>
</tr>
</table>
</center>
</body>
</html>
</xsl:template>


<xsl:template name="slider_image">
<tr>
<td align="right" width="187"></td><td width="10"></td><td width="388"><a href="../">
<xsl:if test="contains($section, 'geist')"><img height="13" width="138" src="{$img-unipub}/r_geist.gif" alt="{$section}" border="0"/></xsl:if>
<xsl:if test="contains($section, 'gesundheit')"><img height="13" width="80" src="{$img-unipub}/r_gesund.gif" alt="{$section}" border="0"/></xsl:if>
<xsl:if test="contains($section, 'umwelt')"><img height="13" width="97" src="{$img-unipub}/r_umwelt.gif" alt="{$section}" border="0"/></xsl:if>
<xsl:if test="contains($section, 'recht')"><img height="13" width="133" src="{$img-unipub}/r_recht.gif" alt="{$section}" border="0"/></xsl:if>
</a>
</td>
</tr>
</xsl:template>

<xsl:template match="NewsComponent/NewsLines/CopyrightLine" mode="copyright">
  <xsl:apply-templates/>,
</xsl:template>

<xsl:template match="NewsComponent/NewsLines/DateLine" mode="copyright">
 <xsl:apply-templates/>,
</xsl:template>

<xsl:template match="NewsComponent/NewsComponent" mode="RelatedContent">
<xsl:for-each select="Role">
<table border="0" cellpadding="0" cellspacing="8" width="100%">
<tr>
<td class="rel-title"><xsl:value-of select="@FormalName"/></td>
</tr>
<xsl:apply-templates select="../NewsComponent/NewsLines" mode="RelatedContent"/>
</table>
</xsl:for-each>
</xsl:template>

<xsl:template match="NewsComponent/NewsComponent/NewsComponent/NewsLines" mode="RelatedContent">
<tr>
<td class="rel-text">
<xsl:apply-templates mode="RelatedContent"/>
</td>
</tr>
</xsl:template>

<xsl:template match="NewsComponent/NewsComponent/NewsComponent/NewsLines/HeadLine" mode="RelatedContent">
<a href="{../NewsItemRef/@NewsItem}"><xsl:value-of select="."/></a><br />
</xsl:template>

<xsl:template match="NewsComponent/NewsComponent/NewsComponent/NewsLines/SlugLine" mode="RelatedContent">
<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="block">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="p">
<p>
<xsl:if test="not(preceding-sibling::p)">
  <xsl:apply-templates select="../hl2" mode="block"/>
</xsl:if>
<xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="hl2" mode="block">
<span class="art-title3"><xsl:value-of select="."/></span><br />
</xsl:template>

<xsl:template match="hl2">
<!-- Comment -->
</xsl:template>

<!--<xsl:template match="media/@media-type[text()='image']">-->
<xsl:template match="media">
<p>&#160;</p>
<table border="0" cellpadding="0" cellspacing="0" width="250">
<tr>
<td>
<xsl:apply-templates select="media-reference" mode="image"/>
</td></tr>
<tr>
<td class="img-text"><xsl:value-of select="media-caption"/></td>
</tr>
<xsl:apply-templates select="up:authorline"/>
</table>
<p>&#160;</p>
</xsl:template>

<xsl:template match="media-reference" mode="image">
<img src="{@source}" alt="{@alternate-text}" />
</xsl:template>

<xsl:template match="up:authorline">
<tr>
<td class="img-author">(<xsl:value-of select="."/>)</td>
</tr>
</xsl:template>

</xsl:stylesheet>

