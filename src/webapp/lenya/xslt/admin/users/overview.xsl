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

<!-- $Id: overview.xsl,v 1.9 2004/03/13 12:42:11 gregor Exp $ -->

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
      <page:title><i18n:text>User Details</i18n:text>: <xsl:value-of select="user/id"/></page:title>
      <page:body>
        <xsl:apply-templates select="message"/>
        <xsl:apply-templates select="user"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="user">
    
    <table class="lenya-noborder">
      
    <tr>
    <td>
    
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Profile</i18n:text></div>
      <div class="lenya-box-body">
        
          <table class="lenya-table-noborder">
            
            <tr>
              <td class="lenya-entry-caption"><i18n:text>User ID</i18n:text>:</td>
              <td><xsl:value-of select="id"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Full Name</i18n:text>:</td>
              <td><xsl:value-of select="name"/></td>
            </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Email</i18n:text>:</td>
              <td><xsl:value-of select="email"/></td>
            </tr>
            <tr>
              <td valign="top" class="lenya-entry-caption"><i18n:text>Description</i18n:text>:</td>
              <td><xsl:value-of select="description"/></td>
            </tr>
            <tr>
              <td/>
              <td>
				        <form method="GET" action="lenya.usecase.change_profile">
				          <input i18n:attr="value" type="submit" value="Edit Profile"/>
				        </form>
              </td>
            </tr>
          </table>
      </div>
    </div>

    <xsl:if test="not(@ldap = 'true')">
      <xsl:call-template name="password"/>
    </xsl:if>
    
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Group Affiliation</i18n:text></div>
      <div class="lenya-box-body">
        
          <table class="lenya-table-noborder">
            
            <tr>
              <td class="lenya-entry-caption" valign="top"><i18n:text>Groups</i18n:text>:</td>
              <td>
                <xsl:apply-templates select="groups"/>
              </td>
            </tr>
            <tr>
              <td/>
              <td>
				        <form method="GET" action="lenya.usecase.change_groups">
				          <input i18n:attr="value" type="submit" value="Edit Group Affiliation"/>
				        </form>
              </td>
            </tr>
          </table>
      </div>
    </div>
            
    </td>
    </tr>
    </table>
    
  </xsl:template>
  
  
  <xsl:template match="groups">
      <xsl:apply-templates select="group">
        <xsl:sort/>
      </xsl:apply-templates>
  </xsl:template>
  
  
  <xsl:template match="group">
    <xsl:if test="position() &gt; 1"><br/></xsl:if>
    <span style="white-space: nowrap">
    <a href="../../groups/{@id}/index.html"><xsl:value-of select="@id"/></a>
    <xsl:if test="normalize-space(.) != ''">
      &#160;(<xsl:value-of select="."/>)
    </xsl:if>
    </span>
  </xsl:template>
  
  
  <xsl:template name="password">
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Password</i18n:text></div>
      <div class="lenya-box-body">
        
          <table class="lenya-table-noborder">
            
            <tr>
              <td class="lenya-entry-caption"><i18n:text>User</i18n:text>:</td>
              <td>
				        <form method="GET" action="lenya.usecase.change_password_user">
				          <input i18n:attr="value" type="submit" value="Change Password"/>
				        </form>
				      </td>
				    </tr>
            <tr>
              <td class="lenya-entry-caption"><i18n:text>Admin</i18n:text>:</td>
              <td>
				        <form method="GET" action="lenya.usecase.change_password_admin">
				          <input i18n:attr="value" type="submit" value="Change Password"/>
				        </form>
              </td>
            </tr>
          </table>
      </div>
    </div>
  </xsl:template>
  
  
  <xsl:template match="message">
    <xsl:if test="text()">
      <tr>
        <td colspan="2"><span class="lenya-form-message-{@type}"><xsl:apply-templates/></span></td>
      </tr>
    </xsl:if>
  </xsl:template>
  
  
</xsl:stylesheet>
