<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:lenya="http://lenya.org/2003/publication"
>

<xsl:template match="/">
  <xsl:apply-templates select="/lenya:publication"/>
</xsl:template>

<xsl:template match="lenya:publication">
<html>
<body>
<table>
<tr>
<td valign="top">
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
</td>
</tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template match="lenya:readme">
  <xsl:copy-of select="*"/>
</xsl:template>

</xsl:stylesheet>
