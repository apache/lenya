<?xml version="1.0"?>

<!-- $Id: introspection.xsl,v 1.1 2004/02/19 08:51:35 michi Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
>

<xsl:output method="xml"/>

<xsl:param name="editURL" select="'http://no-such-url'"/>

<xsl:template match="atom:introspection" xmlns:atom="http://purl.org/atom/ns#">
  <introspection xmlns="http://purl.org/atom/ns#">
    <xsl:apply-templates/>
    <edit-entry><xsl:value-of select="$editURL"/></edit-entry>
  </introspection>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
