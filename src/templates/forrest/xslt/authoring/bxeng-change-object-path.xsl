<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0" version="1.0">
  <xsl:param name="documentid" />
  <xsl:variable name="nodeid">
    <xsl:call-template name="getnodeid">
      <xsl:with-param name="url" select="$documentid" />
    </xsl:call-template>
  </xsl:variable>
  <xsl:template match="lenya:object/@data">
    <xsl:attribute name="data">
      <xsl:value-of select="concat($nodeid, '/', .)" />
    </xsl:attribute>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
  <xsl:template name="getnodeid">
    <xsl:param name="url" />
    <xsl:param name="prefix">.xml</xsl:param>
    <xsl:param name="langprefix">_</xsl:param>
    <xsl:param name="slash">/</xsl:param>
    <xsl:choose>
      <xsl:when test="contains($url, $slash)">
        <xsl:call-template name="getnodeid">
          <xsl:with-param name="url" select="substring-after($url, $slash)" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of
        select="$url" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>

