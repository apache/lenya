<?xml version="1.0" encoding="UTF-8" ?>

<!-- $Id: lenya-header.xsl,v 1.4 2003/08/08 13:20:46 gregor Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="dc"
    >

<xsl:template match="dc:title">
  <h1><xsl:apply-templates/></h1>
</xsl:template>

<xsl:template match="dc:description">
  <p class="abstract"><xsl:apply-templates/></p>
</xsl:template>

</xsl:stylesheet> 
