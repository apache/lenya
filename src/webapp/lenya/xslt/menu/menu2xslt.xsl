<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
  >
  
<xsl:param name="context-prefix"/>
<xsl:param name="publication-id"/>
<xsl:param name="area"/>
<xsl:param name="document-id"/>
  
<xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>
  
<xsl:template match="/">
  
  <xso:stylesheet version="1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xhtml">
    
  <xso:output method="html"/>

  <xsl:if test="$area != 'live'">
    
  <xso:template match="/">
    <html>
      <head>
        <xso:call-template name="title"/>
        <script src="{$context-prefix}/lenya/menu/menu.js" type="text/javascript"/>
        <link href="{$context-prefix}/lenya/css/menu.css" rel="stylesheet" type="text/css"/>
        <xso:apply-templates select="xhtml:html/xhtml:head/*[local-name() != 'title']"/>
      </head>
      <body bgcolor="#ffffff" leftmargin="0" marginheight="0" marginwidth="0" topmargin="0">
        
        <xsl:apply-templates select="xhtml:div[@id = 'lenya-menubar']"/>
      
        <div id="lenya-cmsbody">
          <xso:apply-templates select="xhtml:html/xhtml:body/node()"/>
        </div>
        <script type="text/javascript"> initialize(); </script>
      </body>
    </html>
  </xso:template>
  
  <xso:template name="title">
    <title>
      Apache Lenya -
      <xsl:value-of select="$publication-id"/> -
      <xsl:value-of select="$area"/> -
      <xsl:value-of select="$document-id"/> -
      <xso:value-of select="xhtml:html/xhtml:head/xhtml:title"/>
    </title>
  </xso:template>
  
  </xsl:if>
    
  <xso:template match="xhtml:*">
    <xso:element>
      <xsl:attribute name="name">{local-name()}</xsl:attribute>
      <xso:apply-templates select="@*|node()"/>
    </xso:element>
  </xso:template>
  
  
  <xso:template match="@*|node()">
    <xso:copy>
      <xso:apply-templates select="@*|node()"/>
    </xso:copy>
  </xso:template>
  
  </xso:stylesheet>
  
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
