<?xml version="1.0" encoding="UTF-8" ?>
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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  
  <!-- includes the default CSS stylesheet -->
  <xsl:template name="include-css">
    <link rel="stylesheet" type="text/css" href="/modules/gui/css/default.css" media="screen"/>
  </xsl:template>
  
  <!-- prints a list of $separator-separated strings -->
  <xsl:template name="print-list">
    <xsl:param name="list-string"/>
    <xsl:param name="separator" select="','"/>
    <xsl:choose>
      <xsl:when test="contains($list-string, $separator)">
        <li><xsl:value-of select="substring-before($list-string, $separator)"/></li>
        <xsl:call-template name="print-list">
          <xsl:with-param name="list-string" select="substring-after($list-string, $separator)"/>
          <xsl:with-param name="separator" select="$separator"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <li><xsl:value-of select="$list-string"/></li>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- prints a list of $separator-separated strings -->
  <xsl:template name="print-list-simple">
    <xsl:param name="list-string"/>
    <xsl:param name="separator" select="','"/>
    
    <xsl:choose>
      <xsl:when test="contains($list-string, $separator)">
        <xsl:value-of select="substring-before($list-string, $separator)"/><br />
        <xsl:call-template name="print-list-simple">
          <xsl:with-param name="list-string" select="substring-after($list-string, $separator)"/>
          <xsl:with-param name="separator" select="$separator"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$list-string"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
</xsl:stylesheet>
