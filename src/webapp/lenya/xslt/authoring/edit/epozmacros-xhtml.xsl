<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:param name="document-path"/>
<xsl:param name="contentfile"/>
<xsl:param name="save-destination"/>
<xsl:param name="reload-on-save" value="1"/>
<xsl:param name="usecss" value="1"/>


<xsl:template match="xhtml:body/xhtml:div/xhtml:div/xhtml:div/xhtml:iframe/@src">
	<xsl:attribute name="src"><xsl:value-of select="$contentfile"/></xsl:attribute>
	<xsl:attribute name="dst"><xsl:value-of select="$save-destination"/></xsl:attribute>
	<xsl:attribute name="reloadsrc"><xsl:value-of select="reload-on-save"/></xsl:attribute>
	<xsl:attribute name="usecss"><xsl:value-of select="use-css"/></xsl:attribute>
</xsl:template>

<xsl:template match="xhtml:title">
	<title>Edit <xsl:value-of select="$document-path"/> with Epoz - Apache Lenya</title>
</xsl:template>

<xsl:template match="@*|node()">
	<xsl:copy>
		<xsl:apply-templates select="@*|node()"/>
	</xsl:copy>
</xsl:template>
   
</xsl:stylesheet> 
