<?xml version="1.0"?>

<!--
 $Id: delete.xsl,v 1.8 2003/08/19 13:22:25 edith Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:variable name="document-id"><xsl:value-of select="/page/info/document-id"/></xsl:variable>
  <xsl:variable name="area"><xsl:value-of select="/page/info/area"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>Delete Document</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">Delete Document</div>
      <div class="lenya-box-body">
        <form method="get">
          <xsl:attribute name="action"></xsl:attribute>
          <input type="hidden" name="lenya.usecase" value="delete"/>
          <input type="hidden" name="lenya.step" value="delete"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          <xsl:call-template name="task-parameters">
            <xsl:with-param name="prefix" select="''"/>
          </xsl:call-template>
	      <p>
			Do you really want to delete <xsl:value-of select="document-id"/>?
	      </p>
          <input type="submit" class="lenya-form-element" value="Delete"/>
          <input type="button" class="lenya-form-element" onClick="location.href='{$request-uri}';" value="Cancel"/>
        </form>
      </div>
    </div>
  </xsl:template>

<xsl:template name="task-parameters">
  <xsl:param name="prefix" select="'task.'"/>
  <input type="hidden" name="{$prefix}properties.node.firstdocumentid" value="{$document-id}"/>
  <input type="hidden" name="{$prefix}properties.firstarea" value="{$area}"/>
  <input type="hidden" name="{$prefix}properties.secarea" value="trash"/>
</xsl:template>

</xsl:stylesheet>
