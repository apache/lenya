<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="section/articles" mode="articles">

      <xsl:for-each select="article">
        <p>
          <a href="{@href}/"><span class="tsr-title"><xsl:apply-templates select="body.head/hedline/hl1"/></span></a><br />
          <xsl:choose>
            <xsl:when test="body.head/teasertext!=''">
              <xsl:apply-templates select="body.head/teasertext"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="body.head/abstract"/>
            </xsl:otherwise>
          </xsl:choose>
           (<xsl:apply-templates select="body.head/dateline/story.date/@norm"/>)
        </p>
      </xsl:for-each>

</xsl:template>

</xsl:stylesheet>
