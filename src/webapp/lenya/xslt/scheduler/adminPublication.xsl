<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0" version="1.0">
  
  <xsl:param name="documentID"/>

  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <!--   Locale templates -->

  <!--   Generate numbers from 1 to maxValue for a <select> and select a -->
  <!--   given value -->
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

  <xsl:template match="sch:scheduler">
    <html>
      <head>
	<title>Scheduler</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
      </head>
      
      <body>
	<font face="Verdana, Arial, Helvetica, sans-serif" size="2"> 
	  <h1>Scheduler</h1>
	  <h3>Scheduled tasks for this publication</h3>
	  <table width="100%" height="3" border="0" cellpadding="2" cellspacing="0">
	    <tr> 
	      <td bgcolor="#EEEEEE">
		<strong>
		  <font size="2" face="Verdana, Arial, Helvetica, sans-serif">
		    Page/Document
		  </font>
		</strong>
	      </td>
	      <td bgcolor="#EEEEEE">
		<strong>
		  <font size="2" face="Verdana, Arial, Helvetica, sans-serif">
		    Task
		  </font>
		</strong>
	      </td>
	      <td bgcolor="#EEEEEE">
		<strong>
		  <font size="2" face="Verdana, Arial, Helvetica, sans-serif">
		    Day
		  </font>
		</strong>
	      </td>
	      <td bgcolor="#EEEEEE">
		<strong>
		  <font size="2" face="Verdana, Arial, Helvetica, sans-serif">
		    Time
		  </font>
		</strong>
	      </td>
	      <td bgcolor="#EEEEEE">&#160;</td>
	    </tr>
	    <xsl:for-each select="sch:publication">
	      <xsl:for-each select="sch:tasks/sch:task">
		<tr>
		  <form method="POST">
		    <xsl:attribute name="action">
		      <xsl:text>/lenya/scratchpad/admin/scheduler/publicationid/</xsl:text>
		      <xsl:value-of select="$documentID"/>
		    </xsl:attribute>
		    <td bgcolor="#CCCCCC">
		      <xsl:apply-templates select="sch:parameter"/>
		    </td>
		    <td bgcolor="#CCCCCC">
		      <select name="scheduleJobName">
			<option>
			  <xsl:attribute name="selected"/>
			  <xsl:apply-templates select="@action-type"/>
			</option>
			<option>Archive Page</option>
			<option>Publish Page</option>
		      </select>
		    </td>
		    <xsl:apply-templates select="sch:trigger"/>
		  </form>
		</tr>
	      </xsl:for-each>
	    </xsl:for-each>
	  </table>
	</font>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="sch:trigger">
    <td bgcolor="#CCCCCC">
      <font size="2"> 
	<xsl:apply-templates select="sch:parameter[@name='day']"/>
	<xsl:apply-templates select="sch:parameter[@name='month']"/>
	<xsl:apply-templates select="sch:parameter[@name='year']"/>
      </font>
    </td>
    <td bgcolor="#CCCCCC">
      <font size="2"> 
	<xsl:apply-templates select="sch:parameter[@name='hour']"/>
	: 
	<xsl:apply-templates select="sch:parameter[@name='minute']"/>
      </font>
    </td>
    <td bgcolor="#CCCCCC">
      <input type="submit" name="Action" value="Modify"/>
      <input type="submit" name="Action" value="Delete"/>
    </td>
  </xsl:template>
  
  <xsl:template match="sch:parameter[@name='day']">
    <select name="startDay">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="1"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="31"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='month']">
    <select name="startMonth">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="1"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="12"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='year']">
    <select name="startYear">
      <xsl:call-template name="generateSelectionNames">
	<xsl:with-param name="currentValue" select="@value"/> 
	<xsl:with-param name="selectedValue" select="@value"/>
	<xsl:with-param name="maxValue" select="@value + 2"/>
      </xsl:call-template>      
    </select>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='hour']">
      <input type="text" name="startHour" size="2" maxlength="2">
      <xsl:attribute name="value">
	<xsl:apply-templates select="@value"/>
      </xsl:attribute>
    </input>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='minute']">
    <input type="text" name="startMin" size="2" maxlength="2">
      <xsl:attribute name="value">
	<xsl:apply-templates select="@value"/>
      </xsl:attribute>
    </input>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='docid']">
    <font face="Verdana, Arial, Helvetica, sans-serif" size="2"> 
      <xsl:apply-templates select="@value"/>
    </font>
    <input type="hidden" name="documentID">
      <xsl:attribute name="value">
	<xsl:apply-templates select="@value"/>
      </xsl:attribute>
    </input>
  </xsl:template>

  <xsl:template match="sch:parameter[@name='jobid']">
    <input type="hidden" name="jobID">
      <xsl:attribute name="value">
	<xsl:apply-templates select="@value"/>
      </xsl:attribute>
    </input>
  </xsl:template>

</xsl:stylesheet>  
