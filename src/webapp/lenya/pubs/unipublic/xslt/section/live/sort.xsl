<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="articles" >
  <!-- copy and sort by the publish date (millis) the articles-->
  <xsl:copy>
    <xsl:apply-templates>
      <xsl:sort select="body.head/dateline/story.date/@millis" data-type="number" order="descending"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template match="* | @*">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>                                                                                                                             

</xsl:stylesheet>
