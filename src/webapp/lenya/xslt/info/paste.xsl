<?xml version="1.0"?>

<!--
 $Id: paste.xsl,v 1.8 2003/07/03 12:59:55 edith Exp $
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
  <xsl:variable name="sec-document-id"><xsl:value-of select="/info/sec-document-id"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/info/task-id"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>Paste Document</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">Paste Document</div>
      <div class="lenya-box-body">
        <form method="get">
          <xsl:attribute name="action"></xsl:attribute>
          <input type="hidden" name="lenya.usecase" value="paste"/>
          <input type="hidden" name="lenya.step" value="paste"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          <xsl:call-template name="task-parameters">
            <xsl:with-param name="prefix" select="''"/>
          </xsl:call-template>
          <p>
	      Do you really want to paste <xsl:value-of select="first-document-id"/> from the clipboard?
          </p>
          <input type="submit" class="lenya-form-element" value="Paste"/>
          <input type="button" class="lenya-form-element" onClick="location.href='{$request-uri}';" value="Cancel"/>
        </form>
      </div>
    </div>
  </xsl:template>

<xsl:template name="task-parameters">
  <xsl:param name="prefix" select="'task.'"/>
  <input type="hidden" name="{$prefix}properties.node.firstdocumentid" value="{$first-document-id}"/>
  <input type="hidden" name="{$prefix}properties.node.secdocumentid" value="{$sec-document-id}"/>
</xsl:template>

</xsl:stylesheet>