<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:ci="http://apache.org/cocoon/include/1.0">
  
  <xsl:param name="uuid"/>
  <xsl:param name="language"/>
  
  <xsl:template match="col:collection">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:if test="@type = 'children'">
        <ci:include src="cocoon://modules/collection/collectionWithChildren/{$uuid}/{$language}.xml"
          select="*/*"/>
      </xsl:if>
      <xsl:copy-of select="col:document"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet>