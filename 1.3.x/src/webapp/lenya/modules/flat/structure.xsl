<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
<!-- Group into flat structure -->

<xsl:key name="structure" match="resource" use="@structure" />

<xsl:template match="/relations">
    <xsl:variable name="home"><xsl:value-of select="@home"/></xsl:variable>
<structures>
<xsl:for-each select="resource[generate-id() = generate-id(key('structure', @structure)[1])]">
    <xsl:sort select="@position" />
    <xsl:variable name="structure"><xsl:value-of select="@structure"/></xsl:variable>
    <structure name="{$structure}" home="{$home}">
    <xsl:for-each select="key('structure', $structure)">
       <resource>
         <xsl:apply-templates select="@*|node()" mode="resource"/>
       </resource>
    </xsl:for-each>
</structure>
  </xsl:for-each>
</structures>
</xsl:template>

<xsl:template match="@structure" mode="resource"/>

<xsl:template match="resource" mode="resource">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="resource"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()" mode="resource" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="resource"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()" priority="-2"/>

</xsl:stylesheet>