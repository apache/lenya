<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: opml2site.xsl,v 1.2 2004/03/13 12:42:07 gregor Exp $ -->

<!--
    Description: Converts OPML files (http://www.opml.org) to the forrest site format
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output indent="yes"/>    

<!-- ignore head part of OPML, and some attributes -->
<xsl:template match="head"/>
<xsl:template match="@type"/>
<xsl:template match="@version"/>

<xsl:template match="body">
  <xsl:apply-templates select="@*|node()"/>
</xsl:template>    

<xsl:template match="opml">
  <xsl:element name="site" namespace="http://apache.org/forrest/linkmap/1.0">
    <xsl:attribute name="href"/>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:element>
</xsl:template>    
    
<xsl:template match="*">
  <xsl:element name="node-{count(preceding::*) + count(ancestor::*)}" namespace="http://apache.org/forrest/linkmap/1.0">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:element>
</xsl:template>    

<xsl:template match="@text">
  <xsl:attribute name="label"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>
    
<xsl:template match="@url">
  <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>
    
</xsl:stylesheet> 
