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

<!-- $Id: site2book.xsl,v 1.2 2004/03/13 12:42:09 gregor Exp $ -->

<!--
Stylesheet for generating book.xml from a suitably hierarchical site.xml file.
The project info is currently hardcoded, but since it isn't used anyway that
isn't a major problem.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="http://apache.org/forrest/linkmap/1.0" exclude-result-prefixes="f">

  <xsl:param name="path"/>
  <xsl:output doctype-system="book-cocoon-v10.dtd" doctype-public="-//APACHE//DTD Cocoon Documentation Book V1.0//EN"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="f:site">
    <book software="Forrest"
      title="Apache Forrest"
      copyright="2002 The Apache Software Foundation">
      <xsl:apply-templates/>
    </book>
  </xsl:template>

  <xsl:template match="*/*">
    <xsl:choose>
      <xsl:when test="contains(@href, '#') or not(@label)">
      </xsl:when>

      <xsl:when test="not(contains(@href, '#')) and count(*) = 0
      or count(*) > 0 and contains(*/@href, '#')">
        <menu-item label="{@label}" href="{@href}"/>
      </xsl:when>
      <xsl:when test="not(@href) or substring(@href, string-length(@href)) = '/'">
        <menu label="{@label}">
          <xsl:apply-templates/>
        </menu>
      </xsl:when>
      <xsl:otherwise>
        <unknown label="{@label}">
          <xsl:apply-templates/>
        </unknown>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
