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

<!-- $Id: users.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

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
      <page:title><i18n:text>User Administration</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="users">
    <div style="margin: 10px 0px">
      <xsl:call-template name="add-user"/>
    </div>
    <table cellspacing="0" class="lenya-table">
      <tr>
        <th><i18n:text>User ID</i18n:text></th>
        <th><i18n:text>Full Name</i18n:text></th>
        <th><i18n:text>Groups</i18n:text></th>
        <th></th>
      </tr>
      <xsl:apply-templates select="user">
        <xsl:sort select="id"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>
  
  
  <xsl:template match="user">
    <tr>
      <td style="vertical-align: middle">
        <a href="users/{id}.html"><xsl:value-of select="id"/></a>
      </td>
      <td style="vertical-align: middle">
        <xsl:value-of select="name"/>
      </td>
      <xsl:apply-templates select="groups"/>
      <td style="vertical-align: middle">
        <form method="GET" name="user-form">
          <input type="hidden" name="lenya.usecase" value="userDeleteUser"/>
          <input name="user-id" type="hidden" value="{id}"/>
          <input i18n:attr="value" type="submit" value="Delete" name="Delete">
            <xsl:if test="@deletable = 'false'">
              <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
          </input>
        </form>
      </td>
    </tr>
  </xsl:template>
  
  
  <xsl:template match="groups">
   <td style="vertical-align: middle">
      <xsl:apply-templates select="group"/>
    </td>
  </xsl:template>
  
  
  <xsl:template match="group">
    <a href="groups/{@id}.html"><xsl:value-of select="@id"/></a>
    <xsl:if test="position() != last()">, <xsl:text/>
    </xsl:if>
  </xsl:template>
  
  
  <xsl:template name="add-user">
    <table class="lenya-table-noborder">
  	  <tr>
	    <td>
  		  <i18n:text>Add User</i18n:text>:&#160;
	    </td>
	    <xsl:for-each select="types/type">
	      <td>
	        <form method="GET" name="add-user-form">
              <input type="hidden" name="lenya.usecase">
		        <xsl:attribute name="value"><xsl:value-of
		          select="@create-use-case"/></xsl:attribute>
		      </input>
              <input i18n:attr="value" type="submit" name="submit">
		        <xsl:attribute name="value"><xsl:value-of
		          select="normalize-space(.)"/></xsl:attribute>
		      </input>
            </form>
	      </td>
	    </xsl:for-each>
      </tr>
    </table>
  </xsl:template>
  
  
</xsl:stylesheet>
