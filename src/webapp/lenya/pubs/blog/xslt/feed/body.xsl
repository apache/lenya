<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:echo="http://purl.org/atom/ns#"
>

<xsl:import href="../entry/body.xsl"/>

<xsl:template match="echo:title">
<div class="title"><a href="../../entries/{normalize-space(../echo:id)}/index.html"><xsl:apply-templates/></a></div>
</xsl:template>

<xsl:template name="permalink">
  <xsl:param name="id"/>
  <xsl:text><a href="../../entries/{normalize-space($id)}/index.html">Permalink</a></xsl:text>
</xsl:template>

</xsl:stylesheet>  
