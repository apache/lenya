<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- Stylesheet which Bitflux uses to convert elements back to the original state before saving them -->

  <xsl:template match="rcblock">
	  <xsl:element name="block">
            <xsl:for-each select="@*">
              <xsl:if test="not(starts-with(name(),'bxe_')) and not(name() = 'style') and not(name() = 'id' and ../@bxe_internalid)">
                <xsl:copy/>
              </xsl:if>
            </xsl:for-each>
            <xsl:apply-templates/>
	</xsl:element>
  </xsl:template>

  <xsl:template match="dos_title">
          <xsl:element name="title">
            <xsl:for-each select="@*">
              <xsl:if test="not(starts-with(name(),'bxe_')) and not(name() = 'style') and not(name() = 'id' and ../@bxe_internalid)">
                <xsl:copy/>
              </xsl:if>
            </xsl:for-each>
            <xsl:apply-templates/>
        </xsl:element>
  </xsl:template>

</xsl:stylesheet>
