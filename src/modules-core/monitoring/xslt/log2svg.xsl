<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:mon="http://apache.org/lenya/monitoring/1.0"
  xmlns:svg="http://www.w3.org/2000/svg">
  
  <xsl:param name="title"/>
  <xsl:param name="width" select="number(600)"/>
  <xsl:param name="height" select="number(400)"/>
  <xsl:param name="yGrid" select="20"/>
  <xsl:param name="marginLeft" select="50"/>
  <xsl:param name="margin" select="20"/>
  
  <xsl:variable name="hourWidth" select="$width div 24"/>
  
  <xsl:variable name="maxValue" select="/mon:log/@max"/>
  <xsl:variable name="max" select="ceiling($maxValue div $yGrid) * $yGrid"/>
  
  <xsl:template match="/mon:log">
    <svg:svg width="{$marginLeft + $width + 2 * $margin}" height="{$height + 2 * $margin}">
      <svg:text x="{$margin + $marginLeft + $width div 2}" y="{$margin - 5}" style="font-weight: bold;" text-anchor="middle"><xsl:value-of select="$title"/></svg:text>
      <xsl:choose>
        <xsl:when test="mon:entry">
          <xsl:call-template name="yLine"/>
          <xsl:call-template name="hourLine"/>
          <xsl:apply-templates select="mon:entry"/>
        </xsl:when>
        <xsl:otherwise>
          <svg:text x="{$margin + $marginLeft + $width div 2}" y="{$margin + 30}" text-anchor="middle">
            The session log is empty.
          </svg:text>
        </xsl:otherwise>
      </xsl:choose>
    </svg:svg>
  </xsl:template>
  
  
  <xsl:template name="yLine">
    <xsl:param name="value" select="$max"/>
    <xsl:variable name="y" select="$margin + $height - ($value div $max) * $height"/>
    <svg:line x1="{$margin + $marginLeft}" y1="{$y}" x2="{$margin + $marginLeft + $width}" y2="{$y}" stroke-width=".25" stroke="black"/>
    <svg:text align="right" x="{$margin + $marginLeft - 5}" y="{$y + 5}" text-anchor="end"><xsl:value-of select="$value"/></svg:text>
    <xsl:if test="$value &gt; 0">
      <xsl:call-template name="yLine">
        <xsl:with-param name="value" select="(ceiling($value div $yGrid) - 1) * $yGrid"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template  name="hourLine">
    <xsl:param name="hour" select="0"/>
    <xsl:variable name="x" select="$margin + $marginLeft + $hour * $hourWidth"/>
    <svg:line x1="{$x}" y1="{$margin}" x2="{$x}" y2="{$margin + $height}" stroke-width=".25" stroke="black"/>
    <svg:text x="{$x - 5}" y="{2 * $margin + $height}"><xsl:value-of select="format-number($hour, '00')"/></svg:text>
    <xsl:if test="$hour &lt; 24">
      <xsl:call-template name="hourLine">
        <xsl:with-param name="hour" select="$hour + 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="mon:entry[preceding-sibling::mon:entry]">
    <xsl:variable name="p" select="preceding-sibling::mon:entry[1]"/>
    <xsl:variable name="px" select="$margin + $marginLeft + $p/@hour * $hourWidth + $p/@min * ($hourWidth div 60) + $p/@sec * ($hourWidth div 3600)"/>
    <xsl:variable name="py" select="$margin + $height - ($p/@sessions div $max) * $height"/>
    <xsl:variable name="x" select="$margin + $marginLeft + @hour * $hourWidth + @min * ($hourWidth div 60) + @sec * ($hourWidth div 3600)"/>
    <xsl:variable name="y" select="$margin + $height - (@sessions div $max) * $height"/>
    <svg:line x1="{$px}" y1="{$py}" x2="{$x}" y2="{$y}" stroke="blue" stroke-width=".25" stroke-linecap="round"/>
  </xsl:template>

</xsl:stylesheet>