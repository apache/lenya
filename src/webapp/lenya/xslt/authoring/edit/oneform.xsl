<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="docid"/>

<xsl:template match="/">
<html>
<body>
<p>
Edit Document <b><xsl:value-of select="$docid"/></b>
</p>

<form method="post" action="?lenya.usecase=1formedit&amp;lenya.step=close">
<table border="1">
<tr>
  <td align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
<tr><td>
<textarea name="content" cols="40" rows="30">
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
<xsl:copy>
<xsl:copy-of select="@*[local-name()!='tagID']"/>
<xsl:apply-templates select="node()" mode="mixed"/>
</xsl:copy>
</xsl:template>
 
</xsl:stylesheet>  
