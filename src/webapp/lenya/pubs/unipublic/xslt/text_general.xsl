<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!--  General Text Templates  -->

<xsl:template match="br">
  <br/>
</xsl:template>

<xsl:template match="bold">
  <b><xsl:apply-templates/></b>
</xsl:template>

<xsl:template match="emphasize">
  <i><xsl:apply-templates/></i>
</xsl:template>

<xsl:template match="subscript">
  <sub><xsl:apply-templates/></sub>
</xsl:template>

<xsl:template match="superscript">
  <sup><xsl:apply-templates/></sup>
</xsl:template>

<xsl:template match="ulink">
  <a href="{@url}"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="a">
  <a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>

</xsl:stylesheet>
