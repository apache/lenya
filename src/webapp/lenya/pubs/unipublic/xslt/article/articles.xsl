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

  <td valign="top" bgcolor="white" width="393" class="art-text">
  <xsl:if test="$authoring">
    <table cellpadding="1" border="0" width="100%" bgcolor="#cccccc"><tr><td>
    <table cellpadding="3" border="0" width="100%" bgcolor="white">
      <tr>
        <td class="tsr-text"><b>Teaser-Image</b></td>
	<td class="tsr-text">
         <a href="index.html?usecase=uploadimage&amp;step=showteaserscreen&amp;documentid={$documentid}&amp;xpath=/NewsML/NewsItem/NewsComponent[1]/ContentItem/DataContent/nitf/body/body.head/hedline">
          <xsl:choose>
            <xsl:when test="body/body.head/media">
              <img src="{body/body.head/media[1]/media-reference/@source}" border="0" alt="Teaser Image" align="middle" /> Change Image
            </xsl:when>
            <xsl:otherwise>
              <img src="{$context_prefix}/images/wyona/cms/util/reddot.gif" alt="Upload Image" border="0"/> Upload Image
            </xsl:otherwise>
          </xsl:choose>
         </a>
        </td>
      </tr>
      <tr>
	<td class="tsr-text"><b>Teaser-Text</b></td>
	<td class="tsr-text"><xsl:value-of select="body/body.head/teasertext" /></td>
      </tr>
    </table>
    </td></tr></table>
    <br/>
  </xsl:if>
    <div class="art-date"><xsl:apply-templates select="../../../../NewsManagement/PublishDate" mode="article"/></div>
    <div class="art-pretitle"><xsl:apply-templates select="body/body.head/spitzmarke"/></div>
    <div class="art-title1"><xsl:apply-templates select="body/body.head/hedline/hl1"/></div>
    <div class="art-lead"><xsl:apply-templates select="body/body.head/abstract"/> </div>
    <div class="art-author"><xsl:apply-templates select="body/body.head/byline"/></div>
    <xsl:if test="$authoring">
      <a href="index.html?usecase=uploadimage&amp;step=showscreen&amp;documentid={$documentid}&amp;xpath=/NewsML/NewsItem/NewsComponent[1]/ContentItem/DataContent/nitf/body/body.content/block[1]/*[1]&amp;insertBefore=true"><img src="{$context_prefix}/images/wyona/cms/util/reddot.gif" alt="Insert Image" border="0"/></a><br/>
    </xsl:if>
    <xsl:apply-templates select="body/body.content/block"/>
    <div class="art-author"><xsl:apply-templates select="body/body.end/tagline"/></div>
  </td>
</xsl:template>

<xsl:template match="PublishDate" mode="article">
  <xsl:value-of select="@day"/>.<xsl:value-of select="@month"/>.<xsl:value-of select="@year"/>
</xsl:template>

<xsl:template match="block">
  <xsl:apply-templates select="p | media | hl2">
    <xsl:with-param name="block-position" select="position()"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="p">
  <xsl:param name="block-position"/>
  <p>
<!--
    <xsl:if test="not(preceding-sibling::p)">
      <xsl:apply-templates select="../hl2" mode="block"/>
    </xsl:if>
-->
    <xsl:apply-templates/>
    <xsl:if test="$authoring">
	<br/>
	<a href="index.html?usecase=uploadimage&amp;step=showscreen&amp;documentid={$documentid}&amp;xpath=/NewsML/NewsItem/NewsComponent[1]/ContentItem/DataContent/nitf/body/body.content/block[{$block-position}]/p[{count(preceding-sibling::p)+1}]"><img src="{$context_prefix}/images/wyona/cms/util/reddot.gif" alt="Insert Image" border="0"/></a><br/>
    </xsl:if>
  </p>
</xsl:template>

<xsl:template match="hl2">
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
    <td class="img-text"><xsl:value-of select="media-caption"/><xsl:apply-templates select="up:authorline|authorline"/></td>
    </tr>
  </table>
  <p>&#160;</p>
</xsl:template>


<xsl:template match="media-reference" mode="image">
  <img src="{@source}" alt="{@alternate-text}" />
</xsl:template>

<xsl:template match="up:authorline|authorline">
  <xsl:if test="text()!=''">
    <span class="img-author"> (Bild: <xsl:value-of select="."/>)</span>
  </xsl:if>
</xsl:template>

<!--  General Text Templates  -->

<xsl:template match="br">
  <br/>
</xsl:template>

<xsl:template match="bold">
  <b><xsl:apply-templates/></b>
</xsl:template>

<xsl:template match="emphasize">
  <i><xsl:apply-templates/></i>
</xsl:template>

<xsl:template match="subscript">
  <sub><xsl:apply-templates/></sub>
</xsl:template>

<xsl:template match="superscript">
  <sup><xsl:apply-templates/></sup>
</xsl:template>

<xsl:template match="itemizedlist">
  <ul>
    <xsl:apply-templates/>
  </ul>
</xsl:template>

<xsl:template match="listitem | bxlistitem">
  <li><xsl:apply-templates/></li>
</xsl:template>

<xsl:template match="ulink">
  <a href="{@url}"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="informaltable">
  <table width="100%" border="0" cellspacing="1" cellpadding="2" bgcolor="white">  
    <xsl:apply-templates/>
  </table>
</xsl:template>

<xsl:template match="tgroup">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="tbody">
  <xsl:copy><xsl:apply-templates/></xsl:copy>
</xsl:template>

<xsl:template match="row">
  <tr><xsl:apply-templates/></tr>
</xsl:template>

<xsl:template match="entry">
  <td class="rel-text" bgcolor="#cccccc"><xsl:apply-templates/></td>
</xsl:template>

</xsl:stylesheet>

