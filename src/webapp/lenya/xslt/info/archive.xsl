<?xml version="1.0"?>

<!--
 $Id: archive.xsl,v 1.9 2003/09/05 14:42:59 andreas Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
   >
  
<xsl:param name="lenya.event"/>

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
      <page:title>Archive Document</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>

  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">Archive Document</div>
      <div class="lenya-box-body">
        <form method="get">
        	
          <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
          <input type="hidden" name="lenya.usecase" value="archive"/>
          <input type="hidden" name="lenya.step" value="archive"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          
					<input type="hidden" name="properties.node.firstdocumentid" value="{$document-id}"/>
					<input type="hidden" name="properties.firstarea" value="{$area}"/>
					<input type="hidden" name="properties.secarea" value="archive"/>
					
          <table class="lenya-table-noborder">
          	<tr>
          		<td/>
          		<td>Do you really want to archive this document?<br/><br/></td>
          	</tr>
          	<tr>
          		<td class="lenya-entry-caption">Document:</td>
          		<td><xsl:value-of select="document-id"/></td>
          	</tr>
          	<tr>
          		<td/>
          		<td>
          			<br/>
                <input type="submit" value="Archive"/>&#160;
                <input type="button" onClick="location.href='{$request-uri}';" value="Cancel"/>
          		</td>
          	</tr>
          </table>
        </form>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
  
