<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/" xmlns:xi="http://www.w3.org/2001/XInclude">
  <xsl:apply-templates select="articles"/>
</xsl:template>

<xsl:template match="articles"  xmlns:xi="http://www.w3.org/2001/XInclude">
 <articles xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xlink="http://www.w3.org/2000/XLink">
    <xsl:for-each select="article">
      <article href="news/{@id}.html" >
        <head xlink:show="embed" xlink:href="news/{@id}.xml#xpointer(/article/head)"/>
      </article>
    </xsl:for-each>
  </articles>
</xsl:template>

</xsl:stylesheet>
