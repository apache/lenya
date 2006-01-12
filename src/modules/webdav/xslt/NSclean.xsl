<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
       xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
       xmlns:jx="http://apache.org/cocoon/templates/jx/1.0"
       xmlns:collection="http://apache.org/cocoon/collection/1.0"
       xmlns:D="DAV:"
       exclude-result-prefixes="jx">

<!-- Identity transformation template -->			
<xsl:template match="/ | @* | * | comment() | processing-instruction() | text()"> 
	<xsl:copy> 
		<xsl:apply-templates select="@* | * | comment() | processing-instruction() | text()"/> 
	</xsl:copy> 
</xsl:template> 

<xsl:template match="D:multistatus">
  <multistatus xmlns="DAV:" xmlns:collection="http://apache.org/cocoon/collection/1.0">
    <xsl:apply-templates select="@* | * | processing-instruction() | text()"/>
  </multistatus>
</xsl:template>

<xsl:template match="D:*">
  <xsl:element name="{local-name()}" namespace="DAV:">
    <xsl:apply-templates select="@* | * | processing-instruction() | text()"/>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>
