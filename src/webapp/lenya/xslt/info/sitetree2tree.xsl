<?xml version="1.0"?>

<!--
        $Id: sitetree2tree.xsl,v 1.17 2003/07/31 11:57:18 gregor Exp $
        Converts a sitetree into a javascript array suitable for the tree widget.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:s="http://apache.org/cocoon/lenya/navigation/1.0">

<xsl:output omit-xml-declaration="yes" encoding = "iso-8859-1" />    

<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="area"/>
<xsl:param name="chosenlanguage"/>
<xsl:param name="defaultlanguage"/>
   
<xsl:template match="lenya">
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
		<xsl:apply-templates select="s:site"/>

//Set this string if Treeview and other configuration files may also be loaded in the same session
foldersTree.treeID = "t2" 
</xsl:template>    

<xsl:template match="s:site">
    <xsl:param name="parentPath"/>
    <xsl:param name="lenyaarea"/>
     <xsl:choose><xsl:when test="descendant::s:node"><xsl:value-of select="generate-id(.)"/> = insFld(foldersTree, gFld("<xsl:value-of select="@label"/>", ""))</xsl:when>
<xsl:otherwise>insDoc(foldersTree, gLnk("R", "<xsl:value-of select="@label"/>", ""))</xsl:otherwise></xsl:choose>
<xsl:apply-templates>
            <xsl:with-param name="parentPath"><xsl:value-of select="@id"/></xsl:with-param>
            <xsl:with-param name="lenyaarea"><xsl:value-of select="translate(@label, 'ALT', 'alt')"/></xsl:with-param>
</xsl:apply-templates>   
</xsl:template>

<xsl:template match="s:node">
    <xsl:param name="parentPath"/>
    <xsl:param name="lenyaarea"/>

<xsl:choose><xsl:when test="descendant::s:node"><xsl:value-of select="generate-id(.)"/> = insFld(<xsl:value-of select="generate-id(..)"/>, gFld("<xsl:call-template name="getLabels"/>", "<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/>/<xsl:value-of select="@basic-url"/><xsl:value-of select="@suffix"/>?lenya.usecase=info&amp;lenya.step=showscreen&amp;lenya.area=<xsl:value-of select="$lenyaarea"/>"))</xsl:when>
  <xsl:otherwise>insDoc(<xsl:value-of select="generate-id(..)"/>, gLnk("R", "<xsl:call-template name="getLabels"/>", "<xsl:value-of select="$contextprefix"/>/<xsl:value-of select="$publicationid"/>/<xsl:value-of select="$area"/>/<xsl:value-of select="@basic-url"/><xsl:value-of select="@suffix"/>?lenya.usecase=info&amp;lenya.step=showscreen&amp;lenya.area=<xsl:value-of select="$lenyaarea"/>"))</xsl:otherwise></xsl:choose>
<xsl:apply-templates>
<xsl:with-param name="parentPath"><xsl:value-of select="$parentPath"/>/<xsl:value-of select="@id"/></xsl:with-param>
<xsl:with-param name="lenyaarea"><xsl:value-of select="$lenyaarea"/></xsl:with-param>
</xsl:apply-templates>
</xsl:template>    

<xsl:template name="getLabels">
   <xsl:choose>
      <xsl:when test="s:label[lang($chosenlanguage)]">
      	<xsl:value-of select="s:label[lang($chosenlanguage)]"/>
      </xsl:when>
      <xsl:otherwise>
      	<xsl:value-of select="s:label[lang($defaultlanguage)]"/>
      </xsl:otherwise>
   </xsl:choose>	
</xsl:template>

<xsl:template match="s:label"/>

</xsl:stylesheet> 