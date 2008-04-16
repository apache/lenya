<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://apache.org/forrest/linkmap/1.0"
  xmlns:site="http://apache.org/forrest/linkmap/1.0"
  xmlns:modules="http://apache.org/lenya/module-list/1.0"
  >
  
  <xsl:output indent="yes" method="xml"/>
  
  <xsl:template match="/xconf">
    <modules>
      <xsl:comment>
        The template for this module list is generated.
        Run "ant modules" in the documentation root directory,
        but make sure not to include your custom modules.
        The generated list is in build/templates/modules.xml.
      </xsl:comment>
      <core_modules label="Core Modules" href="../modules/">
        <xsl:apply-templates select="component/modules/module">
          <xsl:with-param name="pattern">/modules-core/</xsl:with-param>
          <xsl:sort select="@shortcut"/>
        </xsl:apply-templates>
      </core_modules>
      <standard_modules label="Standard Modules" href="../modules/">
        <xsl:apply-templates select="component/modules/module">
          <xsl:with-param name="pattern">/modules/</xsl:with-param>
          <xsl:sort select="@shortcut"/>
        </xsl:apply-templates>
      </standard_modules>
    </modules>
  </xsl:template>
  
  
  <xsl:template match="module">
    <xsl:param name="pattern"/>
    <xsl:if test="contains(@src, $pattern)">
      <xsl:element name="{@shortcut}_module_section">
        <xsl:attribute name="label"><xsl:value-of select="@shortcut"/></xsl:attribute>
        <xsl:attribute name="href"><xsl:value-of select="@shortcut"/>/</xsl:attribute>
        <xsl:element name="{@shortcut}_module_overview">
          <xsl:attribute name="label">Overview</xsl:attribute>
          <xsl:attribute name="href">index.html</xsl:attribute>
        </xsl:element>
        <xsl:element name="{@shortcut}_module_api">
          <xsl:attribute name="label">API</xsl:attribute>
          <xsl:attribute name="href">../../../../apidocs/1.4/modules/<xsl:value-of select="@shortcut"/>/index.html</xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:if>
  </xsl:template>
  

</xsl:stylesheet>