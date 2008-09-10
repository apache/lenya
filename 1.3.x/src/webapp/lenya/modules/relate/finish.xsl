<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:h="http://apache.org/cocoon/request/2.0"
    exclude-result-prefixes="h"

>
<xsl:param name="publication"/>
<xsl:param name="module"/>
<xsl:param name="structure"/>

<xsl:template match="/">
<xsl:apply-templates select="resources/newresource"/>
</xsl:template>

<xsl:template match="newresource">
<xsl:variable name="status"><xsl:value-of select="@status"/></xsl:variable>
<html><head><title><xsl:value-of select="$structure"/></title></head>
<body><h1>
<xsl:value-of select="$publication"/>&#160;<xsl:value-of select="$structure"/>&#160;
<xsl:choose>
<xsl:when test="string-length($status) = 0">saved.
</xsl:when>
<xsl:when test="$status = 'SUCCESS'">
saved as new Relations Design Resource.
</xsl:when>
<xsl:otherwise>
failed because <xsl:value-of select="."/>
</xsl:otherwise>
</xsl:choose>
</h1>
<br/><xsl:element name="a">
<xsl:attribute name="href">/<xsl:value-of select="$publication"/>/<xsl:value-of select="$module"/>/<xsl:value-of select="$structure"/></xsl:attribute>
Return to editing <xsl:value-of select="$structure"/> Relations.
</xsl:element>
<br/><xsl:element name="a">
<xsl:attribute name="href">/<xsl:value-of select="$publication"/>/edit</xsl:attribute>
Return to main edit screen.
</xsl:element>
</body>
</html>
</xsl:template>

</xsl:stylesheet> 
