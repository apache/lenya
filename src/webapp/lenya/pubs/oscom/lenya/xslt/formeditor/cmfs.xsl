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

<tr>
  <td>&#160;</td><td valign="top">Description</td>
  <td><textarea name="&lt;xupdate:update select=&quot;/system/description[@tagID='{description/@tagID}']&quot;&gt;" cols="40" rows="5"><xsl:apply-templates select="description" mode="mixed"/></textarea></td>
</tr>

<tr>
  <td>&#160;</td><td>Home</td>
  <td><input type="text" name="&lt;xupdate:update select=&quot;/system/main_url[@tagID='{main_url/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="main_url" /></xsl:attribute></input></td>
</tr>

<tr>
  <td>&#160;</td><td>License Name</td>
  <td><input type="text" name="&lt;xupdate:update select=&quot;/system/license/license_name[@tagID='{license/license_name/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="license/license_name" /></xsl:attribute></input></td>
</tr>

<tr>
  <td>&#160;</td><td>License URL</td>
  <td><input type="text" name="&lt;xupdate:update select=&quot;/system/license/license_url[@tagID='{license/license_url/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="license/license_url" /></xsl:attribute></input></td>
</tr>


<xsl:if test="not(programming-language)">
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-after select=&quot;/system/license&quot;&gt;&lt;xupdate:element name=&quot;programming-language&quot;&gt;Enter your religion&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;" value="LENYA"/></td>
  <td colspan="2">Programming Language</td>
</tr>
</xsl:if>
<xsl:apply-templates select="programming-language"/>


<xsl:if test="not(related-info)">
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-after select=&quot;/system/system_name&quot;&gt;&lt;xupdate:element name=&quot;related-info&quot;&gt;&lt;info-item&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;uri&gt;http://&lt;/uri&gt;&lt;/info-item&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;" value="LENYA"/></td>
  <td colspan="2">Related Information</td>
</tr>
</xsl:if>
<xsl:apply-templates select="related-info"/>


<xsl:if test="not(features)">
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-after select=&quot;/system/system_name&quot;&gt;&lt;xupdate:element name=&quot;features&quot;&gt;&lt;feature&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;description&gt;New Description&lt;/description&gt;&lt;/feature&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;" value="LENYA"/></td>
  <td colspan="2">Features</td>
</tr>
</xsl:if>
<xsl:apply-templates select="features"/>
</xsl:template>


<xsl:template match="programming-language">
<tr>
  <td>&#160;</td><td>Programming Language</td>
  <td><input type="text" name="&lt;xupdate:update select=&quot;/system/programming-language[@tagID='{@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="." /></xsl:attribute></input></td>
</tr>
</xsl:template>

<xsl:template match="related-info">
<tr>
  <td>&#160;</td><td colspan="2">Related Information</td>
</tr>
<xsl:apply-templates select="info-item"/>
</xsl:template>

<xsl:template match="info-item">
<tr>
  <td><input type="image" src="/lenya/lenya/images/delete.gif" value="true" name="&lt;xupdate:remove select=&quot;/system/related-info/info-item[@tagID='{@tagID}']&quot;/&gt;"/></td><td colspan="2">Info</td>
</tr>
<tr>
  <td>&#160;</td><td>Title</td><td><input type="text" name="&lt;xupdate:update select=&quot;/system/related-info/info-item/title[@tagID='{title/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="title" /></xsl:attribute></input></td>
</tr>
<tr>
  <td>&#160;</td><td>URL</td><td><input type="text" name="&lt;xupdate:update select=&quot;/system/related-info/info-item/uri[@tagID='{uri/@tagID}']&quot;&gt;" size="40"><xsl:attribute name="value"><xsl:value-of select="uri" /></xsl:attribute></input></td>
</tr>
<tr>
  <td><input type="image" src="/lenya/lenya/images/insert.gif" name="&lt;xupdate:insert-after select=&quot;/system/related-info/info-item[@tagID='{@tagID}']&quot;&gt;&lt;xupdate:element name=&quot;info-item&quot;&gt;&lt;title&gt;New Title&lt;/title&gt;&lt;uri&gt;http://&lt;/uri&gt;&lt;/xupdate:element&gt;&lt;/xupdate:insert-after&gt;" value="LENYA"/></td>
  <td colspan="2">Info</td>
</tr>
</xsl:template>


<xsl:template match="features">
<tr>
  <td>&#160;</td><td colspan="2">Features</td>
</tr>
<xsl:apply-templates select="feature"/>
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



<!-- Copy mixed content -->



<xsl:template match="description//*" mode="mixed">
<xsl:copy>
<xsl:copy-of select="@*[local-name()!='tagID']"/>
<xsl:apply-templates select="node()"/>
</xsl:copy>
</xsl:template>
 
</xsl:stylesheet>  
