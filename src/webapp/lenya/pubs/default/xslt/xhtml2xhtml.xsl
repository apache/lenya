<?xml version="1.0" encoding="UTF-8" ?>

<!--
$Id: xhtml2xhtml.xsl,v 1.3 2004/02/04 20:50:30 gregor Exp $
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xhtml"
    >

<xsl:param name="rendertype" select=""/>

<xsl:template match="/xhtml:html">
  <div id="body">
    <xsl:if test="$rendertype = 'edit'">
      <xsl:attribute name="bxe_xpath">/xhtml:html/xhtml:body</xsl:attribute>
    </xsl:if>
    <xsl:apply-templates select="xhtml:body/node()"/>
  </div>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 