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

<!-- $Id: publishScreen.xsl,v 1.4 2004/03/13 12:42:13 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="publish-all">
<publish>
  <task-id>publish</task-id>
  <referer>/lenya/oscom/authoring/matrix/index.html</referer>
  <publication-id>oscom</publication-id>
  <current_username/>
  <context>/lenya</context>
  <prefix>/lenya/oscom</prefix>

<uris>
/lenya/oscom/live/matrix/index.html<xsl:for-each select="oscom/system">,/lenya/oscom/live/matrix/<xsl:value-of select="id"/>.html</xsl:for-each>
</uris>

<sources>
<xsl:for-each select="oscom/system">/matrix/<xsl:value-of select="id"/>.xml<xsl:if test="position() != last()">,</xsl:if></xsl:for-each>
</sources>
</publish>
</xsl:template>
 
</xsl:stylesheet>  
