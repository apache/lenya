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
    xmlns:tree="http://www.lenya.org/2003/sitetree"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="tree"
    >
    
<xsl:import href="util.xsl"/>    
    
<xsl:param name="path" select="'index.html'"/>    

<xsl:template match="tree:site">
  <div id="breadcrumb">
    <xsl:apply-templates select="tree:node">
      <xsl:with-param name="path" select="$path"/>
    </xsl:apply-templates>
  </div>
</xsl:template>


<xsl:template match="tree:node">
  <xsl:param name="path"/>
  
  <xsl:variable name="first-step">
    <xsl:call-template name="first-step">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  
  <xsl:if test="$first-step = @id">
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
  <a><xsl:call-template name="node2href"/><xsl:apply-templates select="tree:label"/></a>
</xsl:template>


<xsl:template match="tree:label">
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
