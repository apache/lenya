<?xml version="1.0"?>

<!--
    Document   : page2xslt.xsl
    Created on : November 20, 2002, 4:17 PM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
    >

<xsl:param name="contextprefix"/>

<xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>


<xsl:template match="xsl:stylesheet">
  <xso:stylesheet xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
  </xso:stylesheet>
</xsl:template>


<xsl:template match="xsl:param[@name='contextprefix']">
  <xso:param name="contextprefix" select="'{$contextprefix}'"/>
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
