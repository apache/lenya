<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    exclude-result-prefixes="xhtml lenya"
    >


<xsl:template match="/link">
<xhtml:html>
<a href="{@href}"><xsl:value-of select="@title"/></a>
</xhtml:html>
</xsl:template>



<!--
<xsl:template match="/">
<xhtml:html>
<xsl:apply-templates select="link"/>
</xhtml:html>
</xsl:template>

<xsl:template match="link">
<a href="{@href}"><xsl:value-of select="@title"/></a>
</xsl:template>
-->
</xsl:stylesheet> 
