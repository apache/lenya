<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.1"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
>

<xsl:template match="/*">
  <page:page>
    <page:title>Apache Lenya - Content Management System</page:title>
    <page:head>
      <xhtml:meta http-equiv="cache-control" content="no-cache"/>
      <xhtml:meta http-equiv="pragma" content="no-cache"/>
    </page:head>  
    <page:body>
      <xsl:apply-templates select="lenya:publications"/>
      <xsl:apply-templates select="page:page"/>
    </page:body>
  </page:page>
</xsl:template>

<xsl:template match="lenya:publications">
  <div class="lenya-sidebar">
    <h2><i18n:text>Publications</i18n:text></h2>
    <p>
      <a href="index.html?lenya.usecase=templating.createPublicationFromTemplate">
        <i18n:text>create-publication</i18n:text> &#187;
      </a>
    </p>
    <table class="lenya-table-list-noborder">
      <tr>
        <th><i18n:text>ID</i18n:text></th>
        <th><i18n:text>Name</i18n:text></th>
      </tr>
      <!-- do not list publications with @show="false" 
        (can be used to hide template publications) -->
      <xsl:for-each select="lenya:publication[not(@show) or @show != 'false']">
        <tr>
          <td>
            <a class="lenyaPubId">
              <xsl:attribute name="href"><xsl:value-of select="@dirname"/><xsl:text>/introduction.html</xsl:text></xsl:attribute>
              <xsl:attribute name="title"><xsl:value-of select="lenya:description"/></xsl:attribute>
              <xsl:value-of select="@dirname"/>
            </a>
          </td>
          <td>
            <span class="lenyaPubName"><xsl:value-of select="lenya:name"/></span>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </div>
</xsl:template>

<xsl:template match="page:page">
   <xsl:apply-templates select="page:body/*"/>
</xsl:template>

<!-- we are aggretating into a new context: 
     (another good reason to move to xhtml2) -->
<xsl:template match="xhtml:h1">
  <h2><xsl:apply-templates select="@*|node()"/></h2>
</xsl:template>
<xsl:template match="xhtml:h2">
  <h3><xsl:apply-templates select="@*|node()"/></h3>
</xsl:template>
<xsl:template match="xhtml:h3">
  <h4><xsl:apply-templates select="@*|node()"/></h4>
</xsl:template>
<xsl:template match="xhtml:h4">
  <h5><xsl:apply-templates select="@*|node()"/></h5>
</xsl:template>
<xsl:template match="xhtml:h5|xhtml:h6">
  <h6><xsl:apply-templates select="@*|node()"/></h6>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>

</xsl:stylesheet>
