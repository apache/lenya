<?xml version="1.0"?>

<!--
 $Id: move-down.xsl,v 1.7 2004/02/23 18:50:52 roku Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title><i18n:text>Move Document Down</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Move Document Down</i18n:text></div>
      <div class="lenya-box-body">
    <form method="post">
      <p>
        <i18n:translate>
           <i18n:text kex="move-down?"/>
           <i18n:param><xsl:value-of select="document-id"/></i18n:param>
        </i18n:translate>
      </p>
      <input i18n:attr="value" type="submit" value="Move"/>
      <input i18n:attr="value" type="submit" value="Cancel"/>
    </form>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>