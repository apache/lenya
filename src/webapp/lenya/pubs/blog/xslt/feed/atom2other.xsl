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
<xsl:stylesheet xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:echo="http://purl.org/atom/ns#" xmlns:rss="http://purl.org/rss/" xmlns:ent="http://www.purl.org/NET/ENT/1.0/" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:param name="type"/>
  <xsl:param name="url"/>
  <xsl:param name="area"/>
  
  <xsl:template match="/echo:feed">
    <xsl:choose>
      <xsl:when test="$type='rss'">
        <rss:rss version="2.0">
          <rss:channel>
            <rss:title>
              <xsl:value-of select="./echo:title/text()"/>
            </rss:title>
            <rss:link>
              <xsl:value-of select="./echo:link/@href"/>
            </rss:link>
            <rss:description>
              <xsl:value-of select="./echo:title/text()"/>
            </rss:description>
            <rss:language>en-us</rss:language>
            <rss:pubDate>
              <xsl:value-of select="./echo:issued/text()"/>
            </rss:pubDate>
            <rss:lastBuildDate>
              <xsl:value-of select="./echo:modified/text()"/>
            </rss:lastBuildDate>
            <xsl:apply-templates select="echo:entry"/>
          </rss:channel>
        </rss:rss>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="echo:entry">
    <rss:item>
      <rss:title>
        <xsl:value-of select="echo:title"/>
      </rss:title>
      <rss:link>
        <xsl:choose>
          <xsl:when test="echo:link/@href='self'">
            <xsl:value-of select="$url"/>/<xsl:value-of select="$area"/>/<xsl:value-of select="echo:id"/>/index.html
          </xsl:when>	
          <xsl:otherwise>
            <xsl:value-of select="echo:link/@href"/>
          </xsl:otherwise>
        </xsl:choose>
      </rss:link>
      <rss:pubDate>
        <xsl:value-of select="echo:issued"/>
      </rss:pubDate>
      <rss:description>
        <xsl:value-of select="echo:content"/>
      </rss:description>
      <rss:guid isPermaLink="true">
        <xsl:value-of select="$url"/>/<xsl:value-of select="$area"/>/<xsl:value-of select="echo:id"/>/index.html
      </rss:guid>
    </rss:item>
  </xsl:template>
  <xsl:template match="@*|node()">
  </xsl:template>
</xsl:stylesheet>
