<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="webperls">
  <table border="0" cellpadding="2" cellspacing="0" bgcolor="#CCCCFF">
  <span bxe-editable="webperlen">
    <tr>
      <td width="125" class="webperlen"><font color="#333333">
        <xsl:value-of select="title"/></font></td>
    </tr>
        <xsl:for-each select="webperl">
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
          </span>
	  </table>
	</xsl:template>

<xsl:template match="webperl">
  <xsl:apply-templates select="title" mode="webperl"/>
  <xsl:apply-templates select="byline" mode="webperl"/>
</xsl:template>

<xsl:template match="title" mode="webperl">
  &#160;<a href="{@href}"><xsl:value-of select="."/></a><br />
</xsl:template>

<xsl:template match="byline" mode="webperl">
  <xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
