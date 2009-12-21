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
  xmlns:svg="http://www.w3.org/2000/svg"
  xmlns="http://www.w3.org/2000/svg"
  exclude-result-prefixes="svg"
>

  <!-- 
     Adds a border and a gradient to the image.
  -->  
  <xsl:template match="/svg:svg">
    
    <!-- translating to pixels -->
    <xsl:variable name="width" select="ceiling(@width)"/>
    <xsl:variable name="height" select="ceiling(@height)"/>
    
    <svg:svg width="{$width + 2}" height="{$height + 2}">
      <defs>
        <linearGradient id="light" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" style="stop-color: #FFFFFF; stop-opacity: 1"/>
          <stop offset="40%" style="stop-color: #FFFFFF; stop-opacity: 0"/>
          <stop offset="60%" style="stop-color: #000000; stop-opacity: 0"/>
          <stop offset="100%" style="stop-color: #000000; stop-opacity: 1"/>
        </linearGradient>
      </defs>
      
      <rect x="0" y="0" width="{$width + 2}" height="{$height + 2}" style="fill: #000000;"/>
      
      <svg:svg x="1" y="1" width="{$width}" height="{$height}"
        viewBox="{@viewBox}" preserveAspectRatio="none">
        <xsl:apply-templates select="node()"/>
      </svg:svg>
      <rect x="1" y="1" width="{$width}" height="{$height}" style="fill: url(#light); opacity: 0.5"/>
    </svg:svg>
  </xsl:template>

  <xsl:template match="@*|node()" name="identity">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- batik seems to choke on comment elements. workaround: -->
  <xsl:template match="svg:comment"/>

</xsl:stylesheet>
