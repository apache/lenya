<?xml version="1.0" encoding="UTF-8"?>
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
<!-- $Id: generate-insertAsset-xsl.xsl 42703 2004-03-13 12:57:53Z gregor $ -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Let the processor do the formatting via indent = yes -->
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:preserve-space elements="xsl:text"/>
    
    <xsl:template match="/">
        <status>
            <xsl:apply-templates select="log"/>
        </status>
    </xsl:template>
    
    <xsl:template match="log">
        <changes>
            <xsl:for-each-group select="logentry" group-by="substring(date, 1, 7)">
                <release version="{substring(date, 1, 7)}">
                    <xsl:apply-templates select="current-group()"/>
                </release>     
            </xsl:for-each-group>       
        </changes>
    </xsl:template>

    <xsl:template match="logentry">
        <action>
            <xsl:attribute name="dev"><xsl:value-of select="author"/></xsl:attribute>
            <xsl:attribute name="type">fix</xsl:attribute>
            <xsl:attribute name="context">
                <xsl:choose>
                    <xsl:when test="contains(paths/path[1], 'BRANCH_1_2_X')">1.2</xsl:when>
                    <xsl:when test="contains(paths/path[1], 'lenya/trunk')">1.4</xsl:when>
                    <xsl:when test="contains(paths/path[1], 'incubator/trunk')">1.0</xsl:when>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="msg"/><xsl:text> </xsl:text>
        </action>
    </xsl:template>

</xsl:stylesheet>