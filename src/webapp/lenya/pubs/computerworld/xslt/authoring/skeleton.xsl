<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="wyona/cmsbody">
    <xsl:apply-templates/>
 </xsl:template>

<xsl:include href="navigation.xsl"/>
<xsl:include href="small-preview.xsl"/>

<xsl:include href="headlines.xsl"/>


<xsl:template match="@*|*">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
</xsl:template>


</xsl:stylesheet>
