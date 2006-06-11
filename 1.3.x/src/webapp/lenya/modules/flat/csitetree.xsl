<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tree="http://apache.org/cocoon/lenya/sitetree/1.0"
>
<xsl:param name="area" select="live"/>

<xsl:template match="tree:site">
<pages>
<xsl:apply-templates select="tree:node"/>
</pages>
</xsl:template>

<xsl:template match="tree:node">
   <xsl:param name="path" select="''"/>
   <xsl:variable name="pos"><xsl:number count="//tree:node" level="any"/></xsl:variable>
   <xsl:apply-templates select="tree:label">
      <xsl:with-param name="path" select="concat($path, '/', @id)"/>
      <xsl:with-param name="visible" select="@visibleinnav"/>
      <xsl:with-param name="href" select="@href"/>
      <xsl:with-param name="pos" select="$pos"/>
   </xsl:apply-templates>
   <xsl:apply-templates select="tree:node">
      <xsl:with-param name="path" select="concat($path, '/', @id)"/>
   </xsl:apply-templates>
</xsl:template>

<xsl:template match="tree:label">
  <xsl:param name="path" select="''"/>
  <xsl:param name="visible" select="''"/>
  <xsl:param name="href" select="''"/>
  <xsl:param name="pos" select="1"/>
<xsl:element name="page">
<xsl:attribute name="position"><xsl:value-of select="$pos"/></xsl:attribute>
<xsl:attribute name="area"><xsl:value-of select="$area"/></xsl:attribute>
<xsl:attribute name="language"><xsl:value-of select="@xml:lang"/></xsl:attribute>
<xsl:attribute name="id"><xsl:value-of select="$path"/></xsl:attribute>
<xsl:attribute name="idl"><xsl:value-of select="$path"/>_<xsl:value-of select="@xml:lang"/></xsl:attribute>
<xsl:attribute name="visible"><xsl:value-of select="$visible"/></xsl:attribute>
<xsl:attribute name="navtitle"><xsl:value-of select="."/></xsl:attribute>
<xsl:if test="$href">
<xsl:attribute name="href"><xsl:value-of select="$href"/></xsl:attribute>
</xsl:if>
</xsl:element>
</xsl:template>
   
</xsl:stylesheet> 