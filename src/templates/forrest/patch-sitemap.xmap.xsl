<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:map="http://apache.org/cocoon/sitemap/1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- serializing as XML to allow further processing by Lenya -->
  <xsl:template match="map:resource[@name='skinit']/map:serialize">
    <map:serialize type="xml"/>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
