<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="hits">
    <hits total-hits="{../@total-hits}">
      <xsl:choose>
        <xsl:when test="../../search/sort-by='score'">
          <xsl:for-each select="hit">
            <xsl:sort data-type="number" order="descending" select="score"/>
<!--
            <xsl:sort data-type="number" order="ascending" select="score"/>
-->
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </xsl:when>
        <xsl:when test="../../search/sort-by='title'">
          <xsl:for-each select="hit">
<!--
            <xsl:sort data-type="text" order="descending" select="title"/>
-->
            <xsl:sort data-type="text" order="ascending" select="title"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="hit">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </xsl:otherwise>
      </xsl:choose>
    </hits>
  </xsl:template>

  <xsl:template match="* | @*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
