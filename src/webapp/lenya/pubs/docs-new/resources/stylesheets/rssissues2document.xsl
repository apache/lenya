<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="rss">
  <document>
    <header>
      <title>Open issues</title>
    </header>
    <body>
      <note>These are the open issues in our <link href="http://issues.cocoondev.org/jira/secure/BrowseProject.jspa?id=10000">bug tracking system</link>.
      They are regenerated on each Forrest run.</note>
      <xsl:apply-templates select="channel/item"/>
    </body>
  </document>
</xsl:template>

<xsl:template match="item">
  <section>
    <title><xsl:value-of select="title" disable-output-escaping="yes"/></title>
    <p><link href="{link}"><xsl:value-of select="link"/></link></p>
    <p><xsl:value-of select="description" disable-output-escaping="yes"/></p>
  </section>
</xsl:template>

</xsl:stylesheet>
