<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lucene="http://apache.org/cocoon/lucene/1.0"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  >
  
  <xsl:param name="index"/>
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  
  <xsl:template match="/lucene:index">
    <lucene:index indexid="{$index}" lucene:clear="false" lucene:merge-factor="100" lucene:analyzer="stopword_{$language}">
      <lucene:document uid="{$uuid}:{$language}">
        <lucene:field name="uuid"><xsl:value-of select="$uuid"/></lucene:field>
        <lucene:field name="language"><xsl:value-of select="$language"/></lucene:field>
        <xsl:apply-templates select="lucene:document/*"/>
      </lucene:document>
    </lucene:index>  
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
