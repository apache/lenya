<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <xsl:apply-templates select="mailing-lists"/>
</xsl:template>

<xsl:template match="mailing-lists">
 <font face="verdana">
 <h3>Mailing Lists</h3>
<p>
There are currently three OSCOM mailings lists available.
</p>
 <!--<h4>Public Lists</h4>-->
 <xsl:apply-templates select="public/list"/>
<!--
 <h4>Private Lists</h4>
 <xsl:apply-templates select="private/list"/>
-->
 </font>
</xsl:template>

<xsl:template match="list">
<p>
<font size="-1">
<b><xsl:value-of select="name"/></b>
<xsl:apply-templates select="description"/>
<br /><a href="{public}">List Info</a>
<!--
<br /><a href="{admin}">List Administration</a>
-->
</font>
</p>
</xsl:template>

<xsl:template match="description">
<br />
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="a">
<xsl:copy-of select="."/>
</xsl:template>
 
</xsl:stylesheet>  
