<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:include href="../../../../../../../stylesheets/cms/Page/root-dhtml.xsl"/>

<xsl:template match="cmsbody">
<xsl:copy-of select="*"/>
</xsl:template>
 
</xsl:stylesheet>  
