<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:echo="http://purl.org/atom/ns#"
	xmlns="http://purl.org/atom/ns#">

<xsl:template match="/">
  <xsl:apply-templates select="echo:feed" />
</xsl:template>                                                                                                                             

<xsl:template match="echo:feed">
<search-results xmlns="http://purl.org/atom/ns#">
  <xsl:for-each select="echo:entry">
<entry>
<title><xsl:value-of select="echo:title"/></title>
<id><xsl:value-of select="echo:link"/>atomapi/entries/<xsl:value-of select="echo:id"/>/index.xml</id>
</entry>
  </xsl:for-each>
</search-results>
</xsl:template>


</xsl:stylesheet>
