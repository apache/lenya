<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: paste.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >

  <xsl:import href="../util/waitScreen.xsl"/>
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:variable name="contextprefix"><xsl:value-of select="/page/info/context-prefix"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>
  <xsl:variable name="first-document-id"><xsl:value-of select="/page/info/first-document-id"/></xsl:variable>
  <xsl:variable name="sec-document-id"><xsl:value-of select="/page/info/sec-document-id"/></xsl:variable>
  <xsl:variable name="first-area"><xsl:value-of select="/page/info/first-area"/></xsl:variable>
  <xsl:variable name="sec-area"><xsl:value-of select="/page/info/sec-area"/></xsl:variable>
  <xsl:variable name="userid"><xsl:value-of select="/page/info/user-id"/></xsl:variable>
  <xsl:variable name="ipaddress"><xsl:value-of select="/page/info/ip-address"/></xsl:variable>
  <xsl:variable name="doctype"><xsl:value-of select="/page/info/doctype"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <xsl:call-template name="wait_script"/>   
      <page:title><i18n:text>Paste Document</i18n:text></page:title>
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
          <i18n:text key="paste-doc"/>
          <i18n:param><q><xsl:value-of select="first-document-id"/></q></i18n:param>			
        </i18n:translate>
      </div>
      <div class="lenya-box-body">
        <form method="get" name="paste-form">
          <xsl:attribute name="action"></xsl:attribute>
          <input type="hidden" name="lenya.usecase" value="paste"/>
          <input type="hidden" name="lenya.step" value="paste"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          <xsl:call-template name="task-parameters">
            <xsl:with-param name="prefix" select="''"/>
          </xsl:call-template>
          <table class="lenya-table-noborder">
          	<tr>
          		<td>
                <i18n:translate>          		
          		    <i18n:text key="paste-doc-from-clip?"/>
                  <i18n:param><strong><xsl:value-of select="first-document-id"/></strong></i18n:param>			
                </i18n:translate>  
                <p>
                  <i18n:text key="move-losing-scheduler-warning"/>
                </p>                          		
          		</td>
          	</tr>
          	<tr>
          		<td>
          			<br/>
                    <input i18n:attr="value" type="submit" value="Paste" onclick="wait()" name="Paste"/>&#160;
					<input i18n:attr="value" type="button" onClick="location.href='{$request-uri}';" value="Cancel"  name="Cancel"/>
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
  <input type="hidden" name="{$prefix}properties.copy.userid" value="{$userid}"/>
  <input type="hidden" name="{$prefix}properties.copy.ipaddress" value="{$ipaddress}"/>
</xsl:template>

</xsl:stylesheet>