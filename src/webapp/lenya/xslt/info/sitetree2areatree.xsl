<?xml version="1.0"?>

<!--
        $Id: sitetree2areatree.xsl,v 1.1 2003/09/04 15:22:24 andreas Exp $
        Adds an area attribute to a sitetree root element.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:s="http://apache.org/cocoon/lenya/sitetree/1.0"
    >

<xsl:param name="area"/>
   
<xsl:template match="s:site">
	<s:site area="{$area}">
		<xsl:copy-of select="@*"/>
		<xsl:apply-templates/>
	</s:site>
</xsl:template>
	

<xsl:template match="@*|node()">
	<xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>

</xsl:stylesheet> 
