<?xml version="1.0" encoding="UTF-8" ?>

<!--
    $Id: scheduler.xsl,v 1.2 2004/02/23 13:04:54 roku Exp $

    Document   : scheduler-page.xsl
    Created on : 12. Mai 2003, 17:26
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
    xmlns:task="http://apache.org/cocoon/lenya/task/1.0"
    xmlns:lenya-info="http://apache.org/cocoon/lenya/info/1.0"
    >

<xsl:import href="../scheduler/common.xsl"/>
    
<xsl:param name="document-url"/>
<xsl:param name="document-id"/>
<xsl:param name="area"/>
<xsl:param name="publication-id"/>
<xsl:param name="context-prefix"/>


<xsl:template match="/sch:scheduler">
	<lenya-info:info>
    <lenya-info:scheduler>
	    <xsl:apply-templates select="sch:exception"/>
      <xsl:apply-templates select="sch:job-group"/>
    </lenya-info:scheduler>
  </lenya-info:info>
</xsl:template>


<xsl:template match="sch:job-group">
    
  <table class="lenya-table">

    <tr>
      <th><i18n:text>Task</i18n:text></th>
      <th><i18n:text>Day</i18n:text></th>
      <th colspan="3"><i18n:text>Time</i18n:text></th>
    </tr>

    <xsl:choose>
      <xsl:when test="sch:job">
        <xsl:apply-templates select="sch:job"/>
      </xsl:when>
      <xsl:otherwise>
        <tr><td colspan="6"><i18n:text>No active jobs</i18n:text></td></tr>
      </xsl:otherwise>
    </xsl:choose>

  </table>
</xsl:template>


<xsl:template match="sch:job">
	<xsl:if test="$document-id = '/'">
    <xsl:variable name="job-document-url" select="substring-after(@url, concat('/', ../@name, '/'))"/>
    <xsl:variable name="link-url" select="concat($context-prefix, '/', ../@name, '/info-', $job-document-url)"/>
		<tr>
			<td colspan="5"><strong><i18n:text>Document</i18n:text>:&#160;</strong>
				<a href="{$link-url}"><xsl:value-of select="$job-document-url"/></a>
			</td>
		</tr>
	</xsl:if>
      
  <tr>
    <form method="GET">
    	
			<td>
        <!-- hidden input fields for parameters -->
        <input type="hidden" name="lenya.usecase" value="info-scheduler"/>
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
          <td colspan="2"><i18n:text>The job date has expired</i18n:text></td>
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
    	<span style="white-space: nobreak">
			<xsl:apply-templates select="sch:parameter[@name='year']"/> -
			<xsl:apply-templates select="sch:parameter[@name='month']"/> -
			<xsl:apply-templates select="sch:parameter[@name='day']"/>
			</span>
    </td>
    <td>
			<xsl:apply-templates select="sch:parameter[@name='hour']"/>
			<xsl:text>&#160;:&#160;</xsl:text>
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
<span style="color: red"><i18n:text>EXCEPTION</i18n:text>: <xsl:value-of select="@type"/></span> (<i18n:text>check the log files</i18n:text>)
</xsl:template>


</xsl:stylesheet> 
