<?xml version="1.0"?>

<!--
 $Id: paste.xsl,v 1.12 2003/09/05 14:42:59 andreas Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>
  <xsl:variable name="first-document-id"><xsl:value-of select="/page/info/first-document-id"/></xsl:variable>
  <xsl:variable name="sec-document-id"><xsl:value-of select="/page/info/sec-document-id"/></xsl:variable>
  <xsl:variable name="first-area"><xsl:value-of select="/page/info/first-area"/></xsl:variable>
  <xsl:variable name="sec-area"><xsl:value-of select="/page/info/sec-area"/></xsl:variable>
  <xsl:variable name="doctype"><xsl:value-of select="/page/info/doctype"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>

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
          <table class="lenya-table-noborder">
          	<tr>
          		<td class="lenya-entry-caption">Document on clipboard:</td>
          		<td><xsl:value-of select="first-document-id"/></td>
          	</tr>
          	<tr>
          		<td/>
          		<td>
          			<br/>
								<input type="submit" value="Paste"/>&#160;
								<input type="button" onClick="location.href='{$request-uri}';" value="Cancel"/>
          		</td>
          	</tr>
          </table>
        </form>
      </div>
    </div>
  </xsl:template>

<xsl:template name="task-parameters">
  <xsl:param name="prefix" select="'task.'"/>
  <input type="hidden" name="{$prefix}properties.node.firstdocumentid" value="{$first-document-id}"/>
  <input type="hidden" name="{$prefix}properties.node.secdocumentid" value="{$sec-document-id}"/>
  <input type="hidden" name="{$prefix}properties.firstarea" value="{$first-area}"/>
  <input type="hidden" name="{$prefix}properties.secarea" value="{$sec-area}"/>
  <input type="hidden" name="{$prefix}properties.wf.doctype" value="{$doctype}"/>
</xsl:template>

</xsl:stylesheet>