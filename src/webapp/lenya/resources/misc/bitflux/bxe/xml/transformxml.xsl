<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: transformxml.xsl,v 1.3 2002/10/25 10:12:34 felixcms Exp $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xhtml="http://www.w3.org/1999/xhtml">
<xsl:output method="xml" encoding="iso-8859-1"/>
<!--<xsl:include href="../../xml/transformxml.xsl"/>-->
<xsl:template match="/">
        <xsl:apply-templates/>

</xsl:template>

<xsl:template match="*">
<!-- <xsl:element name="{translate(name(),'ABCDEFGHIJKLMNOPQRSTUVXYZ','abcdefghijklmnopqrstuvwxyz')}">-->
<xsl:copy>
<!--<xsl:element name="{name()}">-->
        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>
        <xsl:call-template name="generateID"/>
        <xsl:apply-templates />
    </xsl:copy>
</xsl:template>


<xsl:template name="generateID">
	<xsl:choose>
    	<xsl:when test="@id">
        </xsl:when>
		<xsl:otherwise>
        	<xsl:attribute name="id" ><xsl:value-of select="generate-id()"/></xsl:attribute>
        	<xsl:attribute name="bxe_internalid" >yes</xsl:attribute>            
        </xsl:otherwise>
</xsl:choose>
<!--
<xsl:attribute name="bxe_originalname"><xsl:value-of select="name()"/></xsl:attribute>
-->
</xsl:template>        

<!-- listitem crashes mozilla, if we delete such a node... replace it bxlistitem 
be sure to adjust BX_elements also to bxlistitem, otherwise it won't work
-->
<xsl:template match="listitem">
	<bxlistitem>
		<xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>
        <xsl:call-template name="generateID"/>
        <xsl:apply-templates />
	</bxlistitem> 
</xsl:template>           
</xsl:stylesheet>
