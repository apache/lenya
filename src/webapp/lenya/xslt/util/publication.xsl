<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:lenya="http://lenya.org/2003/publication"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://www.lenya.org/2003/cms-page"
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
<div class="lenya-publication-item"><a href="../docs-new/index.html">New&#160;Documentation</a></div>
<div class="lenya-publication-item"><a href="../docs/index.html">Deprecated&#160;Documentation</a></div>
</div>

<div class="lenya-frontpage">
<h2>About</h2>
<h3><xsl:value-of select="lenya:name"/></h3>
<p>
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
  <xsl:copy-of select="*"/>
</xsl:template>

<xsl:template match="lenya:tests">
<a name="lenya:tests" />
<h2>Tests</h2>
<ol>
  <xsl:apply-templates select="lenya:test"/>
</ol>
</xsl:template>

<xsl:template match="lenya:test">
  <li><xsl:copy-of select="."/></li>
</xsl:template>

</xsl:stylesheet>
