<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="variables.xsl"/>

<xsl:template match="MainNavigation" xmlns:xi="http://www.w3.org/2001/XInclude">
  <table border="0" cellpadding="0" cellspacing="0" width="115">
    <xsl:for-each select="Channels/Channel">
      <xsl:choose>
        <xsl:when test="@name='magazin'">

          <xsl:for-each select="Sections/Section">
            <tr>
              <td><a href="{$unipublic}/{../../@name}/{@id}/"><img height="25" src="{$img-unipub}/m_{@id}.gif" border="0" name="{@id}" alt="{../Section}" width="115"/></a></td>
            </tr>
            <tr>
              <td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
            </tr>
          </xsl:for-each>

        </xsl:when>
        <xsl:otherwise>

          <tr>
            <td><img height="19" width="100" src="{$img-unipub}/1.gif"/></td>
          </tr>

          <tr>
            <td align="right"><img height="21" width="103" src="{$img-unipub}/t_camp.gif" border="0" alt="campus"/></td>
          </tr>

          <xsl:for-each select="Sections/Section">
            <tr>
              <td><a href="{$unipublic}/{../../@name}/{@id}/"><img height="25" src="{$img-unipub}/c_{@id}.gif" border="0" name="{@id}" alt="{../Section}" width="115"/></a></td>
            </tr>
            <tr>
              <td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
            </tr>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>

<xsl:apply-templates select="../Dossiers"/>

</table>
</xsl:template>

<xsl:template match="Dossiers" xmlns:xi="http://www.w3.org/2001/XInclude">
  <tr>
    <td><img height="19" width="100" src="{$img-unipub}/1.gif"/></td>
  </tr>
  <tr>
    <td><a href="{Dossier/@href}"><img src="{$unipublic}/img/dossiers.gif" width="120" height="21" border="0"/></a></td>
  </tr>

  <tr>
    <td class="tsr-text"><span class="tsr-title"><a href="{Dossier/@href}{Dossier/Title/@href}">
      <xsl:value-of select="Dossier/Title"/></a></span><br />
      <xsl:value-of select="Dossier/Teaser"/></td>
  </tr>
</xsl:template>

</xsl:stylesheet>
