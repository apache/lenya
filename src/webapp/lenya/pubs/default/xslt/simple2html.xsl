<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output encoding="ISO-8859-1"/>

<xsl:template match="simple-document">
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="body">
	<xsl:apply-templates />
</xsl:template>


<xsl:template match="p">
	<p><xsl:apply-templates /></p>
</xsl:template>

<!-- table -->

<xsl:template match="informaltable">
		<xsl:element name="table">
		<xsl:attribute name="border">
			<xsl:value-of select="@border"/>
		</xsl:attribute>
		<xsl:apply-templates />
		</xsl:element>
</xsl:template>

	<xsl:template match="row">
		<tr><xsl:apply-templates /></tr>
	</xsl:template>

	
	<xsl:template match="tbody">
		<xsl:apply-templates />
	</xsl:template>



	<xsl:template match="entry">
		<td><xsl:apply-templates /></td>
	</xsl:template>


<xsl:template match="subtitle">
	<h1><xsl:apply-templates /></h1>
</xsl:template>


<xsl:template match="listitem">
	<ul><xsl:apply-templates /></ul>
</xsl:template>

<xsl:template match="itemizedlist">
	<li><xsl:apply-templates /></li>
</xsl:template>

</xsl:stylesheet>
