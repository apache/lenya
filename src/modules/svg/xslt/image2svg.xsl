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
  xmlns:xhtml="http://www.w3.org/1999/xhtml">
  
  <xsl:template match="xhtml:img[@height and @width]/@src">
    <xsl:attribute name="src">
      <!-- if the src attribute already contains a querystring, cut it off. otherwise, multiple
           querystrings can accumulate! 
           NB: substring-before returns the empty string if the needle is not found.
      -->
      <xsl:choose>
        <xsl:when test="contains(.,'?')">
           <xsl:value-of select="substring-before(.,'?')"/>
        </xsl:when>
        <xsl:otherwise>
           <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>?</xsl:text>
      <xsl:text>lenya.module=svg&amp;</xsl:text>
      <xsl:if test="string(../@height)">
        <xsl:text>height=</xsl:text><xsl:value-of select="../@height"/><xsl:text>&amp;</xsl:text>
      </xsl:if>
      <xsl:if test="string(../@width)">
        <xsl:text>width=</xsl:text><xsl:value-of select="../@width"/>
      </xsl:if>         
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>