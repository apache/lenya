<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="docid"/>
<xsl:param name="cols" select="'80'"/>
<xsl:param name="rows" select="'30'"/>

<xsl:template match="/">
<html>
<body>
<p>
Edit Document <b><xsl:value-of select="$docid"/></b>
<br/>Cols = <xsl:value-of select="$cols"/>
<br/>Rows = <xsl:value-of select="$rows"/>
</p>

<form method="post" action="?lenya.usecase=1formedit&amp;lenya.step=close">
<table border="1">
<tr>
  <td align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
<tr><td>
<textarea name="content" cols="{$cols}" rows="{$rows}">
<xsl:apply-templates mode="mixed"/>
</textarea>
</td></tr>
<tr>
  <td align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
</table>
</form>

<a href="http://www.w3.org/TR/REC-xml#syntax">Predefined Entities</a>:
<ul>
<li>&amp;lt; instead of &lt; (left angle bracket <b>must</b> be escaped)</li>
<li>&amp;amp; instead of &amp; (ampersand <b>must</b> be escaped)</li>
<li>&amp;gt; instead of > (right angle bracket)</li>
<li>&amp;apos; instead of ' (single-quote)</li>
<li>&amp;quot; instead of " (double-quote)</li>
</ul>
</body>
</html>
</xsl:template>



<!-- Copy mixed content -->

<xsl:template match="//*" mode="mixed">
<xsl:variable name="prefix"><xsl:if test="contains(name(),':')">:<xsl:value-of select="substring-before(name(),':')"/></xsl:if></xsl:variable>

<xsl:choose>
<xsl:when test="node()">
&lt;<xsl:value-of select="name()"/><xsl:if test="namespace-uri()"><xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="namespace-uri()"/>"</xsl:if><xsl:apply-templates select="@*" mode="mixed"/>&gt;
<xsl:apply-templates select="node()" mode="mixed"/>
&lt;/<xsl:value-of select="name()"/>&gt;
</xsl:when>
<xsl:otherwise>
&lt;<xsl:value-of select="name()"/> /&gt;
</xsl:otherwise>
</xsl:choose>

<!-- FIXME: <br /> are transformed into <br> by the html serializer -->
<!--
<xsl:copy>
<xsl:copy-of select="@*"/>
<xsl:apply-templates select="node()" mode="mixed"/>
</xsl:copy>
-->
</xsl:template>

<xsl:template match="@*" mode="mixed">
<xsl:text> </xsl:text><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:template>
 
</xsl:stylesheet>  
