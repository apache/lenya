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
    xmlns:sch="http://www.lenya.org/2002/sch"
    >

  <!-- ============================================================= -->
  <!--   Generate the necessary form to schedule new jobs -->
  <!-- ============================================================= -->
  <xsl:template name="scheduler-form">
  
    <xsl:variable name="date" select="//sch:current-date"/>
  
    <input type="hidden" name="lenya.usecase" value="schedule"/>
    <input type="hidden" name="lenya.step" value="showscreen"/>
    
    <table class="scheduler-job" border="0" cellpadding="0" cellspacing="0">

      <tr> 
        <th><strong>Add new job</strong></th>
        <th>Task</th>
        <th>Day</th>
        <th>Time</th>
        <th>&#160;</th>
        <th>&#160;</th>
      </tr>
      <tr>
      <form method="GET">
        
        <!-- hidden input fields for parameters -->
        
        <td>
          &#160;
        </td>
        
	<td></td>
        
	<td>
	  <font size="2">
	    <select name="trigger.startDay">
	      <xsl:call-template name="generateSelectionNames">
		<xsl:with-param name="currentValue" select="1"/>
		<xsl:with-param name="selectedValue" select="$date/sch:day"/>
		<xsl:with-param name="maxValue" select="31"/>
	      </xsl:call-template>
	    </select>
	    <select name="trigger.startMonth">
	      <xsl:call-template name="generateSelectionNames">
		<xsl:with-param name="currentValue" select="1"/>
		<xsl:with-param name="selectedValue"
                    select="$date/sch:month"/>
		<xsl:with-param name="maxValue" select="12"/>
	      </xsl:call-template>
	    </select>
	    <select name="trigger.startYear">
              <xsl:variable name="year"><xsl:value-of select="$date/sch:year"/></xsl:variable>
              <xsl:call-template name="generateSelectionNames">
                <xsl:with-param name="currentValue" select="$year"/>
                <xsl:with-param name="selectedValue" select="$year"/>
                <xsl:with-param name="maxValue" select="$year + 2"/>
              </xsl:call-template>
	    </select>
	  </font>
	</td>
	<td>
	  <font size="2">
	    <input name="trigger.startHour" type="text" size="2" maxlength="2">
              <xsl:attribute name="value">
                <xsl:value-of select="format-number($date/sch:hour, '00')"/>
              </xsl:attribute>
            </input>
	    :
	    <input name="trigger.startMin" type="text" size="2" maxlength="2">
              <xsl:attribute name="value">
                <xsl:value-of select="format-number($date/sch:minute, '00')"/>
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
