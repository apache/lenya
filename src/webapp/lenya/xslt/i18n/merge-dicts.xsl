<?xml version="1.0"?>
<!--
  $Id: merge-dicts.xsl,v 1.1 2004/03/12 20:58:32 roku Exp $
  
  This xslt merges i18n catalogue files which should be related to ONE locale.
  March 2004
-->

 <xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 >

  <xsl:template match="*">
    <catalogue>
      <xsl:copy-of select="catalogue/*"/>
    </catalogue>
  </xsl:template>

 </xsl:stylesheet>  