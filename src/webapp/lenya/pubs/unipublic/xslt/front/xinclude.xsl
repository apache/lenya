<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/" xmlns:xi="http://www.w3.org/2001/XInclude">
  <xsl:apply-templates select="Articles"/>
</xsl:template>

<xsl:template match="Articles"  xmlns:xi="http://www.w3.org/2001/XInclude">
 <Articles xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xlink="http://www.w3.org/2000/XLink">
    <xsl:for-each select="Article">
      <Article href="{@channel}/{@section}/{@year}/{@dir}" section="{@section}" channel="{@channel}">
<!--
        <head xlink:show="embed" xlink:href="docs/publication/authoring/{@channel}/{@section}/articles/{@year}/{@dir}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/nitf/body/body.head)"/>
-->
        <head xlink:show="embed" xlink:href="../{@channel}/{@section}/articles/{@year}/{@dir}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/nitf/body/body.head)"/>
<!--
        <xi:include xml:base="cocoon:" href="{@channel}/{@section}/{@year}/{@dir}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/nitf/body/body.head)"/>
-->
      </Article>
    </xsl:for-each>
  </Articles>
</xsl:template>

</xsl:stylesheet>
