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
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >
    
<xsl:param name="url"/>
<xsl:param name="chosenlanguage"/>
<xsl:param name="defaultlanguage"/>
<xsl:param name="breadcrumbprefix"/>

<xsl:template match="nav:site">
  <div id="breadcrumb">
    <xsl:value-of select="$breadcrumbprefix"/><xsl:apply-templates select="nav:node"/>
  </div>
</xsl:template>


<xsl:template match="nav:node">
  
  <xsl:if test="starts-with($url, @basic-url)">
    <xsl:call-template name="separator"/>
    <xsl:call-template name="step"/>
    <xsl:apply-templates select="nav:node"/>
  </xsl:if>
  
</xsl:template>


<xsl:template name="step">
  <xsl:choose>
    <xsl:when test="substring(@href, (string-length(@href) - string-length($url)) + 1) = $url">
      <xsl:apply-templates select="nav:label"/>
    </xsl:when>
    <xsl:otherwise>
      <a href="{@href}"><xsl:apply-templates select="nav:label"/></a>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

    
<xsl:template match="nav:label">
   <xsl:choose>
      <xsl:when test="self::*[lang($chosenlanguage)]">
      	<xsl:value-of select="self::*[lang($chosenlanguage)]"/>
      </xsl:when>
      <xsl:otherwise>
      	<xsl:value-of select="self::*[lang($defaultlanguage)]"/>
      </xsl:otherwise>
   </xsl:choose>	
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
