<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : epozsave-xhtml.xsl
    Created on : 2003/12/12
    Author     : Rolf Kulemann
    Author     : Michael Wechner
    Description:
        Merges lenya:meta into xhtml send by epoz for save.
        We also remove some <link>s here i.e. for css and rel.
-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:lenya="http://apache.org/cocoon/lenya/page-envelope/1.0"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
 >

<!-- FIXME: Insert doctype declaration.  -->
<!--
<xsl:output method="xml" version="1.0" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>
-->

<xsl:template match="edit-envelope">
  <html>
    <xsl:copy-of select="original/xhtml:html/lenya:meta"/>
    <head>
      <xsl:apply-templates select="edited/xhtml:html/xhtml:head/*"/>
    </head>
    <xsl:copy-of select="edited/xhtml:html/xhtml:body"/>
  </html>
</xsl:template>

<xsl:template match="xhtml:link"/>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template> 

</xsl:stylesheet>
