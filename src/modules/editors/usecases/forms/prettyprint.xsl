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

<!-- $Id: oneform.xsl 42908 2004-04-26 14:57:25Z michi $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  >
  
  <xsl:param name="indent-space" select="'  '"/>
  
  
  <xsl:template name="linebreak">
<xsl:text disable-output-escaping="yes">
</xsl:text>
  </xsl:template>
  
  
  <xsl:template match="comment() | processing-instruction()">
    <xsl:param name="indent" select="''"/>
    <xsl:call-template name="linebreak"/>
    <xsl:value-of select="$indent"/>
    <xsl:copy />
  </xsl:template>
  
  
  <xsl:template match="text()">
    <xsl:param name="indent" select="''"/>
    <xsl:call-template name="linebreak"/>    
    <xsl:value-of select="$indent"/>
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>
  
  
  <xsl:template match="text()[normalize-space(.)='']"/>
  
  
  <xsl:template match="*">
    <xsl:param name="indent" select="''"/>
    <xsl:call-template name="linebreak"/>    
    <xsl:value-of select="$indent"/>
    <xsl:choose>
      <xsl:when test="*">
        <xsl:copy>
          <xsl:copy-of select="@*"/>
          <xsl:apply-templates select="*|text()">
            <xsl:with-param name="indent" select="concat($indent, $indent-space)"/>
          </xsl:apply-templates>
          <xsl:call-template name="linebreak"/>
          <xsl:value-of select="$indent"/>
        </xsl:copy>
      </xsl:when>       
      <xsl:otherwise>
        <xsl:copy-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
</xsl:stylesheet>
