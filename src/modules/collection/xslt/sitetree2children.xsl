<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:site="http://apache.org/cocoon/lenya/sitetree/1.0"
  xmlns:col="http://apache.org/cocoon/lenya/collection/1.0"
  xmlns:ci="http://apache.org/cocoon/include/1.0">
  
  
  <xsl:template match="site:fragment">
    <col:collection>
      <xsl:apply-templates select="site:node"/>
    </col:collection>
  </xsl:template>
  
  
  <xsl:template match="site:node">
    <col:document uuid="{@uuid}"/>
  </xsl:template>

</xsl:stylesheet>