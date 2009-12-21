<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<!--
        Converts a sitetree into a javascript array suitable for the tree widget.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:s="http://apache.org/cocoon/lenya/navigation/1.0">

<xsl:import href="../util/string-functions.xsl"/>

<xsl:output omit-xml-declaration="yes"/>    

<xsl:param name="contextprefix"/>
<xsl:param name="publicationid"/>
<xsl:param name="chosenlanguage"/>
<xsl:param name="defaultlanguage"/>
<xsl:param name="cutdocumentid"/>
<xsl:param name="incremental"/>
<xsl:param name="areas"/>
   
<xsl:template match="/">
    <xsl:param name="parentPath"/>
// You can find instructions for this file at http://www.treeview.net

//Environment variables are usually set at the top of this file.
USETEXTLINKS = 1
STARTALLOPEN = 0
USEFRAMES = 0
USEICONS = 0
WRAPTEXT = 1
PRESERVESTATE = 1
HIGHLIGHT = 1
HIGHLIGHT_BG = "#DDDCCF"
HIGHLIGHT_COLOR = "#666666"
CONTEXT_PREFIX = "<xsl:value-of select="$contextprefix"/>";
PUBLICATION_ID = "<xsl:value-of select="$publicationid"/>";
CHOSEN_LANGUAGE = "<xsl:value-of select="$chosenlanguage"/>";
DEFAULT_LANGUAGE = "<xsl:value-of select="$defaultlanguage"/>";
CUT_DOCUMENT_ID = "<xsl:value-of select="$cutdocumentid"/>";
ALL_AREAS = "<xsl:value-of select="$areas"/>"
PIPELINE_PATH = '/authoring/info-sitetree/sitetree-fragment.xml'
<xsl:choose>
  <xsl:when test="$incremental='true'">
INCREMENTAL_LOADING = true;
  </xsl:when>
  <xsl:otherwise>
INCREMENTAL_LOADING = false;
  </xsl:otherwise>
</xsl:choose>

<!-- incremental loading does not work with the preserve state mechanism (cookies) -->
if (INCREMENTAL_LOADING) PRESERVESTATE=0;

foldersTree = gFld("&lt;strong&gt;<xsl:value-of select="$publicationid"/>&lt;/strong&gt;")

  <xsl:if test="$incremental!='true'">
    <xsl:apply-templates select="lenya/s:site"/>
  </xsl:if>

//Set this string if Treeview and other configuration files may also be loaded in the same session
foldersTree.treeID = "t2"
</xsl:template>

<xsl:template match="s:site">
  <xsl:param name="parentPath"/>
  <xsl:variable name="suffix">
  	<xsl:if test="not($chosenlanguage = $defaultlanguage)">_<xsl:value-of select="$chosenlanguage"/></xsl:if>
  </xsl:variable>
  
  <xsl:variable name="link">
      <xsl:text>, 'javascript:setLink(\'</xsl:text>
      <xsl:value-of select="concat('/', $suffix)"/>
      <xsl:text>\')'</xsl:text>
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
      <xsl:text>, 'javascript:setLink(\'</xsl:text>
      <xsl:value-of select="concat('/', @basic-url, @language-suffix, @suffix)"/>
      <xsl:text>\')'</xsl:text>
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
      <xsl:call-template name="escape-characters">
        <xsl:with-param name="input" select="s:label[lang($chosenlanguage)]"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="s:label[lang($defaultlanguage)]">
      <xsl:call-template name="escape-characters">
        <xsl:with-param name="input" select="s:label[lang($defaultlanguage)]"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="escape-characters">
        <xsl:with-param name="input" select="s:label"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>	
</xsl:template>

<xsl:template match="s:label"/>


<xsl:template name="escape-characters">
  <xsl:param name="input"/>
  <xsl:variable name="escape-lt">
    <xsl:call-template name="search-and-replace">
      <xsl:with-param name="input" select="$input"/>
      <xsl:with-param name="search-string">&lt;</xsl:with-param>
      <xsl:with-param name="replace-string">&amp;lt;</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="escape-gt">
    <xsl:call-template name="search-and-replace">
      <xsl:with-param name="input" select="$escape-lt"/>
      <xsl:with-param name="search-string">&gt;</xsl:with-param>
      <xsl:with-param name="replace-string">&amp;gt;</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="escape-quot">
    <xsl:call-template name="search-and-replace">
      <xsl:with-param name="input" select="$escape-gt"/>
      <xsl:with-param name="search-string">"</xsl:with-param>
      <xsl:with-param name="replace-string">\"</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$escape-quot"/>
</xsl:template>

</xsl:stylesheet> 
