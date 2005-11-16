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

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    >

  <xsl:import href="fallback://lenya/resources/kupu/apache-lenya/lenya/kupusave.xsl"/>
  <xsl:import href="fallback://lenya/xslt/bxe/change-object-path-back.xsl"/>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- Unsupported by the schema -->
  <xsl:template match="@shape|@target|xhtml:u">
    <xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="xhtml:b">
    <xhtml:strong>
      <xsl:apply-templates />
    </xhtml:strong>
  </xsl:template>
  
  <xsl:template match="xhtml:i">
    <xhtml:em>
      <xsl:apply-templates />
    </xhtml:em>
  </xsl:template>
  
</xsl:stylesheet> 
