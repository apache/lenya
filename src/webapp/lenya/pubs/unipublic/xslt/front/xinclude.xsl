<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="Page/Content/MainColumn/articles" xmlns:xi="http://www.w3.org/2001/XInclude">
<!--<xsl:template match="frontpage"  xmlns:xi="http://www.w3.org/2001/XInclude">-->
 <articles xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:for-each select="article">
      <article href="{@href}" section="{@section}">
        <xi:include xml:base="cocoon:" href="{@channel}/{@section}/{@year}/{@dir}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
      </article>
    </xsl:for-each>
  </articles>
</xsl:template>

</xsl:stylesheet>
