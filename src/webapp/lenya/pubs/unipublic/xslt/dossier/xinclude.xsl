<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<!-- Copies everything else to the result tree  -->
<xsl:template match="/ | @* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="articles">
  <articles xmlns:xlink="http://www.w3.org/2002/XLink" xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:for-each select="article">
      <article href="{@channel}/{@section}/{@year}/{@id}">
	<head xlink:show="embed" xlink:href="../../../{@channel}/{@section}/{@year}/{@id}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/nitf/body/body.head)" />
      </article>
    </xsl:for-each>
  </articles>
</xsl:template>

</xsl:stylesheet>
