<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/" xmlns:xi="http://www.w3.org/2001/XInclude">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             

<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
  <files xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xlink="http://www.w3.org/2002/XLink">
    <xsl:for-each select="dir:file">
      <file href="{@name}">             
      <title xlink:show="embed" xlink:href="{@name}#xpointer(head/title)"/>
      </file>
    </xsl:for-each>
  </files>
</xsl:template>

</xsl:stylesheet>
