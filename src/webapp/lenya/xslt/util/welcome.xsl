<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya="http://lenya.org/2003/publication"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    >

<xsl:template match="/">
<html>
<head>
  <title>Lenya - Content Management System</title>
  <link rel="stylesheet" type="text/css" href="/lenya/lenya/css/default.css" />
</head>
<body>
  <h1>Lenya - Content Management System</h1>
  
  <xsl:apply-templates select="/lenya:lenya/lenya:publications"/>
  <xsl:copy-of select="/lenya:lenya/xhtml:xhtml/xhtml:body"/>
</body>
</html>
</xsl:template>

<xsl:template match="lenya:publications">
<h2>Publications</h2>
<!--
<p>
We are working on a catalog of sample publications. The idea is that an
"integrator" can pull out an appropriate publication and reuse it for building
efficiently its own publication. The <a href="docs/tutorial/index.html">tutorial</a> describes how to do that.
</p>
-->
<ol>
<xsl:for-each select="lenya:publication">
  <xsl:choose>
    <xsl:when test="lenya:XPSEXCEPTION">
      <li><font color="red">Exception:</font> (publication id = <xsl:value-of select="@pid"/>) <xsl:value-of select="lenya:XPSEXCEPTION"/></li>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="lenya:publication/@show = 'false'">
          <!-- do not list this publication. Might be a "template" publication -->
        </xsl:when>
        <xsl:otherwise>
          <li><a href="{@pid}/introduction.html">
          <xsl:apply-templates select="lenya:publication/lenya:name"/></a></li>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
</ol>
</xsl:template>

</xsl:stylesheet>
