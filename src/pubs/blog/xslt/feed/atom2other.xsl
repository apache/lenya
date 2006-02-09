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
<xsl:stylesheet xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:echo="http://purl.org/atom/ns#" xmlns:rss="http://purl.org/rss/" xmlns:ent="http://www.purl.org/NET/ENT/1.0/" version="1.0">
  <xsl:param name="type"/>
  <xsl:param name="url"/>
  <xsl:param name="area"/>
  
  <xsl:template match="/echo:feed">
    <xsl:choose>
      <xsl:when test="$type='rss'">
        <rss version="2.0">
          <channel>
            <title>
              <xsl:value-of select="./echo:title/text()"/>
            </title>
            <link>
              <xsl:value-of select="./echo:link/@href"/>
            </link>
            <description>
              <xsl:value-of select="./echo:title/text()"/>
            </description>
            <language>en-us</language>
            <pubDate>
              <xsl:value-of select="./echo:issued/text()"/>
            </pubDate>
            <lastBuildDate>
              <xsl:value-of select="./echo:modified/text()"/>
            </lastBuildDate>
            <xsl:apply-templates select="echo:entry"/>
          </channel>
        </rss>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="echo:entry">
    <item>
      <title>
        <xsl:value-of select="echo:title"/>
      </title>
      <link>
        <xsl:choose>
          <xsl:when test="echo:link/@href=''">
        <xsl:value-of select="concat($url, '/', $area, '/entries/', echo:id, '/index.html')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="echo:link/@href"/>
          </xsl:otherwise>
        </xsl:choose>
      </link>
      <pubDate>
        <xsl:value-of select="echo:issued"/>
      </pubDate>
      <description>
        <xsl:value-of select="echo:content"/>
      </description>
      <guid isPermaLink="true">
        <xsl:value-of select="concat($url, '/', $area, '/entries/', echo:id, '/index.html')"/>
      </guid>
    </item>
  </xsl:template>
  <xsl:template match="@*|node()">
  </xsl:template>
</xsl:stylesheet>
