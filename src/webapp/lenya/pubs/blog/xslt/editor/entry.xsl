<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://example.com/newformat#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>

<xsl:param name="docid"/>

<xsl:template match="/">
<html>
<body>
<p>
Edit Document <b><xsl:value-of select="$docid"/></b>
</p>
<form method="post" action="?lenya.usecase=edit&amp;lenya.step=close">
<table border="1">
<tr>
  <td>&#160;</td><td>Title</td><td><input type="text" name="element./echo:entry/echo:title[{/echo:entry/echo:title/@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="/echo:entry/echo:title" /></xsl:attribute></input></td>
</tr>

<xsl:if test="not(/echo:entry/echo:subtitle)">
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="insert-after" value="sibling./echo:entry/echo:title[]element./echo:entry/echo:subtitle"/></td><td colspan="2">Subtitle</td>
</tr>
</xsl:if>

<xsl:if test="not(/echo:entry/echo:summary)">
<tr>
<td><input type="image" src="/lenya/lenya/images/insert.gif" name="insert" value="sibling./echo:entry/echo:title[]element./echo:entry/echo:summary"/></td><td colspan="2">Summary</td>
</tr>
</xsl:if>

<xsl:apply-templates select="/echo:entry/echo:subtitle"/>
<xsl:apply-templates select="/echo:entry/echo:summary"/>
<xsl:apply-templates select="/echo:entry/echo:content"/>
<tr>
  <td colspan="3" align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
</table>
</form>
</body>
</html>
</xsl:template>

<xsl:template match="echo:subtitle">
<tr>
  <td><input type="image" src="/lenya/lenya/images/delete.gif" name="delete" value="element./echo:entry/echo:subtitle[{@tagID}]"/></td><td>Subtitle</td><td><input type="text" name="element./echo:entry/echo:subtitle[{@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute></input></td>
</tr>
</xsl:template>

<xsl:template match="echo:summary">
<tr>
  <td valign="top"><input type="image" src="/lenya/lenya/images/delete.gif" name="delete" value="element./echo:entry/echo:summary[{@tagID}]"/></td><td valign="top">Summary</td><td><textarea name="element./echo:entry/echo:summary[{@tagID}]" cols="40" rows="5"><xsl:value-of select="." /></textarea></td>
</tr>
</xsl:template>

<xsl:template match="echo:content[@type='text/plain']">
<tr>
  <td>&#160;</td><td valign="top">Content (text/plain)</td><td><textarea name="element./echo:entry/echo:content[{@tagID}]" cols="40" rows="5"><xsl:value-of select="."/></textarea></td>
</tr>
</xsl:template>

<xsl:template match="echo:content">
<tr>
  <td>&#160;</td><td valign="top">Content (<xsl:value-of select="@type"/>)</td><td><xsl:apply-templates/></td>
</tr>
</xsl:template>
 
</xsl:stylesheet>  
