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
<!--   Generate the form to schedule new jobs -->
<!-- ============================================================= -->
<xsl:template name="scheduler-form">
	<xsl:param name="form-name"/>
	<xsl:variable name="scheduler-data" select="//sch:scheduler-data"/>
	
		<xsl:for-each select="$scheduler-data/sch:parameter">
			<input type="hidden" name="{@name}" value="{@value}"/>
		</xsl:for-each>
		
		<input type="hidden" name="scheduler.action" value="add"/>
		
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
					<select name="scheduler.trigger.day">
						<xsl:call-template name="generateSelectionNames">
							<xsl:with-param name="currentValue" select="1"/>
							<xsl:with-param name="selectedValue" select="$scheduler-data/sch:day"/>
							<xsl:with-param name="maxValue" select="31"/>
						</xsl:call-template>
					</select>&#160;
					<select name="scheduler.trigger.month">
						<xsl:call-template name="generateSelectionNames">
							<xsl:with-param name="currentValue" select="1"/>
							<xsl:with-param name="selectedValue" select="$scheduler-data/sch:month"/>
							<xsl:with-param name="maxValue" select="12"/>
						</xsl:call-template>
					</select>&#160;
					<select name="scheduler.trigger.year">
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
					<input name="scheduler.trigger.hour" type="text" size="2" maxlength="2">
					  <xsl:attribute name="value"><xsl:value-of select="format-number($scheduler-data/sch:hour, '00')"/></xsl:attribute>
					</input>
					:
					<input name="scheduler.trigger.minute" type="text" size="2" maxlength="2"> 
					  <xsl:attribute name="value"><xsl:value-of select="format-number($scheduler-data/sch:minute, '00')"/></xsl:attribute>
					</input>
				</td>
				<td>
					<input type="submit" name="lenya.usecase" value="schedule"/>
				</td>
			</tr>
		</table>
</xsl:template>
  
  
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
			<option><xsl:value-of select="$currentValue"/></option>
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
