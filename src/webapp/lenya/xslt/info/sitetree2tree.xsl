<?xml version="1.0"?>

<!--
        $Id: sitetree2tree.xsl,v 1.32 2003/10/13 09:07:19 andreas Exp $
        Converts a sitetree into a javascript array suitable for the tree widget.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:s="http://apache.org/cocoon/lenya/navigation/1.0">

<xsl:output omit-xml-declaration="yes" encoding = "iso-8859-1" />    

<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="chosenlanguage"/>
<xsl:param name="defaultlanguage"/>
<xsl:param name="cutdocumentid"/>
   
<xsl:template match="lenya">
    <xsl:param name="parentPath"/>
// You can find instructions for this file at http://www.treeview.net

//Environment variables are usually set at the top of this file.
USETEXTLINKS = 1
STARTALLOPEN = 0
USEFRAMES = 0
USEICONS = 0
WRAPTEXT = 1
PERSERVESTATE = 1
HIGHLIGHT = 1
HIGHLIGHT_BG = "#DDDCCF"
HIGHLIGHT_COLOR = "#666666"

foldersTree = gFld("&lt;strong&gt;<xsl:value-of select="$publicationid"/>&lt;/strong&gt;")
		<xsl:apply-templates select="s:site"/>

//Set this string if Treeview and other configuration files may also be loaded in the same session
foldersTree.treeID = "t2"
</xsl:template>

<xsl:template match="s:site">
  <xsl:param name="parentPath"/>
  <xsl:variable name="suffix">
  	<xsl:if test="not($chosenlanguage = $defaultlanguage)">_<xsl:value-of select="$chosenlanguage"/></xsl:if>
  </xsl:variable>
  
  <xsl:variable name="link"><xsl:value-of select="concat($contextprefix, '/', $publicationid, '/info-', @area, '/', $suffix)"/>?lenya.usecase=info-overview&amp;lenya.step=showscreen</xsl:variable>
  <xsl:choose>
  	<xsl:when test="descendant::s:node"><xsl:value-of select="generate-id(.)"/> = insFld(foldersTree, gFld("&#160;<xsl:value-of select="@label"/>&#160;", "<xsl:value-of select="$link"/>"))</xsl:when>
    <xsl:otherwise>insDoc(foldersTree, gLnk("S", "&#160;<xsl:value-of select="@label"/>&#160;", "<xsl:value-of select="$link"/>"))</xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates>
    <xsl:with-param name="parentPath"><xsl:value-of select="@id"/></xsl:with-param>
  </xsl:apply-templates>   
</xsl:template>

<xsl:template match="s:node">
  <xsl:param name="parentPath"/>
  <xsl:variable name="tree-area" select="ancestor::s:site/@area"/>
  <xsl:variable name="link"><xsl:value-of select="concat($contextprefix, '/', $publicationid, '/info-', $tree-area, '/', @basic-url, @language-suffix, @suffix)"/>?lenya.usecase=info-overview&amp;lenya.step=showscreen</xsl:variable>
  <xsl:variable name="exists-language" select="s:label[lang($chosenlanguage)]"/>
  <xsl:variable name="no-language-pre"><xsl:if test="not($exists-language)">&lt;span class=\"lenya-info-nolanguage\"&gt;</xsl:if></xsl:variable>
  <xsl:variable name="no-language-post"><xsl:if test="not($exists-language)">&lt;/span&gt;</xsl:if></xsl:variable>
  
  <xsl:variable name="cut-pre"><xsl:if test="$cutdocumentid = concat('/', @basic-url)">&lt;span class='lenya-info-cut'&gt;[</xsl:if></xsl:variable>
  <xsl:variable name="cut-post"><xsl:if test="$cutdocumentid = concat('/', @basic-url)">]&lt;/span&gt;</xsl:if></xsl:variable>

  <xsl:choose>
  	<xsl:when test="descendant::s:node">
  		<xsl:value-of select="generate-id(.)"/>
  		= insFld(
  			   <xsl:value-of select="generate-id(..)"/>,
           gFld("&lt;span style=\"padding: 0px 5px;\"&gt;<xsl:value-of select="$cut-pre"/><xsl:value-of select="$no-language-pre"/><xsl:call-template name="getLabels"/><xsl:value-of select="$no-language-post"/><xsl:value-of select="$cut-post"/>&lt;/span&gt;",
           "<xsl:value-of select="$link"/>")
      );
    </xsl:when>
    <xsl:otherwise>
    	insDoc(<xsl:value-of select="generate-id(..)"/>,
    	       gLnk(
    	           "S",
    	           "&lt;span style=\"padding: 0px 5px;\"&gt;<xsl:value-of select="$cut-pre"/><xsl:value-of select="$no-language-pre"/><xsl:call-template name="getLabels"/><xsl:value-of select="$no-language-post"/><xsl:value-of select="$cut-post"/>&lt;/span&gt;",
    	           "<xsl:value-of select="$link"/>")
      );
      </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates>
    <xsl:with-param name="parentPath"><xsl:value-of select="$parentPath"/>/<xsl:value-of select="@id"/></xsl:with-param>
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
