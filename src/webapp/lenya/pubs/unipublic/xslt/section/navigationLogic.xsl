<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:param name="section"/>

<xsl:template match="*"> 
  <xsl:copy> 
    <xsl:for-each select="@*"> 
      <xsl:attribute name="{name()}"> 
        <xsl:value-of select="."/> 
      </xsl:attribute> 
    </xsl:for-each> 
    <xsl:apply-templates/> 
  </xsl:copy> 
</xsl:template>                                                                                                                             

<xsl:template match="Section">
  <xsl:choose>
    <xsl:when test='@id=$section'>
      <Section id="{@id}" highlighted="true"><xsl:value-of select="."/></Section>
    </xsl:when>
    <xsl:otherwise>
      <Section id="{@id}" highlighted="false"><xsl:value-of select="."/></Section>
    </xsl:otherwise>
  </xsl:choose>                                                                                                                 
</xsl:template>

</xsl:stylesheet>
