<?xml version="1.0"?>

<!--
 $Id: workflow2info.xsl,v 1.1 2003/08/15 08:30:56 andreas Exp $
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
					<th>Event</th>
					<th>State</th>
				</tr>
				<xsl:apply-templates select="wf:version"/>
			</table>
		</lenya-info:workflow>
	</lenya-info:info>
</xsl:template>

<xsl:template match="wf:version">
	<tr>
		<td>
			<xsl:value-of select="@event"/>
		</td>
		<td>
			<xsl:value-of select="@state"/>
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
