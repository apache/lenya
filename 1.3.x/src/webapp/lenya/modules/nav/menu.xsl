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
  <div id="menu">
    <xsl:apply-templates select="resource"/>
  </div>
</xsl:template>

<xsl:template match="resource">
  <xsl:choose>
    <xsl:when test="descendant-or-self::resource[@fullid = $currentfull]">
      <div class="menublock-selected" level="{count(ancestor-or-self::nav:node)}">
        <xsl:call-template name="item"/>
        <xsl:apply-templates select="resource"/>
      </div>
    </xsl:when>
    <xsl:otherwise>
      <div class="menublock" level="{count(ancestor-or-self::resource)}">
        <xsl:call-template name="item"/>
      </div>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="item">
      <xsl:choose>
         <xsl:when test="@fullid = $currentfull">
           <xsl:call-template name="item-selected"/>
         </xsl:when>
         <xsl:otherwise>
           <xsl:call-template name="item-default"/>
         </xsl:otherwise>
       </xsl:choose>
</xsl:template>

<xsl:template name="item-default">
   <xsl:variable name="extension"><xsl:choose>
<xsl:when test="@extension"><xsl:value-of select="@extension"/></xsl:when>
<xsl:otherwise>html</xsl:otherwise>
</xsl:choose></xsl:variable>
  <div class="menuitem" level="{count(ancestor-or-self::resource)}" id="{@id}">
     <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="@fullid"/>_<xsl:value-of select="$language"/>.<xsl:value-of select="$extension"/></xsl:attribute>
        <xsl:value-of select="@title"/>
     </xsl:element>
  </div>
</xsl:template>

<xsl:template name="item-selected">
  <div class="menuitem-selected" level="{count(ancestor-or-self::resource)}" id="{@id}">
     <xsl:value-of select="@title"/>
  </div>
</xsl:template>
</xsl:stylesheet> 
