<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="front"/>
	
	<xsl:template match="/">
		<xsl:apply-templates select="wyona/cmsbody/html"/>
	</xsl:template>
	
	<!-- load frontpage banners or inside banners, depending on location -->
<!--
	<xsl:choose>
		<xsl:when test="$front = 'yes'">
-->
			<xsl:include href="ads.xsl"/>
<!--
		</xsl:when>
		<xsl:otherwise>
			<xsl:include href="ads-inside.xsl"/>
		</xsl:otherwise>
	</xsl:choose>
-->
	
	<xsl:include href="../today.xsl"/>
	<xsl:include href="../navigation.xsl"/>
	<xsl:include href="small-preview.xsl"/>
	<xsl:include href="headlines.xsl"/>
	
	<xsl:template match="@*|*">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
