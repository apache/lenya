<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : scheduler-page.xsl
    Created on : 12. Mai 2003, 17:26
    Author     : andreas
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
    >

<xsl:import href="common.xsl"/>    
    
<xsl:param name="document-uri"/>
<xsl:param name="area"/>
<xsl:param name="context-prefix"/>
<xsl:param name="publication-id"/>

<xsl:variable name="uri-prefix" select="concat($context-prefix, '/', $publication-id)"/>
<xsl:variable name="uri" select="concat($uri-prefix, '/', $area, '/', $document-uri)"/>


<!-- navigation menu -->
<xsl:template name="navigation-menu">
  <div class="menu">
    <xsl:variable name="menu-separator" select="'&#160;&#160;|&#160;&#160;'"/>
    <a class="menu-item" href="{$uri-prefix}/authoring/{$document-uri}">Back to page</a>
    <xsl:value-of select="$menu-separator"/>
    <a class="menu-item">
      <xsl:attribute name="href">
        <xsl:text/><xsl:value-of select="$uri"/>?<xsl:call-template name="parameters-as-request-parameters"/>
        <xsl:text/>
      </xsl:attribute>Refresh</a>
    
    <xsl:value-of select="$menu-separator"/>
    <a class="menu-item" href="{$context-prefix}/scheduler-servlet?lenya.usecase=schedule-page&amp;lenya.step=showscreen">Servlet</a>
  </div>
</xsl:template>

  
<xsl:template match="/sch:scheduler">
  <page:page>
    <page:title>Scheduler</page:title>
    
    <page:body>
      <xsl:call-template name="navigation-menu"/>
      <xsl:apply-templates select="sch:exception"/>
    
      <ul>
        <li><strong>Publication:</strong>&#160;&#160;<xsl:value-of select="$publication-id"/></li>
        <li><strong>Document:</strong>&#160;&#160;<xsl:value-of select="$document-uri"/></li>
      </ul>
    
      <table class="scheduler-job" border="0" cellpadding="0" cellspacing="0">

        <tr>
          <th><strong>Edit existing jobs</strong></th>
          <th>Task</th>
          <th>Day</th>
          <th>Time</th>
          <th>&#160;</th>
          <th>&#160;</th>
        </tr>

        <xsl:if test="not(sch:publication/sch:jobs/sch:job)">
        <tr><td colspan="6">No active jobs.</td></tr>
        </xsl:if>
        
        <xsl:apply-templates select="sch:publication/sch:jobs/sch:job"/>

      </table>
    </page:body>
    
  </page:page>
  
</xsl:template>


<xsl:template match="sch:job">
  <tr>
    <form method="GET">

      <td>
        &#160;
        <!-- hidden input fields for parameters -->
        <input type="hidden" name="lenya.usecase" value="schedule"/>
        <input type="hidden" name="lenya.step" value="showscreen"/>
        
        <xsl:apply-templates select="sch:parameter"/>
      </td>
      <td>
        <xsl:call-template name="tasks">
          <xsl:with-param name="current-task-id"
              select="sch:task/sch:parameter[@name='id']/@value"/>
        </xsl:call-template>
      </td>
      <xsl:choose>
        <xsl:when test="sch:trigger">
          <xsl:apply-templates select="sch:trigger"/>
          <td>
            <input type="submit" name="Action" value="Modify"/>
          </td>
        </xsl:when>
        <xsl:otherwise>
          <td colspan="2">
            <p>The job date has expired.</p>
          </td>
          <td>&#160;</td>
        </xsl:otherwise>
      </xsl:choose>
      <td>
        <input type="submit" name="Action" value="Delete"/>
      </td>
    </form>
  </tr>
</xsl:template>


  <!-- ============================================================= -->
  <!-- create new request parameters for all request parameters -->
  <!-- ============================================================= -->
  <xsl:template name="parameters-as-request-parameters">
    <xsl:text>lenya.usecase=schedule&amp;lenya.step=showscreen&amp;</xsl:text>
    
    <xsl:for-each select="/sch:scheduler/sch:parameters/sch:parameter">
      <xsl:if test="not(starts-with(@name, 'job.'))">
      <xsl:if test="not(starts-with(@name, 'trigger.'))">
      <xsl:if test="not(starts-with(@name, 'task.id'))">
      <xsl:if test="not(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'action')">
        <xsl:if test="position() &gt; 1">
          <xsl:text>&amp;</xsl:text>
        </xsl:if>
        <xsl:value-of select="concat(@name, '=', @value)"/>
        <xsl:text/>
      </xsl:if>
      </xsl:if>
      </xsl:if>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template match="sch:trigger">
    <td>
      <font size="2"> 
        <xsl:apply-templates select="sch:parameter[@name='day']"/>
        <xsl:apply-templates select="sch:parameter[@name='month']"/>
        <xsl:apply-templates select="sch:parameter[@name='year']"/>
      </font>
    </td>
    <td>
      <font size="2"> 
        <xsl:apply-templates select="sch:parameter[@name='hour']"/>
        : 
        <xsl:apply-templates select="sch:parameter[@name='minute']"/>
      </font>
    </td>
  </xsl:template>
  
  <xsl:template match="sch:parameter[@name='day']">
    <select name="trigger.startDay">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="1"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="31"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='month']">
    <select name="trigger.startMonth">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="1"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="12"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='year']">
    <select name="trigger.startYear">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="@value"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="@value + 2"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='hour']">
      <input type="text" name="trigger.startHour" size="2" maxlength="2">
      <xsl:attribute name="value">
	<xsl:value-of select="format-number(@value, '00')"/>
      </xsl:attribute>
    </input>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='minute']">
    <input type="text" name="trigger.startMin" size="2" maxlength="2">
      <xsl:attribute name="value">
	<xsl:value-of select="format-number(@value, '00')"/>
      </xsl:attribute>
    </input>
  </xsl:template>

  <!-- job id -->  
  <xsl:template match="sch:parameter[@name='id']">
    <input type="hidden" name="job.id" value="{@value}"/>
  </xsl:template>

<xsl:template match="sch:exception">
<font color="red">EXCEPTION: <xsl:value-of select="@type"/></font> (check the log files)
</xsl:template>

  <!-- ============================================================= -->
  <!-- Create ComboBox entries for all available tasks -->
  <!-- ============================================================= -->
  <xsl:template name="tasks">
    <xsl:param name="current-task-id"/>
      <select name="task.id">
      <!--
        <xsl:attribute name="selected">
          <xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = $current-task-id]/@label"/>
        </xsl:attribute>
        -->
        <xsl:for-each select="/sch:scheduler/sch:tasks/sch:task">
          <option value="{@id}">
            <xsl:if test="@id = $current-task-id">
              <xsl:attribute name="selected"/>
            </xsl:if>
            <xsl:value-of select="label"/>
          </option>
        </xsl:for-each>
      </select>
  </xsl:template>

</xsl:stylesheet> 
