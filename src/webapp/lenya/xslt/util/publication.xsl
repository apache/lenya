<?xml version="1.0"?>

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
  <li>Collection&#160;Types</li>
  <li><a href="#lenya:tests">Use&#160;Cases/Tests</a></li>
  <li>Features</li>
</ul>
<div class="lenya-publication-item"><a href="../index.html">Other&#160;Publications</a></div>
<div class="lenya-publication-item"><a href="../docs-new/docs/index.html">Documentation</a></div>
<!--
<div class="lenya-publication-item"><a href="../docs/index.html">Deprecated&#160;Documentation</a></div>
-->
<div class="lenya-publication-item"><a href="http://wiki.cocoondev.org/Wiki.jsp?page=Lenya">Wiki&#160;Documentation</a></div>
</div>

<div class="lenya-frontpage">
<h2><xsl:value-of select="lenya:name"/></h2>
<p>
<h3>About</h3>
  <xsl:copy-of select="lenya:description"/>
  <br/><br/>
  <xsl:apply-templates select="lenya:readme"/>
</p>
<xsl:apply-templates select="lenya:tests"/>
</div>

</page:body>
</page:page>
</xsl:template>

<xsl:template match="lenya:readme">
<h3>Readme</h3>
  <xsl:copy-of select="*"/>
</xsl:template>

<xsl:template match="lenya:tests">
<a name="lenya:tests" />
<h3>Tests</h3>
<ol>
  <xsl:apply-templates select="lenya:test"/>
</ol>
</xsl:template>

<xsl:template match="lenya:test">
  <li><xsl:copy-of select="."/></li>
</xsl:template>

</xsl:stylesheet>
