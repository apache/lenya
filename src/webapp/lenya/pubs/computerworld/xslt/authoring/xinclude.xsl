<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/" xmlns:xi="http://www.w3.org/2001/XInclude">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             

<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
  <articles xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xlink="http://www.w3.org/2002/XLink">
    <xsl:for-each select="dir:file">
      <article href="news/{substring-before(@name, '.')}.html">
     <head xlink:show="embed" xlink:href="{@name}#xpointer(head)"/>
      <body xlink:show="embed" xlink:href="{@name}#xpointer(body)"/>
      </article>
    </xsl:for-each>
  </articles>
</xsl:template>

</xsl:stylesheet>
