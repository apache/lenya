<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: transformxmlback.xsl,v 1.2 2002/10/24 14:41:18 felixcms Exp $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="iso-8859-1"/>
<!-- the following 2 lines are not needed for this simpler example, they are needed for the 
wyona uni_zh integration :

<xsl:include href="../../xml/transformxmlback.xsl"/>
<xsl:variable name="attributes" select="document('../../wysiwyg_config/xml/attributes.xml')/attributes"/>
-->

<xsl:template match="*">
<!--		<xsl:call-template name="apply"/>-->
<xsl:choose>
	<xsl:when test="@bxe_originalname">
		<xsl:call-template name="apply">
			<xsl:with-param name="elementName" />
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
    	<xsl:when test="not(@*[not(name() = 'bxe_originalname') and not(name() = 'bxe_internalid') and not(name() = 'id')]) and not(normalize-space(.))  and not(*)">
        </xsl:when>
        <xsl:when test="@bxe_temporaryelement = 'yes'"></xsl:when>
<!--        <xsl:when test="@id = 'BX_cursor'"></xsl:when>-->
        <xsl:otherwise>
		    <xsl:copy>

    		    <xsl:for-each select="@*">
	        		<xsl:if test="not(starts-with(name(),'bxe_')) and not(name() = 'style') and not(name() = 'id' and ../@bxe_internalid)">
						<xsl:copy/>
					</xsl:if>
	        	</xsl:for-each>
    		    <xsl:apply-templates/>
		    </xsl:copy>
        </xsl:otherwise>
</xsl:choose>


</xsl:template>
</xsl:stylesheet>
