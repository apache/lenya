<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="nav"
    >
    
<xsl:template match="nav:site">
  <div id="menu">
    <xsl:apply-templates select="nav:node"/>
  </div>
</xsl:template>

<xsl:template match="nav:node[@visibleinnav = 'false']"/>

<xsl:template match="nav:node">
  <xsl:choose>
    <xsl:when test="descendant-or-self::nav:node[@current = 'true']">
      <div class="menublock-selected" level="{count(ancestor-or-self::nav:node)}">
        <xsl:call-template name="item"/>
        <xsl:apply-templates select="nav:node"/>
      </div>
    </xsl:when>
    <xsl:otherwise>
      <div class="menublock" level="{count(ancestor-or-self::nav:node)}">
        <xsl:call-template name="item"/>
<!--        <xsl:apply-templates select="nav:node"/> -->
      </div>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="item">
      <xsl:choose>
         <xsl:when test="@current = 'true'">
           <xsl:call-template name="item-selected"/>
         </xsl:when>
         <xsl:otherwise>
           <xsl:call-template name="item-default"/>
         </xsl:otherwise>
       </xsl:choose>
</xsl:template>


<xsl:template name="item-default">
  <div class="menuitem" level="{count(ancestor-or-self::nav:node)}" id="{@id}">
    <a href="{@fullhref}"><xsl:apply-templates select="nav:label"/></a>
  </div>
</xsl:template>
    
    
<xsl:template name="item-selected">
  <div class="menuitem-selected" level="{count(ancestor-or-self::nav:node)}" id="{@id}">
    <xsl:apply-templates select="nav:label"/>
  </div>
</xsl:template>


<xsl:template match="nav:label">
  <xsl:value-of select="."/>
</xsl:template>
    
    
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet> 
