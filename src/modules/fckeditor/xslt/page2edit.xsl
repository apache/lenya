<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:nav="http://apache.org/cocoon/lenya/navigation/1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml">
  <!-- Escapes text areas so the forms with them
       can be edited by FCK -->
  <xsl:template match="xhtml:textarea">
    <xsl:text>&lt;textarea</xsl:text>
     <xsl:for-each select="@*">
       <xsl:text> </xsl:text>
       <xsl:value-of select="name()"/>
       <xsl:text>="</xsl:text>
       <xsl:value-of select="."/>
       <xsl:text>"</xsl:text>
     </xsl:for-each>
    <xsl:text>&gt;</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>&lt;/textarea&gt;</xsl:text>
  </xsl:template>

</xsl:stylesheet>
