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

<!-- $Id: search.xsl,v 1.4 2004/03/13 12:31:34 gregor Exp $ -->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:atom="http://purl.org/atom/ns#"
>

<xsl:param name="serverName"/>
<xsl:param name="serverPort"/>
<xsl:param name="contextPath"/>
<xsl:param name="publicationId"/>

<xsl:template match="/">
<search-results xmlns="http://purl.org/atom/ns#">
  <xsl:apply-templates select="atom:feed" />
</search-results>
</xsl:template>

<xsl:template match="atom:feed">
<xsl:for-each select="atom:entry">
  <entry>
    <title><xsl:value-of select="atom:title"/></title>
    <id>http://<xsl:value-of select="$serverName"/>:<xsl:value-of select="$serverPort"/><xsl:value-of select="$contextPath"/>/<xsl:value-of select="$publicationId"/>/atomapi/entries/<xsl:value-of select="atom:id"/>/index.xml</id>
  </entry>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
