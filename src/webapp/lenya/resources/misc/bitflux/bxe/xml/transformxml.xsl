<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: transformxml.xsl,v 1.1 2002/09/13 20:26:51 michicms Exp $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="iso-8859-1"/>
<!--<xsl:include href="../../xml/transformxml.xsl"/>-->
<xsl:template match="/">
        <xsl:apply-templates/>

</xsl:template>

<!--<xsl:template match="*/text()">
        <xsl:value-of select="normalize-space(."/>
</xsl:template>-->

<xsl:template match="*">
<xsl:element name="{translate(name(),'ABCDEFGHIJKLMNOPQRSTUVXYZ','abcdefghijklmnopqrstuvwxyz')}">
<!--<xsl:element name="{name()}">-->
        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>
        <xsl:call-template name="generateID"/>
        <xsl:apply-templates />
    </xsl:element>
</xsl:template>

<xsl:template match="media-reference">
        <!-- this is usz/bitflux cms related... maybe we should include it from another dir (wysiwyg_config)-->

    <xsl:copy>
        <xsl:for-each select="@*">
            <xsl:copy/>
        </xsl:for-each>

        <xsl:call-template name="generateID"/>
        <img src="{@source}">
        	<xsl:attribute name="temporaryelement" >yes</xsl:attribute>            
            </img>        
        <xsl:apply-templates />
    </xsl:copy>

</xsl:template>

<xsl:template name="generateID">
	<xsl:choose>
    	<xsl:when test="@id">
        </xsl:when>
		<xsl:otherwise>
        	<xsl:attribute name="id" ><xsl:value-of select="generate-id()"/></xsl:attribute>
        	<xsl:attribute name="internalid" >yes</xsl:attribute>            
        </xsl:otherwise>
</xsl:choose>
<xsl:attribute name="bx_originalname"><xsl:value-of select="name()"/></xsl:attribute>

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
