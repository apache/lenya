<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : opml2site.xsl
    Created on : 20-04-2003, 11:45
    Author     : Gregor J. Rothfuss gregor@apache.org
    Description: Converts OPML files (http://www.opml.org) to the forrest site format
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output indent="yes"/>    

<!-- ignore head part of OPML, and some attributes -->
<xsl:template match="head"/>
<xsl:template match="@type"/>
<xsl:template match="@version"/>

<xsl:template match="body">
  <xsl:apply-templates select="@*|node()"/>
</xsl:template>    

<xsl:template match="opml">
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

<xsl:template match="@text">
  <xsl:attribute name="label"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>
    
<xsl:template match="@url">
  <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
</xsl:template>
    
</xsl:stylesheet> 
