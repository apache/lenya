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

<!-- $Id: admin.xsl 42892 2004-04-24 21:04:39Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
>

<xsl:template match="dir:directory">
  <xsl:apply-templates select="dir:file/dir:xpath"/>
</xsl:template>

<xsl:template match="dir:xpath">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="crawler">
<h3>Crawler</h3>
Filename: <em><xsl:value-of select="../../@name"/></em>
<br/>
User-Agent: <em><xsl:value-of select="user-agent"/></em>
<br/>
Start crawling at: <em><xsl:value-of select="base-url/@href"/></em>
<br/>
Scope of crawling: <em><xsl:value-of select="scope-url/@href"/></em>
<br/>
Directory where documents will be dumped: <em><xsl:value-of select="htdocs-dump-dir/@src"/></em>
<br/>
List of all crawled URLs: <em><xsl:value-of select="uri-list/@src"/></em>
</xsl:template>

<xsl:template match="lucene">
<h3>Lucene</h3>
Filename: <em><xsl:value-of select="../../@name"/></em>
<br/>
Type of index update: <em><xsl:value-of select="update-index/@type"/></em>
<br/>
Directory of Documents to be indexed: <em><xsl:value-of select="htdocs-dump-dir/@src"/></em>
<br/>
Directory of Search Index: <em><xsl:value-of select="index-dir/@src"/></em>
<br/>
Indexer class: <em><xsl:value-of select="indexer/@class"/></em>
</xsl:template>

</xsl:stylesheet>
