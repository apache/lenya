<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="span[@id = 'today']" >
    <!-- Process todays date here... -->
    <xsl:apply-templates select="/wyona/today" />
</xsl:template>

<xsl:template match="today">
	<xsl:value-of select="."/>
</xsl:template>

	<xsl:template match="@*|*">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
