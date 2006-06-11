<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
<!-- Combine resources. -->

<xsl:template match="/content">
<resources>
<xsl:apply-templates select="resources"/>
</resources>
</xsl:template>

<xsl:template match="resources">
<xsl:apply-templates select="resource"></xsl:apply-templates>
</xsl:template>

<xsl:template match="resource">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="resource"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()" mode="resource">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="resource"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()" priority="-1"/>

</xsl:stylesheet> 