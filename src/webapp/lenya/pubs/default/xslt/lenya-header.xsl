<?xml version="1.0" encoding="UTF-8" ?>

<!-- $Id: lenya-header.xsl,v 1.5 2003/11/04 10:10:01 andreas Exp $ -->
<!-- RKUPATCH -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="dc"
    >

<xsl:template match="dc:*[not(node())]"/>

<xsl:template match="dc:title">
  <h1 bxe_xpath="/simple:simple-document/lenya:meta/dc:title"><xsl:apply-templates/></h1>
</xsl:template>

<xsl:template match="dc:description">
  <p class="abstract"><xsl:apply-templates/></p>
</xsl:template>

</xsl:stylesheet> 