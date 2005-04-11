<?xml version="1.0" encoding="iso-8859-1"?>
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

<!-- $Id: xhtml.xsl 42703 2004-03-13 12:57:53Z gregor $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
>

<xsl:import href="../../../../../../usecases/edit/forms/form.xsl"/>
<xsl:import href="xhtml-common.xsl"/>

<xsl:template match="xhtml:html">
  
<namespace prefix="xhtml" uri="http://www.w3.org/1999/xhtml"/>
<namespace prefix="lenya" uri="http://apache.org/cocoon/lenya/page-envelope/1.0"/>
<namespace prefix="dc" uri="http://purl.org/dc/elements/1.1/"/>
  
<node name="Title" select="/xhtml:html/lenya:meta/dc:title[@tagID='{lenya:meta/dc:title/@tagID}']">
  <content><input type="text" name="&lt;xupdate:update select=&quot;/xhtml:html/lenya:meta/dc:title[@tagID='{lenya:meta/dc:title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="lenya:meta/dc:title"/></xsl:attribute></input></content>
</node>

<xsl:apply-templates select="xhtml:body"/>

</xsl:template>

</xsl:stylesheet>  
