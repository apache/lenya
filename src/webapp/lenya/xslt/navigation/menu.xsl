<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : menu.xsl
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

<xsl:template match="nav:site">
  <div id="menu">
    <xsl:apply-templates select="nav:node"/>
  </div>
</xsl:template>


<xsl:template match="nav:node">
  <xsl:choose>
    <xsl:when test="starts-with($url, @basic-url)">
      <div class="menublock-selected-{count(ancestor-or-self::nav:node)}">
        <xsl:call-template name="item"/>
        <xsl:apply-templates select="nav:node"/>
      </div>
    </xsl:when>
    <xsl:otherwise>
      <div class="menublock-{count(ancestor-or-self::nav:node)}">
        <xsl:call-template name="item"/>
        <xsl:apply-templates select="nav:node"/>
      </div>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="item">
  <xsl:choose>
    <xsl:when test="substring(@href, (string-length(@href) - string-length($url)) + 1) = $url">
      <xsl:call-template name="item-selected"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="item-default"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="item-default">
  <div class="menuitem-{count(ancestor-or-self::nav:node)}">
    <a href="{@href}"><xsl:apply-templates select="nav:label"/></a>
  </div>
</xsl:template>
    
    
<xsl:template name="item-selected">
  <div class="menuitem-selected-{count(ancestor-or-self::nav:node)}">
    <xsl:apply-templates select="nav:label"/>
  </div>
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
    
    
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
