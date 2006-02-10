<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lucene="http://apache.org/cocoon/lucene/1.0"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
>

<xsl:param name="index"/>
<xsl:param name="id"/>

<xsl:template match="/lucene:index">
  <lucene:index clear="false" indexid="{$index}" merge-factor="100">
    <lucene:document uid="{$id}">
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
