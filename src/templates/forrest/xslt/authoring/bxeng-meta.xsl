<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xslout="Can be anything, doesn't matter">
<xsl:output method="xml"/>
<xsl:namespace-alias stylesheet-prefix="xslout" result-prefix="xsl"/>

<!-- Copies everything else to the result tree  -->
<xsl:template match="@* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="*[@bxe-editable='body']">
<!--
    <xslout:apply-templates select="/xhtml:html/lenya:meta/dc:title"/>
-->
    <xslout:apply-templates select="/xhtml:html/xhtml:body"/>
</xsl:template>

<!-- Adds the stylesheet specific elements -->
<xsl:template match="/">
  <xslout:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xslout:output method="xml"/>
    <xslout:template match="/">
      <xsl:apply-templates/>
    </xslout:template>

  <xslout:template match="*">
    <xslout:copy-of select="."/>
  </xslout:template>

  </xslout:stylesheet>
</xsl:template>

</xsl:stylesheet>
