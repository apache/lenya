<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:variable name="context_prefix" select="/wyona/menu/context_prefix"/>

<xsl:template match="wyona">
  <div style="position:absolute;top:0px;left:0px;z-index:2">
  <html>
    <head>
      <title>Authoring</title>
    <style type="text/css">
      <xsl:comment>
        .awyona {
            color: #0066FF;
            text-decoration: none;
        }

        .awyona:visited {
            <!--color: #669999;-->
            color: #0066FF;
            text-decoration: none;
        }
      </xsl:comment>
    </style>
    </head>
    <body bgcolor="#ffffff">
      <xsl:apply-templates select="menu"/>
    </body>
  </html>
  </div>


  <div style="position:absolute;top:60px;left:0px;z-index:1">
      <xsl:apply-templates select="cmsbody"/>
  </div>
</xsl:template>

<xsl:include href="menu.xsl"/>
 
</xsl:stylesheet>  
