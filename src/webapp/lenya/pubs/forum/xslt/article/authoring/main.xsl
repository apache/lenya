<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:include href="../../../../../../../stylesheets/cms/Page/root.xsl"/>

<xsl:template match="cmsbody">
  <xsl:call-template name="page"/>
</xsl:template>

<xsl:include href="../../page.xsl"/>

<xsl:template name="cmsbody">
  <xsl:apply-templates select="article"/>
</xsl:template>

<xsl:include href="../body.xsl"/>
 
</xsl:stylesheet>  
