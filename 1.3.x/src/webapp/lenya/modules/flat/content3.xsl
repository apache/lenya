<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
<!-- Add attributes and index fields. Cleanup.  -->

<xsl:template match="/content">
  <xsl:copy>
<xsl:apply-templates/>
<!--
<xsl:apply-templates select="@*"/>
   <xsl:apply-templates select="resource[@id='/index']"/>
   <xsl:apply-templates select="resource[@id!='/index']"/>
-->
</xsl:copy>
</xsl:template>

<xsl:template match="resource">
<xsl:variable name="unid"><xsl:value-of select="format-number(position(), '0000')"/></xsl:variable>
  <xsl:copy><xsl:apply-templates select="@id"/><xsl:apply-templates select="@*"/>
<xsl:attribute name="unid"><xsl:value-of select="$unid"/></xsl:attribute>
<xsl:attribute name="filename">/resource.xml</xsl:attribute>
<xsl:choose>
<xsl:when test="@id='/index'"><xsl:attribute name="doctype">home</xsl:attribute></xsl:when>
<xsl:when test="@type='xml'"><xsl:attribute name="doctype">xhtml</xsl:attribute></xsl:when>
</xsl:choose>

<xsl:for-each select="translation/file">
<xsl:element name="index">
<xsl:attribute name="name"><xsl:value-of select="@area"/></xsl:attribute>
<xsl:attribute name="position"><xsl:value-of select="@position"/></xsl:attribute>
</xsl:element>
<xsl:if test="@visible='true'">
<xsl:element name="index">
<xsl:attribute name="name"><xsl:value-of select="@area"/>menu</xsl:attribute>
<xsl:attribute name="position"><xsl:value-of select="@position"/></xsl:attribute>
</xsl:element>
</xsl:if>
</xsl:for-each>
        <xsl:apply-templates select="translation"/>
</xsl:copy>
</xsl:template>

<xsl:template match="translation">

<xsl:variable name="livef"><xsl:value-of select="file[@area='live']/@time"/></xsl:variable>
<xsl:variable name="live"><xsl:choose>
   <xsl:when test="string-length($livef) &gt; 0"><xsl:value-of select="$livef"/></xsl:when>
   <xsl:otherwise>1</xsl:otherwise>
</xsl:choose></xsl:variable>

<xsl:variable name="editf"><xsl:value-of select="file[@area='authoring']/@time"/></xsl:variable>
<xsl:variable name="edit"><xsl:choose>
   <xsl:when test="string-length($editf) &gt; 0"><xsl:value-of select="$editf"/></xsl:when>
   <xsl:otherwise><xsl:value-of select="$live"/></xsl:otherwise>
</xsl:choose></xsl:variable>

  <xsl:copy><xsl:apply-templates select="@*"/>
<xsl:attribute name="filename">/<xsl:value-of select="@language"/>/translation.xml</xsl:attribute>
<xsl:attribute name="edit"><xsl:value-of select="$edit"/></xsl:attribute>
<xsl:attribute name="live"><xsl:value-of select="$live"/></xsl:attribute>
        <xsl:apply-templates select="file"/>
</xsl:copy>
</xsl:template>

<xsl:template match="file">
  <xsl:copy>
<xsl:apply-templates select="@*"/>
<xsl:attribute name="oldfilename"><xsl:value-of select="@filename"/></xsl:attribute>
<xsl:attribute name="filename">/<xsl:value-of select="@language"/>/<xsl:choose>
<xsl:when test="@time"><xsl:value-of select="@time"/></xsl:when>
<xsl:otherwise>1</xsl:otherwise>
</xsl:choose>.xml</xsl:attribute>
</xsl:copy>
</xsl:template>

<xsl:template match="file/@area"/>
<xsl:template match="file/@filename"/>
<xsl:template match="file/@language"/>
<xsl:template match="file/@time"/>
<xsl:template match="file/@visible"/>
<xsl:template match="file/@position"/>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 