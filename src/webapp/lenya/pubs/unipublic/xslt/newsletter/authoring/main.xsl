<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : main.xsl
    Created on : November 18, 2002, 5:59 PM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:include href="../../../../../../../stylesheets/cms/Page/menu/root.xsl"/>

<xsl:template match="cmsbody">
  <xsl:apply-templates/>
</xsl:template>

<xsl:include href="../../head.xsl"/>
<xsl:include href="../../foot.xsl"/>
<xsl:include href="../../HTMLhead.xsl"/>
<xsl:include href="../../variables_authoring.xsl"/>
<xsl:include href="../../variables.xsl"/>
<xsl:include href="../page.xsl"/>
<xsl:include href="../articles.xsl"/>
<xsl:include href="../../Article/relatedContent.xsl"/>

</xsl:stylesheet> 
