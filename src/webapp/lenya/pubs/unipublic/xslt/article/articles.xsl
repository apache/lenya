
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:param name="section" select="'default_value'"/>

<xsl:template match="text()">
<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="nitf">
<!--<xsl:template match="NewsComponent" mode="article">-->
  <td width="10" bgcolor="white" valign="top">&#160;
    <p>&#160;</p>
  </td>
<!--
  <td valign="top" bgcolor="white" width="388" class="art-text">
    <div class="art-date"><xsl:apply-templates select="NewsLines/DateLine" mode="article"/></div>
    <p class="art-pretitle"><xsl:apply-templates select="ContentItem/DataContent/nitf/head/hedline/dossier"/></p>
    <div class="art-title1"><xsl:apply-templates select="ContentItem/DataContent/nitf/body/body.head/hedline/hl1"/></div>
    <div class="art-lead"><xsl:apply-templates select="ContentItem/DataContent/nitf/body/body.head/abstract"/> </div>
    <p class="art-author"><xsl:apply-templates select="ContentItem/DataContent/nitf/body/body.head/byline"/></p>
    <xsl:apply-templates select="ContentItem/DataContent/nitf/body/body.content/block"/>
    <p class="art-author"><xsl:apply-templates select="ContentItem/DataContent/nitf/body/body.end/tagline"/></p>
  </td>
-->
  <td valign="top" bgcolor="white" width="388" class="art-text" id_xopus="body" xml_xopus="magazin/gesundheit/articles/2002/0508/forum.xml" xsl_xopus="Page/Article/Authoring/xopus.xsl" xsd_xopus="article.xsd">
    <div class="art-date"><xsl:apply-templates select="../../../NewsLines/DateLine" mode="article"/></div>
    <div class="art-pretitle"><xsl:apply-templates select="head/hedline/dossier"/></div>
    <div class="art-title1"><xsl:apply-templates select="body/body.head/hedline/hl1"/></div>
    <div class="art-lead"><xsl:apply-templates select="body/body.head/abstract"/> </div>
    <div class="art-author"><xsl:apply-templates select="body/body.head/byline"/></div>
    <xsl:apply-templates select="body/body.content/block"/>
    <div class="art-author"><xsl:apply-templates select="body/body.end/tagline"/></div>
  </td>
</xsl:template>

<xsl:template name="slider_image">
  <xsl:variable name="width">
    <xsl:if test="contains($section, 'geist')">138</xsl:if>
    <xsl:if test="contains($section, 'gesundheit')">80</xsl:if>
    <xsl:if test="contains($section, 'portrait')">80</xsl:if>
    <xsl:if test="contains($section, 'recht')">133</xsl:if>
    <xsl:if test="contains($section, 'umwelt')">97</xsl:if>
    <xsl:if test="contains($section, 'uni-news')">67</xsl:if>
  </xsl:variable>
  <tr>
    <td align="right" width="187"></td><td width="10"></td><td width="388"><a href="../">
      <img height="13" width="{$width}" src="{$img-unipub}/r_{$section}.gif" alt="{$section}" border="0"/>
      </a>
    </td>
  </tr>
</xsl:template>

<xsl:template match="DateLine" mode="article">
  <xsl:value-of select="@day"/>.<xsl:value-of select="@month"/>.<xsl:value-of select="@year"/>
</xsl:template>

<xsl:template match="block">
  <xsl:apply-templates select="p | media"/>
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

<!--<xsl:template match="media/@media-type[text()='image']">-->
<xsl:template match="media">
  <p>&#160;</p>
  <table border="0" cellpadding="0" cellspacing="0" width="250">
    <tr><td>
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

