
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="NewsComponent" mode="article">
  <td width="10" bgcolor="white" valign="top">&#160;
    <p>&#160;</p>
  </td>
  <td valign="top" bgcolor="white" width="388" class="art-text">
    <p class="art-date"><xsl:apply-templates select="NewsLines/DateLine" mode="article"/></p>
    <p class="art-pretitle"><xsl:apply-templates select="ContentItem/DataContent/head/hedline/dossier"/></p>
    <p class="art-title1"><xsl:apply-templates select="ContentItem/DataContent/body/body.head/hedline/hl1"/></p>
    <p class="art-lead"><xsl:apply-templates select="ContentItem/DataContent/body/body.head/abstract"/> </p>
    <p class="art-author"><xsl:apply-templates select="ContentItem/DataContent/body/body.head/byline"/></p>
    <xsl:apply-templates select="ContentItem/DataContent/body/body.content/block"/>
    <p class="art-author"><xsl:apply-templates select="ContentItem/DataContent/body/body.end/tagline"/></p>
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

