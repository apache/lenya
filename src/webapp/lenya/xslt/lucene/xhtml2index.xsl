<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:lucene="http://apache.org/cocoon/lucene/1.0"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
>

<xsl:param name="index"/>
<xsl:param name="uri"/>
<xsl:param name="id"/>
<xsl:param name="action"/>

<xsl:variable name="boost" select="number(/descendant-or-self::dc:rights)"/>  

<xsl:template match="/">  
  <xsl:choose>
    <xsl:when test="$action = 'delete'">
       <lucene:delete indexid="{$index}">
        <lucene:document uid="{$id}"/>
      </lucene:delete>     
    </xsl:when>
    <xsl:when test="$action = 'index'">        
      <lucene:index clear="false" indexid="{$index}" merge-factor="100">
        <lucene:document uid="{$id}">
          <lucene:field name="url" boost="{$boost}"><xsl:value-of select="$uri"/></lucene:field>
          <xsl:apply-templates/>
        </lucene:document>
      </lucene:index>  
    </xsl:when>
  </xsl:choose>

</xsl:template>

<xsl:template match="dc:rights" priority="1">
</xsl:template>

<xsl:template match="xhtml:body" priority="1">
  <lucene:field name="body" boost="{$boost}"><xsl:value-of select="descendant-or-self::*"/></lucene:field>
</xsl:template>

<xsl:template match="dc:title" priority="1">
  <lucene:field name="title" boost="{$boost}"><xsl:value-of select="."/></lucene:field>
</xsl:template>

<xsl:template match="dc:description" priority="1">
  <lucene:field name="description" boost="{$boost}"><xsl:value-of select="."/></lucene:field>
</xsl:template>

<xsl:template match="dc:subject" priority="1">
  <lucene:field name="subject" boost="{$boost}"><xsl:value-of select="."/></lucene:field>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
    <xsl:apply-templates/>
</xsl:template>


</xsl:stylesheet>
