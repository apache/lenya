<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:param name="channel"/>
<xsl:param name="section"/>
<xsl:param name="year"/>

<xsl:template match="/" xmlns:xi="http://www.w3.org/2001/XInclude">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             

<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
<section type="{$section}">
  <type><xsl:value-of select="$section"/></type>
  <articles xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xlink="http://www.w3.org/2002/XLink">
    <xsl:for-each select="dir:directory">
      <article href="{@name}">
      <head xlink:show="embed" xlink:href="cocoon:{$channel}/{$section}/{$year}/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
<!--
      <xi:include xml:base="cocoon:" href="{$channel}/{$section}/{$year}/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
-->
      </article>
    </xsl:for-each>
  </articles>
</section>
</xsl:template>

</xsl:stylesheet>
