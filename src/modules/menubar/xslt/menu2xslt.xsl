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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
  xmlns:menu="http://apache.org/cocoon/lenya/menubar/1.0"
  exclude-result-prefixes="menu"
  >
  
  <xsl:param name="contextprefix"/>
  <xsl:param name="publicationid"/>
  <xsl:param name="area"/>
  <xsl:param name="documenturl"/>
  
  <xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>
  
  <xsl:template match="/">
    
    <xso:stylesheet version="1.0"
      xmlns:xhtml="http://www.w3.org/1999/xhtml"
      exclude-result-prefixes="xhtml">
      
      <xso:output method="xml" indent="yes"/>
      
      <xsl:if test="$area != 'live'">
        
        <xso:template match="/">
          <html>
            <head>
              <xso:call-template name="title"/>
              <script src="/modules/menubar/javascript/menu.js" type="text/javascript"/>
              <link href="/modules/gui/css/menu.css" rel="stylesheet" type="text/css"/>
              <link rel="icon" href="/favicon.ico" type="image/ico"/>
              <xso:apply-templates select="xhtml:html/xhtml:head/*[local-name() != 'title']"/>
               <!-- internet explorer 6 switches to quirks mode with the xml declaration
                   the quirks layout inconsistencies are fixed in a ie6 only stylesheet -->
              <xso:variable name="contextprefix">
                <xsl:value-of select="$contextprefix"/>
              </xso:variable>
              <xso:comment>[if IE 6]<![CDATA[>]]>&lt;link rel="stylesheet" type="text/css" href="<xso:value-of select="$contextprefix"/>/lenya/css/ie6hacksonly.css" /><![CDATA[<![endif]]]></xso:comment>
            </head>
            <body>
              <xso:apply-templates select="xhtml:html/xhtml:body/@*"/>
              <xsl:apply-templates select="xhtml:div/xhtml:div[@id = 'lenya-logo']"/>
              <xsl:apply-templates select="xhtml:div/xhtml:div[@id = 'lenya-menus']"/>
              <div id="lenya-cmsbody">
                <xso:apply-templates select="xhtml:html/xhtml:body/node()"/>
              </div>
              <script type="text/javascript"> initialize(); </script>
            </body>
          </html>
        </xso:template>
        
        <xso:template name="title">
          <title>
            Apache Lenya |
            <xsl:value-of select="$publicationid"/> |
            <xsl:value-of select="$area"/> |
            <xsl:value-of select="$documenturl"/> |
            <xso:value-of select="xhtml:html/xhtml:head/xhtml:title"/>
          </title>
        </xso:template>
        
      </xsl:if>
      
      <xso:template match="xhtml:script">
        <xso:copy>
          <xso:apply-templates select="@*|node()"/>
          <xso:if test="not(.//text())"><xso:text>&#160;</xso:text></xso:if>
        </xso:copy>
      </xso:template>
      
      <xso:template match="xhtml:*">
        <xso:element name="{string('{local-name()}')}">
          <xso:apply-templates select="@*|node()"/>
        </xso:element>
      </xso:template>
      
      
      <xso:template match="@*|node()">
        <xso:copy>
          <xso:apply-templates select="@*|node()"/>
        </xso:copy>
      </xso:template>
      
    </xso:stylesheet>
    
  </xsl:template>
  
  
  <xsl:template match="xhtml:*">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>
  
  
  <!-- add only first message as title attribute --> 
  <xsl:template match="menu:message[preceding-sibling::menu:message]"/>
  
  <xsl:template match="menu:message">
    <xsl:attribute name="title">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet>
