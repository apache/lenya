<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             
<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0" xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xlink="http://www.w3.org/2002/XLink">
<oscom>
  <xsl:for-each select="dir:file">
    <project xlink:href="matrix/{@name}" xlink:show="embed"/>
  </xsl:for-each>

  <related-content/>
</oscom>
</xsl:template>

</xsl:stylesheet>
