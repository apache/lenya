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
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

    
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


</xsl:stylesheet> 
