<?xml version="1.0" encoding="UTF-8" ?>
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

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    >

<xsl:param name="current"/>
<xsl:variable name="language"><xsl:value-of select="/index/@language"/></xsl:variable>
<xsl:variable name="currentfull">/<xsl:value-of select="$current"/></xsl:variable>

<xsl:template match="/index">
  <div id="tabs">
    <xsl:call-template name="pre-separator"/>
    <xsl:apply-templates select="resource"/>
    <xsl:call-template name="post-separator"/>
  </div>
</xsl:template>

<xsl:template match="resource">
      <xsl:if test="position() &gt; 1">
        <xsl:call-template name="separator"/>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="descendant-or-self::resource[@fullid = $currentfull]">
<testD fullid="{@fullid}"/>
          <xsl:call-template name="tab-selected"/>
        </xsl:when>
        <xsl:otherwise>
<test fullid="{@fullid}"/>
          <xsl:call-template name="tab"/>
        </xsl:otherwise>
      </xsl:choose>
</xsl:template>

<xsl:template name="tab">
  <span class="tab"><xsl:call-template name="label"/></span>
</xsl:template>


<xsl:template name="tab-selected">
  <span class="tab-selected"><xsl:call-template name="label"/></span>
</xsl:template>

<xsl:template name="label">
<xsl:variable name="extension"><xsl:choose>
<xsl:when test="@extension"><xsl:value-of select="@extension"/></xsl:when>
<xsl:otherwise>html</xsl:otherwise>
</xsl:choose></xsl:variable>

   <xsl:choose>
    <xsl:when test="@fullid = $currentfull"><xsl:value-of select="@title"/></xsl:when>
    <xsl:otherwise>
     <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@fullid"/>_<xsl:value-of select="$language"/>.<xsl:value-of select="$extension"/></xsl:attribute>
        <xsl:value-of select="@title"/>
     </xsl:element>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="pre-separator">
</xsl:template>

<xsl:template name="separator">
   <xsl:text>&#160;</xsl:text>
</xsl:template>

<xsl:template name="post-separator">
</xsl:template>

</xsl:stylesheet> 
