<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
  xmlns:ent="http://www.purl.org/NET/ENT/1.0/"
>

<xsl:import href="../../../../../xslt/authoring/edit/form.xsl"/>

<xsl:template match="echo:entry">
<tr>
  <td>&#160;</td>
  <td>Title</td>
  <td><input type="text" name="&lt;xupdate:update select=&quot;/echo:entry/echo:title[@tagID='{echo:title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="echo:title" /></xsl:attribute></input></td>
</tr>

<xsl:if test="not(echo:summary)">
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-after select=&quot;/echo:entry/echo:title[@tagID='{echo:title/@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;echo:summary&quot; namespace=&quot;http://purl.org/atom/ns#&quot;&gt;New summary&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;" value="LENYA"/></td>
  <td colspan="2">Summary</td>
</tr>
</xsl:if>

<xsl:apply-templates select="echo:summary"/>
<xsl:apply-templates select="echo:content"/>

<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:append select=&quot;/echo:entry&quot;&gt;&lt;xupdate:element name=&quot;echo:content&quot; namespace=&quot;http://purl.org/atom/ns#&quot;&gt;&lt;xupdate:attribute name=&quot;type&quot;&gt;text/plain&lt;/xupdate:attribute&gt;New content&lt;/xupdate:element&gt;&lt;/xupdate:append&gt;" value="LENYA"/></td>
  <td colspan="2">Content (text/plain)</td>
</tr>
</xsl:template>

<xsl:template match="echo:summary">
<tr>
  <td valign="top"><input type="image" src="/lenya/lenya/images/delete.gif" name="&lt;xupdate:remove select=&quot;/echo:entry/echo:summary[@tagID='{@tagID}']&quot;/&gt;" value="true"/></td>
  <td valign="top">Summary</td>
  <td><textarea name="&lt;xupdate:update select=&quot;/echo:entry/echo:summary[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="5"><xsl:value-of select="." /></textarea></td>
</tr>
</xsl:template>

<xsl:template match="echo:content[@type='text/plain']">
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-before select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;echo:content&quot; namespace=&quot;http://purl.org/atom/ns#&quot;&gt;&lt;xupdate:attribute name=&quot;type&quot;&gt;text/plain&lt;/xupdate:attribute&gt;New content&lt;/xupdate:element&gt;&lt;/xupdate:insert-before&gt;" value="LENYA"/></td>
  <td colspan="2">Content (text/plain)</td>
</tr>
<tr>
  <td valign="top"><input type="image" src="/lenya/lenya/images/delete.gif" name="&lt;xupdate:remove select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;/&gt;" value="true"/></td>
  <td valign="top">Content (text/plain)</td>
  <td><textarea name="&lt;xupdate:update select=&quot;/echo:entry/echo:content[@tagID='{@tagID}']&quot;&gt;" cols="40" rows="5"><xsl:value-of select="."/></textarea></td>
</tr>
</xsl:template>

<xsl:template match="echo:content">
<tr>
  <td>&#160;</td><td valign="top">Content (<xsl:value-of select="@type"/>)</td><td><xsl:apply-templates/></td>
</tr>
</xsl:template>
 
</xsl:stylesheet>  
