<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: transformxmlback.xsl,v 1.6 2003/03/05 15:07:44 gregor Exp $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="iso-8859-1"/>
<!-- the following 2 lines are not needed for this simpler example, they are needed for the 
lenya uni_zh integration :

<xsl:include href="../../xml/transformxmlback.xsl"/>
<xsl:variable name="attributes" select="document('../../wysiwyg_config/xml/attributes.xml')/attributes"/>
-->

<xsl:template match="*">
<!--		<xsl:call-template name="apply"/>-->
<xsl:choose>
	<xsl:when test="@bxe_originalname">
		<xsl:call-template name="apply">
			<xsl:with-param name="elementName" select="@bxe_originalname"/>
		</xsl:call-template>
	</xsl:when>
	<xsl:otherwise>
		<xsl:call-template name="apply"/>
	</xsl:otherwise>
</xsl:choose>
</xsl:template>

<!-- listitem crashes mozilla, if we delete such a node... replace it back from bxlistitem 
be sure to adjust BX_elements also to bxlistitem, otherwise it won't work
-->
<xsl:template match="bxlistitem">
	<xsl:call-template name="apply">
		<xsl:with-param name="elementName" select="'listitem'"/>
	</xsl:call-template>
</xsl:template>


<xsl:template name="apply">
<xsl:param name="elementName" select="name()"/>
     <xsl:choose>
	 
	 <!-- check if we can delete the element
	 1) if it has no other attributes than bxe_originalname, bxe_internalid and id
	 2) if it has attributes id and bxe_internalid (this will not delete elements with only id)
	 3) if there is no text in the element except whitespaces
	 4) if there are no child elements
	 5) if there is no attribute bxe_bitfluxspan -->
    	<xsl:when test="(not(@*[not(name() = 'bxe_originalname') and not(name() = 'bxe_internalid') and not(name() = 'id')]) 
		and ( @id and @bxe_internalid)
		and  not(normalize-space(.))  
		and not(*)) 
		and not(@bxe_bitfluxspan)">
        </xsl:when>
        <xsl:when test="@bxe_temporaryelement = 'yes'"></xsl:when>
<!--        <xsl:when test="@id = 'BX_cursor'"></xsl:when>-->
        <xsl:otherwise>
		    <xsl:element name="{$elementName}">
    		    <xsl:for-each select="@*">
	        		<xsl:if test="not(starts-with(name(),'bxe_')) and not(name() = 'style') and not(name() = 'id' and ../@bxe_internalid)">
						<xsl:copy/>
					</xsl:if>
	        	</xsl:for-each>
    		    <xsl:apply-templates/>
		    </xsl:element>
        </xsl:otherwise>
</xsl:choose>


</xsl:template>
</xsl:stylesheet>
