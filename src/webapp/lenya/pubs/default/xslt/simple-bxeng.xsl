<?xml version="1.0" encoding="iso-8859-1"?>
<!-- $Id: simple-bxeng.xsl,v 1.1 2003/10/06 10:24:23 michi Exp $ -->
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:simple="http://apache.org/cocoon/lenya/doctypes/simple-document/1.0"
>

<xsl:output method="xml" encoding="iso-8859-1" />

<xsl:template match="/">
    <div>Simple Document</div>
    <xsl:apply-templates select="/simple:simple-document"/>
</xsl:template>

<xsl:template match="*">
    <xsl:copy-of select="."/>
</xsl:template>

</xsl:stylesheet>
