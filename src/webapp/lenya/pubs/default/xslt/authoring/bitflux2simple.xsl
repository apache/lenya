<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:xslout="Can be anything, doesn't matter">
<xsl:output type="xml"/>
<xsl:namespace-alias stylesheet-prefix="xslout" result-prefix="xsl"/>


<!-- Copies everything else to the result tree -->
<xsl:template match="@* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>


<!-- Replaces the html code of the editable section by the bitflux specific code -->
<xsl:template match="preview">
	<preview contentEditable="true">
                <xslout:for-each select="preview">
  			<xslout:apply-templates/>
                </xslout:for-each>
	</preview>
</xsl:template>

<xsl:template name="preview-bxe">
	<table border="0" cellpadding="2" cellspacing="0" bgcolor="#CCCCFF">
		<span bxe-editable="preview">
		</span>
	</table>
</xsl:template>

  <xsl:template match="title">
    <title contentEditable="true">
      <xsl:for-each select=".">
        <xsl:apply-templates/>
      </xsl:for-each>
    </title>
  </xsl:template>

  <xsl:template match="body">
    <body contentEditable="true">
      <xsl:for-each select=".">
        <xsl:apply-templates/>
      </xsl:for-each>
    </body>
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

