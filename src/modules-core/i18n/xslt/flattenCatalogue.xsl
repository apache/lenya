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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1">
  
  
  <xsl:template match="*/i18n:catalogue">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="i18n:catalogue-wrapper">
    <xsl:if test="catalogue">
      <xsl:message terminate="yes">
        <xsl:text>The i18n catalogue of the </xsl:text><xsl:value-of select="@name"/> <xsl:text> </xsl:text>
        <xsl:text>is missing the i18n namespace (http://apache.org/cocoon/i18n/2.1).</xsl:text>
      </xsl:message>
    </xsl:if>
    <xsl:apply-templates select="i18n:catalogue"/>
  </xsl:template>
  
  
  <xsl:template match="i18n:message">
    <xsl:if test="not(preceding::i18n:message[@key = current()/@key])">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

  
  <xsl:template match="@*|node()|comment()" priority="-2">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  

</xsl:stylesheet>