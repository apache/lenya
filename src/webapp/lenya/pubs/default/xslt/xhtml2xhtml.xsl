<?xml version="1.0" encoding="UTF-8" ?>

<!--
$Id: xhtml2xhtml.xsl,v 1.5 2004/02/27 17:14:45 gregor Exp $
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" 
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    exclude-result-prefixes="xhtml lenya dc"
    >

<xsl:param name="rendertype" select=""/>
<xsl:param name="nodeid"/>

<xsl:template match="/xhtml:html">
  <div id="body">
    <xsl:if test="$rendertype = 'edit'">
      <xsl:attribute name="bxe_xpath">/xhtml:html/xhtml:body</xsl:attribute>
    </xsl:if>
    <xsl:apply-templates select="xhtml:body/node()"/>
  </div>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

  <xsl:template name="substring-after-last">
    <xsl:param name="input"/>
    <xsl:param name="substr"/>
    <xsl:variable name="temp" select="substring-after($input, $substr)"/>
    <xsl:choose>
      <xsl:when test="$substr and contains($temp, $substr)">
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="$temp"/>
          <xsl:with-param name="susbtr" select="$substr"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$temp"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="lenya:asset">
    <xsl:variable name="extent">
      <xsl:value-of select="dc:metadata/dc:extent"/>
    </xsl:variable>
    <xsl:variable name="suffix">
      <xsl:call-template name="substring-after-last">
        <xsl:with-param name="input" select="@src"/>
        <xsl:with-param name="substr">.</xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    <div class="asset">
        <xsl:text>&#160;</xsl:text>
        <a href="{$nodeid}/{@src}">
          <xsl:value-of select="text()"/>
        </a>
        (<xsl:value-of select="format-number($extent div 1024, '#.#')"/>KB)
    </div>
  </xsl:template>
  
    <xsl:template match="xhtml:object" priority="3">
      <xsl:when test="@href != ''">
        <a href="{@href}">
          <img border="0">
            <xsl:attribute name="src">
              <xsl:value-of select="$nodeid"/>/<xsl:value-of select="@data"/>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <!-- the overwritten title (stored in @name) has precedence over dc:title -->
              <xsl:choose>
                <xsl:when test="@name != ''">
                  <xsl:value-of select="@name"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="dc:metadata/dc:title"/>                    
                </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
             <xsl:if test="string(@height)">
              <xsl:attribute name="height">
                <xsl:value-of select="@height"/>
              </xsl:attribute>
            </xsl:if> 
            <xsl:if test="string(@width)">
              <xsl:attribute name="width">
                <xsl:value-of select="@width"/>
              </xsl:attribute>
            </xsl:if>         
          </img>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <img border="0">
          <xsl:attribute name="src">
            <xsl:value-of select="$nodeid"/>/<xsl:value-of select="@data"/>
          </xsl:attribute>
          <xsl:attribute name="alt">
              <!-- the overwritten title (stored in @name) has precedence over dc:title -->
              <xsl:choose>
                <xsl:when test="@name != ''">
                  <xsl:value-of select="@name"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="dc:metadata/dc:title"/>                    
                </xsl:otherwise>
                </xsl:choose>
          </xsl:attribute>
          <xsl:if test="string(@height)">
              <xsl:attribute name="height">
                <xsl:value-of select="@height"/>
              </xsl:attribute>
        </xsl:if> 
        <xsl:if test="string(@width)">
              <xsl:attribute name="width">
                <xsl:value-of select="@width"/>
              </xsl:attribute>
        </xsl:if>         
        </img>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>  

  <xsl:template match="dc:metadata"/>
  
</xsl:stylesheet> 