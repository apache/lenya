<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">


<xsl:include href="../../../../../../../stylesheets/cms/Page/root-dhtml.xsl"/>
<!--
<xsl:include href="../../../../../../../stylesheets/cms/Page/root.xsl"/>
-->

<xsl:template match="cmsbody">
  <xsl:apply-templates/>
</xsl:template>

<!--
<xsl:include href="../../root.xsl"/>
-->



<xsl:include href="../../head.xsl"/>
<xsl:include href="../../foot.xsl"/>
<xsl:include href="../../HTMLhead.xsl"/>
<xsl:include href="../../variables_authoring.xsl"/>
<xsl:include href="../../variables.xsl"/>
<xsl:include href="../../text_general.xsl"/>
<xsl:include href="../articles.xsl"/>
<xsl:include href="../page.xsl"/>
<xsl:include href="../relatedContent.xsl"/>
<xsl:include href="../dossier.xsl"/>

</xsl:stylesheet>

