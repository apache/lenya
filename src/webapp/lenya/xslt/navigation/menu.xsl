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
  <div class="menu">
    <xsl:apply-templates select="tree:node">
      <xsl:with-param name="relative-path" select="''"/>
    </xsl:apply-templates>
  </div>
</xsl:template>


<xsl:template match="tree:node">
  <xsl:param name="relative-path"/>
  
  <xsl:variable name="href">
    <xsl:choose>
      <xsl:when test="substring(@href, string-length(@href), 1) = '/'">
        <xsl:value-of select="concat($relative-path, @href, 'index.html')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat($relative-path, @href)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:choose>
    <xsl:when test="$path = $href">
      <xsl:call-template name="item-selected">
        <xsl:with-param name="href" select="$href"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="item">
        <xsl:with-param name="href" select="$href"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
  
  <xsl:apply-templates select="tree:node">
    <xsl:with-param name="relative-path" select="concat($relative-path, @href)"/>
  </xsl:apply-templates>
  
</xsl:template>


<xsl:template name="item">
  <xsl:param name="href"/>
  <div class="menuitem-{count(ancestor-or-self::tree:node)}">
    <a href="{$path-to-context}{$href}"><xsl:value-of select="@label"/></a>
  </div>
</xsl:template>
    
    
<xsl:template name="item-selected">
  <xsl:param name="href"/>
  <div class="menuitem-selected-{count(ancestor-or-self::tree:node)}">
    <xsl:value-of select="@label"/>
  </div>
</xsl:template>
    
    
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
