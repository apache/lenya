<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template match="body.head" mode="media-column">
  <xsl:choose>
    <xsl:when test="media/media-reference">
      <a href="{../@href}/">
        <img src="{$unipublic}/{../@href}/{media/media-reference/@source}" width="80" height="60" border="0" alt="{media/media-reference/@alternate-text}" align="right"/>
        </a>
    </xsl:when>
    <xsl:otherwise>
        <font color="red"> Attention: no image </font> <br/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="body.head" mode="media-top">
  <xsl:choose>
    <xsl:when test="media/media-reference">
        <a href="{../@href}/">
          <img src="{$unipublic}/{../@href}/{media/media-reference/@source}" width="80" height="60" border="0" alt="{media/media-reference/@alternate-text}" align="right"/>
        </a>
    </xsl:when>
    <xsl:otherwise>
        <font color="red"> Attention:</font><br/><font color="red">no image </font><br/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
