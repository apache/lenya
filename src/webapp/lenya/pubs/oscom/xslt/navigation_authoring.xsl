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

<!-- $Id: navigation_authoring.xsl,v 1.7 2004/03/13 12:42:18 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:oscom="http://www.oscom.org/2002/oscom">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="oscom_navigation">
    <font face="verdana" size="-2">
      <xsl:apply-templates>
        <xsl:with-param name="offset">&#160;&#160;</xsl:with-param>
        <xsl:with-param name="index"></xsl:with-param>
<!--
        <xsl:with-param name="offset">..</xsl:with-param>
        <xsl:with-param name="index">+</xsl:with-param>
-->
      </xsl:apply-templates>
    </font>
</xsl:template>

<xsl:template match="branch">
  <xsl:param name="offset"/>
  <xsl:param name="index"/>

  <xsl:value-of select="$index"/>
  <xsl:choose>
    <xsl:when test="@href">
      <xsl:choose>
        <xsl:when test="contains(@href,'http://')">
          <a href="{@href}"><xsl:value-of select="@name"/></a>
        </xsl:when>
        <xsl:otherwise>
          <a href="{$CONTEXT_PREFIX}/authoring/{@href}"><xsl:value-of select="@name"/></a>
          <!--<a href="{$CONTEXT_PREFIX}/live/{@href}"><xsl:value-of select="@name"/></a>-->
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="@name"/>
    </xsl:otherwise>
  </xsl:choose>
  <br />

  <xsl:apply-templates>
    <xsl:with-param name="offset"><xsl:value-of select="$offset"/></xsl:with-param>
    <xsl:with-param name="index"><xsl:value-of select="concat($index,$offset)"/></xsl:with-param>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="leaf">
  <xsl:param name="offset"/>
  <xsl:param name="index"/>

  <xsl:value-of select="$index"/>
  <xsl:choose>
    <xsl:when test="@href">
      <xsl:choose>
        <xsl:when test="contains(@href,'http://')">
          <a href="{@href}"><xsl:value-of select="@name"/></a>
        </xsl:when>
        <xsl:otherwise>
          <a href="{$CONTEXT_PREFIX}/authoring/{@href}"><xsl:value-of select="@name"/></a>
          <!--<a href="{$CONTEXT_PREFIX}/live/{@href}"><xsl:value-of select="@name"/></a>-->
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="@name"/>
    </xsl:otherwise>
  </xsl:choose>
  <br />
</xsl:template>
 
</xsl:stylesheet>  
