<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="rcblock">
	  <xsl:element name="block">
	  	<xsl:for-each select="@*">
	        <xsl:copy/>
	      </xsl:for-each>
		<xsl:apply-templates/>
	</xsl:element>
  </xsl:template>

  <xsl:template match="dos_title">
          <xsl:element name="title">
                <xsl:for-each select="@*">
                <xsl:copy/>
              </xsl:for-each>
                <xsl:apply-templates/>
        </xsl:element>
  </xsl:template>

</xsl:stylesheet>
