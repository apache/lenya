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

<!-- $Id$ -->

<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xi="http://www.w3.org/2001/XInclude"
  >

<xsl:param name="year"/>
<xsl:param name="area"/>

<xsl:template match="/">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             

<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
<atom:feed xmlns:atom="http://purl.org/atom/ns#">
  <xi:include href="feeds/all/index.xml#xmlns(atom=http://purl.org/atom/ns#)xpointer(/atom:feed/atom:title)"/>
  <xi:include href="feeds/all/index.xml#xmlns(atom=http://purl.org/atom/ns#)xpointer(/atom:feed/atom:link)"/>
  <xi:include href="feeds/all/index.xml#xmlns(atom=http://purl.org/atom/ns#)xpointer(/atom:feed/atom:modified)"/>

  <xsl:variable name="month"><xsl:value-of select="@name"/></xsl:variable>
  <xsl:for-each select="dir:directory">
    <xsl:variable name="day"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:for-each select="dir:directory">
      <xsl:variable name="entryid"><xsl:value-of select="@name"/></xsl:variable>
      <entry>
        <xi:include href="content/{$area}/entries/{$year}/{$month}/{$day}/{$entryid}/index.xml"/>
      </entry>  
    </xsl:for-each>
  </xsl:for-each>
</atom:feed>
</xsl:template>

</xsl:stylesheet>
