<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lenya="http://apache.org/cocoon/lenya/publication/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    >

<xsl:template match="lenya:lenya">
  <page:page>
    
  <page:title>Apache Lenya - Content Management System</page:title>
  <page:body>
    <xsl:apply-templates select="xhtml:div[@class = 'lenya-frontpage']"/>
    <xsl:apply-templates select="lenya:publications"/>
  </page:body>
  </page:page>
</xsl:template>

<xsl:template match="lenya:publications">
<div class="lenya-sidebar">
<!--<div class="lenya-padding">-->
<div class="lenya-sidebar-heading">Publications</div>
<!--
<p>
We are working on a catalog of sample publications. The idea is that an
"integrator" can pull out an appropriate publication and reuse it for building
efficiently its own publication. The <a href="docs/tutorial/index.html">tutorial</a> describes how to do that.
</p>
-->
<xsl:for-each select="lenya:publication">
  <xsl:choose>
    <xsl:when test="lenya:XPSEXCEPTION">
<!--
      <div class="lenya-publication-item">
        <font color="red">Exception:</font>
        (publication id = <xsl:value-of select="@pid"/>) <xsl:value-of select="lenya:XPSEXCEPTION"/>
      </div>
-->
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="lenya:publication/@lenya:show = 'false'">
          <!-- do not list this publication. Might be a "template" publication -->
        </xsl:when>
        <xsl:otherwise>
          <div class="lenya-publication-item">
            <a href="{@pid}/introduction.html">
            <xsl:apply-templates select="lenya:publication/lenya:name"/></a>
          </div>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
<!--</div>-->
</div>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>

</xsl:stylesheet>
