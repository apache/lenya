<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : tree2site.xsl
    Created on : 7. April 2003, 11:45
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output indent="yes"/>    

<xsl:template match="tree">
  <xsl:element name="site" namespace="http://apache.org/forrest/linkmap/1.0">
    <xsl:attribute name="href"/>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:element>
</xsl:template>    
    
<xsl:template match="*">
  <xsl:element name="node-{count(preceding::*) + count(ancestor::*)}" namespace="http://apache.org/forrest/linkmap/1.0">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:element>
</xsl:template>    

<xsl:template match="@menuName">
  <xsl:attribute name="label"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>
    
<xsl:template match="branch[not(branch)]/@relURI">
  <xsl:attribute name="href"><xsl:value-of select="."/>bla.html</xsl:attribute>
</xsl:template>
    
<xsl:template match="@relURI">
</xsl:template>
    
<xsl:template match="@doctype"/>

</xsl:stylesheet> 
