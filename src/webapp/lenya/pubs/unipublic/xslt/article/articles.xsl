<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:param name="section" select="'default_value'"/>
<xsl:param name="documentid"/>
<xsl:param name="authoring"/>

<xsl:template match="text()">
<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="nitf">
  <td width="5" bgcolor="white" valign="top">&#160;</td>

  <td valign="top" bgcolor="white" width="393" class="art-text" id_xopus="body" xml_xopus="magazin/gesundheit/articles/2002/0508/forum.xml" xsl_xopus="Page/Article/Authoring/xopus.xsl" xsd_xopus="article.xsd">
    <div class="art-date"><xsl:apply-templates select="../../../../NewsManagement/PublishDate" mode="article"/></div>
    <div class="art-pretitle"><xsl:apply-templates select="head/hedline/dossier"/></div>
    <div class="art-title1"><xsl:apply-templates select="body/body.head/hedline/hl1"/></div>
    <div class="art-lead"><xsl:apply-templates select="body/body.head/abstract"/> </div>
    <div class="art-author"><xsl:apply-templates select="body/body.head/byline"/></div>
    <xsl:apply-templates select="body/body.content/block"/>
    <div class="art-author"><xsl:apply-templates select="body/body.end/tagline"/></div>
  </td>
</xsl:template>

<xsl:template match="PublishDate" mode="article">
  <xsl:value-of select="@day"/>.<xsl:value-of select="@month"/>.<xsl:value-of select="@year"/>
</xsl:template>

<xsl:template match="block">
  <xsl:apply-templates select="p | media">
    <xsl:with-param name="block-position" select="position()"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="p">
  <xsl:param name="block-position"/>
  <p>
    <xsl:if test="not(preceding-sibling::p)">
      <xsl:apply-templates select="../hl2" mode="block"/>
    </xsl:if>
    <xsl:apply-templates/>
    <xsl:if test="$authoring">
	<br/>
	<a href="index.html?usecase=uploadimage&amp;step=showscreen&amp;documentid={$documentid}&amp;xpath=/NewsML/NewsItem/NewsComponent[1]/ContentItem/DataContent/nitf/body/body.content/block[{$block-position}]/p[{count(preceding-sibling::p)+1}]"><img src="{$context_prefix}/images/wyona/cms/util/reddot.gif" alt="Insert Image" border="0"/></a><br/>
    </xsl:if>
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

