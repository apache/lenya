<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:h="http://apache.org/cocoon/request/2.0"
    exclude-result-prefixes="h"

>
<xsl:param name="publication"/>

<xsl:key name="resources" match="resource" use="@unid"/>

<xsl:template match="/">
<xsl:apply-templates select="data/h:request/h:requestParameters"/>
</xsl:template>

<xsl:template match="h:requestParameters">
<xsl:variable name="name"><xsl:value-of select="h:parameter[@name='id']/h:value"/></xsl:variable>
<xsl:variable name="data"><xsl:value-of select="h:parameter[@name='relations']/h:value"/></xsl:variable>
<xsl:element name="resources">
<xsl:attribute name="structure"><xsl:value-of select="$name"/></xsl:attribute>
<xsl:element name="newresource">
<xsl:attribute name="unid"><xsl:value-of select="$publication"/>+<xsl:value-of select="$name"/></xsl:attribute>
<xsl:attribute name="type">relate</xsl:attribute>
<xsl:attribute name="id"><xsl:value-of select="$name"/></xsl:attribute>
</xsl:element>

<xsl:call-template name="resource">
   <xsl:with-param name="data" select="$data"/>
</xsl:call-template>
</xsl:element>
</xsl:template>

<xsl:template name="resource">
<xsl:param name="data"/>
<xsl:param name="parent"/>
<xsl:variable name="d1b1"><xsl:value-of select="substring-before($data, '&lt;')"/></xsl:variable>
<xsl:variable name="d1a1"><xsl:value-of select="substring-after($data, '&lt;')"/></xsl:variable>
<xsl:variable name="d2b1"><xsl:value-of select="substring-before($data, '>')"/></xsl:variable>
<xsl:variable name="d2a1"><xsl:value-of select="substring-after($data, '>')"/></xsl:variable>
<xsl:variable name="id"><xsl:value-of select="key('resources', $d1b1)[1]/@id"/></xsl:variable>
<xsl:variable name="fullid"><xsl:value-of select="$parent"/>/<xsl:value-of select="$id"/></xsl:variable>
<xsl:variable name="grandparent"><xsl:call-template name="beforelastslash">
   <xsl:with-param name="string" select="$parent"/>
</xsl:call-template></xsl:variable>
<xsl:choose>
   <xsl:when test="string-length($d2b1) = 0">
      <xsl:text disable-output-escaping="yes">&lt;/resource&gt;</xsl:text>
      <xsl:if test="string-length($d2a1) &gt; 0">
         <xsl:call-template name="resource">
            <xsl:with-param name="data" select="$d2a1"/>
            <xsl:with-param name="parent" select="$grandparent"/>
         </xsl:call-template>
      </xsl:if>
   </xsl:when>
   <xsl:otherwise>
      <xsl:text disable-output-escaping="yes">&lt;resource unid="</xsl:text><xsl:value-of select="$d1b1"/>" id="<xsl:value-of select="$id"/>" full="<xsl:value-of select="$fullid"/>"<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
      <xsl:call-template name="resource">
         <xsl:with-param name="data" select="$d1a1"/>
         <xsl:with-param name="parent" select="$fullid"/>
      </xsl:call-template>
   </xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template name="beforelastslash">
<xsl:param name="string"/>
<xsl:variable name="after"><xsl:value-of select="substring-after($string, '/')"/></xsl:variable>
<xsl:value-of select="substring-before($string, '/')"/><xsl:if test="contains($after, '/')">/<xsl:call-template name="beforelastslash">
   <xsl:with-param name="string" select="$after"/>
</xsl:call-template></xsl:if></xsl:template>


</xsl:stylesheet> 
