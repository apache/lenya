<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>

 <xsl:output indent="yes"/>

 <xsl:template match='/'>
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match='@*|dtd|attlist|attributeDecl|enumeration|notationDecl'>
  <xsl:copy>
   <xsl:apply-templates select='@*|*'/>
  </xsl:copy>
 </xsl:template>

 <xsl:template match='group[count(*)=1][group]'>
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match='contentModel|any|empty|group|pcdata|element|separator|occurrence'>
  <xsl:copy>
   <xsl:apply-templates select='@*|*'/>
  </xsl:copy>
 </xsl:template>

 <xsl:template match="comment"/>

 <xsl:template match='*'>
  <xsl:apply-templates/>
 </xsl:template>

</xsl:stylesheet>
