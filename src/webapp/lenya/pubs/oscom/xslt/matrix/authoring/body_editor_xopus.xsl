<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="text()">
  <xsl:copy />
</xsl:template>

<xsl:template match="system">
<div>
 <font face="verdana">

 <h3>
<xsl:choose>
  <xsl:when test="@type='cms'">
Content Management System
  </xsl:when>
  <xsl:when test="@type='framework'">
Content Management Framework
  </xsl:when>
  <xsl:when test="@type='editor'">
TTW WYSIWYG Editor
  </xsl:when>
  <xsl:otherwise>
No such type: <xsl:value-of select="@type"/>
  </xsl:otherwise>
</xsl:choose>
</h3>

 <h2><xsl:value-of select="system_name"/></h2>
 <xsl:apply-templates select="editor"/>
 <xsl:if test="not(editor)">
   No Contact!
 </xsl:if>
 <xsl:apply-templates select="description"/>
 <ul>
   <li>Home: <xsl:apply-templates select="main_url"/></li>
   <li><xsl:apply-templates select="license"/></li>
 </ul>
 </font>
</div>
</xsl:template>

<xsl:template match="license">
 License: <xsl:apply-templates select="license_name"/> 
<xsl:choose>
<xsl:when test="license_url">
(<xsl:apply-templates select="license_url"/>)
</xsl:when>
<xsl:otherwise>
(No License URL!)
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="description">
  <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="editor">
<p>
<font size="-1">
Contact: <xsl:apply-templates/> (<xsl:value-of select="@email"/>)
</font>
</p>
</xsl:template>
 
</xsl:stylesheet>  
