<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">

<html>
<head>
<title>bla</title>
</head>
	<body contentEditable="true">
  			<xsl:apply-templates select="/body"/>
	</body>
</html>
</xsl:template>


<!-- Copies everything else to the result tree -->
<xsl:template match="@* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>

