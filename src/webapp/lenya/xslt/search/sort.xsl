<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id: sort.xsl,v 1.3 2004/03/13 12:42:20 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="hits">
    <hits total-hits="{../@total-hits}">
      <xsl:choose>
        <xsl:when test="../../search/sort-by='score'">
          <xsl:for-each select="hit">
            <xsl:sort data-type="number" order="descending" select="score"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </xsl:when>
        <xsl:when test="../../search/sort-by='title'">
          <xsl:for-each select="hit">
            <xsl:sort data-type="text" order="ascending" select="title"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="hit">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </hits>
  </xsl:template>

  <xsl:template match="* | @*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
