<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xslout="Can be anything, doesn't matter">

<xsl:namespace-alias stylesheet-prefix="xslout" result-prefix="xsl"/>


<!-- Copies everything else to the result tree -->
<xsl:template match="@* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>


<!-- Replaces the html code of the editable section by the bitflux specific code -->
<xsl:template match="*[@bxe-editable='headlines']">
	<Articles contentEditable="true">
                <xslout:for-each select=".">
  			<xslout:apply-templates/>
  			<br />
                </xslout:for-each>
	</Articles>
</xsl:template>

<!-- Adds the stylesheet specific elements -->
<xsl:template match="/">
  <xslout:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xslout:output type="xml"/>
    <xslout:template match="/">
      <xsl:apply-templates/>
    </xslout:template>

	Template used by Bitfluxeditor to make things editable 
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

