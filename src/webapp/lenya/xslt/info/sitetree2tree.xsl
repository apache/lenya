<?xml version="1.0"?>

<!--
        $Id: sitetree2tree.xsl,v 1.8 2003/07/03 13:47:40 gregor Exp $
        Converts a sitetree into a javascript array suitable for the tree widget.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output omit-xml-declaration="yes" encoding = "iso-8859-1" />    

    <xsl:param name="contextprefix"/>
   <xsl:param name="publicationid"/>
   <xsl:param name="area"/>

<xsl:template match="/*[local-name()='site']">
    <xsl:param name="parentPath"/>
// You can find instructions for this file at http://www.treeview.net

//Environment variables are usually set at the top of this file.
USETEXTLINKS = 1
STARTALLOPEN = 1
USEFRAMES = 1
USEICONS = 0
WRAPTEXT = 1
PERSERVESTATE = 0
HIGHLIGHT = 1

foldersTree = gFld("<b>Site</b>", "<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/>/?lenya.usecase=info&amp;lenya.step=showscreen")
		<xsl:apply-templates select="*[local-name()='node']">
            <xsl:with-param name="parentPath"><xsl:value-of select="@id"/></xsl:with-param>
		</xsl:apply-templates>

//Set this string if Treeview and other configuration files may also be loaded in the same session
foldersTree.treeID = "t2" 
</xsl:template>    


    
<xsl:template match="*[local-name()='node']">
    <xsl:param name="parentPath"/>
<xsl:choose><xsl:when test="descendant::*[local-name()='node']"><xsl:value-of select="generate-id(.)"/> = insFld(<xsl:choose><xsl:when test="local-name(parent::node())='site'">foldersTree</xsl:when><xsl:otherwise><xsl:value-of select="generate-id(..)"/></xsl:otherwise></xsl:choose>, gFld("<xsl:value-of select="*[local-name()='label']"/>", "<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/><xsl:value-of select="$parentPath"/>/<xsl:value-of select="@id"/>?lenya.usecase=info&amp;lenya.step=showscreen"))</xsl:when>
<xsl:otherwise>insDoc(<xsl:choose><xsl:when test="local-name(parent::node())='site'">foldersTree</xsl:when><xsl:otherwise><xsl:value-of select="generate-id(..)"/></xsl:otherwise></xsl:choose>, gLnk("R", "<xsl:value-of select="*[local-name()='label']"/>", "<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/><xsl:value-of select="$parentPath"/>/<xsl:value-of select="@id"/>?lenya.usecase=info&amp;lenya.step=showscreen"))</xsl:otherwise></xsl:choose>
<xsl:apply-templates>
            <xsl:with-param name="parentPath"><xsl:value-of select="$parentPath"/>/<xsl:value-of select="@id"/></xsl:with-param>
</xsl:apply-templates>
</xsl:template>    

<xsl:template match="*[local-name()='label']"/>
    
</xsl:stylesheet> 