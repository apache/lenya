<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:include href="../../../xslt/menu2/root.xsl"/>

<!--
<xsl:template match="lenya/cmsbody">
    <xsl:apply-templates/>
 </xsl:template>

	<xsl:template match="link">
		<link rel="stylesheet" type="text/css">
			<xsl:choose>
				<xsl:when test="starts-with(@href, 'skin')">
					<xsl:attribute name="href">../<xsl:value-of select="@href"/></xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</link>
	</xsl:template>

	<xsl:template match="img/@src">
		<xsl:choose>
			<xsl:when test="starts-with(., 'skin')">
				<xsl:attribute name="src">../<xsl:value-of select="."/></xsl:attribute>
			</xsl:when>
			<xsl:when test="starts-with(., 'images')">
				<xsl:attribute name="src">../<xsl:value-of select="."/></xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="src"><xsl:value-of select="."/></xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

-->

<xsl:template match="@*|*">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
</xsl:template> 

</xsl:stylesheet>  
