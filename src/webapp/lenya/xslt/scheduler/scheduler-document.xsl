<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0" version="1.0">
  
  <xsl:import href="../util/page-util.xsl"/>
  <xsl:import href="util.xsl"/>

  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:param name="publication-id" select="''"/>
  <xsl:param name="documentUri" select="''"/>
  <xsl:param name="documentType"/>

  <xsl:variable name="separator" select="','"/>

  <xsl:variable name="context-prefix">
    <xsl:text/>
    <xsl:value-of select="/sch:scheduler/sch:parameters/sch:parameter[@name='context-prefix']/@value"/>
    <xsl:text/>
  </xsl:variable>
  
  <xsl:variable name="publication-id">
    <xsl:text/>
    <xsl:value-of select="/sch:scheduler/sch:parameters/sch:parameter[@name='publication-id']/@value"/>
    <xsl:text/>
  </xsl:variable>
  
  <xsl:variable name="uri-prefix">
    <xsl:text/>
    <xsl:value-of select="concat($context-prefix, '/', $publication-id)"/>
    <xsl:text/>
  </xsl:variable>
  
  <!-- navigation menu -->
  <xsl:template name="navigation-menu">
    <div class="menu">
        <xsl:variable name="menu-separator" select="'&#160;&#160;|&#160;&#160;'"/>
        <a href="{$uri-prefix}/authoring/{$documentUri}">
          <strong>Back to page</strong>
        </a>
        <xsl:value-of select="$menu-separator"/>
        <a>
          <xsl:attribute name="href">
            <xsl:text/>
            <xsl:value-of select="$uri-prefix"/>
            <xsl:text>/scheduler/docid/</xsl:text>
            <xsl:value-of select="$documentUri"/>
            <xsl:text/>
            <xsl:call-template name="parameters-as-request-parameters"/>
            <xsl:text/>
          </xsl:attribute>
        
<!--         href="{$uri-prefix}/scheduler/docid/{$documentUri}?task.sources={$task.sources}&amp;task.uris={$task.uris}&amp;documentType={$documentType}&amp;documentUri={$documentUri}">-->
          <strong>Refresh</strong>
        </a>
    </div>
  </xsl:template>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="sch:scheduler">
    <html>
      <head>
	<title>Scheduler</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
        <xsl:call-template name="include-css">
          <xsl:with-param name="context-prefix" select="$uri-prefix"/>
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
        <p>
          <strong>Publication:</strong>&#160;&#160;<xsl:value-of select="$publication-id"/>
          <br/>
          <strong>Document:</strong>&#160;&#160;<xsl:value-of select="$documentUri"/>
        </p>
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
            
            <xsl:if test="not(sch:publication/sch:jobs/sch:job)">
            <tr><td colspan="6"><br/>No active jobs.</td></tr>
            </xsl:if>

	    <xsl:for-each select="sch:publication">
	      <xsl:for-each select="sch:jobs">
		<xsl:for-each select="sch:job">
		  <tr>
		    <form method="POST">
		      <xsl:attribute name="action">
			<xsl:value-of select="$uri-prefix"/><xsl:text>/scheduler/docid/</xsl:text><xsl:value-of select="$documentUri"/>
		      </xsl:attribute>
                      <!-- hidden input fields for parameters -->
                      <xsl:call-template name="parameters-as-inputs"/>
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

</xsl:stylesheet>  
