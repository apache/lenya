<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:include href="../../../xslt/menu/root.xsl"/>

<xsl:template match="lenya/cmsbody">
    <xsl:apply-templates/>
 </xsl:template>

<xsl:template match="@*|*">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
</xsl:template> 

</xsl:stylesheet>  
