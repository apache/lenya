<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!--<xsl:include href="Live/dossier.xsl"/>-->
<xsl:include href="Live/webperls.xsl"/>
<xsl:include href="services.xsl"/>


<xsl:template match="/" xmlns:xi="http://www.w3.org/2001/XInclude">
  <Page>
    <Content>
      <FirstColumn>
        <xi:include xml:base="cocoon:" href="navigation.xml"/>
<!--
        <xsl:apply-templates select="Dossiers"/>
-->
      </FirstColumn>
      <MainColumn>
        <xsl:apply-templates select="Frontpage"/>
      </MainColumn>
      <LastColumn>
<!--
        <xsl:apply-templates select="Services"/>
        <xsl:apply-templates select="Webperls"/>
-->
        <xsl:call-template name="Service"/>
        <xsl:call-template name="Webperlen"/>
      </LastColumn>
    </Content>
  </Page>
</xsl:template>


<xsl:template match="Frontpage"  xmlns:xi="http://www.w3.org/2001/XInclude">
 <Articles xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:for-each select="Articles/Article">
      <Article href="{@channel}/{@section}/{@year}/{@dir}">
        <xi:include xml:base="cocoon:" href="{@channel}/{@section}/{@year}/{@dir}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
      </Article>
    </xsl:for-each>
  </Articles>
</xsl:template>


</xsl:stylesheet>
