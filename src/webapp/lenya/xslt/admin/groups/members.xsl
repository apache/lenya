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

<!-- $Id: members.xsl,v 1.3 2004/03/13 12:42:14 gregor Exp $ -->

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
      <page:title>Group Members: <xsl:value-of select="group/id"/></page:title>
      <page:body>
    <table class="lenya-noborder">
    <tr>
    <td>
    
    <div class="lenya-box">
      <div class="lenya-box-title">Group Affiliation</div>
      <div class="lenya-box-body">
        
        <form method="GET" action="{continuation}.continuation">
          <input type="hidden" name="user-id" value="{id}"/>
          
                <table class="lenya-table-noborder-nopadding">
                  <tr>
                    <td><strong>Group users</strong></td>
                    <td/>
                    <td><strong>All users</strong></td>
                  </tr>
                  <tr>
                    <td valign="middle">
                      <select name="group_user" size="15" class="lenya-form-element-narrow">
                        <xsl:apply-templates select="list[@type='group-users']"/>
                      </select>
                    </td>
                    <td valign="middle">
                      <input name="add_user" type="submit" value="&lt;"/>
                      <br/>
                      <input name="remove_user" type="submit" value="&gt;"/>
                    </td>
                    <td valign="middle">
                      <select name="user" size="15" class="lenya-form-element-narrow">
                        <xsl:apply-templates select="list[@type='users']"/>
                      </select>
                    </td>
                  </tr>
                </table>
                
                <div style="margin-top: 10px; text-align: center">
                  <input type="submit" name="submit" value="Submit"/>
                  &#160;
                  <input type="submit" name="cancel" value="Cancel"/>
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
  
  
  <xsl:template match="list">
    <xsl:apply-templates select="member"/>
  </xsl:template>
  
  
  <xsl:template match="member">
    <option value="{@id}">
    	<xsl:value-of select="@id"/>
    	<xsl:if test="normalize-space(.)">
    		&#160;<em>(<xsl:value-of select="normalize-space(.)"/>)</em>
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
