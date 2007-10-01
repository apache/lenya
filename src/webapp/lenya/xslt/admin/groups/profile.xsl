<?xml version="1.0" encoding="UTF-8"?>
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

<!-- $Id: profile.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="UTF-8" indent="yes" version="1.0"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title><i18n:text><xsl:value-of select="title"/></i18n:text></page:title>
      <page:body>
        <xsl:apply-templates select="messages"/>
        <xsl:apply-templates select="group"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="messages">
    <xsl:if test="message">
      <ul>
        <xsl:for-each select="message">
          <li><xsl:value-of select="."/></li>
        </xsl:for-each>
      </ul>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template match="group">
    
    <table class="lenya-noborder">
    <tr>
    <td valign="top">
    
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Group Data</i18n:text></div>
      <div class="lenya-box-body">
        
        <form method="GET" name="groupprofile-form">
          <input type="hidden" name="lenya.continuation" value="{/page/continuation}"/>
          <table class="lenya-table-noborder">
            
            <xsl:apply-templates select="message"/>
            
            <tr>
              
              <td class="lenya-entry-caption"><i18n:text>Group ID</i18n:text></td>
              <td>
                 <xsl:choose>
                   <xsl:when test="@new = 'true'">
                     <input class="lenya-form-element" name="group-id" type="text" value="{id}"/>
                   </xsl:when>
                   <xsl:otherwise>
                     <xsl:value-of select="id"/>
                   </xsl:otherwise>
                 </xsl:choose>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Name</i18n:text></td>
              <td>
                <input class="lenya-form-element" name="name" type="text" value="{name}"/>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption" valign="top"><i18n:text>Description</i18n:text></td>
              <td>
                <textarea class="lenya-form-element" name="description"><xsl:value-of select="description"/>&#160;</textarea>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption" valign="top"><i18n:text>Rule</i18n:text></td>
              <td>
                <textarea class="lenya-form-element" name="rule" rows="5"><xsl:value-of select="rule"/>&#160;</textarea>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
                <input i18n:attr="value" name="submit" type="submit" value="Save"/>
                &#160;
                <input i18n:attr="value" name="cancel" type="submit" value="Cancel"/>
              </td>
            </tr>
          </table>
          <hr/>
          
          <strong><i18n:text>Example rule</i18n:text>:</strong><br/>
          <code style="white-space: nowrap;">
            swissEduPersonHomeOrganization == "unizh" &amp;&amp; surname == "Meier"
          </code>
          <br/><br/>
          <strong><i18n:text>Available attributes</i18n:text></strong>:
          <table class="lenya-table" style="margin-top: 1em;">
            <tr>
              <th style="border-right: none"><i18n:text>Name</i18n:text></th>
              <th style="border-left: none"><i18n:text>Description</i18n:text></th>
            </tr>
            <xsl:for-each select="../attributeNames/attributeName">
              <xsl:sort select="."/>
              <tr>
                <td style="border-right: none"><xsl:value-of select="."/></td>
                <td style="border-left: none">
                  <i18n:text>accesscontrol.attribute.<xsl:value-of select="."/></i18n:text>
                </td>
              </tr>
            </xsl:for-each>
          </table>
          
        </form>
      </div>
    </div>
    
    </td>
    </tr>
    </table>
    
  </xsl:template>
  
  
  <xsl:template match="message">
    <xsl:if test="text()">
      <tr>
        <td colspan="2"><span class="lenya-form-message-{@type}"><i18n:text><xsl:apply-templates/></i18n:text></span></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
