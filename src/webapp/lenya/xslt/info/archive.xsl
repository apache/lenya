<?xml version="1.0"?>

<!--
 $Id: archive.xsl,v 1.2 2003/06/10 13:05:36 gregor Exp $
 -->


 <xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://www.lenya.org/2003/cms-page"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>Archive Document</page:title>
      <page:body>
	<h1>Archive Document</h1>
	
	<xsl:apply-templates select="body"/>
	<xsl:apply-templates select="info"/>
	
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <form method="post">
      <xsl:attribute name="action"></xsl:attribute>
      <p>
	Do you really want to archive <xsl:value-of select="documentid"/>?
      </p>
      <input type="submit" value="Archive"/>
      <input type="submit" value="Cancel"/>
    </form>
  </xsl:template>
  
</xsl:stylesheet>
