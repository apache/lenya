<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:param name="view"/>

<xsl:template match="/">
  <xsl:apply-templates select="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0"/>
</xsl:template>                                                                                                                             
<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
<front>
  <articles xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xlink="http://www.w3.org/2002/XLink">
    <xsl:for-each select="dir:directory">
      <article href="{@name}">
      <article xlink:show="embed" xlink:href="docs/publication/{$view}/articles/{@name}/index.xml"/>
<!--
      <article xlink:show="embed" xlink:href="cocoon:/articles/{@name}/index.xml"/>
-->

<!--
      <xi:include xml:base="cocoon:" href="{$channel}/{$section}/{$year}/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
-->
      </article>
    </xsl:for-each>
  </articles>
</front>
</xsl:template>

</xsl:stylesheet>
