<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="year"/>

<xsl:template match="/">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             

<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
<atom:feed xmlns:atom="http://purl.org/atom/ns#" xmlns:xlink="http://www.w3.org/2002/XLink">
  <description xlink:href="feeds/all/index.xml#xmlns(atom=http://purl.org/atom/ns#)xpointer(/atom:feed/atom:title)xpointer(/atom:feed/atom:link)xpointer(/atom:feed/atom:modified)" xlink:show="embed"/>

  <xsl:variable name="month"><xsl:value-of select="@name"/></xsl:variable>
  <xsl:for-each select="dir:directory">
    <xsl:variable name="day"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:for-each select="dir:directory">
      <xsl:variable name="entryid"><xsl:value-of select="@name"/></xsl:variable>
      <entry xlink:href="entries/{$year}/{$month}/{$day}/{$entryid}/index.xml" xlink:show="embed"/>
    </xsl:for-each>
  </xsl:for-each>
</atom:feed>
</xsl:template>

</xsl:stylesheet>
