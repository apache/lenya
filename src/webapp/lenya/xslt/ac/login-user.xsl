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

<!-- $Id: login.xsl 473841 2006-11-12 00:46:38Z gregor $ -->
    
    <xsl:stylesheet version="1.0"
      xmlns:i18n="http://apache.org/cocoon/i18n/2.1"      
      xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
      xmlns:session="http://www.apache.org/xsp/session/2.0"
      xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <xsl:output indent="yes" version="1.0" />
      <xsl:param name="publication_name" />
      <xsl:param name="contextprefix" />
      <xsl:param name="area" />
      <xsl:variable name="copyright">Copyright &#169; 2003-2005 The Apache Software Foundation</xsl:variable>
      <xsl:template match="/">
        <xsl:apply-templates />
      </xsl:template>
      <xsl:template match="page">
        <page:page>
          <page:title>
            <xsl:call-template name="html-title" />
          </page:title>
          <page:body>
            <xsl:apply-templates select="body" />
            <p>
              <font face="verdana" size="-2">
                <xsl:value-of select="$copyright" />
              </font>
            </p>
          </page:body>
        </page:page>
      </xsl:template>
      <xsl:template name="html-title">
        <i18n:translate>
          <i18n:text i18n:key="login-to-pub"/>
          <i18n:param><xsl:call-template name="pubname" /></i18n:param>
        </i18n:translate>
      </xsl:template>
      <xsl:template match="login">
        <p>
          <xsl:apply-templates select="authentication_failed" />
          <xsl:apply-templates select="errors" />
          <xsl:apply-templates select="protected_destination" />
          <xsl:apply-templates select="current_username" />
          <xsl:apply-templates select="no_username_yet" />
          <xsl:apply-templates select="authenticator" />
        </p>
        <p>
          <strong>
            <i18n:text>NOTE</i18n:text>: </strong>
          <i18n:translate>
            <i18n:text i18n:key="try-user-lenya" />
            <i18n:param>"lenya"</i18n:param>
            <i18n:param>"levi"</i18n:param>
            <i18n:param>"alice"</i18n:param>
            <i18n:param>"levi"</i18n:param>
          </i18n:translate>
        </p>
        <div class="lenya-box">
          <div class="lenya-box-title">
            <i18n:text>Login</i18n:text>
          </div>
          <div class="lenya-box-body">
            <xsl:call-template name="loginFormWrapper"/>
          </div>
        </div>
      </xsl:template>
      
      
      <!--
        This template allows extending stylesheets to hide or change the login form.
      -->
      <xsl:template name="loginFormWrapper">
        <xsl:call-template name="loginForm"/>
      </xsl:template>
      
      
      <xsl:template name="loginForm">
        <form name="login" method="post" action="?lenya.usecase=login&amp;lenya.step=login">
          <table class="lenya-table-noborder">
            <tr>
              <td>
                <i18n:text>Username</i18n:text>:</td>
              <td>
                <input class="lenya-form-element" name="username"
                  type="text" />
              </td>
            </tr>
            <tr>
              <td>
                <i18n:text>Password</i18n:text>:</td>
              <td>
                <input class="lenya-form-element" name="password"
                  type="password" />
              </td>
            </tr>
            <tr>
              <td />
              <td>
                <input i18n:attr="value" type="submit" value="Login" name="submit"/>
              </td>
            </tr>
          </table>
        </form>
      </xsl:template>
      
      
      <xsl:template name="afterLoginForm"/>
      
      <xsl:template name="pubname">
        <xsl:value-of
          select="translate($publication_name, 'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
      </xsl:template>
      <xsl:template match="current_username">
        <br />
        Current username: 
        <xsl:apply-templates />
      </xsl:template>
      <xsl:template match="authenticator">
        <br />
        Last Authenticator: 
        <xsl:value-of select="name" />
        (
        <xsl:value-of select="@type" />
        )</xsl:template>
      <xsl:template match="no_username_yet">
        <br />
        No username yet</xsl:template>
      <xsl:template match="protected_destination">
        <br />
        Request for protected uri: 
        <a>
          <xsl:attribute name="href">
            <xsl:apply-templates />
          </xsl:attribute>
          <xsl:apply-templates />
        </a>
      </xsl:template>
      <xsl:template match="authentication_failed">
        <br />
        <font color="red">
          <i18n:text>Authentication failed</i18n:text>
        </font>
      </xsl:template>
      
      
      <xsl:template match="errors">
        <xsl:for-each select="error">
          <span style="color: red"><xsl:copy-of select="node()"/></span>
          <br/>
        </xsl:for-each>
      </xsl:template>
      
    </xsl:stylesheet>
    
    