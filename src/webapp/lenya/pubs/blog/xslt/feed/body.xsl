<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: body.xsl,v 1.11 2004/02/15 17:50:23 gregor Exp $ -->
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
  <a href="../../entries/{normalize-space($id)}/index.html">Permalink</a>
</xsl:template>

</xsl:stylesheet>  
