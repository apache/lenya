<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:variable name="context_prefix">/wyona-cms/nwt</xsl:variable>

<xsl:include href="../../../../../../../stylesheets/root.xsl"/>
<!--
<xsl:include href="../../root.xsl"/>
-->

<xsl:template match="cmsbody">
  <xsl:apply-templates select="oscom"/>
</xsl:template>

<xsl:include href="../../html.xsl"/>
<xsl:include href="body.xsl"/>
 
</xsl:stylesheet>  
