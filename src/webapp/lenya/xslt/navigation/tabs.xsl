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

<!-- $Id: tabs.xsl,v 1.12 2004/03/13 12:42:05 gregor Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >


<xsl:template match="nav:site">

  <div id="tabs">

    <xsl:call-template name="pre-separator"/>
    <xsl:for-each select="nav:node">
      <xsl:if test="position() &gt; 1">
        <xsl:call-template name="separator"/>
      </xsl:if>
      
      <xsl:choose>
        <xsl:when test="descendant-or-self::nav:node[@current = 'true']">
          <xsl:call-template name="tab-selected"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="tab"/>
        </xsl:otherwise>
      </xsl:choose>
        
    </xsl:for-each>
    <xsl:call-template name="post-separator"/>
  </div>
</xsl:template>


<xsl:template name="tab">
  <span class="tab"><xsl:call-template name="label"/></span>
</xsl:template>


<xsl:template name="tab-selected">
  <span class="tab-selected"><xsl:call-template name="label"/></span>
</xsl:template>


<xsl:template name="label">
  <a href="{@href}"><xsl:apply-templates select="nav:label"/></a>
</xsl:template>


<xsl:template match="nav:label">
  <xsl:value-of select="."/>
</xsl:template>


<xsl:template name="pre-separator">
</xsl:template>


<xsl:template name="separator">
   <xsl:text>&#160;</xsl:text>
</xsl:template>


<xsl:template name="post-separator">
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
