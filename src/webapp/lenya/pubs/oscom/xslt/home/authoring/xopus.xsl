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

<!-- $Id: xopus.xsl,v 1.3 2004/03/13 12:42:08 gregor Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="text()">
  <xsl:copy />
</xsl:template>

<xsl:template match="home">
<div>
<table border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <font face="verdana">
        <xsl:apply-templates select="about/p"/>
      </font>
    </td>
  </tr>
  <tr>
    <td>
      <font face="verdana">
        <xsl:apply-templates select="features"/>
      </font>
    </td>
  </tr>
</table>
</div>
</xsl:template>

<xsl:template match="features">
  <xsl:apply-templates select="feature"/>
</xsl:template>

<xsl:template match="feature">
  <xsl:apply-templates select="title"/>
  <xsl:apply-templates select="p"/>
</xsl:template>

<xsl:template match="title">
  <h3><xsl:apply-templates/></h3>
</xsl:template>

<xsl:template match="p">
<p>
  <xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="a">
<a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>

</xsl:stylesheet>
