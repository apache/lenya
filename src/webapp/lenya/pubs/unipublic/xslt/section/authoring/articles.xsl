<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="section/articles" mode="articles">
      <xsl:for-each select="article">
        <p>
          <a href="{@href}/"><span class="tsr-title"><xsl:apply-templates select="body.head/hedline/hl1"/></span></a><br />
          <xsl:apply-templates select="body.head/abstract"/> <!-- (<xsl:apply-templates select="body.head/dateline/story.date/@norm"/>) -->
          <xsl:if test="body.head/dateline/story.date/@norm!=''">
            (<xsl:apply-templates select="body.head/dateline/story.date/@norm"/>)
          </xsl:if>
          <xsl:if test="body.head/dateline/story.date/@norm=''">
            <br/><font size="-2" color="#ff0000">Noch nie publiziert</font>
          </xsl:if>

        </p>
      </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
