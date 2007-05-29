<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: sitetree2nav.xsl 153044 2005-02-09 09:32:59Z michi $ -->

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tree="http://apache.org/cocoon/lenya/sitetree/1.0"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    exclude-result-prefixes="tree">

  <xsl:param name="chosenlanguage"/>
  <xsl:param name="defaultlanguage"/>
  <xsl:param name="currentPath"/>


  <xsl:template match="tree:fragment">
    <nav:fragment>
      <xsl:copy-of select="@*"/>
      <xsl:choose>
        <xsl:when test="@base">
          <xsl:apply-templates>
            <xsl:with-param name="parentPath" select="@base"/>
          </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </nav:fragment>
  </xsl:template>


  <xsl:template match="tree:site">
    <nav:site>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </nav:site>
  </xsl:template>


<!--
Resolves the existing language of a node, preferrably
the default language.
-->
  <xsl:template name="resolve-existing-language">
    <xsl:choose>
      <xsl:when test="tree:label[lang($chosenlanguage)]">
        <xsl:value-of select="$chosenlanguage"/>
      </xsl:when>
      <xsl:when test="tree:label[lang($defaultlanguage)]">
        <xsl:value-of select="$defaultlanguage"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="tree:label/@xml:lang"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


<!--
Apply nodes recursively
-->
  <xsl:template match="tree:node[not(@visible = 'false')]">

    <!-- base path of parent node -->
    <xsl:param name="parentPath" select="''"/>

    <xsl:variable name="existinglanguage">
      <xsl:call-template name="resolve-existing-language"/>
    </xsl:variable>

    <nav:node>

      <xsl:copy-of select="@id"/>
      <xsl:copy-of select="@visibleinnav"/>
      <xsl:copy-of select="@protected"/>
      <xsl:copy-of select="@folder"/>
      <xsl:copy-of select="@uuid"/>

      <!-- base path - for all nodes -->

      <xsl:variable name="path" select="concat($parentPath, '/', @id)"/>

      <!-- suffix - only when @href is not present -->

      <xsl:variable name="extensionParam">
        <xsl:if test="@suffix">
          <xsl:text>?uuid2url.extension=</xsl:text>
          <xsl:value-of select="@suffix"/>
        </xsl:if>
      </xsl:variable>

      <xsl:if test="$currentPath = $path">
        <xsl:attribute name="current">true</xsl:attribute>
      </xsl:if>

      <xsl:attribute name="href">
        <xsl:choose>
          <xsl:when test="@href">
            <xsl:value-of select="@href"/>
          </xsl:when>
          <xsl:when test="@uuid">
            <xsl:text>lenya-document:</xsl:text>
            <xsl:value-of select="@uuid"/>
            <xsl:value-of select="$extensionParam"/>
          </xsl:when>
        </xsl:choose>
      </xsl:attribute>

      <xsl:if test="@mimetype">
        <xsl:attribute name="mimetype">
          <xsl:value-of select="@mimetype"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="tree:label[lang($existinglanguage)]">
          <xsl:apply-templates select="tree:label[lang($existinglanguage)]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="tree:label[1]"/>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:apply-templates select="tree:node">
        <xsl:with-param name="parentPath" select="$path"/>
      </xsl:apply-templates>

    </nav:node>
  </xsl:template>


  <xsl:template match="tree:label">
    <nav:label>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </nav:label>
  </xsl:template>


  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>
