<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="/">
  <xsl:call-template name="page"/>
</xsl:template>

<!--
<xsl:include href="page_q42.xsl"/>
-->
<xsl:include href="page_lenya.xsl"/>
 
</xsl:stylesheet>  
