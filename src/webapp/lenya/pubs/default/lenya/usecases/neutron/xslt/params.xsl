<?xml version="1.0" encoding="ISO-8859-1" ?>

<!-- $Id: includeAssetMetaData.xsl,v 1.4 2004/06/23 16:07:25 edith Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:xslt="http://www.unizh.ch/MetaTransform"

  >

  <xsl:namespace-alias stylesheet-prefix="xslt" result-prefix="xsl"/>

  <xsl:param name="root"/>
  <xsl:param name="document-id"/>
  <xsl:param name="document-type"/>
  <xsl:param name="url"/>
  <xsl:param name="language"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="xsl:param[@name='root']">
    <xslt:param name="root"><xsl:value-of select="$root"/></xslt:param>
  </xsl:template>

  <xsl:template match="xsl:param[@name='document-id']">
    <xslt:param name="document-id"><xsl:value-of select="$document-id"/></xslt:param>
  </xsl:template>

  <xsl:template match="xsl:param[@name='document-type']">
    <xslt:param name="document-type"><xsl:value-of select="$document-type"/></xslt:param>
  </xsl:template>

  <xsl:template match="xsl:param[@name='url']">
    <xslt:param name="url"><xsl:value-of select="$url"/></xslt:param>
  </xsl:template>

  <xsl:template match="xsl:param[@name='language']">
    <xslt:param name="language"><xsl:value-of select="$language"/></xslt:param>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
