<?xml version="1.0"?>
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

<!-- $Id: relativize-linkmap.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->
    
<!--
Stylesheet which adds ..'s to @href attributes, to make the URIs relative to
some root.  Eg, given an 'absolutized' file (from absolutize-linkmap.xsl):

<site href="">
  <community href="community/">
    <faq href="community/faq.html">
      <how_can_I_help href="community/faq.html#help"/>
    </faq>
  </community>
</site>

if $path was 'community/', then '../' would be added to each href:

<site href="../">
  <community href="../community/">
    <faq href="../community/">
      <how_can_I_help href="../community/faq.html#help"/>
    </faq>
  </community>
</site>
-->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="path"/>

  <xsl:include href="dotdots.xsl"/>

  <!-- Path to site root, eg '../../' -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:template match="@href">
    <xsl:attribute name="href">
      <xsl:choose>
        <xsl:when test="starts-with(., 'http:') or starts-with(., 'https:')">
          <xsl:value-of select="."/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$root"/><xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
      </xsl:attribute>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
