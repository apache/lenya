<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:up="http://www.unipublic.unizh.ch/2002/up">

<xsl:template name="styles">
<link type="text/css" rel="stylesheet" href="{$unipublic}/unipublic.css"/>
<link rel="stylesheet" type="text/css" href="{$unipublic}/unipublic.mac.css" />
</xsl:template>

<xsl:template name="jscript">
<script type="text/javascript" language="JavaScript">
<xsl:comment>
<!--antiframe-->
if (top.frames.length &#62; 0) {top.location.href = self.location;}
<!-- CSS Triage-->
if (navigator.appVersion.indexOf ('Win') &#62;= 0) {
   seite = '<xsl:value-of select="string($unipublic)"/>/unipublic.win.css';
   document.write('&#60;link rel="stylesheet" type="text/css" href="'+seite+'"&#62;');
}
</xsl:comment>
</script>
</xsl:template>

</xsl:stylesheet>

