<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
>

<xsl:template match="dir:directory">
  <xsl:apply-templates select="dir:file/dir:xpath"/>
</xsl:template>

<xsl:template match="dir:xpath">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="crawler">
<h3>Crawler</h3>
Filename: <i><xsl:value-of select="../../@name"/></i>
<br/>
User-Agent: <i><xsl:value-of select="user-agent"/></i>
<br/>
Start crawling at: <i><xsl:value-of select="base-url/@href"/></i>
<br/>
Scope of crawling: <i><xsl:value-of select="scope-url/@href"/></i>
<br/>
Directory where documents will be dumped: <i><xsl:value-of select="htdocs-dump-dir/@src"/></i>
<br/>
List of all crawled URLs: <i><xsl:value-of select="uri-list/@src"/></i>
</xsl:template>

<xsl:template match="lucene">
<h3>Lucene</h3>
Filename: <i><xsl:value-of select="../../@name"/></i>
<br/>
Type of index update: <i><xsl:value-of select="update-index/@type"/></i>
<br/>
Directory of Documents to be indexed: <i><xsl:value-of select="htdocs-dump-dir/@src"/></i>
<br/>
Directory of Search Index: <i><xsl:value-of select="index-dir/@src"/></i>
<br/>
Indexer class: <i><xsl:value-of select="indexer/@class"/></i>
</xsl:template>

</xsl:stylesheet>
