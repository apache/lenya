
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:include href="../../head.xsl"/>
<xsl:include href="../../HTMLhead.xsl"/>
<xsl:include href="../../variables.xsl"/>

<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
  <xsl:apply-templates select="Page/Content/NewsML"/>
</xsl:template>

<xsl:template match="NewsML">
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

                <xsl:apply-templates select="NewsItem" mode="RelatedContents"/>

              </td>
              <xsl:apply-templates select="NewsItem/NewsComponent" mode="article"/>
           </tr>

           <xsl:apply-templates select="NewsItem/NewsComponent/NewsLines" mode="copyright"/>

         </table>
       </center>
     </body>
  </html>
</xsl:template>

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

<xsl:template match="NewsLines" mode="copyright">
  <tr>
    <td width="187"></td>
    <td width="10" bgcolor="white">&#160;</td>
    <td bgcolor="white" width="388"><br />
     <div align="left"><a href="#topofpage"><font size="1">zum Anfang<br /> <br />
      </font></a> <img height="1" width="390" src="{$img-unipub}/999999.gif" alt=" "/><br />
      <font size="1"><xsl:apply-templates select="CopyrightLine" mode="copyright"/>
      <xsl:apply-templates select="DateLine" mode="article"/>,
      <a href="http://www.unipublic.unizh.ch/ssi_unipublic/impressum.html">Impressum</a></font></div>
    </td>
  </tr>
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

<xsl:template match="CopyrightLine" mode="copyright">
  <xsl:apply-templates/>,
</xsl:template>

<xsl:template match="NewsML/NewsItem" mode="RelatedContents">
  <table width="180" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top">
      <td width="180">
        <table width="180" border="0" cellspacing="0" cellpadding="0">
          <tr valign="top">
            <td width="180"><img height="19" width="187" src="{$img-unipub}/t_teil7.gif" alt="Muscheln1"/></td>
          </tr>
          <tr valign="top">
            <td width="180" valign="middle" bgcolor="#CCCC99">
              <xsl:apply-templates select="NewsComponent/NewsComponent" mode="RelatedContent"/>
            </td>
          </tr>
          <tr valign="top">
            <td width="180"><img height="27" width="181" src="{$img-unipub}/t_teil8.gif" align="right"/></td>
          </tr>
        </table>
     </td>
   </tr>
 </table>
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
      <xsl:apply-templates mode="RelatedC"/>
    </td>
  </tr>
</xsl:template>

<xsl:template match="NewsComponent/NewsComponent/NewsComponent/NewsLines/HeadLine" mode="RelatedC">
  <a href="{../NewsItemRef/@NewsItem}"><xsl:value-of select="."/></a><br />
</xsl:template>

<xsl:template match="SlugLine" mode="RelatedC">
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

