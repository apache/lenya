<?xml version="1.0"?>
<!--
Stylesheet to recursively append all @href's in a tree. Eg, given as input:

<site href="">
  <community href="community/">
    <faq href="faq.html">
      <how_can_I_help href="#help"/>
    </faq>
  </community>
</site>

Output would be:

<site href="">
  <community href="community/">
    <faq href="community/faq.html">
      <how_can_I_help href="community/faq.html#help"/>
    </faq>
  </community>
</site>

This is applied to site.xml to generate the 'abs-linkmap' URIs in the sitemap.

Jeff Turner <jefft@apache.org>
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- Recursive template to collate @href's -->
  <xsl:template name="absolutize">
    <xsl:param name="node"/>
    <!-- Only append ancestor hrefs if we're not a http(s): URL -->
    <xsl:if test="not(starts-with($node/@href, 'http:') or starts-with($node/@href, 'https:'))">
      <xsl:if test="$node/..">
        <xsl:call-template name="absolutize">
          <xsl:with-param name="node" select="$node/.."/>
        </xsl:call-template>
      </xsl:if>
    </xsl:if>
    <xsl:value-of select="$node/@href"/>

  </xsl:template>

  <xsl:template match="@href">
    <xsl:attribute name="href">
      <xsl:call-template name="absolutize">
        <xsl:with-param name="node" select=".."/>
      </xsl:call-template>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
