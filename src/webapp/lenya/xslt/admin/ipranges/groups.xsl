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

<!-- $Id: groups.xsl,v 1.4 2004/03/13 12:42:06 gregor Exp $ -->

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
      <page:title><i18n:text>IP Range Overview</i18n:text>: <xsl:value-of select="iprange/id"/></page:title>
      <page:body>
    <table class="lenya-noborder">
    <tr>
    <td>
    
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Group Affiliation</i18n:text></div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{continuation}.continuation">
          <input type="hidden" name="iprange-id" value="{id}"/>
          
                <table class="lenya-table-noborder-nopadding">
                  <tr>
                    <td><strong><i18n:text>IP Range Groups</i18n:text></strong></td>
                    <td/>
                    <td><strong><i18n:text>All Groups</i18n:text></strong></td>
                  </tr>
                  <tr>
                    <td valign="middle">
                      <select name="iprange_group" size="15" class="lenya-form-element-narrow">
                        <xsl:apply-templates select="iprange-groups"/>
                      </select>
                    </td>
                    <td valign="middle">
                      <input name="add_group" type="submit" value="&lt;"/>
                      <br/>
                      <input name="remove_group" type="submit" value="&gt;"/>
                    </td>
                    <td valign="middle">
                      <select name="group" size="15" class="lenya-form-element-narrow">
                        <xsl:apply-templates select="groups"/>
                      </select>
                    </td>
                  </tr>
                </table>
                
                <div style="margin-top: 10px; text-align: center">
                  <input i18n:attr="value" type="submit" name="submit" value="Save"/>
                  &#160;
                  <input i18n:attr="value" type="submit" name="cancel" value="Cancel"/>
                </div>
                </form>
              </div>
            </div>
          </td>
        </tr>
    </table>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="groups">
    <xsl:apply-templates select="group"/>
  </xsl:template>
  
  
  <xsl:template match="group">
    <option value="{@id}">
    	<xsl:value-of select="@id"/>
    	<xsl:if test="normalize-space(.)">
    		&#160;(<xsl:value-of select="normalize-space(.)"/>)
    	</xsl:if>
    </option>
  </xsl:template>
  
  
  <xsl:template match="message">
    <xsl:if test="text()">
      <tr>
        <td colspan="2"><span class="lenya-form-message-{@type}"><xsl:apply-templates/></span></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
