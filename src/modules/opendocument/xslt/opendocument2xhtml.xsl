<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  >
  <xsl:import href="fallback://lenya/modules/opendocument/xslt/common/odt_to_xhtml.xsl"/>
  
  <!-- default parameter value -->
  <xsl:param name="rendertype" select="'null'"/>
  <xsl:param name="language" select="'none'"/>
  <xsl:param name="nodeid" select="'null'"/>
  
  <xsl:template match="office:document-content">
    <html>
      <body>
        <h1>OpenDocument Content (<a href="{$nodeid}.odt">ODT</a>)</h1>
        <xsl:apply-templates select="office:body/office:text"/>
      </body>
    </html>
  </xsl:template>
  
  
</xsl:stylesheet>
