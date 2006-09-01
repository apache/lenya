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

<!-- $Id: xhtml2xhtml.xsl 201776 2005-06-25 18:25:26Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    exclude-result-prefixes="xhtml lenya"
    >
<xsl:include href="fallback://lenya/modules/xhtml/xslt/helper-object.xsl"/>
<xsl:param name="rendertype" select="''"/>
<xsl:param name="nodeid"/>

<xsl:template match="/xhtml:html">
  <xsl:copy-of select="lenya:meta"/>
  <html>
    <body>
      <xsl:if test="$rendertype = 'edit'">
        <xsl:attribute name="bxe_xpath">/xhtml:html/xhtml:body</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="xhtml:body/node()"/>
    </body>
  </html>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

  <xsl:template name="substring-after-last">
    <xsl:param name="input"/>
    <xsl:param name="substr"/>
    <xsl:variable name="temp" select="substring-after($input, $substr)"/>
    <xsl:choose>
      <xsl:when test="$substr and contains($temp, $substr)">
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="$temp"/>
          <xsl:with-param name="susbtr" select="$substr"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$temp"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

 <xsl:template match="lenya:asset">
   <xsl:variable name="extent">
     <xsl:value-of select="@size"/>
   </xsl:variable>
   <xsl:variable name="suffix">
     <xsl:call-template name="substring-after-last">
       <xsl:with-param name="input" select="@src"/>
       <xsl:with-param name="substr">.</xsl:with-param>
     </xsl:call-template>
   </xsl:variable>
   <div class="asset">
       <xsl:text>&#160;</xsl:text>
       <a href="{@src}" title="{text()}">
         <xsl:value-of select="text()"/>
       </a>
       (<xsl:value-of select="number($extent)"/>KB)
   </div>
 </xsl:template>

 <xsl:template match="dc:metadata"/>
  
</xsl:stylesheet> 
