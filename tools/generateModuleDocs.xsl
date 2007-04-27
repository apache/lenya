<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://apache.org/forrest/linkmap/1.0"
  xmlns:site="http://apache.org/forrest/linkmap/1.0"
  xmlns:modules="http://apache.org/lenya/module-list/1.0"
  >
  
  <xsl:output indent="no"/>
  
  <xsl:param name="lenyaHome"/>
  
  <xsl:variable name="moduleList" select="concat($lenyaHome, '/build/lenya/temp/modules.xconf')"/>
  
  <xsl:template match="site:module_list">
    <xsl:copy>
      <xsl:call-template name="lineBreak"/>
      <xsl:copy-of select="site:index"/>
      <xsl:call-template name="lineBreak"/>
      <xsl:comment>
        The template for this module list is generated.
        Run "ant modules" in the documentation root directory,
        but make sure not to include your custom modules.
      </xsl:comment>
      <xsl:call-template name="lineBreak"/>
      <xsl:apply-templates select="document($moduleList)/xconf/component/modules/module">
        <xsl:sort select="@shortcut"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="module">
    <xsl:call-template name="lineBreak"/>
    <xsl:element name="{@shortcut}_module_section" namespace="http://apache.org/forrest/linkmap/1.0">
      <xsl:attribute name="label"><xsl:value-of select="@shortcut"/></xsl:attribute>
      <xsl:call-template name="lineBreak"/>
      <xsl:text>  </xsl:text>
      <xsl:element name="{@shortcut}_module_overview" namespace="http://apache.org/forrest/linkmap/1.0">
        <xsl:attribute name="label">Overview</xsl:attribute>
        <xsl:attribute name="href"><xsl:value-of select="@shortcut"/>.html</xsl:attribute>
      </xsl:element>
      <xsl:call-template name="lineBreak"/>
      <xsl:text>  </xsl:text>
      <xsl:element name="{@shortcut}_module_api" namespace="http://apache.org/forrest/linkmap/1.0">
        <xsl:attribute name="label">API</xsl:attribute>
        <xsl:attribute name="href">../../../../apidocs/1.4/modules/<xsl:value-of select="@shortcut"/>/index.html</xsl:attribute>
      </xsl:element>
      <xsl:call-template name="lineBreak"/>
    </xsl:element>
  </xsl:template>
  
  
  <xsl:template name="lineBreak">
    <xsl:text>
    </xsl:text>
  </xsl:template>
  
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>