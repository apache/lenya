<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : util.xsl
    Created on : November 21, 2002, 5:30 PM
    Author     : ah
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:sch="http://www.wyona.org/2002/sch"
    >

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

  <!-- ============================================================= -->
  <!-- create hidden inputs for all request parameters -->
  <!-- ============================================================= -->
  <xsl:template name="parameters-as-inputs">
<!--    <ul>-->
    <xsl:for-each select="/sch:scheduler/sch:parameters/sch:parameter">
      <xsl:if test="not(starts-with(@name, 'job.'))">
      <xsl:if test="not(starts-with(@name, 'trigger.'))">
      <xsl:if test="not(starts-with(@name, 'task.id'))">
      <xsl:if test="not(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'action')">
        <input type="hidden" name="{@name}" value="{@value}"/>
<!--        <li>Parameter: <xsl:value-of select="@name"/> = <xsl:value-of select="@value"/></li>-->
      </xsl:if>
      </xsl:if>
      </xsl:if>
      </xsl:if>
    </xsl:for-each>
<!--    </ul>-->
  </xsl:template>
  
  <!-- ============================================================= -->
  <!-- create new request parameters for all request parameters -->
  <!-- ============================================================= -->
  <xsl:template name="parameters-as-request-parameters">
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
  
  <!-- ============================================================= -->
  <!--   Generate the necessary form to schedule new jobs -->
  <!-- ============================================================= -->
  <xsl:template name="schedulerForm">
    <tr>
      <form method="POST">
        
        <!-- hidden input fields for parameters -->
        <xsl:call-template name="parameters-as-inputs"/>
        <td />
        
        <!-- task selection combobox -->
	<td><xsl:call-template name="tasks"/></td>
        
	<td>
	  <font size="2">
	    <select name="trigger.startDay">
	      <xsl:call-template name="generateSelectionNames">
		<xsl:with-param name="currentValue" select="1"/>
		<xsl:with-param name="selectedValue" select="/sch:scheduler/sch:current-date/sch:day"/>
		<xsl:with-param name="maxValue" select="31"/>
	      </xsl:call-template>
	    </select>
	    <select name="trigger.startMonth">
	      <xsl:call-template name="generateSelectionNames">
		<xsl:with-param name="currentValue" select="1"/>
		<xsl:with-param name="selectedValue"
                    select="/sch:scheduler/sch:current-date/sch:month"/>
		<xsl:with-param name="maxValue" select="12"/>
	      </xsl:call-template>
	    </select>
	    <select name="trigger.startYear">
	      <xsl:call-template name="generateSelectionNames">
		<xsl:with-param name="currentValue" select="2002"/>
		<xsl:with-param name="selectedValue"
                    select="/sch:scheduler/sch:current-date/sch:year"/>
		<xsl:with-param name="maxValue" select="@value + 2"/>
	      </xsl:call-template>
	    </select>
	  </font>
	</td>
	<td>
	  <font size="2">
	    <input name="trigger.startHour" type="text" size="2" maxlength="2">
              <xsl:attribute name="value">
                <xsl:value-of select="format-number(/sch:scheduler/sch:current-date/sch:hour, '00')"/>
              </xsl:attribute>
            </input>
	    :
	    <input name="trigger.startMin" type="text" size="2" maxlength="2">
              <xsl:attribute name="value">
                <xsl:value-of select="format-number(/sch:scheduler/sch:current-date/sch:minute, '00')"/>
              </xsl:attribute>
            </input>
	  </font>
	</td>
        <td>&#160;</td>
	<td>
	  <input type="submit" name="Action" value="Add"/>
	</td>
      </form>
    </tr>
  </xsl:template>
  
  <xsl:template name="table-separator-space">
    <xsl:param name="background" select="'White'"/>
    <tr>
      <td colspan="6" bgcolor="{$background}">
        <img src="{$uri-prefix}/images/util/pixel.gif" width="1" height="5"/>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template name="table-separator">
    <xsl:call-template name="table-separator-space"/>
    <tr height="1">
<!--      <td />-->
      <td class="table-separator" colspan="6">
        <img src="{$uri-prefix}/images/util/pixel.gif"/>
      </td>
    </tr>
    <xsl:call-template name="table-separator-space"/>
  </xsl:template>
  
</xsl:stylesheet> 
