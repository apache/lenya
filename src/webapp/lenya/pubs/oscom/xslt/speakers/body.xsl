<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <xsl:apply-templates select="speakers"/>
</xsl:template>

<xsl:template match="speakers">
 <font face="verdana">
 <h3>Speakers</h3>
 <ul>
 <xsl:apply-templates select="speaker"/>
 </ul>
 </font>
</xsl:template>

<xsl:template match="speaker">
 <li>
 <xsl:choose>
  <xsl:when test="email">
    <a href="mailto:{email}"><xsl:value-of select="name"/></a>
  </xsl:when>
  <xsl:otherwise>
    <xsl:value-of select="name"/>
  </xsl:otherwise>
 </xsl:choose>

 (<xsl:value-of select="@type"/>)

<!--
 <xsl:choose>
  <xsl:when test="@selected='true'">
    (selected)
  </xsl:when>
  <xsl:otherwise>
    (not selected)
  </xsl:otherwise>
 </xsl:choose>
-->

 <br />Project: <a href="{project/@href}"><xsl:value-of select="project"/></a>
 <br />Contact (<xsl:value-of select="contact/status/@type"/>):
 <ul>
   <xsl:for-each select="contact/replies/reply">
     <li><a href="{@href}"><xsl:value-of select="@href"/></a></li>
   </xsl:for-each>
 </ul>
 <br />&#160;
 </li>
</xsl:template>
 
</xsl:stylesheet>  
