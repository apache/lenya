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

<!-- $Id: breadcrumb.xsl 42703 2004-03-13 12:57:53Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="nav"
  >
  
  <xsl:template match="nav:site">
    <div id="breadcrumb">
      <xsl:apply-templates select="nav:node"/>
    </div>
  </xsl:template>
  
  
  <xsl:template match="nav:node">
    <xsl:if test="descendant-or-self::nav:node[@current = 'true']">
      <xsl:call-template name="separator"/>
      <xsl:call-template name="step"/>
      <xsl:apply-templates select="nav:node"/>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template name="step">
    <xsl:choose>
      <xsl:when test="@current = 'true'">
        <xsl:apply-templates select="nav:label"/>
      </xsl:when>
      <xsl:otherwise>
        <a href="{@href}"><xsl:apply-templates select="nav:label"/></a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="nav:label">
    <xsl:value-of select="."/>
  </xsl:template>
  
  
  <xsl:template name="separator">
    &#x00BB;
  </xsl:template>
  
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet> 
