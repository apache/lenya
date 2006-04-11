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

<xsl:stylesheet version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0">

  <xsl:param name="requesturi"/>
  <xsl:param name="area"/>
  <xsl:param name="language"/>
  <xsl:param name="href"/>
  <xsl:param name="documentid"/>
  <xsl:param name="taskid"/>
  <xsl:param name="lenya.event"/>

  <xsl:template match="/">
    <page:page>
      <page:title>
        <i18n:text>Edit Navigation Link</i18n:text>
      </page:title>
      <page:body>
        <div class="lenya-box">
          <div class="lenya-box-title">
            <i18n:text>Edit Navigation Link</i18n:text>
          </div>
          <div class="lenya-box-body">
            <form method="get" name="rename-label-form">
              <xsl:attribute name="action"></xsl:attribute>
              <input type="hidden" name="task-id" value="{$taskid}"/>
              <input type="hidden" name="properties.change.href.document-id" value="{$documentid}"/>
              <input type="hidden" name="properties.change.href.language" value="{$language}"/>
              <input type="hidden" name="properties.change.href.area" value="{$area}"/>
              <input type="hidden" name="lenya.usecase" value="change-href"/>
              <input type="hidden" name="lenya.step" value="change-href"/>
              <input type="hidden" name="lenya.event" value="{$lenya.event}"/>

              <table class="lenya-table-noborder">
                <tr>
                  <td class="lenya-entry-caption">
                    <i18n:text>New Navigation Link</i18n:text>:</td>
                  <td>
                    <input type="text" class="lenya-form-element" name="properties.change.href.href" value="{$href}"/>
                  </td>
                </tr>
                <tr>
                  <td/>
                  <td>
                    <br/>
                    <input i18n:attr="value" type="submit" value="Save" name="Save"/>&#160;
                    <input i18n:attr="value" type="button" onClick="location.href='{$requesturi}';" value="Cancel" name="Cancel"/>
                  </td>
                </tr>
              </table>
            </form>
          </div>
        </div>
      </page:body>
    </page:page>
  </xsl:template>

</xsl:stylesheet>