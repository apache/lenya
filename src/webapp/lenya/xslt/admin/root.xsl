<?xml version="1.0"?>

<!-- $Id: root.xsl,v 1.2 2003/06/19 16:08:39 michi Exp $ -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="../menu/root.xsl"/>
    
<xsl:template match="lenya/cmsbody">
  <xsl:apply-templates select="html"/>
</xsl:template>
    
<xsl:template match="html">
  <xsl:copy-of select="."/>
</xsl:template>

</xsl:stylesheet>
