<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="dir:directory" xmlns:dir="http://apache.org/cocoon/directory/2.0">
<section>
  <articles xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:for-each select="dir:directory">
      <article href="{@name}">
      <xi:include xml:base="cocoon:" href="magazin/gesundheit/2002/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
<!--
      <xi:include xml:base="cocoon:" href="magazin/geist/2002/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
-->
<!--
      <xi:include xml:base="cocoon:" href="magazin/recht/2002/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
-->
<!--
      <xi:include xml:base="cocoon:" href="magazin/umwelt/2002/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
-->
<!--
      <xi:include xml:base="cocoon:" href="campus/uni-news/2002/{@name}/index.xml#xpointer(/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/body/body.head)"/>
-->
      </article>
    </xsl:for-each>
  </articles>
</section>
</xsl:template>

</xsl:stylesheet>
