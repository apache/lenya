<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
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

<!-- $Id: toc-main.xsl,v 1.2 2004/03/13 12:42:07 gregor Exp $ -->

<xsl:include href="lenya.org.xsl"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template name="body">
 <xsl:apply-templates select="/site/tree"/><br />
</xsl:template>

<xsl:template match="tree">
  <html>
  <head><title>Lenya Documentation</title></head>
  <body>
  <font face="verdana">
  <xsl:apply-templates select="branch" mode="trunk"/>
  </font>
  </body>
  </html>
</xsl:template>

<xsl:template match="branch" mode="trunk">
 <h1>New Lenya Documentation</h1>
 <h2>Table of Contents</h2>
 <ol>
  <xsl:apply-templates mode="tree">
    <xsl:with-param name="parentPath">/lenya/docs</xsl:with-param>
  </xsl:apply-templates>
 </ol>
</xsl:template>

<xsl:template match="branch" mode="tree">
 <xsl:param name="parentPath"/>
 <li>
 <xsl:choose>
   <xsl:when test="@doctype='Guide'">
     <a name="{@relURI}"/>
     <b><xsl:value-of select="@menuName"/></b>
   </xsl:when>
   <xsl:when test="@doctype='Empty'">
     <xsl:value-of select="@menuName"/>
   </xsl:when>
   <xsl:when test="@doctype='HTML'">
     <a href="{$parentPath}/{@relURI}/index.html"><xsl:value-of select="@menuName"/></a>
   </xsl:when>
   <xsl:when test="@doctype='XDoc'">
     <xsl:choose>
       <xsl:when test="@relURI!=''">
         <a href="xdocs/{@relURI}"><xsl:value-of select="@menuName"/></a>
       </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select="@menuName"/>
       </xsl:otherwise>
     </xsl:choose> 
   </xsl:when>
   <xsl:otherwise>
   [template match="branch" mode="tree"] EXCEPTION: No such doctype
   </xsl:otherwise>
 </xsl:choose>
 <ol>
  <xsl:apply-templates mode="tree">
    <xsl:with-param name="parentPath"><xsl:value-of select="$parentPath"/>/<xsl:value-of select="@relURI"/></xsl:with-param>
  </xsl:apply-templates>
 </ol>
   <xsl:if test="@doctype='Guide'">
   <br />&#160;
   </xsl:if>
 </li>
</xsl:template>

<xsl:template match="leaf" mode="tree">
 <xsl:param name="parentPath"/>
 <xsl:choose>
   <xsl:when test="@doctype='HTML'">
     <li><a>
     <xsl:attribute name="href">
     <xsl:if test="@relURI"><xsl:value-of select="$parentPath"/>/</xsl:if>
     <xsl:apply-templates select="@relURI"><xsl:with-param name="parentPath" select="$parentPath"/></xsl:apply-templates>
     <xsl:apply-templates select="@absURI"/>
     <xsl:apply-templates select="@URL"/>
     </xsl:attribute>
     <xsl:if test="@URL">
     <xsl:attribute name="target">_blank</xsl:attribute>
     </xsl:if>
     <xsl:value-of select="@menuName"/></a></li>
     <!--
     <li><a href="{$parentPath}/{@relURI}.html"><xsl:value-of select="@menuName"/></a></li>
     -->
   </xsl:when>
   <xsl:when test="@doctype='XDoc'">
     <li><a href="xdocs/{@relURI}"><xsl:value-of select="@menuName"/></a></li>
   </xsl:when>
   <xsl:when test="@doctype='Empty'">
     <li><xsl:value-of select="@menuName"/></li>
   </xsl:when>
   <xsl:when test="@doctype='Shared'">
     <li><a href="{@relURI}"><xsl:value-of select="@menuName"/></a></li>
   </xsl:when>
   <xsl:otherwise>
     ! INVALID DOCTYPE DEFINITION !
   </xsl:otherwise>
 </xsl:choose>
</xsl:template>

</xsl:stylesheet>

