<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lucene="http://apache.org/cocoon/lucene/1.0"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
>

<xsl:template match="/">
  <!-- attributes of the index and document element will be added by the lucene module -->
  <lucene:index>
    <lucene:document>
      <xsl:apply-templates/>
    </lucene:document>
  </lucene:index>  
</xsl:template>

<xsl:template match="xhtml:body" priority="1">
  <lucene:field name="body" boost="1">
    <xsl:for-each select=".//text()">
      <xsl:value-of select="concat(normalize-space(.),' ')"/>
    </xsl:for-each>
  </lucene:field>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
