<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:variable name="server-uri" select="'http://www.unipublic.unizh.ch'"/>
                
<xsl:template match="articles">
  <xsl:apply-templates/>
</xsl:template>                

<xsl:template name="underline">
  <xsl:param name="title"/>
  <xsl:if test="string-length($title) &gt; 0">*<xsl:text/>
    <xsl:call-template name="underline">
      <xsl:with-param name="title" select="substring($title, 2)"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<xsl:template match="articles/article">
  <code>
    <xsl:apply-templates select="body.head/hedline/hl1"/>
    <br />
    <xsl:call-template name="underline">
      <xsl:with-param name="title" select="body.head/hedline/hl1"/>
    </xsl:call-template>
    <br />
    <xsl:choose>
      <xsl:when test="body.head/teasertext!=''">
        <xsl:value-of select="body.head/teasertext"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="body.head/abstract"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="body.head/dateline/story.date/@norm!=''">
        (<xsl:value-of select="body.head/dateline/story.date/@norm"/>)
      </xsl:when>
      <xsl:otherwise>
        <br />
        (Noch nie publiziert)
      </xsl:otherwise>
    </xsl:choose>
    <br />
    Mehr unter:
    <br />
    <xsl:value-of select="concat($server-uri, '/', @href,'/index.html')"/>
    <br />
    <br />
    <br clear="all" />
  </code>
</xsl:template>

</xsl:stylesheet>
