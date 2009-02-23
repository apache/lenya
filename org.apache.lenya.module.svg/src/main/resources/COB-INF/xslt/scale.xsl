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
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:media="http://apache.org/lenya/metadata/media/1.0">
  
  <xsl:param name="url"/>
  <xsl:param name="height"/>
  <xsl:param name="width"/>
  
  <xsl:variable name="imgWidth" select="/svg:svg/media:width"/>
  <xsl:variable name="imgHeight" select="/svg:svg/media:height"/>
  
  <xsl:variable name="targetWidth">
    <xsl:choose>
      <xsl:when test="$width"><xsl:value-of select="$width"/></xsl:when>
      <xsl:when test="$height"><xsl:value-of select="($height div $imgHeight) * $imgWidth"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$imgWidth"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:variable name="targetHeight">
    <xsl:choose>
      <xsl:when test="$height"><xsl:value-of select="$height"/></xsl:when>
      <xsl:when test="$width"><xsl:value-of select="($width div $imgWidth) * $imgHeight"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$imgHeight"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:template match="/svg:svg">
    <svg width="{$targetWidth}" height="{$targetHeight}" version="1.1">
      <image x="0" y="0" width="{$targetWidth}" height="{$targetHeight}" xlink:href="{$url}" preserveAspectRatio="none"/>        
    </svg>
  </xsl:template>
  
</xsl:stylesheet>