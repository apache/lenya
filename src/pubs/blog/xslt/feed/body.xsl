<?xml version="1.0" encoding="UTF-8"?>
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
  xmlns:echo="http://purl.org/atom/ns#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
  xmlns="http://www.w3.org/1999/xhtml"
>

<xsl:import href="../entry/body.xsl"/>

<xsl:template match="echo:title">
  <xsl:param name="id"/>
  <xsl:variable name="lenyaEntryID">../../entries/<xsl:value-of select="$id"/>/index.html</xsl:variable>
  <div class="title"><a href="{$lenyaEntryID}"><xsl:apply-templates/></a></div>
</xsl:template>

<xsl:template name="permalink">
  <xsl:param name="id"/>
  <xsl:variable name="lenyaEntryID">../../entries/<xsl:value-of select="$id"/>/index.html</xsl:variable>
  <a href="{$lenyaEntryID}">Permalink</a>
</xsl:template>

</xsl:stylesheet>  
