<?xml version="1.0"?>
<!--
Stylesheet which adds ..'s to @href attributes, to make the URIs relative to
some root.  Eg, given an 'absolutized' file (from absolutize-linkmap.xsl):

<site href="">
  <community href="community/">
    <faq href="community/faq.html">
      <how_can_I_help href="community/faq.html#help"/>
    </faq>
  </community>
</site>

if $path was 'community/', then '../' would be added to each href:

<site href="../">
  <community href="../community/">
    <faq href="../community/">
      <how_can_I_help href="../community/faq.html#help"/>
    </faq>
  </community>
</site>


Jeff Turner <jefft@apache.org>
-->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="path"/>

  <xsl:include href="dotdots.xsl"/>

  <!-- Path to site root, eg '../../' -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:template match="@href">
    <xsl:attribute name="href">
      <xsl:choose>
        <xsl:when test="starts-with(., 'http:') or starts-with(., 'https:')">
          <xsl:value-of select="."/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$root"/><xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
      </xsl:attribute>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
