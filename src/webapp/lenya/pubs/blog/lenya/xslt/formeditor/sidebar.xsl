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
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-before select=&quot;/sidebar/block[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;block&quot;&gt;&lt;title&gt;New title&lt;/title&gt;&lt;content&gt;New content&lt;/content&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-before&gt;" value="LENYA"/></td>
  <td colspan="2">Block</td>
</tr>
<tr>
  <td><input type="image" src="/lenya/lenya/images/delete.gif" name="&lt;xupdate:remove select=&quot;/sidebar/block[@tagID='{@tagID}']&quot;/&gt;" value="true"/></td>
  <td colspan="2">Block</td>
</tr>
<tr>
  <td>&#160;</td>
  <td>Title</td>
  <td><input type="text" name="&lt;xupdate:update select=&quot;/sidebar/block/title[@tagID='{title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="title" /></xsl:attribute></input></td>
</tr>
<tr>
  <td>&#160;</td><td valign="top">Content</td><td><pre><xsl:copy-of select="content/@*|content/node()" /></pre></td>
<!--
  <td>&#160;</td><td valign="top">Content</td><td><textarea name="&lt;xupdate:update select=&quot;/sidebar/block/content[@tagID='{content/@tagID}']&quot;&gt;" cols="40" rows="3"><xsl:copy-of select="content/@*|content/node()" /></textarea></td>
-->
</tr>
</xsl:template>
 
</xsl:stylesheet>  
