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
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xhtml dcterms"
 >

  <xsl:param name="creator"/>
  <xsl:param name="title"/>
  <xsl:param name="description"/>
  <xsl:param name="subject"/>
  <xsl:param name="language"/>
  <xsl:param name="publisher"/>
  <xsl:param name="date"/>
  <xsl:param name="rights"/>

  <xsl:template match="dc:creator[$creator!='']">
    <dc:creator>
      <xsl:value-of select="$creator"/>
    </dc:creator>
  </xsl:template>  

  <xsl:template match="dc:title[$title!='']">
    <dc:title>
      <xsl:value-of select="$title"/>
    </dc:title>
  </xsl:template>  

  <xsl:template match="dc:description[$description!='']">
    <dc:description>
      <xsl:value-of select="$description"/>
    </dc:description>
  </xsl:template>  

  <xsl:template match="dc:subject[$subject!='']">
    <dc:subject>
      <xsl:value-of select="$subject"/>
    </dc:subject>
  </xsl:template>  

  <xsl:template match="dc:language[$language!='']">
    <dc:language>
      <xsl:value-of select="$language"/>
    </dc:language>
  </xsl:template>  

  <xsl:template match="dc:publisher[$publisher!='']">
    <dc:publisher>
      <xsl:value-of select="$publisher"/>
    </dc:publisher>
  </xsl:template>  

  <xsl:template match="dc:date[$date!='']">
    <dc:date>
      <xsl:value-of select="$date"/>
    </dc:date>
  </xsl:template>  

  <xsl:template match="dc:rights[$rights!='']">
    <dc:rights>
      <xsl:value-of select="$rights"/>
    </dc:rights>
  </xsl:template>  

  <!-- Identity transformation -->
  <xsl:template match="@*|*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>  
  
</xsl:stylesheet>
