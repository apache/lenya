<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/*[local-name()='simple-document']">
<html>
<head>
</head>
  	<xsl:apply-templates select="*[local-name()='body']"/>
</html>
</xsl:template>

  <xsl:template match="*[local-name()='body']">
    <body contentEditable="true">
      <xsl:for-each select="*[local-name()='body']">
        <xsl:apply-templates/>
      </xsl:for-each>
    </body>
   </xsl:template>

<!-- Copies everything else to the result tree -->
  <xsl:template match="*">
        <xsl:copy>
            <xsl:for-each select="@*">
                <xsl:copy/>
            </xsl:for-each>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
  </xsl:template>

</xsl:stylesheet>

