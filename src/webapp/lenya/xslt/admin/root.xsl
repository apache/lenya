<?xml version="1.0"?>

<!--
 $Id: root.xsl,v 1.1 2003/06/11 15:25:10 gregor Exp $
 -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

<xsl:include href="../menu/root.xsl"/>
    
<xsl:template match="lenya/cmsbody">
<xsl:apply-templates />
</xsl:template>

</xsl:stylesheet> 