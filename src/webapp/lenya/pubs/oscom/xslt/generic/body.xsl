<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:error="http://apache.org/cocoon/error/2.0" xmlns:n-rdf="http://my.netscape.com/rdf/simple/0.9/">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template name="body">
  <div id_xopus="generic_body" xml_xopus="conferences/sanfrancisco2002/cfp.xhtml" xsl_xopus="Generic/authoring/xopus.xsl" xsd_xopus="generic.xsd">
    <xsl:apply-templates select="html"/>
  </div>
</xsl:template>

<xsl:template match="html">
  <xsl:copy-of select="body/*"/>
</xsl:template>
 
</xsl:stylesheet>  
