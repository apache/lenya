<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="frontpage"  xmlns:xi="http://www.w3.org/2001/XInclude">
 <articles xmlns:xi="http://www.w3.org/2001/XInclude"> 
    <xsl:for-each select="articles/article">
      <article href="{@href}">
        <xi:include xml:base="cocoon:" href="{@channel}/{@section}/{@year}/{@dir}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
<!--
        <xi:include xml:base="cocoon:" href="magazin/gesundheit/2002/0508/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
-->
<!--
        <xi:include xml:base="cocoon:"><xsl:attribute name="href"><xsl:value-of select="@channel"/>/{@section}/{@year}/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"</xsl:attribute></xi:include>
-->
      </article>
    </xsl:for-each>
  </articles>
</xsl:template>

</xsl:stylesheet>
