<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up" >

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="dossiers">
  <dossiers xmlns:xlink="http://www.w3.org/2002/XLink" xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:for-each select="dossier">
      <dossier id="{@id}">
	<head xlink:show="embed" xlink:href="../dossiers/{@id}/index.xml#xpointer(/dossier/head)" />
      </dossier>
    </xsl:for-each>
  </dossiers>
</xsl:template>

</xsl:stylesheet>
