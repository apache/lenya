<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : util.xsl
    Created on : 10. April 2003, 14:29
    Author     : nobby
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:tree="http://www.lenya.org/2003/sitetree"
    >

    
<xsl:variable name="path-to-context"><xsl:call-template name="create-path-to-context"/></xsl:variable>
  
<xsl:template name="create-path-to-context">
  <xsl:param name="local-uri" select="$path"/>
  <xsl:if test="contains($local-uri, '/')">
    <xsl:text/>../<xsl:call-template name="create-path-to-context">
      <xsl:with-param name="local-uri" select="substring-after($local-uri, '/')"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>



<xsl:template name="first-step">
  <xsl:param name="path"/>
  <xsl:choose>
    <xsl:when test="contains($path, '/')">
      <xsl:value-of select="substring-before($path, '/')"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$path"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="node2href">
  <xsl:apply-templates select="." mode="href"/>
</xsl:template>


<xsl:template match="tree:node" mode="href">
  <xsl:variable name="last-step">
    <xsl:value-of select="@id"/>
      
    <!-- has children? -->
    <xsl:choose>
      <xsl:when test="tree:node">
        <!-- add trailing slash -->
        <xsl:if test="substring(@id, (string-length(@id) - string-length('/')) + 1) != '/'">
           <xsl:text>/</xsl:text>
        </xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>
    
  <xsl:attribute name="href">
    <xsl:value-of select="$path-to-context"/>
    <xsl:apply-templates select="parent::tree:node" mode="href"/>
    <xsl:value-of select="$last-step"/>
  </xsl:attribute>
</xsl:template>


</xsl:stylesheet> 
