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
    
<xsl:param name="path"/>

<xsl:template match="tree:site">

  <xsl:variable name="first-step">
    <xsl:call-template name="first-step">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  
  <div id="tabs">

    <xsl:call-template name="pre-separator"/>
    <xsl:for-each select="tree:node">
      <xsl:if test="position() &gt; 1">
        <xsl:call-template name="separator"/>
      </xsl:if>
      
      <xsl:choose>
        <xsl:when test="@id = $first-step">
          <xsl:call-template name="tab-selected"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="tab"/>
        </xsl:otherwise>
      </xsl:choose>
        
    </xsl:for-each>
    <xsl:call-template name="post-separator"/>
  </div>
</xsl:template>


<xsl:template name="tab">
  <span class="tab"><xsl:call-template name="label"/></span>
</xsl:template>


<xsl:template name="tab-selected">
  <span class="tab-selected"><xsl:call-template name="label"/></span>
</xsl:template>


<xsl:template name="label">
  <a><xsl:call-template name="node2href"/><xsl:apply-templates select="tree:label"/></a>
</xsl:template>


<xsl:template match="tree:label">
  <xsl:apply-templates select="node()"/>
</xsl:template>


<xsl:template name="pre-separator">
</xsl:template>


<xsl:template name="separator">
   <xsl:text>&#160;</xsl:text>
</xsl:template>


<xsl:template name="post-separator">
</xsl:template>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
