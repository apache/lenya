<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : scheduler-page.xsl
    Created on : 12. Mai 2003, 17:26
    Author     : andreas
    Description:
       
    Creates a job list for a document with no modification functionality.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
    xmlns:task="http://apache.org/cocoon/lenya/task/1.0"
    >

<xsl:import href="common.xsl"/>
    
<xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>

<xsl:param name="document-url"/>
<xsl:param name="area"/>
<xsl:param name="context-prefix"/>
<xsl:param name="publication-id"/>


<xsl:template match="/">
  <xso:stylesheet exclude-result-prefixes="sch">
  	
    <xso:template match="sch:scheduler-form">
			<div class="lenya-box">
				<div class="lenya-box-title">Scheduler&#160;
					<a href="?lenya.usecase=schedule&amp;lenya.step=showscreen">[Scheduler Admin]</a>
				</div>
				<div class="lenya-box-body">
					<xsl:apply-templates select="sch:scheduler/sch:job-group"/>
				</div>
			</div>
    </xso:template>
    
    <xso:template match="@*|node()">
      <xso:copy><xso:apply-templates select="@*|node()"/></xso:copy>
    </xso:template>
 
  </xso:stylesheet>
</xsl:template>
		

<xsl:template match="/sch:scheduler">
  <xsl:apply-templates/>
</xsl:template>


<xsl:template match="sch:job-group">
  <table border="0" cellpadding="2" cellspacing="0">
  	
  	<!--
  	<tr>
  		<th>Task</th>
  		<th>Year</th>
  		<th>Mth</th>
  		<th>Day</th>
  		<th>Hr</th>
  		<th>Min</th>
  		<th></th>
  	</tr>
  	-->

    <xsl:choose>
      <xsl:when test="sch:job">
        <xsl:apply-templates select="sch:job"/>
      </xsl:when>
      <xsl:otherwise>
        <tr><td colspan="6">No active jobs.</td></tr>
      </xsl:otherwise>
    </xsl:choose>
    
    <xsl:call-template name="scheduler-form"/>

  </table>
</xsl:template>


<xsl:template match="sch:job">
  <tr class="lenya-scheduler-existing-job">
  	<td>
  		<xsl:if test="position() = 1"><strong>Active jobs:</strong>&#160;&#160;&#160;</xsl:if>
  	</td>
		<td>
			<xsl:variable name="current-task-id" select="task:task/task:parameter[@name='wrapper.task-id']/@value"/>
			<xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = $current-task-id]/sch:label"/>&#160;&#160;&#160;
		</td>
		<xsl:choose>
			<xsl:when test="sch:trigger">
				<xsl:apply-templates select="sch:trigger"/>
			</xsl:when>
			<xsl:otherwise>
				<td colspan="2">The job date has expired.</td>
			</xsl:otherwise>
		</xsl:choose>
		<td>&#160;<!-- schedule button in "new job" row --></td>
  </tr>
</xsl:template>


<xsl:template match="sch:trigger">
	<td>
		<xsl:value-of select="sch:parameter[@name='year']/@value"/>
	</td>
	<td>-
		<xsl:value-of select="format-number(sch:parameter[@name='month']/@value, '00')"/>
	</td>
	<td>-
		<xsl:value-of select="format-number(sch:parameter[@name='day']/@value, '00')"/>
	</td>
	<td>
		<xsl:value-of select="format-number(sch:parameter[@name='hour']/@value, '00')"/>
	</td>
	<td>:
		<xsl:value-of select="format-number(sch:parameter[@name='minute']/@value, '00')"/>
	</td>
</xsl:template>
  

</xsl:stylesheet> 
