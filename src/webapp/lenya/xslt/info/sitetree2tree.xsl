<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: sitetree2tree.xsl,v 1.36 2004/03/13 12:42:06 gregor Exp $ -->

<!--
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
CONTEXT_PREFIX = "<xsl:value-of select="$contextprefix"/>";

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
  
  <xsl:variable name="link">
    <xsl:if test="not(@protected = 'true')">
      <xsl:text>, "</xsl:text>
      <xsl:value-of select="concat($contextprefix, '/', $publicationid, '/info-', @area, '/', $suffix)"/>
      <xsl:text>?lenya.usecase=info-overview&amp;lenya.step=showscreen</xsl:text>
      <xsl:text>"</xsl:text>
    </xsl:if>
  </xsl:variable>
  
  <xsl:variable name="protected-pre"><xsl:if test="@protected = 'true'">&lt;span class=\"lenya-info-protected\"&gt;</xsl:if></xsl:variable>
  <xsl:variable name="protected-post"><xsl:if test="@protected = 'true'">&lt;/span&gt;</xsl:if></xsl:variable>
  
  <xsl:variable name="pre" select="$protected-pre"/>
  <xsl:variable name="post" select="$protected-post"/>
  
  <xsl:choose>
  	<xsl:when test="descendant::s:node"><xsl:value-of select="generate-id(.)"/> = insFld(foldersTree, gFld("&#160;<xsl:value-of select="$pre"/><xsl:value-of select="@label"/><xsl:value-of select="$post"/>&#160;" <xsl:value-of select="$link"/>))</xsl:when>
    <xsl:otherwise>insDoc(foldersTree, gLnk("S", "&#160;<xsl:value-of select="$pre"/><xsl:value-of select="@label"/><xsl:value-of select="$post"/>&#160;" <xsl:value-of select="$link"/>))</xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates>
    <xsl:with-param name="parentPath"><xsl:value-of select="@id"/></xsl:with-param>
  </xsl:apply-templates>   
</xsl:template>

<xsl:template match="s:node">
  <xsl:param name="parentPath"/>
  <xsl:variable name="tree-area" select="ancestor::s:site/@area"/>
  <xsl:variable name="link">
    <xsl:if test="not(@protected = 'true')">
      <xsl:text>, "</xsl:text>
      <xsl:value-of select="concat($contextprefix, '/', $publicationid, '/info-', $tree-area, '/', @basic-url, @language-suffix, @suffix)"/>
      <xsl:text>?lenya.usecase=info-overview&amp;lenya.step=showscreen</xsl:text>
      <xsl:text>"</xsl:text>
    </xsl:if>
  </xsl:variable>
  <xsl:variable name="exists-language" select="s:label[lang($chosenlanguage)]"/>
  
  <xsl:variable name="protected-pre"><xsl:if test="@protected = 'true'">&lt;span class=\"lenya-info-protected\"&gt;</xsl:if></xsl:variable>
  <xsl:variable name="protected-post"><xsl:if test="@protected = 'true'">&lt;/span&gt;</xsl:if></xsl:variable>
  
  <xsl:variable name="no-language-pre"><xsl:if test="not($exists-language)">&lt;span class=\"lenya-info-nolanguage\"&gt;</xsl:if></xsl:variable>
  <xsl:variable name="no-language-post"><xsl:if test="not($exists-language)">&lt;/span&gt;</xsl:if></xsl:variable>
  
  <xsl:variable name="cut-pre"><xsl:if test="$cutdocumentid = concat('/', @basic-url)">&lt;span class='lenya-info-cut'&gt;[</xsl:if></xsl:variable>
  <xsl:variable name="cut-post"><xsl:if test="$cutdocumentid = concat('/', @basic-url)">]&lt;/span&gt;</xsl:if></xsl:variable>
  
  <xsl:variable name="pre" select="concat($no-language-pre, $protected-pre, $cut-pre)"/>
  <xsl:variable name="post" select="concat($cut-post, $protected-post, $no-language-post)"/>

  <xsl:choose>
  	<xsl:when test="descendant::s:node">
  		<xsl:value-of select="generate-id(.)"/>
  		= insFld(
  			   <xsl:value-of select="generate-id(..)"/>,
           gFld("&lt;span style=\"padding: 0px 5px;\"&gt;<xsl:value-of select="$pre"/><xsl:call-template name="getLabel"/><xsl:value-of select="$post"/>&lt;/span&gt;"
           <xsl:value-of select="$link"/>)
      );
    </xsl:when>
    <xsl:otherwise>
    	insDoc(<xsl:value-of select="generate-id(..)"/>,
    	       gLnk(
    	           "S",
    	           "&lt;span style=\"padding: 0px 5px;\"&gt;<xsl:value-of select="$pre"/><xsl:call-template name="getLabel"/>&lt;/span&gt;"
    	           <xsl:value-of select="$link"/>)
      );
      </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates>
    <xsl:with-param name="parentPath"><xsl:value-of select="$parentPath"/>/<xsl:value-of select="@id"/></xsl:with-param>
  </xsl:apply-templates>
</xsl:template>

<xsl:template name="getLabel">
  <xsl:choose>
    <xsl:when test="s:label[lang($chosenlanguage)]">
    	<xsl:value-of select="s:label[lang($chosenlanguage)]"/>
    </xsl:when>
    <xsl:when test="s:label[lang($defaultlanguage)]">
    	<xsl:value-of select="s:label[lang($defaultlanguage)]"/>
    </xsl:when>
    <xsl:otherwise>
    	<xsl:value-of select="s:label"/>
    </xsl:otherwise>
  </xsl:choose>	
</xsl:template>

<xsl:template match="s:label"/>

</xsl:stylesheet> 
