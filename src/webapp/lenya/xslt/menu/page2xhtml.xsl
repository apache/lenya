<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  exclude-result-prefixes="xhtml"
  >
  
  <xsl:param name="context-prefix"/>
  <xsl:param name="publication-id"/>
  <xsl:param name="area"/>
  <xsl:param name="document-id"/>
  
  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="$area = 'live'">
        <xsl:apply-templates select="lenya/cmsbody"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  
  <xsl:template match="lenya">
    <html>
      <head>
        <xsl:call-template name="title"/>
        <script src="{$context-prefix}/lenya/menu/menu.js" type="text/javascript"/>
        <link href="{$context-prefix}/lenya/css/menu.css" rel="stylesheet" type="text/css"/>
      </head>
      <body bgcolor="#ffffff" leftmargin="0" marginheight="0" marginwidth="0" topmargin="0">
        <xsl:apply-templates select="xhtml:div[@id = 'lenya-menubar']"/>
        <div id="lenya-cmsbody">
          <xsl:apply-templates select="cmsbody"/>
        </div>
        <script type="text/javascript"> initialize(); </script>
      </body>
    </html>
  </xsl:template>
  
  
  <xsl:template match="cmsbody">
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  
  <xsl:template name="title">
    <title>
      Apache Lenya -
      <xsl:value-of select="$publication-id"/> -
      <xsl:value-of select="$area"/> -
      <xsl:value-of select="$document-id"/> -
      <xsl:value-of select="cmsbody/html/head/title"/>
    </title>
  </xsl:template>
  
  
  <xsl:template match="xhtml:*">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>
  
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  
</xsl:stylesheet>
