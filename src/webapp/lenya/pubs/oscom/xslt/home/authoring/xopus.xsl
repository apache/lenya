<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="text()">
  <xsl:copy />
</xsl:template>

<xsl:template match="home">
<div>
<table border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <font face="verdana">
        <xsl:apply-templates select="about/p"/>
      </font>
    </td>
  </tr>
  <tr>
    <td>
      <font face="verdana">
        <xsl:apply-templates select="features"/>
      </font>
    </td>
  </tr>
</table>
</div>
</xsl:template>

<xsl:template match="features">
  <xsl:apply-templates select="feature"/>
</xsl:template>

<xsl:template match="feature">
  <xsl:apply-templates select="title"/>
  <xsl:apply-templates select="p"/>
</xsl:template>

<xsl:template match="title">
  <h3><xsl:apply-templates/></h3>
</xsl:template>

<xsl:template match="p">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="a">
<a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>

</xsl:stylesheet>
