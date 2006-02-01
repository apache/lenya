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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"    
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
  >
 
<xsl:import href="../util/page-util.xsl"/>

<xsl:output version="1.0" indent="yes" encoding="UTF-8"/>

<xsl:param name="lenya.event"/>

<xsl:template match="/usecase:reject">

  <page:page>
    <page:title><i18n:text>Reject</i18n:text>: <xsl:value-of select="document-id"/></page:title>
    <page:body>
      <div class="lenya-box">
        <div class="lenya-box-title">
          <i18n:translate>
          <i18n:text i18n:key="reject-doc?"/>
          <i18n:param><xsl:value-of select="document-id"/></i18n:param>
          </i18n:translate>
        </div>     
      <div class="lenya-box-body">
        <form method="GET" action="" name="reject-form">
          <table class="lenya-table-noborder">
            <tr>
              <td class="lenya-entry-caption" valign="top">
                <input type="hidden" name="lenya.usecase" value="transition"/>
                <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
                <input type="hidden" name="task-id" value="ant"/>
                <input type="hidden" name="target" value="mail"/>
                <input type="hidden" name="notification.tolist" value=""/>
                <not:notification>
                  <not:preset><xsl:copy-of select="not:users"/></not:preset>
                </not:notification>
              </td>
            </tr>
            <tr>
              <td>&#160;</td>
            </tr>
            <tr>
          <td/>
          <td>
            <input i18n:attr="value" type="submit" value="Reject"
          onclick="elements['notification.tolist'].value = document.getElementById('notification.tolist.preset').value" name="Reject"/>
            <input i18n:attr="value" type="button" onClick="location.href='{usecase:referer}';" value="Cancel" name="Cancel"/>
          </td>
          </tr>
            <tr>
              <td>&#160;</td>
            </tr>
          </table>
        </form>
      </div>         
      </div> 
    </page:body>
  </page:page>
</xsl:template>

</xsl:stylesheet>