<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:include href="xopus.xsl"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:variable name="context_prefix" select="/lenya/menu/context_prefix"/>

<xsl:template match="lenya">
  <html>
    <xsl:call-template name="xopus_html_attribute"/>
    <xsl:call-template name="xopus_top"/>
    <head>
      <xsl:call-template name="xopus_head"/>
      <title>Authoring</title>
    <style type="text/css">
      <xsl:comment>
        .alenya {
            color: #0066FF;
            text-decoration: none;
        }

        .alenya:visited {
            <!--color: #669999;-->
            color: #0066FF;
            text-decoration: none;
        }
      </xsl:comment>
    </style>
    </head>
    <body bgcolor="#ffffff">
      <xsl:call-template name="xopus_body"/>

      <xsl:apply-templates select="cmsbody"/>
    </body>
  </html>
</xsl:template>

<xsl:template match="xopus" mode="top">
  <xsl:call-template name="xopus_top"/>
</xsl:template>

<xsl:template match="xopus" mode="head">
  <xsl:call-template name="xopus_head"/>
</xsl:template>

<xsl:template match="xopus" mode="body">
  <xsl:call-template name="xopus_body"/>
</xsl:template>

<xsl:template match="xopus" mode="html_attribute">
  <xsl:call-template name="xopus_html_attribute"/>
</xsl:template>
 
</xsl:stylesheet>  
