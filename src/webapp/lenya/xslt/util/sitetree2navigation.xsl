<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id: sitetree2navigation.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:lenya="http://www.wyona.org/2003/"
  version="1.0">


  <xsl:output indent="yes"/>

  <xsl:param name="nav_id"/>


  <!-- the idea is to use the xsl:key function -->
  <!-- xsl:key name="nav_id_key" match="//node" use="@id"/ -->


<xsl:template match="sitetree">
  <navigation xmlns:lenya="http://www.wyona.org/2003/">
    <nav_id><xsl:value-of select="$nav_id"/></nav_id>
    <xsl:apply-templates select="block"/>
  </navigation>
</xsl:template>



<xsl:template match="block">
    <xsl:if test="@id='main'">
      <main-navigation>
        <xsl:apply-templates select="node"/>
      </main-navigation>
    </xsl:if>


    <xsl:if test="@id='top'">
      <top-navigation>
        <xsl:apply-templates select="node"/>
      </top-navigation>
    </xsl:if>
    <xsl:if test="@id='left'">
      <navigation>
        <xsl:apply-templates select="node"/>
      </navigation>
    </xsl:if>
</xsl:template>



<xsl:template match="node">
    <xsl:param name="level" select="1"/>

    <xsl:variable name="target">
      <xsl:choose>
        <xsl:when test="@id=$nav_id">
          <xsl:value-of select="'file'"/>
        </xsl:when>
        <xsl:when test="descendant::node[@id=$nav_id]">
          <!-- the idea is to use the xsl:key function -->
          <!-- xsl:when test="key( 'nav_id_key', $nav_id )" -->
          <xsl:value-of select="'dir'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="''"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <item nav_id="{@id}">
      <xsl:attribute name="level"><xsl:value-of select="$level"/></xsl:attribute>
      <xsl:if test="$target != ''">
        <xsl:attribute name="target"><xsl:value-of select="$target"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@link">
        <xsl:attribute name="href"><xsl:value-of select="@link"/></xsl:attribute>
      </xsl:if>

      <xsl:apply-templates select="name"/>

      <xsl:if test="$target = 'dir' ">
        <xsl:apply-templates select="node">
          <xsl:with-param name="level" select="$level + 1"/>
        </xsl:apply-templates>
      </xsl:if>

    </item>
</xsl:template>



<xsl:template match="name">
    <name><xsl:apply-templates/></name>
</xsl:template>

</xsl:stylesheet>
