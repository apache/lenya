<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
<!-- Group into flat structure -->

<xsl:key name="parent" match="resource" use="@parent" />

<xsl:template match="/resources">
<resources>
    <xsl:attribute name="structure"><xsl:value-of select="@name"/></xsl:attribute>
    <xsl:for-each select="key('parent', '')">
       <xsl:sort select="@position" data-type="number"/>
       <xsl:call-template name="subresource">
          <xsl:with-param name="parent" select="@unid"/>
       </xsl:call-template>
    </xsl:for-each>
</resources>
</xsl:template>

<xsl:template match="@parent"/>
<xsl:template match="@position"/>
<xsl:template match="@structure"/>

<xsl:template name="subresource">
   <xsl:param name="parent"/>
   <xsl:variable name="unid"><xsl:value-of select="@unid"/></xsl:variable>
   <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="key('parent', $unid)">
       <xsl:sort select="@position" data-type="number"/>
          <xsl:call-template name="subresource">
             <xsl:with-param name="parent" select="$unid"/>
          </xsl:call-template>
      </xsl:for-each>
   </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="resource"/>
  </xsl:copy>
</xsl:template>
</xsl:stylesheet>