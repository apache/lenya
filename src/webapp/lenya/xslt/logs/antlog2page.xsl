<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : antlog2page.xsl
    Created on : 13. Mai 2003, 18:40
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:page="http://www.lenya.org/2003/cms-page"
    >

<xsl:template match="/">
  <page:page>
    <page:title>Task Log</page:title>
    <page:body>
      <xsl:copy-of select="html/body/*"/>
    </page:body>
  </page:page>
</xsl:template>

</xsl:stylesheet> 
