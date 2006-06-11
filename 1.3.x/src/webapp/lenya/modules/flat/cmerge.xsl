<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

<xsl:template match="/content">
<xsl:apply-templates select="pages"/>
</xsl:template>

<xsl:template match="pages">
<resources>
<xsl:apply-templates select="page"/>
</resources>
</xsl:template>

<xsl:template match="page">
<xsl:variable name="idl"><xsl:value-of select="@idl"/></xsl:variable>
<xsl:variable name="href"><xsl:value-of select="@href"/></xsl:variable>
<xsl:element name="resource">
<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
<xsl:attribute name="idl"><xsl:value-of select="@idl"/></xsl:attribute>
<xsl:attribute name="language"><xsl:value-of select="@language"/></xsl:attribute>
<xsl:attribute name="visible"><xsl:value-of select="@visible"/></xsl:attribute>
<xsl:attribute name="navtitle"><xsl:value-of select="@navtitle"/></xsl:attribute>
<xsl:attribute name="navtitle"><xsl:value-of select="@navtitle"/></xsl:attribute>
<xsl:attribute name="area"><xsl:value-of select="@area"/></xsl:attribute>
<xsl:attribute name="position"><xsl:value-of select="@position"/></xsl:attribute>
<xsl:choose>
<xsl:when test="$href = ''">
<xsl:attribute name="type">xml</xsl:attribute>
<xsl:attribute name="filename">content/<xsl:value-of select="@area"/><xsl:value-of select="/content/files/file[@idl=$idl]/@filename"/></xsl:attribute>
<xsl:attribute name="time"><xsl:value-of select="/content/files/file[@idl=$idl]/@time"/></xsl:attribute>
</xsl:when>
<xsl:otherwise>
<xsl:attribute name="type">link</xsl:attribute>
<xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
</xsl:otherwise>
</xsl:choose>
</xsl:element>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
</xsl:template>

</xsl:stylesheet> 