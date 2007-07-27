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

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    >

  <xsl:import href="fallback://lenya/modules/kupu/resources/kupu/apache-lenya/lenya/kupusave.xsl"/>
  <xsl:import href="fallback://lenya/modules/bxe/xslt/change-object-path-back.xsl"/>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- Prevents empty titles from interfering with IE during editing -->
  <xsl:template match="xhtml:title">
    <xsl:param name="title" select="." />
    <xsl:choose>
      <xsl:when test="string-length($title) = 0">
        <xhtml:title>&#160;</xhtml:title>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates />
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>



  <!-- Remove tag that disables Firefox spell check in FCK -->
  <xsl:template match="xhtml:body[@spellcheck]" >
    <xsl:copy>
       <xsl:apply-templates select="@*[name()!='spellcheck']" />
       <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <!-- Unsupported by the schema -->
  <xsl:template match="@shape|@target|xhtml:u">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="xhtml:b">
    <xhtml:strong>
      <xsl:apply-templates />
    </xhtml:strong>
  </xsl:template>
  
  <xsl:template match="xhtml:i">
    <xhtml:em>
      <xsl:apply-templates />
    </xhtml:em>
  </xsl:template>
  
  <!-- this template converts the img tag to object 
    for more, see http://www.xml.com/pub/a/2003/07/02/dive.html -->
  <xsl:template match="xhtml:img">
    <object>
      <xsl:attribute name="data">
        <!-- strip the nodeid out again (it is not saved in the object @data) -->
        <xsl:choose>
          <xsl:when test="starts-with(@src, '/')">
            <xsl:value-of select="@src"/>              
          </xsl:when>
          <xsl:when test="starts-with(@src, 'http:') or starts-with(@src, 'https:')">
            <xsl:value-of select="@src"/>              
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="substring-after(@src, '/')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="title">
        <xsl:value-of select="@alt"/>
      </xsl:attribute>
      <!-- use the rarely-used ismap to roundtrip the type attribute for the object element -->
      <xsl:attribute name="type">
        <xsl:value-of select="@ismap"/>
      </xsl:attribute>
      <xsl:if test="string(@height)">
        <xsl:attribute name="height">
          <xsl:value-of select="@height"/>
        </xsl:attribute>
      </xsl:if> 
      <xsl:if test="string(@width)">
        <xsl:attribute name="width">
          <xsl:value-of select="@width"/>
        </xsl:attribute>
      </xsl:if>         
    </object>
  </xsl:template>
  
  <xsl:template match="lenya:asset">
    <p>
      <a href="{@src}" class="lenya.asset"><xsl:apply-templates/></a>
    </p>
  </xsl:template>

</xsl:stylesheet> 
