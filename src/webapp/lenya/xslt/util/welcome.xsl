<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
<html>
<body>
  <h1>Lenya - Content Management System</h1>
  
  <xsl:apply-templates select="/lenya/publications"/>
  <xsl:apply-templates select="/lenya/xhtml/body"/>
</body>
</html>
</xsl:template>

<xsl:template match="publications">
<h2>Publications</h2>
<!--
<p>
We are working on a catalog of sample publications. The idea is that an
"integrator" can pull out an appropriate publication and reuse it for building
efficiently its own publication. The <a href="docs/tutorial/index.html">tutorial</a> describes how to do that.
</p>
-->
<ol>
<xsl:for-each select="publication">
  <li><a href="{@pid}/introduction.html"><xsl:apply-templates select="publication/name"/></a></li>
</xsl:for-each>
</ol>
</xsl:template>

<xsl:template match="body">
  <xsl:copy-of select="*"/>
</xsl:template>

</xsl:stylesheet>
