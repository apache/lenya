<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up"
                xmlns:dir="http://apache.org/cocoon/directory/2.0">

<xsl:template match="/">
  <dossierlist>
    <xsl:apply-templates />
  </dossierlist>
</xsl:template>

<xsl:template match="dir:directory">
  <dossiers xmlns:xlink="http://www.w3.org/2002/XLink" xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:for-each select="dir:directory">
      <dossier href="{@name}">
	<head xlink:show="embed" xlink:href="{@name}/index.xml#xpointer(/dossier/head)" />
      </dossier>
    </xsl:for-each>
  </dossiers>
</xsl:template>

</xsl:stylesheet>
