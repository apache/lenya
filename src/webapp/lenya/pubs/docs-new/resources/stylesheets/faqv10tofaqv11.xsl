<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:preserve-space elements="*" />
  <!-- faq-v10.dtd to faq-v11.dtd transformation -->
  
  <xsl:output doctype-public="-//APACHE//DTD FAQ V1.1//EN" doctype-system="faq-v11.dtd"/>
  
  <xsl:template match="/">
        <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="link/@idref">
    <xsl:message terminate="no">The link element has no idref attribute defined in the document-v11.dtd, please fix your document.</xsl:message>
    [[link/@idref: <xsl:value-of select="."/> ]]
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
