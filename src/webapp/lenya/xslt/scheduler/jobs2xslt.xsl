<?xml version="1.0" encoding="UTF-8" ?>

<!--
  $Id: jobs2xslt.xsl,v 1.4 2004/02/25 20:25:18 roku Exp $
  Created on : 12. Mai 2003, 17:26
  
  Description:       
  
    Creates a job list for a document with no modification functionality.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:xso="http://apache.org/cocoon/lenya/xslt/1.0"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
    xmlns:task="http://apache.org/cocoon/lenya/task/1.0"
    >

<xsl:import href="common.xsl"/>
    
<xsl:namespace-alias stylesheet-prefix="xso" result-prefix="xsl"/>

<xsl:param name="publicationid"/>
<xsl:param name="documenturl"/>
<xsl:param name="contextpath"/>
<xsl:param name="referer"/>
<xsl:param name="initialreferer"/>


<xsl:template match="/">
  
  <xsl:variable name="scheduler-url"
      select="concat($contextpath, '/', $publicationid, '/info-authoring', $documenturl)"/>
  
  <xso:stylesheet exclude-result-prefixes="sch xso">
  	
    <xso:template match="sch:scheduler-form">
      
			<div class="lenya-box">
				<div class="lenya-box-title"><i18n:text>Scheduler</i18n:text>&#160;
					<a href="{$scheduler-url}?lenya.usecase=info-scheduler&amp;lenya.step=showscreen">[<i18n:text>Administration</i18n:text>]</a>
				</div>
				<div class="lenya-box-body">
          <table border="0" cellpadding="2" cellspacing="0">
            <xsl:apply-templates select="sch:scheduler/sch:job-group"/>
            <xso:apply-templates select="sch:job"/>
          </table>
				</div>
			</div>
    </xso:template>
    
    <xso:template match="sch:job">
      <xsl:variable name="new-referer">
        <xsl:choose>
          <xsl:when test="$referer != ''">
            <xsl:value-of select="$referer"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$initialreferer"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      
      <form>
        <xso:copy-of select="node()"/>
        <input type="hidden" name="referer" value="{$new-referer}"/>
        <xsl:call-template name="scheduler-form"/>
      </form>
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
        <tr><td colspan="6"><i18n:text>No active jobs</i18n:text></td></tr>
      </xsl:otherwise>
    </xsl:choose>
    
</xsl:template>


<xsl:template match="sch:job">
  <tr class="lenya-scheduler-existing-job">
  	<td>
  		<xsl:if test="position() = 1"><strong><i18n:text>Active jobs</i18n:text>:</strong>&#160;&#160;&#160;</xsl:if>
  	</td>
		<td>
			<xsl:variable name="current-task-id" select="task:task/task:parameter[@name='wrapper.task-id']/@value"/>
			<i18n:text><xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = $current-task-id]/sch:label"/></i18n:text>&#160;&#160;&#160;
		</td>
		<xsl:choose>
			<xsl:when test="sch:trigger">
				<xsl:apply-templates select="sch:trigger"/>
			</xsl:when>
			<xsl:otherwise>
				<td colspan="4" style="white-space: nowrap"><i18n:text>The job date has expired</i18n:text></td>
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
