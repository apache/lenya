<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="articles">
  <xsl:copy>
  <xsl:for-each select="article">
    <xsl:choose>
      <xsl:when test="body.head/dateline/story.date/@millis">
        <!-- do nothing -->
      </xsl:when>
      <xsl:otherwise>
        <!--copy first the articles which weren't already published-->
          <xsl:apply-templates select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:for-each select="article">
    <!--copy and sort by the publish date (millis) the articles which were already published-->  
    <xsl:sort select="body.head/dateline/story.date/@millis" data-type="number" order="descending"/>
    <xsl:if test="body.head/dateline/story.date/@millis">
      <xsl:apply-templates select="."/>
    </xsl:if>
  </xsl:for-each>
  </xsl:copy>                                                                                                                               
</xsl:template>

<xsl:template match="* | @*">
 <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>                                                                                                                             

</xsl:stylesheet>
