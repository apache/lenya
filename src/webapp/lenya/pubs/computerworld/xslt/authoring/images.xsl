<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="link">
		<link rel="stylesheet" type="text/css">
			<xsl:choose>
				<xsl:when test="starts-with(@href, '/css')">
					<xsl:attribute name="href">/lenya/computerworld/authoring<xsl:value-of select="@href"/></xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</link>
	</xsl:template>
	<xsl:template match="@style[starts-with(.,'background-image')]">
		<xsl:attribute name="style">background-image:url(/lenya/computerworld/authoring<xsl:value-of select="substring(., 22)"/></xsl:attribute>
	</xsl:template>
	
	<xsl:template match="img/@src">
		<!-- only match /img, not menu -->
		<xsl:choose>
			<xsl:when test="starts-with(., '/img')">
				<xsl:attribute name="src">/lenya/computerworld/authoring<xsl:value-of select="."/></xsl:attribute>
			</xsl:when>
			<xsl:when test="starts-with(., '/images')">
				<xsl:attribute name="src">/lenya/computerworld/authoring<xsl:value-of select="."/></xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="src"><xsl:value-of select="."/></xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="@*|*">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
