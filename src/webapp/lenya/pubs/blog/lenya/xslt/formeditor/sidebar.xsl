<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:import href="../../../../../xslt/authoring/edit/form.xsl"/>

<xsl:template match="sidebar">
<xsl:apply-templates select="block"/>
</xsl:template>

<xsl:template match="block">
<tr>
  <td><input type="image" src="/lenya/lenya/images/delete.gif" name="delete" value="element./sidebar/block[{@tagID}]"/></td><td colspan="2">Block</td>
</tr>
<tr>
  <td>&#160;</td><td>Title</td><td><input type="text" name="element./sidebar/block/title[{title/@tagID}]" size="40"><xsl:attribute name="value"><xsl:value-of select="title" /></xsl:attribute></input></td>
</tr>
<tr>
  <td>&#160;</td><td valign="top">Content</td><td><pre><xsl:copy-of select="content/@*|content/node()" /></pre></td>
<!--
  <td>&#160;</td><td valign="top">Content</td><td><textarea name="element./sidebar/block/content[{content/@tagID}]" cols="40" rows="3"><xsl:copy-of select="content/@*|content/node()" /></textarea></td>
-->
</tr>
</xsl:template>
 
</xsl:stylesheet>  
