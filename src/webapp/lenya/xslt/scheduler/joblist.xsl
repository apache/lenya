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
    <form method="GET">
    	
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
    </form>
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
