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

<!-- $Id$ -->

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
      <page:title><i18n:text>Group Administration</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="groups">
    <div style="margin: 10px 0px">
      <xsl:call-template name="add-group"/>
    </div>
    <table cellspacing="0" class="lenya-table">
      <tr>
        <th><i18n:text>Group ID</i18n:text></th>
        <th><i18n:text>Name</i18n:text></th>
        <th></th>
      </tr>
      <xsl:apply-templates select="group">
        <xsl:sort select="id"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>
  
  
  <xsl:template match="group">
    <tr>
      <td style="vertical-align: middle">
        <a href="groups/{id}.html"><xsl:value-of select="id"/></a>
      </td>
      <td style="vertical-align: middle">
        <xsl:value-of select="name"/>
      </td>
      <td style="vertical-align: middle">
        <form method="GET">
          <input type="hidden" name="lenya.usecase" value="admin.deleteGroup"/>
          <input name="groupId" type="hidden" value="{id}"/>
          <input i18n:attr="value" type="submit" value="Delete"/>
        </form>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template name="add-group">
    <form method="GET">
          <input type="hidden" name="lenya.usecase" value="admin.addGroup"/>
      <input i18n:attr="value" type="submit" value="Add Group"/>
    </form>
  </xsl:template>
  
  
</xsl:stylesheet>
