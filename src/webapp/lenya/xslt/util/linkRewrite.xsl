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
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xhtml"
  >

  <xsl:param name="urlbefore"/>
  <xsl:param name="urlafter"/>
  <xsl:param name="language"/>

  <!-- base url for rewriting links to children of moved node -->

  <xsl:variable name="baseurlbefore">
    <xsl:choose>
      <xsl:when test="$language != ''">
        <xsl:value-of select="concat(substring-before($urlbefore, concat('_', $language, '.html')), '/')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat(substring-before($urlbefore, '.html'), '/')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

   <xsl:variable name="baseurlafter">
    <xsl:choose>
      <xsl:when test="$language != ''">
        <xsl:value-of select="concat(substring-before($urlafter, concat('_', $language, '.html')), '/')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat(substring-before($urlafter, '.html'), '/')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>


  <xsl:template match="xhtml:a">
    <xsl:variable name="href">
      <xsl:choose>
	<xsl:when test="@href=$urlbefore">
	  <xsl:value-of select="$urlafter"/>
	</xsl:when>
        <xsl:when test="contains(@href, $baseurlbefore)">
          <xsl:value-of select="$baseurlafter"/><xsl:value-of select="substring-after(@href, $baseurlbefore)"/>
        </xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="@href"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <a href="{$href}">
      <xsl:apply-templates select="@*[not(local-name()='href')]|node()"/>
    </a>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
