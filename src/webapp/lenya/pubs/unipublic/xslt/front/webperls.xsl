<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="../variables.xsl"/>

<xsl:template match="Webperls">
  <table border="0" cellpadding="2" cellspacing="0" bgcolor="#CCCCFF">
    <tr>
      <td width="125" class="webperlen"><font color="#333333">
        <xsl:value-of select="Title"/></font></td>
    </tr>
        <xsl:for-each select="Webperl">
	  <tr>
            <td><img height="1" width="131" src="{$img-unipub}/strich_weiss.gif"/></td>
	  </tr>
	  <tr>
            <td class="webperlen"><img height="7" width="7" src="{$img-unipub}/t_perle.gif"/>
            <xsl:apply-templates select="."/></td>
	  </tr>
        </xsl:for-each>

	  <tr>
            <td><img height="1" width="125" src="{$img-unipub}/white.gif"/></td>
	  </tr>
	  <tr>
	    <td class="webperlen"><img height="7" width="7" src="{$img-unipub}/t_perle.gif"/>&#160;<img height="7" width="7" src="{$img-unipub}/t_perle.gif"/>&#160;<img height="7" width="7" src="{$img-unipub}/t_perle.gif"/>&#160;<a href="{@id}">weitere Perlen ...</a></td>
	  </tr>
	  </table>
	</xsl:template>

<xsl:template match="Webperl">
  <xsl:apply-templates select="Title" mode="Webperl"/>
  <xsl:apply-templates select="Byline" mode="Webperl"/>
</xsl:template>

<xsl:template match="Title" mode="Webperl">
  &#160;<a href="{@href}"><xsl:value-of select="."/></a><br />
</xsl:template>

<xsl:template match="Byline" mode="Webperl">
  <xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
