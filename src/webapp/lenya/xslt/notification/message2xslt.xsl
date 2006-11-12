<?xml version="1.0" encoding="iso-8859-1"?>
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

<!--
	This stylesheet converts a notification message to a named stylesheet.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xslt="http://apache.org/cocoon/lenya/xslt/1.0"
    xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    >
    
<xsl:param name="server-uri"/>
<xsl:param name="document-url"/>
    
<xsl:namespace-alias stylesheet-prefix="xslt" result-prefix="xsl"/>
    
<xsl:template match="/">
	<xslt:stylesheet exclude-result-prefixes="not" version="1.0">
		<xsl:apply-templates/>
	</xslt:stylesheet>
</xsl:template>
		
		
<xsl:template match="/not:notification[not(@enabled = 'true')]">
	
	<xslt:template match="not:notification"/>
	<xslt:template match="not:notification-subject"/>
	<xslt:template match="not:notification-comment"/>
		
	<xslt:template match="@*|node()">
		<xslt:copy><xslt:apply-templates select="@*|node()"/></xslt:copy>
	</xslt:template>
 
</xsl:template>
	
	
<xsl:template match="/not:notification[@enabled = 'true']">
	
	<xslt:template match="not:notification">
		<input id="notification.subject" type="hidden" name="notification.subject"
			value="{not:message/not:subject}"/>
		<div class="lenya-box">
			<div class="lenya-box-title">Notification</div>
			<div class="lenya-box-body">
			<table class="lenya-table-noborder">
				<tr>
					<td class="lenya-entry-caption">Recipient(s):</td>
					<td>
						<xslt:apply-templates/>
					</td>
				</tr>
				<tr>
					<td class="lenya-entry-caption">Comment:</td>
					<td>
						<textarea id="notification.message" name="notification.message" class="lenya-form-element">
							<xsl:text/>
							<xsl:apply-templates select="not:message/not:body"/>
							<xsl:text>&#160;</xsl:text>
						</textarea>
					</td>
				</tr>
			</table>	
			</div>
		</div>
	</xslt:template>
	
	
	<xslt:template match="not:select">
		<select name="notification.tolist" class="lenya-form-element">
		  <option value="">[no notification]</option>
			<xslt:for-each select="not:users/not:user">
				<option>
					<xslt:attribute name="value"><xslt:value-of select="@email"/></xslt:attribute>
					<xslt:value-of select="@id"/>
					<xslt:if test="@name != ''">&#160;(<xslt:value-of select="@name"/>)</xslt:if>
				</option>
			</xslt:for-each>
		</select>
	</xslt:template>
	
	
	<xslt:template match="not:textarea">
		<textarea id="notification.tolist" name="notification.tolist" class="lenya-form-element">&#160;</textarea>
	</xslt:template>
	
	
	<xslt:template match="not:preset">
		<xslt:variable name="user" select="not:users/not:user"/>
		<input type="hidden" name="notification.tolist">
			<xslt:attribute name="value"><xslt:value-of select="$user/@email"/></xslt:attribute>
		</input> 
		<span style="white-space: nobreak">
			<xslt:value-of select="$user/@id"/>
			<xslt:if test="@name != ''">&#160;(<xslt:value-of select="$user/@name"/>)</xslt:if>
		</span>
	</xslt:template>
	
	
	<xslt:template match="@*|node()">
		<xslt:copy><xslt:apply-templates select="@*|node()"/></xslt:copy>
	</xslt:template>
 
</xsl:template>


<xsl:template match="not:body">
	<xsl:apply-templates select="node()"/>
</xsl:template>

<xsl:template match="not:body/text()"><xsl:value-of select="."/></xsl:template>

<xsl:template match="not:document-url">
	<xsl:value-of select="$server-uri"/><xsl:value-of select="$document-url"/>
</xsl:template>
	

</xsl:stylesheet>
