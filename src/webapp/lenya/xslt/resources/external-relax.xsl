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
    >

  <xsl:param name="publicationid" />
  <xsl:param name="contextprefix"/>
  
  <xsl:template match="@href[starts-with(.,'fallback://lenya/resources/')]" priority="10">
    <xsl:attribute name="href">
      <xsl:variable name="resource" select="substring-after(., 'fallback://lenya/resources/')"/>
      <xsl:value-of select="concat($contextprefix, '/lenya/', $resource )"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="@href[starts-with(.,'fallback')]" priority="5">
    <xsl:variable name="nofallback" select="substring-after(.,'fallback://lenya/modules/')"/>
    <xsl:variable name="restype" select="substring-before($nofallback,'/resources')"/>
    <xsl:variable name="resource" select="substring-after($nofallback,'resources/')"/>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($contextprefix, '/' , $publicationid, '/modules/' , $restype, '/', $resource)"/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet> 
