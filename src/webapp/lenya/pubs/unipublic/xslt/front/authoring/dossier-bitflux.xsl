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

<xsl:template match="*[@bxe-editable='dossierkasten']">
  <dossiers contentEditable="true">
    <xslout:for-each select="dossiers">
      <xslout:apply-templates/>
    </xslout:for-each>
  </dossiers>
</xsl:template>

<!-- Adds the stylesheet specific elements -->
<xsl:template match="/">
  <xslout:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xslout:output method="xml"/>
    <xslout:template match="/">
      <xsl:apply-templates/>
    </xslout:template>
<!--
        <xslout:template match="dossiers">
                <dossiers contentEditable="true">
                        <xslout:for-each select="dossier">
                                <dossier>
                                        <xslout:apply-templates/>
                                </dossier>
                        </xslout:for-each>
                </dossiers>
        </xslout:template>
-->
	<!-- Templates used by Bitfluxeditor to make things editable -->
        <xslout:template match="*">
                <xslout:copy>
                        <xslout:for-each select="@*">
                                <xslout:copy/>
                        </xslout:for-each>
                        <xslout:apply-templates select="node()"/>
                </xslout:copy>
        </xslout:template>

  </xslout:stylesheet>
</xsl:template>

</xsl:stylesheet>
