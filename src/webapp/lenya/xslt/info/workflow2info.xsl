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

<!-- $Id: workflow2info.xsl,v 1.6 2004/03/13 12:42:06 gregor Exp $ -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
   xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
   >
  
<xsl:output version="1.0" indent="yes" encoding="UTF-8"/>

<xsl:template match="wf:history">
	<lenya-info:info>
		<lenya-info:workflow>
			<h2><i18n:text>Variables</i18n:text></h2>
			<table class="lenya-table">
				<tr><th><i18n:text>Name</i18n:text></th><th><i18n:text>Value</i18n:text></th></tr>
				<xsl:apply-templates select="wf:variable"/>
			</table>
			<h2><i18n:text>History</i18n:text></h2>
			<table class="lenya-table">
				<tr>
					<th><i18n:text>Date</i18n:text></th>
					<th><i18n:text>Event</i18n:text></th>
					<th><i18n:text>State</i18n:text></th>
					<th><i18n:text>User</i18n:text></th>
					<th><i18n:text>IP Address</i18n:text></th>
				</tr>
				<xsl:apply-templates select="wf:version">
				    <xsl:sort select="@date" order="descending"/>
				</xsl:apply-templates>
			</table>
		</lenya-info:workflow>
	</lenya-info:info>
</xsl:template>

<xsl:template match="wf:version">
	<tr>
<!--		<td><i18n:date src-pattern="dd.MM.yyyy HH:mm:ss" pattern="yyyy-M-dd HH:mm:ss"><xsl:value-of select="@date"/></i18n:date></td> -->
		<td><xsl:value-of select="@date"/></td>
		<td><xsl:value-of select="@event"/></td>
		<td><i18n:text><xsl:value-of select="@state"/></i18n:text></td>
		<td>
			<span style="white-space: nobreak">
			<xsl:value-of select="wf:identity/wf:user/@id"/>
			<xsl:if test="wf:identity/wf:user/@name != ''">
				(<xsl:value-of select="wf:identity/wf:user/@name"/>)
			</xsl:if>
			</span>
		</td>
		<td>
			<xsl:value-of select="wf:identity/wf:machine/@ip-address"/>
		</td>
	</tr>
</xsl:template>

<xsl:template match="wf:variable">
	<tr>
		<td>
			<xsl:value-of select="@name"/>
		</td>
		<td>
			<xsl:value-of select="@value"/>
		</td>
	</tr>
</xsl:template>

<xsl:template match="@*|node()">
	<xsl:copy>
		<xsl:apply-templates select="@*|node()"/>
	</xsl:copy>
</xsl:template>  

</xsl:stylesheet>
