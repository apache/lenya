<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:output indent="no"/>

<!-- FIXME: CDATA is also being modified by replace-predefined-entities.xsl, which actually shouldn't -->


<xsl:template match="text()">
  <xsl:call-template name="search-and-replace">
    <xsl:with-param name="string" select="."/>
  </xsl:call-template>
</xsl:template>



<xsl:template match="node()|@*" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="node()|@*"/>
  </xsl:copy>
</xsl:template>




<xsl:template name="search-and-replace">
<xsl:param name="string"/>

<xsl:choose>
<xsl:when test="contains($string, '&lt;')">
  <xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-before($string, '&lt;')"/></xsl:call-template>&amp;lt;<xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-after($string, '&lt;')"/></xsl:call-template>
</xsl:when>
<xsl:when test="contains($string, '&gt;')">
  <xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-before($string, '&gt;')"/></xsl:call-template>&amp;gt;<xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-after($string, '&gt;')"/></xsl:call-template>
</xsl:when>
<xsl:when test="contains($string, '&amp;')">
  <xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-before($string, '&amp;')"/></xsl:call-template>&amp;amp;<xsl:call-template name="search-and-replace"><xsl:with-param name="string" select="substring-after($string, '&amp;')"/></xsl:call-template>
</xsl:when>
<!-- FIXME: &quot; and &apos; -->
<xsl:otherwise>
  <xsl:value-of select="$string"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template> 

</xsl:stylesheet>
