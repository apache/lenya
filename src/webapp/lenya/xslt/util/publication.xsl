<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:lenya="http://lenya.org/2003/publication"
>

<xsl:template match="/">
  <xsl:apply-templates select="/lenya:publication"/>
</xsl:template>

<xsl:template match="lenya:publication">
<html>
<head>
  <title>Lenya CMS Publication: <xsl:value-of select="lenya:name"/></title>
  <link rel="stylesheet" type="text/css" href="/lenya/lenya/css/default.css" />
</head>
<body>
<table>
<tr>
<td valign="top">
This&#160;Publication
<br />
&#160;&#160;&#160;<a href="authoring/">Login&#160;as&#160;Editor</a>
<br />
&#160;&#160;&#160;Document&#160;Types
<br />
&#160;&#160;&#160;Collection&#160;Types
<br />
&#160;&#160;&#160;<a href="#lenya:tests">Use&#160;Cases/Tests</a>
<br />
&#160;&#160;&#160;Features
<br />
<a href="../index.html">Other&#160;Publications</a>
<br />
<a href="../docs/index.html">Documentation</a>
</td>

<td>&#160;</td>

<td valign="top">
  <h3>Publication</h3>
  <h1><xsl:value-of select="lenya:name"/></h1>
  
<p>
  <xsl:copy-of select="lenya:description"/>
</p>

<p>
  <xsl:apply-templates select="lenya:readme"/>
</p>

<p>
  <xsl:apply-templates select="lenya:tests"/>
</p>
</td>
</tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template match="lenya:readme">
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
  <li><xsl:apply-templates/></li>
</xsl:template>

</xsl:stylesheet>
