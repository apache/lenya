<?xml version="1.0" encoding="UTF-8"?>
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

<!-- $Id: password.xsl,v 1.2 2004/03/13 12:42:11 gregor Exp $ -->

<xsl:stylesheet
    version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns:session="http://www.apache.org/xsp/session/2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >
    
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title>Change Password</page:title>
      <page:body>
        <xsl:apply-templates select="user"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
    
    <table class="lenya-noborder">
    <tr>
    <td valign="top">
    
    <div class="lenya-box">
      <div class="lenya-box-title">Change Password</div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{/page/continuation}.continuation">
          <table class="lenya-table-noborder">
            
            <xsl:apply-templates select="message"/>
            
            <tr>
              
              <td class="lenya-entry-caption">User&#160;ID:</td>
              <td>
                 <xsl:value-of select="id"/>
              </td>
            </tr>
            <xsl:if test="check-password/text() = 'true'">
            <tr>
              <td class="lenya-entry-caption">Old&#160;password:</td>
              <td>
                <input class="lenya-form-element-narrow" name="old-password" type="password"/>
              </td>
            </tr>
            </xsl:if>
            <tr>
              <td class="lenya-entry-caption">New&#160;password:</td>
              <td>
                <input class="lenya-form-element-narrow" name="new-password" type="password">
                  <xsl:attribute name="value">
                    <xsl:value-of select="new-password"/>
                  </xsl:attribute>
                </input>
              </td>
            </tr>
            <tr>
              <td class="lenya-entry-caption">Confirm&#160;password:</td>
              <td>
                <input class="lenya-form-element-narrow" name="confirm-password" type="password">
                  <xsl:attribute name="value">
                    <xsl:value-of select="confirm-password"/>
                  </xsl:attribute>
                </input>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
                <input name="submit" type="submit" value="Submit"/>
                &#160;
                <input name="cancel" type="submit" value="Cancel"/>
              </td>
            </tr>
          </table>
        </form>
      </div>
    </div>
    
    </td>
    </tr>
    </table>
    
  </xsl:template>
  
  
  <xsl:template match="groups">
    <xsl:apply-templates select="group"/>
  </xsl:template>
  
  
  <xsl:template match="group">
    <xsl:if test="position() &gt; 1"><br/></xsl:if>
    <xsl:value-of select="."/>
  </xsl:template>
  
  
  <xsl:template match="message">
    <xsl:if test="text()">
      <tr>
        <td colspan="2"><span class="lenya-form-message-error"><xsl:apply-templates/></span></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
