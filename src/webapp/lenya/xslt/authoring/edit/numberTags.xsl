<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" version="1.0" indent="yes"/>

<xsl:template match="/">
  <xsl:apply-templates select="*">
    <xsl:with-param name="parentID" select="'tag'"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="*|text()|@*">
  <xsl:param name="parentID"/>
  <xsl:variable name="thisID" select="concat($parentID,'.',position())"/>
  <xsl:copy>
    <xsl:attribute name="tagID"><xsl:value-of select="$thisID"/></xsl:attribute>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates select="*|text()|@*">
      <xsl:with-param name="parentID" select="$thisID"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
