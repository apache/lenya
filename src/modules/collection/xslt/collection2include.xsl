<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:ci="http://apache.org/cocoon/include/1.0">
  
  
  <xsl:param name="pub"/>
  <xsl:param name="area"/>
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  
  
  <xsl:template match="/col:collection">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <ci:include src="cocoon://modules/collection/metadata/{$pub}/{$area}/{$uuid}/{$language}.xml"/>
      <xsl:apply-templates select="*"/>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="*/col:collection">
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  
  <xsl:template match="col:document">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="not(*)">
        <ci:include src="cocoon://modules/collection/metadata/{$pub}/{$area}/{@uuid}/{@xml:lang}.xml"/>
        <ci:include src="lenya-document:{@uuid},pub={$pub},area={$area},lang={@xml:lang}?format=xhtml"/>
      </xsl:if>
      <xsl:copy-of select="*"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet>