<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id$ -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
  xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
>

<!-- ============================================================= -->
<!--   Generate the form to schedule new jobs -->
<!-- ============================================================= -->
  <xsl:template name="scheduler-form">
    <xsl:variable name="scheduler-data"
    select="//sch:scheduler-data" />
    <xsl:for-each select="$scheduler-data/sch:parameter">
      <xsl:if
      test="@name != 'workflow.event' and @name != 'wrapper.task-id'">

        <input type="hidden" name="{@name}" value="{@value}" />
      </xsl:if>
    </xsl:for-each>
    <input type="hidden" name="scheduler.action" value="add" />
    <input type="hidden" name="lenya.usecase" value="schedule" />
    <tr>
      <td>
        <strong><i18n:text>New job</i18n:text>:</strong>
      </td>
      <td>
        <i18n:text><xso:value-of select="@tasklabel" /></i18n:text>
        <xso:if test="count(sch:job) &gt; 1">
          <xso:variable name="form-name"
          select="ancestor::xhtml:form/@name" />
<!-- This is a very ugly hack. The whole scheduler concept
                       has to be reconsidered ASAP. (Andreas) YES(Rolf) -->
          <select>
            <xso:for-each select="sch:job">
              <option>
                <xsl:attribute name="onclick">
                javascript:taskId.value='{@task-id}';wrapperTaskId.value='{@task-id}';workflowEvent.value='{@event}';</xsl:attribute>
                <xso:value-of select="." />
              </option>
            </xso:for-each>
          </select>
        </xso:if>
      </td>
      <td style="white-space: nowrap">
        <select name="scheduler.trigger.year">
          <xsl:variable name="year">
            <xsl:value-of select="$scheduler-data/sch:year" />
          </xsl:variable>
          <xsl:call-template name="generateSelectionNames">
            <xsl:with-param name="currentValue" select="$year" />
            <xsl:with-param name="selectedValue" select="$year" />
            <xsl:with-param name="maxValue" select="$year + 2" />
          </xsl:call-template>
        </select>
      </td>
      <td style="white-space: nowrap">- 
      <select name="scheduler.trigger.month">
        <xsl:call-template name="generateSelectionNames">
          <xsl:with-param name="currentValue" select="1" />
          <xsl:with-param name="selectedValue"
          select="$scheduler-data/sch:month" />
          <xsl:with-param name="maxValue" select="12" />
        </xsl:call-template>
      </select>
      </td>
      <td style="white-space: nowrap">- 
      <select name="scheduler.trigger.day">
        <xsl:call-template name="generateSelectionNames">
          <xsl:with-param name="currentValue" select="1" />
          <xsl:with-param name="selectedValue"
          select="$scheduler-data/sch:day" />
          <xsl:with-param name="maxValue" select="31" />
        </xsl:call-template>
      </select>
      </td>
      <td style="white-space: nowrap">
        <input name="scheduler.trigger.hour" type="text" size="2"
        maxlength="2">
          <xsl:attribute name="value">
            <xsl:value-of
            select="format-number($scheduler-data/sch:hour, '00')" />
          </xsl:attribute>
        </input>
      </td>
      <td style="white-space: nowrap">: 
      <input name="scheduler.trigger.minute" type="text" size="2"
      maxlength="2">
        <xsl:attribute name="value">
          <xsl:value-of
          select="format-number($scheduler-data/sch:minute, '00')" />
        </xsl:attribute>
      </input>
      </td>
      <td>
        <input type="hidden" name="notification.subject" value=""/>
        <input type="hidden" name="notification.message" value=""/>
        <input type="hidden" name="notification.tolist" value=""/>
        <input i18n:attr="value" type="submit" value="Add"
          onclick="
            elements['notification.subject'].value = document.getElementById('notification.subject').value;
            elements['notification.message'].value = document.getElementById('notification.message').value;
            elements['notification.tolist'].value = document.getElementById('notification.tolist').value.replace(/[^a-zA-Z0-9_,.@%-]/g, '')+','+document.getElementById('notification.tolist.preset').value;
          " name="Add"/>
      </td>
    </tr>
  </xsl:template>
<!-- ============================================================= -->
<!--   Generate numbers from 1 to maxValue for a <select> and select a -->
<!--   given value -->
<!-- ============================================================= -->
  <xsl:template name="generateSelectionNames">
    <xsl:param name="currentValue" />
    <xsl:param name="selectedValue" />
    <xsl:param name="maxValue" />
    <xsl:choose>
      <xsl:when test="$currentValue = $selectedValue">
        <option>
          <xsl:attribute name="selected" />
          <xsl:value-of select="$currentValue" />
        </option>
      </xsl:when>
      <xsl:otherwise>
        <option>
          <xsl:value-of select="$currentValue" />
        </option>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="$currentValue &lt; $maxValue">
      <xsl:call-template name="generateSelectionNames">
        <xsl:with-param name="currentValue"
        select="$currentValue + 1" />
        <xsl:with-param name="selectedValue"
        select="$selectedValue" />
        <xsl:with-param name="maxValue" select="$maxValue" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>

