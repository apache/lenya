<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="variables.xsl"/>

<xsl:param name="year"/>

<xsl:template match="MainNavigation" xmlns:xi="http://www.w3.org/2001/XInclude">
  <xsl:param name="is-section" />
  <table border="0" cellpadding="0" cellspacing="0" width="115">
    <xsl:for-each select="Channels/Channel">

	  <!-- On the section pages the "magazin" GIF is drawn by page.xsl because of alignment issues (HTML) -->
	  <xsl:if test="not($is-section) or not(contains(@name,'magazin'))">
            <tr>
              <td><img height="21" width="120" src="{$img-unipub}/t_{@name}.gif" border="0" alt="{@name}"/></td>
            </tr>                                                                                                                             
	  </xsl:if>

          <xsl:for-each select="Sections/Section">
            <xsl:variable name="sectiontext"><xsl:value-of select="."/></xsl:variable>
            <tr>
              <xsl:choose>
                <xsl:when test="@highlighted='true'">
                  <td align="right"><a href="{$unipublic}{$view}/{../../@name}/{@id}/{$year}/"><img height="25" src="{$img-unipub}/nav_light_{@id}.gif" border="0" name="{@id}" alt="{$sectiontext}" width="115"/></a></td>
                </xsl:when>
                <xsl:otherwise>
                  <td align="right">
                  <a>
                  <xsl:choose>
                    <xsl:when test="@href">
                      <xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:attribute name="href"><xsl:value-of select="$unipublic"/><xsl:value-of select="$view"/>/<xsl:value-of select="../../@name"/>/<xsl:value-of select="@id"/>/<xsl:value-of select="$year"/>/</xsl:attribute>
                    </xsl:otherwise>
                  </xsl:choose>
                  <img height="25" src="{$img-unipub}/nav_{@id}.gif" border="0" name="{@id}" alt="{$sectiontext}" width="115"/>
                  </a>
                  </td>
                </xsl:otherwise>
              </xsl:choose>
            </tr>
            <tr>
              <td><img height="1" width="1" src="{$img-unipub}/1.gif"/></td>
            </tr>
          </xsl:for-each>
          <tr>
            <td><img height="19" width="100" src="{$img-unipub}/1.gif"/></td>
          </tr>
    </xsl:for-each>

</table>

<xsl:apply-templates select="../Dossiers"/>

</xsl:template>
<xsl:template match="Dossiers">
	<img src="{$img-unipub}/spacer.gif" alt=" " width="50" height="15" border="0" />
	<br />
	<a href="{$unipublic}{$view}/{@href}/"><img src="{$img-unipub}/dossiers/doss_rub_title.gif" alt="Dossiers" width="112" height="28" border="0" /></a>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<xsl:for-each select="Dossier">
	  <tr>
	    <td colspan="2" bgcolor="{Color}"><a href="{$unipublic}{$view}/{../@href}/{@href}/"><img src="{$unipublic}/{../@href}/{@href}/{Image/@href}" alt="" width="80" height="60" border="0" /></a></td>
	  </tr>
	  <tr>
	    <td class="tsr-text" bgcolor="white">
		<span class="tsr-title"><a href="{$unipublic}{$view}/{../@href}/{@href}/"><xsl:value-of select="Title"/></a></span><br />
		<xsl:value-of select="Teaser"/> 
	    </td>
	    <td class="tsr-text" valign="top" bgcolor="white" width="3"><img src="{$img-unipub}/spacer.gif" alt=" " width="3" height="10" border="0" /></td>
	  </tr>
	</xsl:for-each>
	</table>
</xsl:template>	

</xsl:stylesheet>
