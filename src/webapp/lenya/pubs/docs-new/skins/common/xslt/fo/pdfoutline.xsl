<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:fox="http://xml.apache.org/fop/extensions"
                version="1.0">

<xsl:template match="document" mode="outline">
  <fox:bookmarks>
    <fox:outline internal-destination="{generate-id()}">
      <fox:label>
        <xsl:value-of select="header/title"/>
      </fox:label>
      <xsl:apply-templates select="body/section" mode="outline"/>
    </fox:outline>
  </fox:bookmarks>
</xsl:template>

<xsl:template match="section" mode="outline">
  <fox:outline internal-destination="{generate-id()}">
    <fox:label>
      <xsl:number format="1.1.1.1.1.1.1" count="section" level="multiple"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="title"/>
    </fox:label>
    <xsl:apply-templates select="section" mode="outline"/>
  </fox:outline>
</xsl:template>

</xsl:stylesheet>
