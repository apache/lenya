<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://example.com/newformat#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>

<xsl:param name="entryid"/>

<xsl:template match="/">
<html>
<body>
Edit Document <xsl:value-of select="$entryid"/>
<form method="post" action="?lenya.usecase=edit&amp;lenya.step=close">
<table>
<tr>
  <td>Title</td><td><input type="text" name="element./echo:entry/echo:title[{/echo:entry/echo:title/@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="/echo:entry/echo:title" /></xsl:attribute></input></td>
</tr>
<tr>
  <td>Subtitle</td><td><input type="text" name="element./echo:entry/echo:subtitle[{/echo:entry/echo:subtitle/@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="/echo:entry/echo:subtitle" /></xsl:attribute></input></td>
</tr>
<tr>
  <td valign="top">Summary</td><td><textarea name="element./echo:entry/echo:summary[{/echo:entry/echo:summary/@tagID}]" cols="40" rows="5"><xsl:value-of select="/echo:entry/echo:summary" /></textarea></td>
</tr>
<xsl:apply-templates select="/echo:entry/echo:content"/>
<tr>
  <td colspan="2" align="right"><input type="submit" value="SAVE" name="save"/><input type="submit" value="CANCEL" name="cancel"/></td>
</tr>
</table>
</form>
</body>
</html>
</xsl:template>

<xsl:template match="echo:content[@type='text/plain']">
<tr>
  <td valign="top">Content (text/plain)</td><td><textarea name="element./echo:entry/echo:content[{@tagID}]" cols="40" rows="5"><xsl:value-of select="."/></textarea></td>
</tr>
</xsl:template>

<xsl:template match="echo:content">
<tr>
  <td valign="top">Content</td><td><xsl:apply-templates/></td>
</tr>
</xsl:template>
 
</xsl:stylesheet>  
