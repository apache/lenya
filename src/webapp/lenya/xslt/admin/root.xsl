<?xml version="1.0"?>

<!-- $Id: root.xsl,v 1.3 2003/07/03 12:44:12 andreas Exp $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    >

<xsl:include href="../menu/root.xsl"/>
    
<xsl:template match="lenya/cmsbody">
  <xsl:copy-of select="xhtml:html"/>
</xsl:template>
    
</xsl:stylesheet>
