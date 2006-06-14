<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    >

<xsl:param name="current"/>
<xsl:variable name="language"><xsl:value-of select="/index/@language"/></xsl:variable>
<xsl:variable name="currentfull">/<xsl:value-of select="$current"/></xsl:variable>

<xsl:template match="/index">
  <div id="tabsall">
     <div id="tabslevel">
       <xsl:apply-templates select="resource" mode="tabslevel"/>
     </div>
     <xsl:apply-templates select="resource"/>
  </div>
</xsl:template>

<xsl:template match="resource[descendant-or-self::resource[@fullid = $currentfull]]">
  <div id="tabslevel">
    <xsl:apply-templates select="resource" mode="tabslevel"/>
  </div>
  <xsl:apply-templates select="resource"/>
</xsl:template>

<xsl:template match="resource" mode="tabslevel">
       <xsl:choose>
        <xsl:when test="@fullid = $currentfull">
          <xsl:call-template name="tab-selected"/>
        </xsl:when>
        <xsl:when test="descendant-or-self::resource[@fullid = $currentfull]">
          <xsl:call-template name="tablevel-selected"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="tab"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>&#160;</xsl:text>
</xsl:template>

<xsl:template match="resource" priority="-1"/>

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
<xsl:variable name="extension"><xsl:choose>
<xsl:when test="@extension"><xsl:value-of select="@extension"/></xsl:when>
<xsl:otherwise>html</xsl:otherwise>
</xsl:choose></xsl:variable>

   <xsl:choose>
    <xsl:when test="@fullid = $currentfull"><xsl:value-of select="@title"/>
</xsl:when>
    <xsl:otherwise>     
<xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@fullid"/>_<xsl:value-of select="$language"/>.<xsl:value-of select="$extension"/></xsl:attribute>
        <xsl:value-of select="@title"/>
     </xsl:element>
</xsl:otherwise>
  </xsl:choose>	  
</xsl:template>


<xsl:template match="@*|node()" priority="-2"/>

</xsl:stylesheet> 
