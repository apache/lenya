<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<!-- Copies everything else to the result tree  -->
<xsl:template match="/ | @* | node()">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="dossier[@id!='']">
  <xsl:copy>
    <xsl:apply-templates select="@*" />
    <head xmlns:xlink="http://www.w3.org/2002/XLink" xmlns:xi="http://www.w3.org/2001/XInclude" xlink:show="embed"  
      xlink:href="../../../../dossiers/{@id}/index.xml#xpointer(/dossier/head)" />
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
