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

  <xsl:param name="height"/>

  <!-- prevent users from causing memory overflows: -->
  <xsl:variable name="maxHeight" select="1024"/>
  
  <!-- the width:height-ratio -->
  <xsl:variable name="ratio" select="1.5"/>
  
  <!-- 
     scales an svg to height $height.
     this is done by surrounding the image with a new <svg/> element with the desired height and 
     proportional width, and a viewBox whose dimensions are taken from the original image.
  -->  
  <xsl:template match="/svg:svg">
    <xsl:choose>
      <xsl:when test="number($height) &gt; 0 and number($height) &lt;= $maxHeight">
        <svg:svg viewBox="0 0 {@width} {@height}" width="{$height * $ratio}" height="{$height}"
          preserveAspectRatio="none">
          <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
          </xsl:copy> 
        </svg:svg>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Wow. You requested an image height of <xsl:value-of select="$height"/>. No way José. Go DoS someone else.
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*|node()" name="identity">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- batik seems to choke on comment elements. workaround: -->
  <xsl:template match="svg:comment"/>

</xsl:stylesheet>
