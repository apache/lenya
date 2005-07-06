<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  >
  
  <xsl:import href="../util/waitScreen.xsl"/>

  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:variable name="contextprefix"><xsl:value-of select="/page/info/contextprefix"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>
  <xsl:variable name="document-id"><xsl:value-of select="/page/info/document-id"/></xsl:variable>
  <xsl:variable name="area"><xsl:value-of select="/page/info/area"/></xsl:variable>
  <xsl:variable name="action"><xsl:value-of select="/page/info/action"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <xsl:call-template name="wait_script"/>   
      <page:title><i18n:text>Cut Document</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
        <xsl:call-template name="wait_screen"/>   
      </page:body>
    </page:page>
  </xsl:template>
  
  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">
        <i18n:translate>
          <i18n:text key="cut-doc"/>
          <i18n:param><q><xsl:value-of select="document-id"/></q></i18n:param>			
        </i18n:translate>
      </div>
      <div class="lenya-box-body">
        <form method="get">
          <xsl:attribute name="action"></xsl:attribute>
					<input type="hidden" name="documentid" value="{$document-id}"/>
					<input type="hidden" name="area" value="{$area}"/>
					<input type="hidden" name="action" value="{$action}"/>
					<input type="hidden" name="lenya.usecase" value="cut"/>
					<input type="hidden" name="lenya.step" value="cut"/>
          <table class="lenya-table-noborder">
          	<tr>
          	  <td>
                <i18n:translate>          		
          		    <i18n:text key="cut-doc-to-clip?"/>
                    <i18n:param><strong><xsl:value-of select="document-id"/></strong></i18n:param>			
                  </i18n:translate>          	
               </td>
          	</tr>
          	<tr>
          		<td>
          			<br/>
                    <input i18n:attr="value" type="submit" value="Cut" onclick="submit_wait(this.form)"/>&#160;
                    <input i18n:attr="value" type="button" onClick="location.href='{$request-uri}';" value="Cancel"/>
          		</td>
          	</tr>
          </table>
        </form>
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>