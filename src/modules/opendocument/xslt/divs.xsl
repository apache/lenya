<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method      ="xml"
                encoding    ="UTF-8"
                indent      ="yes"/>

<!-- divs.xsl: add idents to OO document 
                @author <a href="http://librarycog.uwindsor.ca">art rhyno</a>
-->

<xsl:template match="node() | @*">
	<xsl:copy>
		<xsl:apply-templates select="@* | node()"/>
	</xsl:copy>
</xsl:template>


<xsl:template match="p[not(@class='Annotated')]">
		<div>
		<xsl:attribute name="id">
			<xsl:value-of select="position()"/>
		</xsl:attribute>
		</div>
	<xsl:copy>
		<xsl:apply-templates select="@* | node()"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="p[@class='Annotated']">
	<xsl:element name="p">
		<xsl:attribute name="class">
			<xsl:text>A1</xsl:text>
		</xsl:attribute>
		<xsl:value-of select="."/>
	</xsl:element>
</xsl:template>

</xsl:stylesheet>
