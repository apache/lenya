<?xml version="1.0"?>

<!-- $Id: page.xsl,v 1.2 2003/07/03 12:44:42 andreas Exp $ -->

<xsl:stylesheet version="1.0"
	  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	  xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
	  >

<xsl:template match="/cmsbody">
  <xsl:copy>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>


<xsl:template match="page:body">
  <xsl:copy>
    <xhtml:div class="lenya-sidebar">
      <xsl:copy-of select="/cmsbody/xhtml:div[@id = 'menu']"/>
    </xhtml:div>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>


<!-- do not copy menu -->
<xsl:template match="xhtml:div[@id = 'menu']"/>


<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet>
