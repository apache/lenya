<?xml version="1.0" encoding="utf-8"?>

<!--
A simple callable template that renders a logo for an entity. The logo will 
be a hyperlink and may include an image (with width and height if specified)
or else it will just include the specified text.

Note that text and image are mandatory parts of the template.
-->

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template name="renderlogo">
    <xsl:param name="name"/>
    <xsl:param name="url"/>
    <xsl:param name="logo"/>
    <xsl:param name="width"/>
    <xsl:param name="height"/>
    <xsl:param name="root"/>
    <a href="{$url}">
      <xsl:choose>
        <xsl:when test="$logo and not($logo = '')">
          <img alt="{$name}" class="logoImage" border="0">
            <xsl:attribute name="src">
	      <xsl:if test="not(starts-with($logo, 'http://'))"><xsl:value-of select="$root"/></xsl:if>
              <xsl:value-of select="$logo"/>
            </xsl:attribute>
            <xsl:if test="$width">
              <xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="$height">
              <xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute>
            </xsl:if>
	  </img>
        </xsl:when>
        <xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
      </xsl:choose>
    </a>
  </xsl:template>

</xsl:stylesheet>
