<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : lenya-header.xsl
    Created on : 14. Mai 2003, 16:38
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya="http://lenya.org/2003/"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="lenya"
    >

<xsl:template match="lenya:document-title">
  <h1><xsl:apply-templates/></h1>
</xsl:template>

<xsl:template match="lenya:abstract">
  <p class="abstract"><xsl:apply-templates/></p>
</xsl:template>

</xsl:stylesheet> 
