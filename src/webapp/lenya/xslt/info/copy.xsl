<?xml version="1.0"?>

<!--
 $Id: copy.xsl,v 1.12 2004/02/24 21:43:39 roku Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:variable name="document-id"><xsl:value-of select="/page/info/document-id"/></xsl:variable>
  <xsl:variable name="action"><xsl:value-of select="/page/info/action"/></xsl:variable>
  <xsl:variable name="area"><xsl:value-of select="/page/info/area"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title><i18n:text>Copy Document</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">
        <i18n:translate>
          <i18n:text key="copy-doc"/>
          <i18n:param><q><xsl:value-of select="document-id"/></q></i18n:param>			
        </i18n:translate>
      </div>
      <div class="lenya-box-body">
        <form method="get">
        <xsl:attribute name="action"></xsl:attribute>
          <input type="hidden" name="documentid" value="{$document-id}"/>
          <input type="hidden" name="area" value="{$area}"/>
          <input type="hidden" name="action" value="{$action}"/>
          <input type="hidden" name="lenya.usecase" value="copy"/>
          <input type="hidden" name="lenya.step" value="copy"/>
          
          <table class="lenya-table-noborder">          
          	<tr>
          		<td/>
          		<td><br/>
                  <i18n:translate>          		
          		    <i18n:text key="copy-doc-to-clip-info"/>
                    <i18n:param><strong><xsl:value-of select="document-id"/></strong></i18n:param>			
                  </i18n:translate>
          		</td>
          	</tr>
          	<tr>
          		<td/>
          		<td>
          			<br/>
        <input i18n:attr="value" type="submit" value="Copy"/>&#160;
        <input i18n:attr="value" type="button" onClick="location.href='{$request-uri}';" value="Cancel"/>
          		</td>
          	</tr>
          </table>
          
    </form>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>