<?xml version="1.0"?>

<!--
 $Id: deactivate.xsl,v 1.8 2003/09/08 20:48:58 andreas Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
   >
   
  <xsl:param name="lenya.event"/>
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:variable name="document-id"><xsl:value-of select="/page/info/document-id"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title>Deactivate Document</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
        <form method="get">
        	
	<div class="lenya-box">
		<div class="lenya-box-title">Deactivate Document</div>
		<div class="lenya-box-body">
			<input type="hidden" name="lenya.usecase" value="deactivate"/>
			<input type="hidden" name="lenya.step" value="deactivate"/>
			<input type="hidden" name="lenya.event" value="{$lenya.event}"/>
			<input type="hidden" name="task-id" value="{$task-id}"/>
			<xsl:call-template name="task-parameters">
				<xsl:with-param name="prefix" select="''"/>
			</xsl:call-template>
			<p>
				Do you really want to deactivate this document?
				<ul>
					<li><xsl:value-of select="$document-id"/></li>
				</ul>
			</p>
		</div>
	</div>
          
					<not:notification>
						<not:textarea/>
					</not:notification>

          <input type="submit" value="Deactivate"/>
          &#160;
          <input type="button" onClick="location.href='{$request-uri}';" value="Cancel"/>
        </form>
  </xsl:template>

<xsl:template name="task-parameters">
  <xsl:param name="prefix" select="'task.'"/>
  <input type="hidden" name="{$prefix}properties.node.firstdocumentid" value="{$document-id}"/>
</xsl:template>



</xsl:stylesheet>