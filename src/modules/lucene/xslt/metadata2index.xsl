<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:lucene="http://apache.org/cocoon/lucene/1.0"
  xmlns:meta="http://apache.org/cocoon/lenya/metadata/1.0"
  >
  
  <xsl:template match="meta:metadata">
    <lucene:document>
      <xsl:apply-templates select="*/*"/>
    </lucene:document>
  </xsl:template>
  
  
  <xsl:template match="*">
    <lucene:field boost="0.5" namespace="{namespace-uri()}" name="{local-name()}"><xsl:value-of select="."/></lucene:field>
  </xsl:template>
  
  
</xsl:stylesheet>
