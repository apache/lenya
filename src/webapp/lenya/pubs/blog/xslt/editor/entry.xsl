<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://example.com/newformat#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>

<xsl:import href="../../../../xslt/authoring/edit/form.xsl"/>

<xsl:template match="echo:entry">
<tr>
  <td>&#160;</td><td>Title</td><td><input type="text" name="element./echo:entry/echo:title[{echo:title/@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="echo:title" /></xsl:attribute></input></td>
</tr>

<xsl:if test="not(echo:subtitle)">
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="insert-after" value="sibling./echo:entry/echo:title[]element./echo:entry/echo:subtitle"/></td><td colspan="2">Subtitle</td>
</tr>
</xsl:if>

<xsl:if test="not(echo:summary)">
<tr>
<td><input type="image" src="/lenya/lenya/images/insert.gif" name="insert" value="sibling./echo:entry/echo:title[]element./echo:entry/echo:summary"/></td><td colspan="2">Summary</td>
</tr>
</xsl:if>

<xsl:apply-templates select="echo:subtitle"/>
<xsl:apply-templates select="echo:summary"/>
<xsl:apply-templates select="echo:content"/>
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
