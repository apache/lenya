<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: transformxmlback.xsl,v 1.1 2002/09/13 20:26:51 michicms Exp $ -->
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
	<xsl:when test="@bx_originalname">
		<xsl:call-template name="apply">
			<xsl:with-param name="elementName" select="@bx_originalname"/>
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
    	<xsl:when test="not(@*[not(name() = 'bx_originalname') and not(name() = 'internalid') and not(name() = 'id')]) and not(normalize-space(.))  and not(*)">
        </xsl:when>
        <xsl:when test="@temporaryelement = 'yes'"></xsl:when>
<!--        <xsl:when test="@id = 'BX_cursor'"></xsl:when>-->
        <xsl:otherwise>
		    <xsl:element name="{$elementName}">

    		    <xsl:for-each select="@*">
	        		<xsl:if test="not(name() = 'style') and not(name() = 'bx_originalname') and not(name() = 'internalid') and not(name() = 'id' and ../@internalid)">
				<xsl:variable name="AttrLookup" select="$attributes/attr[@name = name(current())]"/>
					<xsl:choose>
						<xsl:when test="$AttrLookup">
							<xsl:attribute name="{$AttrLookup}"><xsl:value-of select="."/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
	       	    				<xsl:copy/>

						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
	        	</xsl:for-each>
    		    <xsl:apply-templates/>
		    </xsl:element>
        </xsl:otherwise>
</xsl:choose>


</xsl:template>
</xsl:stylesheet>
