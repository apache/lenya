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

<xsl:template match="nav:site">

  <div id="tabs">

    <xsl:call-template name="pre-separator"/>
    <xsl:for-each select="nav:node">
      <xsl:if test="position() &gt; 1">
        <xsl:call-template name="separator"/>
      </xsl:if>
      
      <xsl:choose>
        <xsl:when test="descendant-or-self::nav:node[@current = 'true']">
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
  <a href="{@href}"><xsl:apply-templates select="nav:label"/></a>
</xsl:template>


<xsl:template match="nav:label">
   <xsl:choose>
      <xsl:when test="parent::nav:node/child::nav:label[@xml:lang = $chosenlanguage]">
      	<xsl:value-of select="self::*[lang($chosenlanguage)]"/>
      </xsl:when>
      <xsl:otherwise>
      	<xsl:value-of select="self::*[lang($defaultlanguage)]"/>
      </xsl:otherwise>
   </xsl:choose>	
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
