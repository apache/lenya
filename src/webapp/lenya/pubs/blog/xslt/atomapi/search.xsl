<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:atom="http://purl.org/atom/ns#"
>

<xsl:param name="serverName"/>
<xsl:param name="serverPort"/>
<xsl:param name="contextPath"/>
<xsl:param name="publicationId"/>

<xsl:template match="/">
<search-results xmlns="http://purl.org/atom/ns#">
  <xsl:apply-templates select="atom:feed" />
</search-results>
</xsl:template>

<xsl:template match="atom:feed">
<xsl:for-each select="atom:entry">
  <entry>
    <title><xsl:value-of select="atom:title"/></title>
    <id>http://<xsl:value-of select="$serverName"/>:<xsl:value-of select="$serverPort"/><xsl:value-of select="$contextPath"/>/<xsl:value-of select="$publicationId"/>/atomapi/entries/<xsl:value-of select="atom:id"/>/index.xml</id>
  </entry>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
