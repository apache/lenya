<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:template match="/">
<xsl:apply-templates select="bxeng"/>
</xsl:template>

<xsl:template match="bxeng">
<html xmlns="http://www.w3.org/1999/xhtml">
<xsl:apply-templates select="xhtml:html/xhtml:head"/>
<xsl:copy-of select="xhtml:html/xhtml:body"/>
</html>
</xsl:template>

<xsl:template match="xhtml:head">
<head xmlns="http://www.w3.org/1999/xhtml">
<xsl:for-each select="/bxeng/namespaces/xmlns">
  <meta name="bxeNS" content="{.}"/>
</xsl:for-each>
<xsl:copy-of select="@*|node()"/>
</head>
</xsl:template>
 
</xsl:stylesheet>  
