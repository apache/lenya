<?xml version="1.0"?>

<!--
        $Id: sitetree2tree.xsl,v 1.2 2003/06/11 12:23:55 gregor Exp $
        Converts a sitetree into a javascript array suitable for the tree widget.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output omit-xml-declaration="yes" encoding = "iso-8859-1" />    

<xsl:template match="/*[local-name()='site']">
// You can find instructions for this file at http://www.treeview.net

//Environment variables are usually set at the top of this file.
USETEXTLINKS = 1
STARTALLOPEN = 1
USEFRAMES = 1
USEICONS = 0
WRAPTEXT = 1
PERSERVESTATE = 0
HIGHLIGHT = 1

foldersTree = gFld("<b>Site</b>", "test.html")
<xsl:apply-templates select="*[local-name()='node']"/>
//Set this string if Treeview and other configuration files may also be loaded in the same session
foldersTree.treeID = "t2" 
</xsl:template>    
    
<xsl:template match="*[local-name()='node']">
<xsl:choose><xsl:when test="descendant::*[local-name()='node']"><xsl:value-of select="generate-id(.)"/> = insFld(<xsl:choose><xsl:when test="local-name(parent::node())='site'">foldersTree</xsl:when><xsl:otherwise><xsl:value-of select="generate-id(..)"/></xsl:otherwise></xsl:choose>, gFld("<xsl:value-of select="*[local-name()='label']"/>", "<xsl:value-of select="@id"/>"))</xsl:when>
<xsl:otherwise>insDoc(<xsl:choose><xsl:when test="local-name(parent::node())='site'">foldersTree</xsl:when><xsl:otherwise><xsl:value-of select="generate-id(..)"/></xsl:otherwise></xsl:choose>, gLnk("R", "<xsl:value-of select="*[local-name()='label']"/>", "<xsl:value-of select="@id"/>"))</xsl:otherwise></xsl:choose>
<xsl:apply-templates />
</xsl:template>    

<xsl:template match="*[local-name()='label']"/>
    
</xsl:stylesheet> 