<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!--
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
-->
 
<xsl:output method="xml" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
  <xsl:copy-of select="*"/>
<!--
  <xsl:element name="RDF">
    <xsl:copy-of select="/rdf:RDF/*"/>
  </xsl:element>
-->
</xsl:template>
 
</xsl:stylesheet>  
