<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:preserve-space elements="*" />
  <!-- changes-v10.dtd to changes-v11.dtd transformation -->
  
  <xsl:output doctype-public="-//APACHE//DTD Changes V1.1//EN" doctype-system="changes-v11.dtd"/>
  
  <xsl:template match="/">
        <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="changes/@title">
    <title><xsl:value-of select="."/></title>
  </xsl:template>

  <xsl:template match="link/@type | link/@actuate | link/@show |
                       jump/@type | jump/@actuate | jump/@show |
                       fork/@type | fork/@actuate | fork/@show"/>

  <!-- the obligatory copy-everything -->
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
