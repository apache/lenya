<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="html-title">
XHTML
</xsl:template>

<xsl:template name="admin-url">
Introspection to the rescue
</xsl:template>

<xsl:template name="body">
  <xsl:apply-templates select="xhtml:html"/>
</xsl:template>

<xsl:template match="xhtml:html">
  <xsl:copy-of select="xhtml:body/node()"/>
</xsl:template>
 
</xsl:stylesheet>  
