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

<!-- $Id: publication.xsl,v 1.17 2004/03/13 12:42:09 gregor Exp $ -->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
>

<xsl:template match="/">
  <xsl:apply-templates select="/lenya:publication"/>
</xsl:template>

<xsl:template match="lenya:publication">
<page:page>
  <page:title>Lenya CMS Publication: <xsl:value-of select="lenya:name"/></page:title>
  <page:body>

<div class="lenya-sidebar">
<div class="lenya-sidebar-heading">This&#160;Publication</div>
<ul>
  <li><a href="authoring/">Login&#160;as&#160;Editor</a></li>
  <li>Document&#160;Types</li>
  <li><a href="tests.html">Use&#160;Cases/Tests</a></li>
  <li>Features</li>
</ul>
<div class="lenya-publication-item"><a href="../index.html">Other&#160;Publications</a></div>
<div class="lenya-publication-item"><a href="../docs-new/docs/index.html">Documentation</a></div>
<div class="lenya-publication-item"><a href="http://wiki.cocoondev.org/Wiki.jsp?page=Lenya">Wiki&#160;Documentation</a></div>
</div>

<div class="lenya-frontpage">
<xsl:apply-templates select="lenya:description"/>
<xsl:apply-templates select="lenya:tests"/>
</div>

</page:body>
</page:page>
</xsl:template>

<xsl:template match="lenya:description">
<h2><xsl:value-of select="../lenya:name"/></h2>
(Based on Lenya <b><xsl:value-of select="../lenya:version"/></b>)
<p>
<h3>About</h3>
  <xsl:apply-templates />
  <br/><br/>
  <xsl:apply-templates select="../lenya:readme"/>
</p>
</xsl:template>

<xsl:template match="lenya:readme">
<h3>Readme</h3>
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="lenya:tests">
<a name="lenya:tests" />
<h3>Tests</h3>
<ol>
  <xsl:apply-templates select="lenya:test"/>
</ol>
</xsl:template>

<xsl:template match="lenya:test">
  <li><xsl:apply-templates /></li>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>

</xsl:stylesheet>
