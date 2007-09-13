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

<!-- $Id: delete.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:import href="../util/waitScreen.xsl"/>

  <xsl:param name="lenya.event" select="'delete'"/>
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:variable name="contextprefix"><xsl:value-of select="/page/info/context-prefix"/></xsl:variable>
  <xsl:variable name="document-id"><xsl:value-of select="/page/info/document-id"/></xsl:variable>
  <xsl:variable name="area"><xsl:value-of select="/page/info/area"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <xsl:call-template name="wait_script"/>   
      <page:title><i18n:text>Delete Document</i18n:text></page:title>
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
          <i18n:text key="delete-doc"/>
          <i18n:param><q><xsl:value-of select="document-id"/></q></i18n:param>
        </i18n:translate>      
      </div>
      <div class="lenya-box-body">
        <form method="get" name="delete-form">
          <xsl:attribute name="action"></xsl:attribute>
          
          <input type="hidden" name="lenya.usecase" value="delete"/>
          <input type="hidden" name="lenya.step" value="delete"/>
          <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          
          <input type="hidden" name="properties.node.firstdocumentid" value="{$document-id}"/>
          <input type="hidden" name="properties.firstarea" value="{$area}"/>
          <input type="hidden" name="properties.secarea" value="trash"/>
          
          <input type="hidden" name="parenturl" value="{parent-url}"/>
          
          <table class="lenya-table-noborder">
            <tr>
              <td>
                <i18n:translate>
                  <i18n:text key="delete-language-versions?"/>
                  <i18n:param><strong><xsl:value-of select="document-id"/></strong></i18n:param>
                </i18n:translate>    
                <br/><br/>
              </td>               
            </tr>
            <tr>
              <xsl:apply-templates select="inconsistent-documents"/>
            </tr>
            <tr>
              <td>
                <br/>
                <input i18n:attr="value" type="submit" value="Delete" onclick="wait()" name="Delete"/>&#160;
                <input i18n:attr="value" type="button" onClick="location.href='{$request-uri}';" value="Cancel" name="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="inconsistent-documents">
    <td class="lenya-entry-caption">
      <span class="lenya-form-message-error"><i18n:text key="docs-have-links-to-doc">The following documents have links to this document:</i18n:text></span>
    </td>
    <td valign="top">
      <xsl:for-each select="inconsistent-document">
	<a target="_blank" href="{@href}"><xsl:value-of select="@id"/><xsl:value-of select="."/></a><br/>
      </xsl:for-each>
    </td>
  </xsl:template>
  
</xsl:stylesheet>
