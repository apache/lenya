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

<!-- $Id: menu.xsl 42703 2004-03-13 12:57:53Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >
    

<xsl:template match="nav:site">
  <div id="menu">
    <xsl:apply-templates select="nav:node"/>
  </div>
</xsl:template>

<xsl:template match="nav:node[@visibleinnav = 'false']"/>

<xsl:template match="nav:node">
  <xsl:choose>
    <xsl:when test="descendant-or-self::nav:node[@current = 'true']">
      <div class="menublock-selected-{count(ancestor-or-self::nav:node)}">
        <xsl:call-template name="item"/>
        <xsl:apply-templates select="nav:node"/>
      </div>
    </xsl:when>
    <xsl:otherwise>
      <div class="menublock-{count(ancestor-or-self::nav:node)}">
        <xsl:call-template name="item"/>
        <xsl:apply-templates select="nav:node"/>
      </div>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="item">
    <xsl:choose>
      <xsl:when test="@current = 'true'">
        <xsl:call-template name="item-selected"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="item-default"/>
      </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template name="item-default">
  <div class="menuitem-{count(ancestor-or-self::nav:node)}">
    <a href="{@href}"><xsl:apply-templates select="nav:label"/></a>
  </div>
</xsl:template>
    
    
<xsl:template name="item-selected">
  <div class="menuitem-selected-{count(ancestor-or-self::nav:node)}">
    <xsl:apply-templates select="nav:label"/>
  </div>
</xsl:template>


<xsl:template match="nav:label">
  <xsl:value-of select="."/>
</xsl:template>
    
    
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
