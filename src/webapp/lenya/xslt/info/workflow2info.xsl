<?xml version="1.0"?>

<!--
 $Id: workflow2info.xsl,v 1.2 2003/08/15 13:14:30 andreas Exp $
 -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:wf="http://apache.org/cocoon/lenya/workflow/1.0"
   xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
   >
  
<xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

<xsl:template match="wf:history">
	<lenya-info:info>
		<lenya-info:workflow>
			<h2>Variables</h2>
			<table class="lenya-table">
				<tr><th>Name</th><th>Value</th></tr>
				<xsl:apply-templates select="wf:variable"/>
			</table>
			<h2>History</h2>
			<table class="lenya-table">
				<tr>
					<th>Date</th>
					<th>Event</th>
					<th>State</th>
					<th>User</th>
					<th>IP Address</th>
				</tr>
				<xsl:apply-templates select="wf:version"/>
			</table>
		</lenya-info:workflow>
	</lenya-info:info>
</xsl:template>

<xsl:template match="wf:version">
	<tr>
		<td><xsl:value-of select="@date"/></td>
		<td><xsl:value-of select="@event"/></td>
		<td><xsl:value-of select="@state"/></td>
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
