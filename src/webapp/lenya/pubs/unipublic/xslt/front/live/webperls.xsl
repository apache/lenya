<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:include href="../../variables.xsl"/>

<xsl:template match="Webperls">
  <table border="0" cellpadding="0" cellspacing="0" width="135">
    <tr>
      <td rowspan="3" bgcolor="#CCCCFF"><img height="5" width="5" src="{$img-unipub}/1.gif"/></td>
      <td bgcolor="#CCCCFF"><img height="6" width="125" src="{$img-unipub}/1.gif"/></td>
      <td rowspan="3" bgcolor="#CCCCFF"><img height="5" width="5" src="{$img-unipub}/1.gif"/></td>
    </tr>
    <tr>
      <td bgcolor="#CCCCFF" width="125" class="{@id}"><font color="#333333">
        <xsl:value-of select="Title"/></font><br />

        <xsl:for-each select="Webperl">
          <img height="5" width="125" src="{$img-unipub}/1.gif"/><br />
          <img height="1" width="125" src="{$img-unipub}/white.gif"/>
          <img height="5" width="125" src="{$img-unipub}/1.gif"/><br />
          <font color="#333333"><b><img height="7" width="7" src="{$img-unipub}/t_perle.gif"/></b></font>
            <xsl:apply-templates select="."/><br />
        </xsl:for-each>

          <img height="5" width="125" src="{$img-unipub}/1.gif"/><br />
          <img height="1" width="125" src="{$img-unipub}/white.gif"/>
          <img height="5" width="125" src="{$img-unipub}/1.gif"/><br />
	 <font color="#333333"><b><img height="7" width="7" src="{$img-unipub}/t_perle.gif"/>&#160;<img height="7" width="7" src="{$img-unipub}/t_perle.gif"/>&#160;<img height="7" width="7" src="{$img-unipub}/t_perle.gif"/></b></font>&#160;<a href="{@id}">weitere Perlen ...</a>

      </td>
    </tr>
    <tr>
      <td bgcolor="#CCCCFF"><img height="6" width="125" src="{$img-unipub}/1.gif"/></td>
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
