<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!--
<xsl:template match="/">
  <xsl:copy-of select="."/>
</xsl:template>
-->

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="html">
<NewsML>
<NewsItem>
<xsl:apply-templates select="body/center[2]/table[1]/tr[3]/td[1]" mode="related-content"/>
<xsl:apply-templates select="body/center[2]/table[1]/tr[3]/td[3]" mode="main-content"/>
</NewsItem>
</NewsML>
</xsl:template>

<xsl:template match="td" mode="related-content">
<NewsComponent>
<ContentItem>
<DataContent>
<related-content>
<block>
<title>
<xsl:apply-templates select="table[1]/tr[1]/td[1]/table[1]/tr[2]/td[1]/table[1]/tr[1]"/>
</title>
<xsl:apply-templates select="table[1]/tr[1]/td[1]/table[1]/tr[2]/td[1]/table[1]/tr" mode="item"/>
</block>
<xsl:copy-of select="."/>
</related-content>
</DataContent>
</ContentItem>
</NewsComponent>
</xsl:template>

<xsl:template match="td" mode="main-content">
<NewsComponent>
<ContentItem>
<DataContent>
<nitf>
<body>
<body.head>
<hedline>
<hl1>
<xsl:apply-templates select="p[2]"/>
</hl1>
</hedline>
<abstract>
<xsl:apply-templates select="p[3]"/>
</abstract>
</body.head>
<body.content>
<block>
<xsl:apply-templates select="p" mode="subparagraph"/>
</block>
</body.content>
</body>
<xsl:copy-of select="."/>
</nitf>
</DataContent>
</ContentItem>
</NewsComponent>
</xsl:template>

<xsl:template match="p" mode="subparagraph">
<p>
<xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="tr" mode="item">
<item>
<xsl:apply-templates/>
</item>
</xsl:template>

</xsl:stylesheet>
