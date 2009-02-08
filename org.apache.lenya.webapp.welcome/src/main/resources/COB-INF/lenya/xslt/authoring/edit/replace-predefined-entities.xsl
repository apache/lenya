<?xml version="1.0"?>
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
>

<xsl:output indent="no"/>

<!-- FIXME: CDATA is also being modified by replace-predefined-entities.xsl, which actually shouldn't -->


<xsl:template match="text()">
  <xsl:call-template name="search-and-replace">
    <xsl:with-param name="string" select="."/>
  </xsl:call-template>
</xsl:template>



<xsl:template match="node()|@*" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="node()|@*"/>
  </xsl:copy>
</xsl:template>




<xsl:template name="search-and-replace">
<xsl:param name="string"/>

<xsl:choose>
<xsl:when test="contains($string, '&lt;')">
  <xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-before($string, '&lt;')"/></xsl:call-template>&amp;lt;<xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-after($string, '&lt;')"/></xsl:call-template>
</xsl:when>
<xsl:when test="contains($string, '&gt;')">
  <xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-before($string, '&gt;')"/></xsl:call-template>&amp;gt;<xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-after($string, '&gt;')"/></xsl:call-template>
</xsl:when>
<xsl:when test="contains($string, '&amp;')">
  <xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-before($string, '&amp;')"/></xsl:call-template>&amp;amp;<xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-after($string, '&amp;')"/></xsl:call-template>
</xsl:when>
<!-- FIXME: &quot; and &apos; -->
<xsl:otherwise>
  <xsl:value-of select="$string"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template> 

</xsl:stylesheet>
