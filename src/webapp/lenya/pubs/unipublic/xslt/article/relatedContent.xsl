<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="related-content">
 <xsl:if test="block">
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
 </xsl:if>
</xsl:template>

<xsl:template match="block" mode="RelatedContent">
    <table border="0" cellpadding="0" cellspacing="8" width="100%">
      <tr>
        <td class="rel-title"><xsl:value-of select="title"/></td>
      </tr>
      <xsl:apply-templates select="item"/>
    </table>
</xsl:template>

<xsl:template match="item">
  <tr>
    <td class="rel-text">
      <xsl:apply-templates/>
    </td>
  </tr>
</xsl:template>

</xsl:stylesheet>

