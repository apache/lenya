<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:session="http://www.apache.org/xsp/session/2.0">

<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:param name="publication_name"/>
<xsl:variable name="copyright">copyright &#169; 2003 Lenya, Apache Software Foundation</xsl:variable>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="page">
  <html>
   <head>
    <title><xsl:value-of select="$publication_name"/> - <xsl:call-template name="html-title"/></title>
    <link rel="stylesheet" type="text/css" href="/lenya/lenya/css/default.css" />
    </head>
    <body bgcolor="#ffffff">
     <h2><xsl:value-of select="$publication_name"/></h2>

     <xsl:apply-templates select="body"/>

     <p>
     <font face="verdana" size="-2">
       <xsl:value-of select="$copyright"/>
     </font>
     </p>
    </body>
  </html>
</xsl:template>

<xsl:template match="body">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template name="html-title">
LOGOUT
</xsl:template>

<xsl:template match="logout">
<font face="verdana">
<br /><b>LOGOUT</b>


<xsl:apply-templates select="referer"/>
<xsl:apply-templates select="no_referer"/>

<br />Your history:
<xsl:apply-templates select="uri"/>
</font>
</xsl:template>

<xsl:template match="uri">
<br /><a><xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a>
</xsl:template>

<xsl:template match="referer">
<p>Referer: <a><xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a></p>
</xsl:template>

<xsl:template match="no_referer">
<p>
<font color="red">EXCEPTION:</font> No referer
</p>
</xsl:template>

</xsl:stylesheet>
