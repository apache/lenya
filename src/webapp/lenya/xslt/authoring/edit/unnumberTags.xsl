<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" version="1.0" indent="yes" encoding="iso-8859-1"/>

<xsl:template match="/">
  <xsl:apply-templates select="*"/>
</xsl:template>

<!-- FIXME: there is a bug in here!!! (in Xalan?) wenn wir was vor Copy schreiben, funzts, sonst nicht.... -->
<xsl:template match="*|text()">
  <xsl:copy>
    <xsl:copy-of select="@*[name()!='tagID']"/>
    <xsl:apply-templates select="*|text()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
