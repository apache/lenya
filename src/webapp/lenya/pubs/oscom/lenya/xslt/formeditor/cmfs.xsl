<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:import href="../../../../../xslt/authoring/edit/form.xsl"/>

<xsl:template match="system">
<tr>
  <td>&#160;</td><td>Project Name</td>
  <td><input type="text" name="&lt;xupdate:update select=&quot;/system/system_name[@tagID='{system_name/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="system_name" /></xsl:attribute></input></td>
</tr>
<!-- FIXME: The project "lenya" throws an exception because of the description within the log file -->
<tr>
  <td>&#160;</td><td valign="top">Description</td>
  <td><textarea name="&lt;xupdate:update select=&quot;/system/description[@tagID='{description/@tagID}']&quot;&gt;&lt;![CDATA[" cols="40" rows="5"><xsl:value-of select="description" /></textarea></td>
</tr>
<xsl:if test="not(features)">
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-after select=&quot;/system/system_name&quot;&gt;&lt;xupdate:element name=&quot;features&quot;&gt;&lt;feature&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;description&gt;New Description&lt;/description&gt;&lt;/feature&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;" value="LENYA"/></td>
  <td colspan="2">Features</td>
</tr>
</xsl:if>
<xsl:apply-templates select="features/feature"/>
</xsl:template>



<xsl:template match="feature">
<tr>
  <td><input type="image" src="/lenya/lenya/images/delete.gif" value="true" name="&lt;xupdate:remove select=&quot;/system/features/feature[@tagID='{@tagID}']&quot;/&gt;"/></td><td colspan="2">Feature</td>
</tr>
<tr>
  <td>&#160;</td><td>Feature Title</td><td><input type="text" name="&lt;xupdate:update select=&quot;/system/features/feature/title[@tagID='{title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="title" /></xsl:attribute></input></td>
</tr>
<tr>
  <td>&#160;</td><td valign="top">Feature Description</td><td><textarea name="&lt;xupdate:update select=&quot;/system/features/feature/description[@tagID='{description/@tagID}']&quot;&gt;" cols="40" rows="3"><xsl:value-of select="description" /></textarea></td>
</tr>
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-after select=&quot;/system/features/feature[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;feature&quot;&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;description&gt;New Description&lt;/description&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;" value="LENYA"/></td>
  <td colspan="2">Feature</td>
</tr>
</xsl:template>
 
</xsl:stylesheet>  
