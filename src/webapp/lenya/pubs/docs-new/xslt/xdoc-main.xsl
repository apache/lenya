<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
<xsl:apply-templates/>
</xsl:template>

<xsl:include href="lenya.org.xsl"/>

<xsl:template name="body">
 <xsl:apply-templates select="/site/document/title"/><br />
 <xsl:copy-of select="/site/document/body/node()|@*"/>
</xsl:template>
 
</xsl:stylesheet>  
