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

<!-- $Id: navigation-layout-2.xsl,v 1.6 2004/03/13 12:42:18 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:oscom="http://www.oscom.org/2002/oscom">
 
<xsl:template match="oscom_navigation">
    <font face="verdana" size="-2">
      <xsl:apply-templates>
        <xsl:with-param name="offset">10</xsl:with-param>
        <xsl:with-param name="index">10</xsl:with-param>
      </xsl:apply-templates>
    </font>
</xsl:template>

<xsl:template match="branch">
  <xsl:param name="offset"/>
  <xsl:param name="index"/>
  <xsl:choose>
    <xsl:when test="@href">
      <xsl:choose>
        <xsl:when test="contains(@href,'http://')">
          <div class="nnbe" style="padding-left: {$index}px;"><a class="nnbr" href="{@href}"><xsl:value-of select="@name"/></a></div>
        </xsl:when>
        <xsl:otherwise>
          <div class="nnbe" style="padding-left: {$index}px;"><a class="nnbr" href="{$CONTEXT_PREFIX}/{@href}"><xsl:value-of select="@name"/></a></div>
          <!--<a href="{$CONTEXT_PREFIX}/live/{@href}"><xsl:value-of select="@name"/></a>-->
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <div class="nnbe" style="padding-left: {$index}px;"><xsl:value-of select="@name"/></div>
    </xsl:otherwise>
  </xsl:choose>
<!--
  <br />
-->

  <xsl:apply-templates>
<!--
    <xsl:with-param name="offset"><xsl:value-of select="$offset"/></xsl:with-param>
    <xsl:with-param name="index"><xsl:value-of select="concat($index,$offset)"/></xsl:with-param>
-->
    <xsl:with-param name="offset"><xsl:value-of select="$offset"/></xsl:with-param>
    <xsl:with-param name="index"><xsl:value-of select="number($index)+number($offset)"/></xsl:with-param>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="leaf">
  <xsl:param name="offset"/>
  <xsl:param name="index"/>

<!--
  <xsl:value-of select="$index"/>
-->
  <xsl:choose>
    <xsl:when test="@href">
      <xsl:choose>
        <xsl:when test="contains(@href,'http://')">
          <div class="nnbe" style="padding-left: {$index}px;"><a class="nnbr" href="{@href}"><xsl:value-of select="@name"/></a></div>
        </xsl:when>
        <xsl:otherwise>
          <div class="nnbe" style="padding-left: {$index}px;"><a class="nnbr" href="{$CONTEXT_PREFIX}/{@href}"><xsl:value-of select="@name"/></a></div>
          <!--<a href="{$CONTEXT_PREFIX}/live/{@href}"><xsl:value-of select="@name"/></a>-->
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <div class="nnbe" style="padding-left: {$index}px;"><xsl:value-of select="@name"/></div>
    </xsl:otherwise>
  </xsl:choose>
<!--
  <br />
-->
</xsl:template>
 
</xsl:stylesheet>  
