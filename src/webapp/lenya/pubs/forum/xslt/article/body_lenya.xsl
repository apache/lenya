<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
<xsl:template match="b">
	<b><xsl:value-of select="text()" /></b>
</xsl:template>

<xsl:template match="article">
  <tr>
    <td bgcolor="#000000">
      <img src="../../images/roundedge.gif" height="16" width="13" align="top"/><font face="arial,helvetica" size="4" color="#ffffff"><b><xsl:apply-templates select="head/title"/></b></font>
    </td>
  </tr>
  <tr>
    <td>
      <xsl:apply-templates select="body"/>
<!--
      (<a href="articles/{../@href}/index.html">Read More...</a> | 0 comments)
-->
    </td>
  </tr>
</xsl:template>

<xsl:template match="body">
  <xsl:apply-templates select="../meta/editor"/>
  <xsl:apply-templates select="p"/>
</xsl:template>

<xsl:template match="p">
  <xsl:apply-templates/>
  <p />
</xsl:template>

<xsl:template match="quotation|QUOTATION">
<i>"<xsl:apply-templates/>"</i>
</xsl:template>

<xsl:template match="editor">
<b>Posted by <a href=""><xsl:value-of select="."/></a><xsl:apply-templates select="../date"/></b><br />
</xsl:template>

<xsl:template match="date">
on <xsl:value-of select="day/@name"/>&#160;<xsl:value-of select="month/@name"/>&#160;<xsl:value-of select="day"/>, @<xsl:value-of select="hour"/>:<xsl:value-of select="minute"/>
</xsl:template>
 
</xsl:stylesheet>  
