<?xml version="1.0" encoding="UTF-8"?>

<!--
   $Id: confirm-delete.xsl,v 1.3 2004/02/16 19:57:56 roku Exp $
-->

<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="UTF-8" indent="yes" version="1.0"/>
  
  <xsl:variable name="type" select="/page/type"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  
  <xsl:template match="page">
    <page:page>
      <page:title>
        <i18n:translate>
          <i18n:text i18n:key="delete-object"/>
          <i18n:param><i18n:text><xsl:value-of select="$type"/></i18n:text></i18n:param>
        </i18n:translate>       
      </page:title>
      <page:body>
      	
    <div class="lenya-box">
      <div class="lenya-box-title">
        <i18n:translate>
          <i18n:text i18n:key="delete-object?"/>
          <i18n:param><i18n:text><xsl:value-of select="$type"/></i18n:text>&#160;<q><xsl:value-of select="id"/></q><xsl:if test="name!=''">&#160;(<xsl:value-of select="name"/>)</xsl:if></i18n:param>
        </i18n:translate>
      </div>
      <div class="lenya-box-body">
          <form method="GET" action="{/page/continuation}.continuation">
            <input i18n:attr="value" type="submit" name="submit" value="Yes"/>
            <input i18n:attr="value" type="submit" name="cancel" value="No"/>
          </form>
      </div>
    </div>
    
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
  </xsl:template>
  
  
</xsl:stylesheet>
