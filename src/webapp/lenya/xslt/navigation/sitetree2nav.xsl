<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : stylesheet.xsl
    Created on : 30. April 2003, 10:53
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tree="http://www.lenya.org/2003/sitetree"
    xmlns:nav="http://www.lenya.org/2003/navigation"
    exclude-result-prefixes="tree"
    >

<xsl:param name="url"/>
    
<xsl:variable name="path-to-context"><xsl:call-template name="create-path-to-context"/></xsl:variable>
  
<xsl:template name="create-path-to-context">
  <xsl:param name="local-url" select="$url"/>
  <xsl:if test="contains($local-url, '/')">
    <xsl:text/>../<xsl:call-template name="create-path-to-context">
      <xsl:with-param name="local-url" select="substring-after($local-url, '/')"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>


<xsl:template match="tree:site">

  <nav:site>
    <xsl:apply-templates/>
  </nav:site>

</xsl:template>    

<!--
Apply nodes recursively
-->
<xsl:template match="tree:node">

  <!-- basic url of parent node -->
  <xsl:param name="previous-url" select="''"/>
  
  <nav:node>
  
    <xsl:copy-of select="@id"/>
  
    <!-- basic url - for all nodes -->
  
    <xsl:variable name="basic-url">
      <xsl:text/>
      <xsl:choose>
        <xsl:when test="@href">
          <xsl:value-of select="@href"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$previous-url"/><xsl:value-of select="@id"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>  
    
    <!-- suffix - only when @href is not present -->
  
    <xsl:variable name="suffix">
      <xsl:if test="not(@href)">
          <xsl:if test="@xml:lang">
            <xsl:text/>_<xsl:value-of select="@xml:lang"/><xsl:text/>
          </xsl:if>
          <xsl:text>.</xsl:text>
          <xsl:choose>
            <xsl:when test="@suffix">
              <xsl:text/><xsl:value-of select="@suffix"/><xsl:text/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>html</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
      </xsl:if>
    </xsl:variable>
    
    <xsl:attribute name="basic-url"><xsl:value-of select="$basic-url"/></xsl:attribute>
    <xsl:attribute name="href">
      <xsl:text/>
      <xsl:value-of select="$path-to-context"/><xsl:text/>
      <xsl:value-of select="$basic-url"/><xsl:text/>
      <xsl:value-of select="$suffix"/><xsl:text/>
    </xsl:attribute>
    
    <xsl:apply-templates>
      <xsl:with-param name="previous-url" select="concat($basic-url, '/')"/>
    </xsl:apply-templates>
    
  </nav:node>
</xsl:template>


<xsl:template match="tree:label">
  <nav:label>
    <xsl:apply-templates/>
  </nav:label>
</xsl:template>


<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
