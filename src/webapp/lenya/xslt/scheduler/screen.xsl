<?xml version="1.0"?>
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

<!-- $Id: screen.xsl,v 1.3 2004/03/13 12:42:07 gregor Exp $ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:import href="../../../../../xslt/util/page-util.xsl"/>

  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>

  <xsl:variable name="separator" select="','"/>

  <xsl:variable name="referer"><xsl:value-of select="/usecase:schedule/usecase:referer"/></xsl:variable>


  <xsl:template match="/usecase:schedule">

    <page:page>
      <page:title><i18n:text>Schedule</i18n:text></page:title>
      <page:body>
        
        <table class="lenya-table-noborder">
        <tr>
        <td>
        
          
        <not:notification>
          <not:preset>
            <xsl:apply-templates select="not:users/not:user"/>
          </not:preset>
          <not:textarea/>
        </not:notification>
          
        <sch:scheduler-form>
          
          <sch:job tasklabel="Publish">
            <input type="hidden" name="wrapper.task-id" value="publish"/>
            <input type="hidden" name="workflow-event" value="publish"/>
            <xsl:call-template name="task-parameters"/>
          </sch:job>
          
          <sch:job tasklabel="Deactivate">
            <input type="hidden" name="wrapper.task-id" value="deactivateDocument"/>
            <input type="hidden" name="workflow-event" value="deactivate"/>
            <xsl:call-template name="task-parameters"/>
          </sch:job>
          
        </sch:scheduler-form>
        
        <div style="text-align: right">
        <form action="{$referer}">
          <input i18n:attr="value" type="submit" value="Back"/>
        </form>
        </div>
        </td>
        </tr>
        </table>
          
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template name="task-parameters">
    <input type="hidden" name="document-id" value="{/usecase:schedule/usecase:document-id}"/>
    <input type="hidden" name="document-language" value="{/usecase:schedule/usecase:document-language}"/>
    <input type="hidden" name="user-id" value="{/usecase:schedule/usecase:user-id}"/>
    <input type="hidden" name="ip-address" value="{/usecase:schedule/usecase:ip-address}"/>
    <input type="hidden" name="role-ids" value="{/usecase:schedule/usecase:role-ids}"/>
  </xsl:template>
  

  <xsl:template match="referenced-documents">
    <td class="lenya-entry-caption" valign="top">
      <span class="lenya-form-message-error"><i18n:text key="doc-has-links-to-unpublished"/></span>
    </td>
    <td valign="top">
      <xsl:for-each select="referenced-document">
        <a target="_blank" href="{@href}"><xsl:value-of select="@id"/><xsl:value-of select="."/></a><br/>
      </xsl:for-each>
    </td>
  </xsl:template>
  

</xsl:stylesheet>  
