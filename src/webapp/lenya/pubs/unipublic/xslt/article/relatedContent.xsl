<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="related-content">
  <table width="180" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top">
      <td width="180">
        <table width="180" border="0" cellspacing="0" cellpadding="0">
          <tr valign="top">
            <td width="180"><img height="19" width="187" src="{$img-unipub}/t_teil7.gif" alt="Muscheln1"/></td>
          </tr>
          <tr valign="top">
            <td width="180" valign="middle" bgcolor="#CCCC99">
              <xsl:apply-templates select="block" mode="RelatedContent"/>
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

<xsl:template match="block" mode="RelatedContent">
    <table border="0" cellpadding="0" cellspacing="8" width="100%">
      <tr>
        <td class="rel-title"><xsl:value-of select="title"/></td>
      </tr>
      <xsl:apply-templates select="item" mode="RelatedContent"/>
    </table>
</xsl:template>

<xsl:template match="item" mode="RelatedContent">
  <tr>
    <td class="rel-text">
      <xsl:apply-templates mode="RelatedContent"/>
    </td>
  </tr>
</xsl:template>

<xsl:template match="a" mode="RelatedContent">
  <a href="{@href}"><xsl:value-of select="."/></a>
</xsl:template>

<!--
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
-->

</xsl:stylesheet>

