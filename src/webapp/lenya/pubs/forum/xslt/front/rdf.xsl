<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="xml" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
  <xsl:apply-templates select="/front/articles"/>
</xsl:template>

<xsl:template match="articles">
  <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns="http://purl.org/rss/1.0/">
    <channel>
      <title>Wyona</title>
      <link>http://www.lenya.org</link>
    </channel>

    <xsl:for-each select="article">
      <item>
        <title><xsl:apply-templates select="article/head/title"/></title>
        <link>http://www.lenya.org</link>
        <xsl:apply-templates select="article/meta/date"/>
      </item>
    </xsl:for-each>
  </rdf:RDF>
</xsl:template>

<xsl:template match="date" xmlns:dc="http://purl.org/dc/elements/1.1/">
  <dc:date><xsl:value-of select="year"/>-<xsl:value-of select="month"/>-<xsl:value-of select="day"/>T<xsl:value-of select="hour"/>:<xsl:value-of select="minute"/></dc:date>
</xsl:template>
 
</xsl:stylesheet>  
