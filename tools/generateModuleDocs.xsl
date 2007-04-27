<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://apache.org/forrest/linkmap/1.0"
  xmlns:site="http://apache.org/forrest/linkmap/1.0"
  xmlns:modules="http://apache.org/lenya/module-list/1.0"
  >
  
  <xsl:param name="lenyaHome"/>
  
  <xsl:output indent="yes" method="xml"/>
  
  <xsl:variable name="moduleList" select="concat($lenyaHome, '/build/lenya/temp/modules.xconf')"/>
  
  
  <xsl:template match="/*">
    <xsl:apply-templates select=".//site:module_list"/>
  </xsl:template>
  
  <xsl:template match="site:module_list">
    <xsl:variable name="listNode" select="current()"/>
    <xsl:copy>
      <xsl:copy-of select="site:index"/>
      <xsl:comment>
        The template for this module list is generated.
        Run "ant modules" in the documentation root directory,
        but make sure not to include your custom modules.
        The generated list is in build/templates/modules.xml.
      </xsl:comment>
      <core_modules label="Core Modules">
        <xsl:apply-templates select="document($moduleList)/xconf/component/modules/module">
          <xsl:with-param name="pattern">/modules-core/</xsl:with-param>
          <xsl:with-param name="listNode" select="$listNode"/>
          <xsl:sort select="@shortcut"/>
        </xsl:apply-templates>
      </core_modules>
      <standard_modules label="Standard Modules">
        <xsl:apply-templates select="document($moduleList)/xconf/component/modules/module">
          <xsl:with-param name="pattern">/modules/</xsl:with-param>
          <xsl:with-param name="listNode" select="$listNode"/>
          <xsl:sort select="@shortcut"/>
        </xsl:apply-templates>
      </standard_modules>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="module">
    <xsl:param name="pattern"/>
    <xsl:param name="listNode"/>
    <xsl:if test="contains(@src, $pattern)">
      <xsl:element name="{@shortcut}_module_section" namespace="http://apache.org/forrest/linkmap/1.0">
        <xsl:attribute name="label"><xsl:value-of select="@shortcut"/></xsl:attribute>
        <xsl:copy-of select="$listNode//*[local-name() = concat(current()/@shortcut, '_module_section')]/*[not(@label = 'API')]"/>
        <!--
        <xsl:element name="{@shortcut}_module_overview" namespace="http://apache.org/forrest/linkmap/1.0">
          <xsl:attribute name="label">Overview</xsl:attribute>
          <xsl:attribute name="href"><xsl:value-of select="@shortcut"/>.html</xsl:attribute>
        </xsl:element>
        <xsl:call-template name="lineBreak"/>
        <xsl:text>  </xsl:text>
        -->
        <xsl:element name="{@shortcut}_module_api" namespace="http://apache.org/forrest/linkmap/1.0">
          <xsl:attribute name="label">API</xsl:attribute>
          <xsl:attribute name="href">../../../../apidocs/1.4/modules/<xsl:value-of select="@shortcut"/>/index.html</xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:if>
  </xsl:template>
  
  
  <!--
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  -->

</xsl:stylesheet>