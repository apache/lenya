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
<!-- $Id$ -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="no"/>
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
            <xsl:variable name="type">
                <xsl:choose>
                    <xsl:when test="contains(paths/path[1]/@action, 'A')">add</xsl:when>
                    <xsl:when test="contains(paths/path[1]/@action, 'M')">update</xsl:when>
                    <xsl:when test="contains(paths/path[1]/@action, 'R')">update</xsl:when>
                </xsl:choose>
            </xsl:variable>
            <xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
            <xsl:variable name="version">
                <xsl:choose>
                    <xsl:when test="contains(paths/path[1], 'BRANCH_1_2_X')">1.2</xsl:when>
                    <xsl:when test="contains(paths/path[1], 'lenya/trunk') and contains(date, '2005')">1.4</xsl:when>
                    <xsl:when test="contains(paths/path[1], 'lenya/trunk') and contains(date, '2004')">1.4</xsl:when>
                    <xsl:when test="contains(date, '2003')">1.0</xsl:when>
                    <xsl:when test="contains(date, '2002')">0.8</xsl:when>
                    <xsl:when test="contains(paths/path[1], 'docu')">Docs</xsl:when>
                    <xsl:when test="contains(paths/path[1], 'site')">Site</xsl:when>
                </xsl:choose>
            </xsl:variable>
            <xsl:attribute name="context">
                <xsl:value-of select="$version"/>
            </xsl:attribute>[<xsl:value-of select="$version"/>] 
            <xsl:value-of select="msg"/><xsl:text> </xsl:text><link href="http://svn.apache.org/viewcvs.cgi?rev={@revision}&amp;view=rev">Diff</link>
        </action>
    </xsl:template>
    
</xsl:stylesheet>