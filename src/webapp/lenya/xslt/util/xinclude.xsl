<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xlink="http://www.w3.org/2002/XLink">

<xsl:template match="/">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             
<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
<publications>
  <xsl:for-each select="dir:directory">
    <publication pid="{@name}">
    <publication xlink:href="{@name}/publication.xml" xlink:show="embed"/>
    </publication>
  </xsl:for-each>
</publications>
</xsl:template>

</xsl:stylesheet>
