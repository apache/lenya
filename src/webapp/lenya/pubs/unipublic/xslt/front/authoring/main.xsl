<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="cmsbody">
  <xsl:apply-templates/>
</xsl:template>

<xsl:include href="../../../../../xslt/menu/root.xsl"/>

<xsl:include href="../../head.xsl"/>
<xsl:include href="../../foot.xsl"/>
<xsl:include href="../../HTMLhead.xsl"/>
<xsl:include href="../../variables_authoring.xsl"/>
<xsl:include href="../../variables.xsl"/>
<!--
<xsl:include href="../../navigation.xsl"/>
-->
<xsl:include href="../webperls.xsl"/>
<xsl:include href="../services.xsl"/>
<xsl:include href="../headlines.xsl"/>
<xsl:include href="media.xsl"/>
<xsl:include href="../page.xsl"/>

</xsl:stylesheet>
