<?xml version="1.0"?>

<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tf="http://chaperon.sourceforge.net/schema/textfragment/1.0">

 <xsl:param name="parse_element">math</xsl:param>

 <xsl:template match="*[name()=$parse_element]">
  <xsl:element name="{$parse_element}">
   <tf:textfragment>
    <xsl:value-of select="."/>
   </tf:textfragment>
  </xsl:element>
 </xsl:template>

  <xsl:template match="@*|*|text()|processing-instruction()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
