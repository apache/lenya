<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : shared.xsl
    Created on : 14. Mai 2003, 18:33
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
    >

<!-- ============================================================= -->
<!--   Generate the necessary form to schedule new jobs -->
<!-- ============================================================= -->
<xsl:template name="scheduler-form">
	
	<xsl:param name="task-id"/>
	
	<xsl:variable name="scheduler-data" select="//sch:scheduler-data"/>
	
	<form method="GET">
		
		<input type="hidden" name="lenya.usecase" value="schedule"/>
		<input type="hidden" name="lenya.step" value="add"/>
		<input type="hidden" name="task.id" value="{$task-id}"/>
		<input type="hidden" name="publication-id" value="{normalize-space($scheduler-data/sch:publication-id)}"/>
		<input type="hidden" name="document-url" value="{normalize-space($scheduler-data/sch:document-url)}"/>
		
		<xsl:call-template name="task-parameters"/>
		
		<table class="lenya-table" border="0" cellpadding="0"
			cellspacing="0">
			<tr>
				<th>Day</th>
				<th>Time</th>
				<th>&#160;</th>
			</tr>
			<tr>
				<!-- hidden input fields for parameters -->
				<td>
					<select name="trigger.startDay">
						<xsl:call-template name="generateSelectionNames">
							<xsl:with-param name="currentValue" select="1"/>
							<xsl:with-param name="selectedValue" select="$scheduler-data/sch:day"/>
							<xsl:with-param name="maxValue" select="31"/>
						</xsl:call-template>
					</select>&#160;
					<select name="trigger.startMonth">
						<xsl:call-template name="generateSelectionNames">
							<xsl:with-param name="currentValue" select="1"/>
							<xsl:with-param name="selectedValue" select="$scheduler-data/sch:month"/>
							<xsl:with-param name="maxValue" select="12"/>
						</xsl:call-template>
					</select>&#160;
					<select name="trigger.startYear">
						<xsl:variable name="year">
							<xsl:value-of select="$scheduler-data/sch:year"/>
						</xsl:variable>
						<xsl:call-template name="generateSelectionNames">
							<xsl:with-param name="currentValue" select="$year"/>
							<xsl:with-param name="selectedValue" select="$year"/>
							<xsl:with-param name="maxValue" select="$year + 2"/>
						</xsl:call-template>
					</select>
				</td>
				<td>
					<input name="trigger.startHour" type="text" size="2" maxlength="2">
					  <xsl:attribute name="value"><xsl:value-of select="format-number($scheduler-data/sch:hour, '00')"/></xsl:attribute>
					</input>
					:
					<input name="trigger.startMin" type="text" size="2" maxlength="2"> 
					  <xsl:attribute name="value"><xsl:value-of select="format-number($scheduler-data/sch:minute, '00')"/></xsl:attribute>
					</input>
				</td>
				<td>
					<input type="submit" name="action" value="add"/>
				</td>
			</tr>
		</table>
	</form>
</xsl:template>
  
  
  <!-- override this template to insert use-case specific task parameters. -->
  <xsl:template name="task-parameters"/>
  
<!-- ============================================================= -->
<!--   Generate numbers from 1 to maxValue for a <select> and select a -->
<!--   given value -->
<!-- ============================================================= -->
<xsl:template name="generateSelectionNames">
	<xsl:param name="currentValue"/>
	<xsl:param name="selectedValue"/>
	<xsl:param name="maxValue"/>
	<xsl:choose>
		<xsl:when test="$currentValue = $selectedValue">
			<option>
				<xsl:attribute name="selected"/>
				<xsl:value-of select="$currentValue"/>
			</option>
		</xsl:when>
		<xsl:otherwise>
			<option>
				<xsl:value-of select="$currentValue"/>
			</option>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:if test="$currentValue &lt; $maxValue">
		<xsl:call-template name="generateSelectionNames">
			<xsl:with-param name="currentValue" select="$currentValue + 1"/>
			<xsl:with-param name="selectedValue" select="$selectedValue"/>
			<xsl:with-param name="maxValue" select="$maxValue"/>
		</xsl:call-template>
	</xsl:if>
</xsl:template>

</xsl:stylesheet> 
