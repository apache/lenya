<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: transformxsl.xsl 687 2004-04-29 08:32:39Z chregu $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 

xmlns:xhtml="http://www.w3.org/1999/xhtml"

>
<xsl:output method="xml" encoding="iso-8859-1" />


<xsl:template match="/">

	<xsl:apply-templates/>

</xsl:template>

<xsl:template match="xsl:apply-templates">
    <xhtml:div id="{generate-id()}" bxe_xpath="{@select}"></xhtml:div>
</xsl:template>

<xsl:template match="*">
    <xsl:copy>

        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>
        <xsl:apply-templates/>

	  </xsl:copy>
</xsl:template>

</xsl:stylesheet>

