<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 
<xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:include href="../../../../../../../stylesheets/cms/Page/menu/root.xsl"/>

<xsl:template match="cmsbody">
  <xsl:call-template name="page"/>
</xsl:template>

<xsl:include href="../../page_wyona.xsl"/>

<xsl:template name="cmsbody">
 <table border="0" cellspacing="0" cellpadding="0" width="100%">
  <xsl:apply-templates select="article"/>
 </table>
</xsl:template>

<xsl:include href="../body_wyona.xsl"/>
 
</xsl:stylesheet>  
