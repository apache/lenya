<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="articles">
      <xsl:for-each select="article">
	<p>
	  <a href="{$unipublic}{$view}/{@href}/index.html">
	    <img height="60" alt="{body.head/media/media-reference/@alternate-text}" src="{$unipublic}{$view}/{@href}/{body.head/media/media-reference/@source}" width="80" align="right" border="0"/>
	  </a>
	  <span class="tsr-title">
	    <a href="{$unipublic}{$view}/{@href}/index.html"><xsl:apply-templates select="body.head/hedline/hl1"/></a>
	  </span>
	  <br/>
          <xsl:choose>
            <xsl:when test="body.head/teasertext!=''">
              <xsl:apply-templates select="body.head/teasertext"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="body.head/abstract"/>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:if test="body.head/dateline/story.date/@norm!=''">
            (<xsl:apply-templates select="body.head/dateline/story.date/@norm"/>)
          </xsl:if>
          <xsl:if test="body.head/dateline/story.date/@norm=''">
            <br/><font size="-2" color="#ff0000">Noch nie publiziert</font>
          </xsl:if>
	  <br clear="all" />
	</p>
      </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
