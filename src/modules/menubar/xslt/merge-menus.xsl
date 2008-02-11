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

<!-- $Id: menu2xhtml.xsl 178289 2005-05-24 21:30:01Z andreas $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:template match="/menu:menu">
    <xsl:copy>
      <xsl:apply-templates select="*"/>
    </xsl:copy>
  </xsl:template>
  
  
  <!--
    Apply the first <menus>.
  -->
  <xsl:template match="menu:menus[not(preceding-sibling::menu:menus)]">
    <xsl:copy>
      <xsl:apply-templates select="menu:menu"/>
    </xsl:copy>
  </xsl:template>
  
  
  <!--
    Apply the <menu> children of the first <menus>.
    Insert items of module menus (which have preceding siblings)
    before the items of this menu.
  -->
  <xsl:template match="/menu:menu/menu:menus[1]/menu:menu">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="/menu:menu/menu:menus[preceding-sibling::menu:menus]/menu:menu[@name = current()/@name]/*"/>
      <xsl:apply-templates select="*"/>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="menu:menus[preceding-sibling::menu:menus]"/>
  
  
  <!--
    Don't apply the <menu> children of the subsequent <block>s with a certain ID.
  -->
  <xsl:template match="menu:block">
    <xsl:if test="not(preceding::menu:block[@id = current()/@id])">
      <xsl:copy>
        <xsl:apply-templates select="@*|menu:item"/>
        <xsl:apply-templates select="following::menu:block[@id = current()/@id]/menu:item"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
