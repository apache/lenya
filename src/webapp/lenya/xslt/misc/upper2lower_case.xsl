<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml"/>

<xsl:template match="*"> <!-- "*" = all elements-->
   <xsl:element name="{translate(name(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 
'abcdefghijklmnopqrstuvwxyz')}">
   <xsl:apply-templates select="node()|@*"/>
    </xsl:element>
</xsl:template>

<xsl:template match="@*"> <!-- "*" = all attributes of elements-->
   <xsl:attribute  name="{translate(name(), 
'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')}">
     <xsl:value-of select="."/>
   </xsl:attribute>
</xsl:template>

<xsl:template match="node()" priority="-1">
	<xsl:copy>
		<xsl:apply-templates select="node()|@*"/>
	</xsl:copy>
</xsl:template>


</xsl:stylesheet>
