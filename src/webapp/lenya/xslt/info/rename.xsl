<?xml version="1.0"?>

<!--
 $Id: rename.xsl,v 1.7 2003/07/03 12:59:55 edith Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:variable name="request-uri"><xsl:value-of select="/info/request-uri"/></xsl:variable>
  <xsl:variable name="first-document-id"><xsl:value-of select="/info/first-document-id"/></xsl:variable>
  <xsl:variable name="last-id"><xsl:value-of select="/info/last-id"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/info/task-id"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>Rename Document</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">Rename Document</div>
      <div class="lenya-box-body">
        <form method="get">
          <xsl:attribute name="action"></xsl:attribute>
          <p>
          <input type="hidden" name="properties.node.firstdocumentid" value="{$first-document-id}"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          <input type="hidden" name="lenya.usecase" value="rename"/>
          <input type="hidden" name="lenya.step" value="rename"/>
	      Rename <xsl:value-of select="last-id"/> to 
          </p>
          <input type="text" class="lenya-form-element" name="properties.node.secdocumentid" value=""/>
          <input type="submit" class="lenya-form-element" value="Rename"/>
          <input type="button" class="lenya-form-element" onClick="location.href='{$request-uri}';" value="Cancel"/>
        </form>
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>