<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://www.wyona.org/2002/sch" version="1.0">
  
  <xsl:import href="../util/page-util.xsl"/>

  <xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:param name="task.sources"/>
  <xsl:param name="task.uris"/>
  <xsl:param name="documentUri"/>
  <xsl:param name="documentType"/>

  <xsl:variable name="separator" select="','"/>

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
    <tr>
      <form method="POST">
        <td />
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
  
  <!-- navigation menu -->
  <xsl:template name="navigation-menu">
    <div class="menu">
        <xsl:variable name="menu-separator" select="'&#160;&#160;|&#160;&#160;'"/>
        <a href="{$context_prefix}/authoring/{$documentUri}">
          <strong>Back to page</strong>
        </a>
        <xsl:value-of select="$menu-separator"/>
        <a href="{$context_prefix}/scheduler/docid/{$documentUri}?task.sources={$task.sources}&amp;task.uris={$task.uris}&amp;documentType={$documentType}&amp;documentUri={$documentUri}">
          <strong>Refresh</strong>
        </a>
    </div>
  </xsl:template>
  
  <xsl:template match="sch:scheduler">
    <html>
      <head>
	<title>Scheduler</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
        <xsl:call-template name="include-css">
          <xsl:with-param name="context-prefix" select="$context_prefix"/>
        </xsl:call-template>
      </head>
      
      <body>
<!--      <body marginwidth="20" marginheight="20" topmargin="20" leftmargin="20">-->
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
        <h1>Scheduler</h1>
        <xsl:call-template name="navigation-menu"/>
        <table border="0" cellpadding="5" cellspacing="0">
          <tr>
            <td>
        <table border="0" cellpadding="3" cellspacing="0">
          <tr>
            <td>
                <strong>Document<xsl:text/>
                <xsl:if test="contains($task.sources, $separator)">s</xsl:if>:&#160;
                </strong>
                <xsl:text/>
            </td>
            <td>
                <ul>
                  <xsl:call-template name="print-list">
                    <xsl:with-param name="list-string" select="$task.sources"/>
                  </xsl:call-template>
                </ul>
            </td>
          </tr>
          <tr>
            <td>
                <strong>Page<xsl:text/>
                <xsl:if test="contains($task.uris, $separator)">s</xsl:if>:</strong>
                <xsl:text/>
             </td>
             <td>
                <ul>
                  <xsl:call-template name="print-list">
                    <xsl:with-param name="list-string" select="$task.uris"/>
                  </xsl:call-template>
                </ul>
             </td>
           </tr>
         </table>
             </td>
           </tr>
         </table>
         <br />
	  <table width="100%" height="3" border="0" cellpadding="0" cellspacing="0">
	    <tr> 
              <td class="table-head"><div class="menu">Add new job</div></td>
	      <td class="table-head">Task</td>
	      <td class="table-head">Day</td>
	      <td class="table-head">Time</td>
	      <td class="table-head">&#160;</td>
	      <td class="table-head">&#160;</td>
	    </tr>
            <xsl:call-template name="table-separator-space"/>
	    <xsl:call-template name="schedulerForm"/>
            <tr>
              <td colspan="6">&#160;</td>
            </tr>
            <tr>
              <td class="table-head">
<!--                    <xsl:if test="position()=1">-->
                    <div class="menu">
                        Edit existing jobs
                    </div>
<!--                    </xsl:if>-->
              </td>
	      <td class="table-head">Task</td>
	      <td class="table-head">Day</td>
	      <td class="table-head">Time</td>
	      <td class="table-head">&#160;</td>
	      <td class="table-head">&#160;</td>
	    </tr>
            <xsl:call-template name="table-separator-space"/>
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
		      <td>
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
                  <xsl:call-template name="table-separator"/>
		</xsl:for-each>
	      </xsl:for-each>
	    </xsl:for-each>
	  </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="table-separator-space">
    <xsl:param name="background" select="'White'"/>
    <tr>
      <td colspan="6" bgcolor="{$background}">
        <img src="{$context_prefix}/images/util/pixel.gif" width="1" height="5"/>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template name="table-separator">
    <xsl:call-template name="table-separator-space"/>
    <tr height="1">
      <td />
      <td class="table-separator" colspan="5">
        <img src="{$context_prefix}/images/util/pixel.gif"/>
      </td>
    </tr>
    <xsl:call-template name="table-separator-space"/>
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
