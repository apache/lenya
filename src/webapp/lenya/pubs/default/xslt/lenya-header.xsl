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

<!-- $Id: lenya-header.xsl,v 1.8 2004/03/13 12:42:16 gregor Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="dc"
    >

<xsl:template match="dc:*[not(node())]"/>

<xsl:template match="dc:title">
  <h1>
    <xsl:if test="$rendertype = 'edit'">
      <xsl:attribute name="bxe_xpath">/html/lenya:meta/dc:title</xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/>
  </h1>
</xsl:template>

<xsl:template match="dc:description">
  <p class="abstract"><xsl:apply-templates/></p>
</xsl:template>

</xsl:stylesheet> 