<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<!-- FIXME: works bugfree, but is kind of ugly, because each element will list all namespaces -->
<!--
<xsl:template match="//*" mode="mixedcontent" priority="-1">
<xsl:variable name="prefix"><xsl:if test="contains(name(),':')">:<xsl:value-of select="substring-before(name(),':')"/></xsl:if></xsl:variable>

<xsl:choose>
<xsl:when test="node()">
<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/>

<xsl:apply-templates select="@*[local-name()!='tagID']" mode="mixedcontent"/>

<xsl:for-each select="namespace::*">
<xsl:variable name="prefix"><xsl:if test="local-name() != ''">:<xsl:value-of select="local-name()"/></xsl:if></xsl:variable>
<xsl:if test=". != 'http://www.w3.org/XML/1998/namespace'">
<xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="."/><xsl:text>"</xsl:text>
</xsl:if>
</xsl:for-each>

<xsl:text>&gt;</xsl:text>

<xsl:apply-templates select="node()" mode="mixedcontent"/>

<xsl:text>&lt;/</xsl:text><xsl:value-of select="name()"/><xsl:text>&gt;</xsl:text>

</xsl:when>

<xsl:otherwise>

<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/>

<xsl:apply-templates select="@*[local-name()!='tagID']" mode="mixedcontent"/>

<xsl:for-each select="namespace::*">
<xsl:variable name="prefix"><xsl:if test="local-name() != ''">:<xsl:value-of select="local-name()"/></xsl:if></xsl:variable>
<xsl:if test=". != 'http://www.w3.org/XML/1998/namespace'">
<xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="."/><xsl:text>"</xsl:text>
</xsl:if>
</xsl:for-each>

<xsl:text>/&gt;</xsl:text></xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="@*" mode="mixedcontent"><xsl:text> </xsl:text><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:template>
-->








<!-- FIXME: namespaces occur multiple times, e.g. xlink:show="" xlink:href="" xmlns:xlink="" xmlns:xlink="" -->
<xsl:template match="//*" mode="mixedcontent" priority="-1">
<xsl:variable name="prefix"><xsl:if test="contains(name(),':')">:<xsl:value-of select="substring-before(name(),':')"/></xsl:if></xsl:variable>

<xsl:choose>
<xsl:when test="node()">
<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/><xsl:if test="namespace-uri()"><xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="namespace-uri()"/>"</xsl:if><xsl:apply-templates select="@*[local-name()!='tagID']" mode="mixedcontent"/><xsl:text>&gt;</xsl:text>
<xsl:apply-templates select="node()" mode="mixedcontent"/>
<xsl:text>&lt;/</xsl:text><xsl:value-of select="name()"/><xsl:text>&gt;</xsl:text>
</xsl:when>

<xsl:otherwise>
<xsl:text>&lt;</xsl:text><xsl:value-of select="name()"/><xsl:if test="namespace-uri()"><xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="namespace-uri()"/>"</xsl:if><xsl:apply-templates select="@*[local-name()!='tagID']" mode="mixedcontent"/><xsl:text> /&gt;</xsl:text>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="@*" mode="mixedcontent">
<xsl:variable name="prefix"><xsl:if test="contains(name(),':')">:<xsl:value-of select="substring-before(name(),':')"/></xsl:if></xsl:variable>

<xsl:text> </xsl:text><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"<xsl:if test="namespace-uri()"><xsl:text> </xsl:text>xmlns<xsl:value-of select="$prefix"/>="<xsl:value-of select="namespace-uri()"/>"</xsl:if></xsl:template>












<xsl:template match="text()" mode="mixedcontent">
  <xsl:call-template name="search-and-replace">
    <xsl:with-param name="string" select="."/>
  </xsl:call-template>
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
