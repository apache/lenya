<?xml version="1.0" encoding="UTF-8" ?>
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

<!-- $Id: joblist.xsl,v 1.3 2004/03/13 12:42:07 gregor Exp $ -->

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


<xsl:template match="/sch:scheduler">
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match="sch:job-group">
    
  <table class="lenya-table" border="0" cellpadding="0" cellspacing="0">

    <tr>
      <th>Task</th>
      <th>Day</th>
      <th>Time</th>
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
		<td>
			<xsl:variable name="current-task-id" select="task:task/task:parameter[@name='wrapper.task-id']/@value"/>
			<xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = $current-task-id]/sch:label"/>
		</td>
		<xsl:choose>
			<xsl:when test="sch:trigger">
				<xsl:apply-templates select="sch:trigger"/>
			</xsl:when>
			<xsl:otherwise>
				<td colspan="2">The job date has expired.</td>
			</xsl:otherwise>
		</xsl:choose>
  </tr>
</xsl:template>


<xsl:template match="sch:trigger">
	<td>
		<xsl:value-of select="sch:parameter[@name='year']/@value"/>
		<xsl:text>-</xsl:text>
		<xsl:value-of select="sch:parameter[@name='month']/@value"/>
		<xsl:text>-</xsl:text>
		<xsl:value-of select="sch:parameter[@name='day']/@value"/>.
	</td>
	<td>
		<xsl:value-of select="sch:parameter[@name='hour']/@value"/>
		<xsl:text>:</xsl:text>
		<xsl:value-of select="sch:parameter[@name='minute']/@value"/>
	</td>
</xsl:template>


</xsl:stylesheet> 
