<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >

<xsl:template match="/nav:site">
  <div id="tabsall">
     <div id="tabslevel">
       <xsl:apply-templates select="nav:node" mode="tabslevel"/>
     </div>
     <xsl:apply-templates select="nav:node"/>
  </div>
</xsl:template>

<xsl:template match="nav:node[descendant-or-self::nav:node[@current = 'true']]">
  <div id="tabslevel">
    <xsl:apply-templates select="nav:node" mode="tabslevel"/>
  </div>
  <xsl:apply-templates select="nav:node"/>
</xsl:template>

<xsl:template match="nav:node" mode="tabslevel">
       <xsl:choose>
        <xsl:when test="@current = 'true'">
          <xsl:call-template name="tab-selected"/>
        </xsl:when>
        <xsl:when test="descendant-or-self::nav:node[@current = 'true']">
          <xsl:call-template name="tablevel-selected"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="tab"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>&#160;</xsl:text>
</xsl:template>

<xsl:template match="nav:node" priority="-1"/>


<xsl:template name="tab">
  <span class="tabsall-tab"><xsl:call-template name="label"/></span>
</xsl:template>

<xsl:template name="tab-selected">
  <span class="tabsall-selected"><xsl:call-template name="label"/></span>
</xsl:template>
<xsl:template name="tablevel-selected">
  <span class="tabsall-levelselected"><xsl:call-template name="label"/></span>
</xsl:template>

<xsl:template name="label">
   <xsl:choose>
    <xsl:when test="@current='true'"><xsl:apply-templates select="nav:label"/></xsl:when>
    <xsl:otherwise><a href="{@href}"><xsl:apply-templates select="nav:label"/></a></xsl:otherwise>
  </xsl:choose>	  
</xsl:template>

<xsl:template match="nav:label">
  <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="@*|node()" priority="-2"/>

</xsl:stylesheet> 
