<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" version="1.0" indent="yes"/>

<xsl:template match="/">
  <xsl:apply-templates select="*"/>
</xsl:template>

<!-- FIXME: there seems to be something wrong!!! (Xalan?) if something is written in front of Copy, then it works, else it doesn't ... -->
<xsl:template match="*|text()">
  <xsl:copy>
    <xsl:copy-of select="@*[name()!='tagID']"/>
    <xsl:apply-templates select="*|text()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
