<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <h1>Tom's Weekly Column</h1>

  <xsl:apply-templates select="weekly-column"/>
</xsl:template>

<xsl:template match="weekly-column">
 <xsl:apply-templates/>
</xsl:template>

<xsl:template match="paragraph">
 <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match="link">
<a href="{@href}"><xsl:apply-templates/></a>
</xsl:template>
 
</xsl:stylesheet>  
