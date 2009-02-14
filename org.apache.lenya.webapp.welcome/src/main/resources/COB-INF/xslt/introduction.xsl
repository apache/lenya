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

<!-- $Id: publication.xsl 473861 2006-11-12 03:51:14Z gregor $ -->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.1"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  
  <xsl:key name="workflows" match="lenya:resource-type" use="@workflow"/>   
  
  <xsl:template match="/*">
    <page:page>
      <page:title><a href="../index.html">Apache Lenya</a> &#187;
        <i18n:translate>
          <i18n:text>... Publication</i18n:text>
          <i18n:param><xsl:value-of select="lenya:publication/lenya:name"/></i18n:param>
        </i18n:translate>
      </page:title>
      <page:head/>
      <page:body>
        <div class="lenya-sidebar">
          <h2><i18n:text>This Publication</i18n:text></h2>
          <ul>
            <li><a href="authoring/"><i18n:text>Login as Editor</i18n:text></a></li>
            <li><a href="live/"><i18n:text>Live View</i18n:text></a></li>
          </ul>
          <h2><i18n:text>Links</i18n:text></h2>
          <ul>
            <li><a href="../index.html"><i18n:text>Other Publications</i18n:text></a></li>
            <li><a href="http://lenya.apache.org/docs/index.html"><i18n:text>Documentation</i18n:text></a></li>
            <li><a href="http://wiki.apache.org/lenya">Wiki</a></li>
          </ul>
        </div>
        
        <div class="lenya-frontpage">
          <h2><i18n:text>Publication properties</i18n:text>:</h2>
          <xsl:apply-templates select="lenya:publication"/>
          <xsl:apply-templates select="page:page"/>
          
        </div>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="lenya:publication">
    <table class="lenya-table-list-noborder">
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Name</i18n:text></th>
        <td class="border"><xsl:value-of select="lenya:name"/></td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Revision</i18n:text></th>
        <td class="border"><xsl:value-of select="lenya:version"/></td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Description</i18n:text></th>
        <td class="border"><xsl:value-of select="lenya:description"/></td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Available languages</i18n:text></th>
        <td class="border">
          <xsl:for-each select="lenya:languages/lenya:language">
            <xsl:choose>
              <xsl:when test="@default">
                <strong><xsl:value-of select="."/></strong>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="."/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>
        </td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Available resource types</i18n:text></th>
        <td class="border">
          <xsl:if test="lenya:templates/lenya:template">
            <i18n:text>Inherited: see template publication</i18n:text><br/>
          </xsl:if>
          <xsl:for-each 
            select="lenya:resource-types/lenya:resource-type[generate-id(.)=generate-id(key('workflows', @workflow)[1])]"
            >
            <xsl:sort select="@workflow"/>
            <xsl:for-each select="key('workflows', @workflow)">
              <xsl:sort select="@name"/>
              <xsl:value-of select="@name"/>
              <xsl:if test="position() != last()">
                <xsl:text>, </xsl:text>
              </xsl:if>
            </xsl:for-each>
            <xsl:text> (Workflow: </xsl:text>
            <xsl:value-of select="@workflow"/>
            <xsl:text>)</xsl:text>
            <xsl:if test="position() != last()">
              <xsl:text>;</xsl:text><br />
            </xsl:if>
          </xsl:for-each>
        </td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Required Lenya version</i18n:text></th>
        <td class="border"><xsl:value-of select="lenya:lenya-version"/></td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Required Lenya revision</i18n:text></th>
        <td class="border"><xsl:value-of select="lenya:lenya-revision"/></td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Required Cocoon version</i18n:text></th>
        <td class="border"><xsl:value-of select="lenya:cocoon-version"/></td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Modules used</i18n:text></th>
        <td class="border">
          <xsl:if test="lenya:templates/lenya:template">
            <i18n:text>Inherited: see template publication</i18n:text><br/>
          </xsl:if>
          <xsl:for-each select="lenya:modules/lenya:module">
            <xsl:value-of select="@name"/>
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>
        </td>
      </tr>
      <tr>
        <th style="white-space: nowrap;"><i18n:text>Template used</i18n:text></th>
        <td class="border">
          <xsl:choose>
            <xsl:when test="lenya:template">
              <a href="../{lenya:template/@id}/introduction.html">
                <xsl:value-of select="lenya:template/@id"/>
              </a>
            </xsl:when>
            <xsl:otherwise>
              <i18n:text>none</i18n:text>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="page:page">
    <!-- FIXME: aggregate-fallback seems to return the last document twice.
      The position predicate is just a quick workaround and needs to be fixed! -->
    <xsl:for-each select="page:body[position() &lt; last()]">
      <xsl:apply-templates select="*"/>
    </xsl:for-each>
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
