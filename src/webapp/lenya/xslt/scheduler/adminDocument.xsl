<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://www.wyona.org/2002/sch" version="1.0">
  
  <xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:param name="task.sources"/>
  <xsl:param name="task.uris"/>
  <xsl:param name="documentUri"/>
  <xsl:param name="documentType"/>

  <!-- FIXME -->
  <xsl:variable name="context_prefix">/wyona-cms/<xsl:value-of select="/sch:scheduler/sch:current-date/sch:publication-id"/></xsl:variable>
  
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

  <!--   Generate the necessary form to scheduler new jobs -->
  <xsl:template name="schedulerForm">
    <tr bgcolor="#EEEEEE">
      <td>
	<font size="2" face="Verdana, Arial, Helvetica, sans-serif">
	  Add new job
	</font>
      </td>
      <form method="POST">
	<xsl:attribute name="action">
	  <xsl:value-of select="$context_prefix"/><xsl:text>/scheduler/docid/</xsl:text><xsl:value-of select="$documentUri"/>
	</xsl:attribute>
        
        <!-- hidden input fields for parameters -->
	<input type="hidden" name="documentUri" value="{$documentUri}"/>
	<input type="hidden" name="documentType" value="{$documentType}"/>
	<input type="hidden" name="task.sources" value="{$task.sources}"/>
	<input type="hidden" name="task.uris" value="{$task.uris}"/>
        
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
  
  <xsl:template match="sch:scheduler">
    <html>
      <head>
	<title>Scheduler</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
      </head>
      
      <body>


<!--
<p>
      DEBUG: Current Time: <xsl:value-of select="/sch:scheduler/sch:current-date/sch:day"/><br />
      DEBUG: Current Time: <xsl:value-of select="/sch:scheduler/sch:current-date/sch:second"/>
</p>
-->
<!--      
        Parameters:
        <ul>
          <li>sources: <xsl:value-of select="$task.sources"/></li>
          <li>uris: <xsl:value-of select="$task.uris"/></li>
          <li>documentUri: <xsl:value-of select="$documentUri"/></li>
        </ul>
-->
        <xsl:apply-templates select="sch:exception"/>
	<font face="Verdana, Arial, Helvetica, sans-serif" size="2"> 
	  <h1>Scheduler</h1>
	  <h3>Schedule tasks for this page/document:
            <a href="{$context_prefix}/authoring/{$documentUri}">
              <xsl:value-of select="$documentUri"/>
            </a>
          </h3>
	  <table width="100%" height="3" border="0" cellpadding="2" cellspacing="0">
	    <tr> 
	      <td bgcolor="#EEEEEE">
<!-- 		<strong> -->
<!-- 		  <font size="2" face="Verdana, Arial, Helvetica, sans-serif"> -->
<!-- 		    Page/Document -->
<!-- 		  </font> -->
<!-- 		</strong> -->
	      </td>
	      <td bgcolor="#EEEEEE">
		<strong>
		  <font size="2" face="Verdana, Arial, Helvetica, sans-serif">
		    Job
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
	      <td bgcolor="#EEEEEE">&#160;</td>
	    </tr>
	    <xsl:call-template name="schedulerForm"/>
	    <xsl:for-each select="sch:publication">
	      <xsl:for-each select="sch:jobs">
		<xsl:for-each select="sch:job">
		  <tr>
		    <form method="POST">
		      <xsl:attribute name="action">
			<xsl:value-of select="$context_prefix"/><xsl:text>/scheduler/docid/</xsl:text><xsl:value-of select="$documentUri"/>
		      </xsl:attribute>
                      <!-- hidden input fields for parameters -->
                      <input type="hidden" name="documentUri" value="{$documentUri}"/>
                      <input type="hidden" name="documentType" value="{$documentType}"/>
                      <input type="hidden" name="task.sources" value="{$task.sources}"/>
                      <input type="hidden" name="task.uris" value="{$task.uris}"/>
		      <td bgcolor="#CCCCCC">
			<xsl:if test="position()=1">
			  <font face="Verdana, Arial, Helvetica, sans-serif" size="2"> 
			    Edit existing job
			  </font>
			</xsl:if>
			<xsl:apply-templates select="sch:parameter"/>
		      </td>
		      <td bgcolor="#CCCCCC">
                        <xsl:call-template name="tasks">
                          <xsl:with-param name="current-task-id"
                              select="sch:task/sch:parameter[@name='id']/@value"/>
                        </xsl:call-template>
		      </td>
                      <xsl:choose>
                        <xsl:when test="sch:trigger">
                          <xsl:apply-templates select="sch:trigger"/>
                          <td bgcolor="#CCCCCC">
                            <input type="submit" name="Action" value="Modify"/>
                          </td>
                        </xsl:when>
                        <xsl:otherwise>
                          <td colspan="2" bgcolor="#CCCCCC">
                            <p>The job date has expired.</p>
                          </td>
                          <td bgcolor="#CCCCCC">&#160;</td>
                        </xsl:otherwise>
                      </xsl:choose>
                      <td bgcolor="#CCCCCC">
                        <input type="submit" name="Action" value="Delete"/>
                      </td>
		    </form>
		  </tr>
		</xsl:for-each>
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

  <!-- document URI -->
  <!--
  <xsl:template match="sch:parameter[@name='documentUri']">
    <input type="hidden" name="documentUri" value="{@value}"/>
  </xsl:template>
  -->

  <!-- job id -->  
  <xsl:template match="sch:parameter[@name='id']">
    <input type="hidden" name="job.id" value="{@value}"/>
  </xsl:template>

<!--   <xsl:template match="sch:parameter"> -->
<!--     default -->
<!--     <br /><xsl:apply-templates select="@name"/>:&#160;<xsl:apply-templates select="@value"/> -->
<!--   </xsl:template> -->

<xsl:template match="sch:exception">
<font color="red">EXCEPTION: <xsl:value-of select="@type"/></font> (check the log files)
</xsl:template>

</xsl:stylesheet>  
