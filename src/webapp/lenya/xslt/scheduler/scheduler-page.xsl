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

<!-- $Id: scheduler-page.xsl 473841 2006-11-12 00:46:38Z gregor $ -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
    xmlns:task="http://apache.org/cocoon/lenya/task/1.0"
    >

<xsl:import href="common.xsl"/>
    
<xsl:param name="document-url"/>
<xsl:param name="area"/>
<xsl:param name="context-prefix"/>
<xsl:param name="publication-id"/>


<!-- navigation menu -->
<xsl:template name="navigation-menu">
  <xsl:variable name="uri-prefix" select="concat($context-prefix, '/', $publication-id)"/>
	<xsl:variable name="menu-separator" select="'&#160;&#160;|&#160;&#160;'"/>
	<p>
	<a class="menu-item" href="{$context-prefix}/{$publication-id}/{$area}{$document-url}">Back to page</a>
	<xsl:value-of select="$menu-separator"/>
	<a class="menu-item" href="lenya.usecase=schedule&amp;lenya.step=showscreen">Refresh</a>
	</p>
	<!-- DEBUG
	<xsl:value-of select="$menu-separator"/>
	<a class="menu-item" href="{$context-prefix}/servlet/LoadQuartzServlet">Servlet</a>
	-->
</xsl:template>


<xsl:template match="/sch:scheduler">
  <page:page>
    <page:title>Scheduler</page:title>
    <page:body>
      <xsl:call-template name="navigation-menu"/>
      <xsl:apply-templates select="sch:exception"/>
      <xsl:apply-templates select="sch:job-group"/>
    </page:body>
  </page:page>
</xsl:template>


<xsl:template match="sch:job-group">
    
  <table class="lenya-table" border="0" cellpadding="0" cellspacing="0">

    <tr>
      <th>Document</th>
      <th>Task</th>
      <th>Day</th>
      <th>Time</th>
      <th>&#160;</th>
      <th>&#160;</th>
    </tr>

    <xsl:choose>
      <xsl:when test="sch:job">
        <xsl:apply-templates select="sch:job"/>
      </xsl:when>
      <xsl:otherwise>
        <tr><td colspan="6">No active jobs.</td></tr>
      </xsl:otherwise>
    </xsl:choose>

  </table>
</xsl:template>


<xsl:template match="sch:job">
  <tr>
    <form method="GET" name="scheduler-form">
    	
   		<td><xsl:value-of select="substring-after(@url, concat('/', ../@name))"/></td>
    	
			<td>
        <!-- hidden input fields for parameters -->
        <input type="hidden" name="lenya.usecase" value="schedule"/>
        <input type="hidden" name="lenya.step" value="showscreen"/>
        <input type="hidden" name="scheduler.publication-id" value="{$publication-id}"/>
        <input type="hidden" name="scheduler.job.id" value="{@id}"/>
        
				<xsl:variable name="current-task-id" select="task:task/task:parameter[@name='wrapper.task-id']/@value"/>
				<xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = $current-task-id]/sch:label"/>
			</td>
      <xsl:choose>
        <xsl:when test="sch:trigger">
          <xsl:apply-templates select="sch:trigger"/>
          <td>
            <input type="submit" name="scheduler.action" value="modify"/>
          </td>
        </xsl:when>
        <xsl:otherwise>
          <td colspan="2">The job date has expired.</td>
          <td>&#160;</td>
        </xsl:otherwise>
      </xsl:choose>
      <td>
        <input type="submit" name="scheduler.action" value="delete"/>
      </td>
    </form>
  </tr>
</xsl:template>


  <xsl:template match="sch:trigger">
    <td>
			<xsl:apply-templates select="sch:parameter[@name='year']"/> -
			<xsl:apply-templates select="sch:parameter[@name='month']"/> -
			<xsl:apply-templates select="sch:parameter[@name='day']"/>
    </td>
    <td>
			<xsl:apply-templates select="sch:parameter[@name='hour']"/>
			: 
			<xsl:apply-templates select="sch:parameter[@name='minute']"/>
    </td>
  </xsl:template>
  
  <xsl:template match="sch:trigger/sch:parameter[@name='day']">
    <select name="scheduler.trigger.{@name}">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="1"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="31"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:trigger/sch:parameter[@name='month']">
    <select name="scheduler.trigger.{@name}">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="1"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="12"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:trigger/sch:parameter[@name='year']">
    <select name="scheduler.trigger.{@name}">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="@value"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="@value + 2"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:trigger/sch:parameter[@name='hour' or @name='minute']">
      <input type="text" name="scheduler.trigger.{@name}" size="2" maxlength="2">
      <xsl:attribute name="value">
        <xsl:value-of select="format-number(@value, '00')"/>
      </xsl:attribute>
    </input>
  </xsl:template>

<xsl:template match="sch:exception">
<span style="color: red">EXCEPTION: <xsl:value-of select="@type"/></span> (check the log files)
</xsl:template>


</xsl:stylesheet> 
