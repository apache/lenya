<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : tabs.xsl
    Created on : 10. April 2003, 17:26
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://www.lenya.org/2003/navigation"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >
    
<xsl:param name="url"/>

<xsl:template match="nav:site">
  <div id="breadcrumb">
    <xsl:apply-templates select="nav:node"/>
  </div>
</xsl:template>


<xsl:template match="nav:node">
  
  <xsl:if test="starts-with($url, @basic-url)">
    <xsl:call-template name="separator"/>
    <xsl:call-template name="step">
      <xsl:with-param name="href" select="''"/>
    </xsl:call-template>
  </xsl:if>
  
</xsl:template>


<xsl:template name="step">
  <xsl:call-template name="label"/>
</xsl:template>

    
<xsl:template name="label">
  <a href="@href"><xsl:apply-templates select="nav:label"/></a>
</xsl:template>


<xsl:template match="nav:label">
  <xsl:apply-templates select="node()"/>
</xsl:template>


<xsl:template name="separator">
  &#x00BB;
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
