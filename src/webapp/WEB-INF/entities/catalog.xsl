<?xml version="1.0" encoding="UTF-8" ?>
<!-- $Id: catalog.xsl,v 1.1 2003/07/23 13:43:45 gregor Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output omit-xml-declaration="yes"/>    

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
<!-- Insert new catalogs -->
</xsl:template>
   
</xsl:stylesheet> 
