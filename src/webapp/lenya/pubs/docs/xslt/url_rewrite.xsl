<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

<!--
<xsl:template match="img/@src[starts-with(., '/lenya/wyona.org')]">
<xsl:attribute name="src"><xsl:value-of select="substring(.,21)"/></xsl:attribute>
</xsl:template>

<xsl:template match="a[starts-with(@href,'/lenya/wyona.org')]">
<a>
<xsl:attribute name="href"><xsl:value-of select="substring(@href,21)"/></xsl:attribute>
<xsl:value-of select="."/>
</a>
</xsl:template>

<xsl:template match="link[starts-with(@href,'/lenya/wyona.org')]">
<link rel="stylesheet" type="text/css">
<xsl:attribute name="href"><xsl:value-of select="substring(@href,21)"/></xsl:attribute>
</link>
</xsl:template>
-->

<xsl:template match="@*|*">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
