<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="feedid"/>

<xsl:template match="/">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             
<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
<feed xmlns:xlink="http://www.w3.org/2002/XLink">
  <description xlink:href="feeds/{$feedid}/index.xml#xpointer(/feed/title)xpointer(/feed/subtitle)xpointer(/feed/link)xpointer(/feed/modified)" xlink:show="embed"/>
  <xsl:for-each select="dir:directory">
    <xsl:variable name="year"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:for-each select="dir:directory">
      <xsl:variable name="month"><xsl:value-of select="@name"/></xsl:variable>
      <xsl:for-each select="dir:directory">
        <xsl:variable name="day"><xsl:value-of select="@name"/></xsl:variable>
        <xsl:for-each select="dir:directory">
          <xsl:variable name="entryid"><xsl:value-of select="@name"/></xsl:variable>
          <entry xlink:href="entries/{$year}/{$month}/{$day}/{$entryid}/index.xml" xlink:show="embed"/>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:for-each>
</feed>
</xsl:template>

</xsl:stylesheet>
