<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="text()">
  <xsl:copy />
</xsl:template>

<xsl:template match="weekly-column">
<div>
<table border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <font face="verdana">
        <xsl:apply-templates select="paragraph"/>
      </font>
    </td>
  </tr>
</table>
</div>
</xsl:template>

<xsl:template match="paragraph">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="link">
<a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>

</xsl:stylesheet>
