<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    exclude-result-prefixes="dir xhtml"
>

<xsl:param name="publication"/>
<xsl:param name="module"/>
<xsl:param name="publicationname"/>
<xsl:param name="publicationlanguages"/>

<xsl:template match="/dir:directory">
  <html>
    <head>
      <title><xsl:value-of select="$publication"/>&#160;<i18n:text>Structures</i18n:text></title>
    </head>	
    <body>
<h1><xsl:value-of select="$publication"/>&#160;<i18n:text>Structures</i18n:text></h1>
<xsl:apply-templates select="dir:file"/>
</body></html>
</xsl:template>

<xsl:template match="dir:file">
<xsl:variable name="structure"><xsl:value-of select="substring-before(@name, '.')"/></xsl:variable>
<xsl:element name="a">
<xsl:attribute name="href">/<xsl:value-of select="$publication"/>/<xsl:value-of select="$module"/>/<xsl:value-of select="$structure"/></xsl:attribute><xsl:value-of select="$structure"/></xsl:element>
<br/>
</xsl:template>


</xsl:stylesheet> 
