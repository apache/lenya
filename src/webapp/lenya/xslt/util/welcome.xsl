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

<!-- $Id: welcome.xsl,v 1.14 2004/03/13 12:42:09 gregor Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    >

<xsl:template match="lenya:lenya">
  <page:page>
    
  <page:title>Apache Lenya - Content Management System</page:title>
  <page:body>
    <xsl:apply-templates select="xhtml:div[@class = 'lenya-frontpage']"/>
    <xsl:apply-templates select="lenya:publications"/>
  </page:body>
  </page:page>
</xsl:template>

<xsl:template match="lenya:publications">
<div class="lenya-sidebar">
<div class="lenya-sidebar-heading">Publications</div>
<xsl:for-each select="lenya:publication">
  <xsl:choose>
    <xsl:when test="lenya:XPSEXCEPTION">
<!--
      <div class="lenya-publication-item">
        <font color="red">Exception:</font>
        (publication id = <xsl:value-of select="@pid"/>) <xsl:value-of select="lenya:XPSEXCEPTION"/>
      </div>
-->
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="lenya:publication/@lenya:show = 'false'">
          <!-- do not list this publication. Might be a "template" publication -->
        </xsl:when>
        <xsl:otherwise>
          <div class="lenya-publication-item">
            <a href="{@pid}/introduction.html">
            <xsl:value-of select="lenya:publication/lenya:name"/></a>
          </div>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
</div>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>

</xsl:stylesheet>
