<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://www.lenya.org/2002/sch" version="1.0">
  
  <xsl:import href="../util/page-util.xsl"/>
  <xsl:import href="util.xsl"/>

  <xsl:output method="html" version="1.0" indent="yes" encoding="ISO-8859-1"/>

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

        <!-- ================================================================== -->
        <!-- print document URI or publication ID -->
        <!-- ================================================================== -->
        <p>
        <xsl:if test="$publication-id != ''">
          <strong>Publication:</strong>&#160;&#160;<xsl:value-of select="$publication-id"/>
        </xsl:if>
        <xsl:if test="$documentUri != ''">
          <br/>
          <strong>Document:</strong>&#160;&#160;<xsl:value-of select="$documentUri"/>
        </xsl:if>
        </p>
        <br />
        
        
	  <table width="100%" height="3" border="0" cellpadding="0" cellspacing="0">
          
        <!-- ================================================================== -->
        <!-- begin: if document URI is provided -->
        <!-- ================================================================== -->
          <xsl:if test="$documentUri">
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
          </xsl:if>
        <!-- ================================================================== -->
        <!-- end: if document URI is provided -->
        <!-- ================================================================== -->
        
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
                        <xsl:choose>
                          <xsl:when test="$documentUri = ''">
                            <xsl:variable name="task-id" select="sch:task/sch:parameter[@name='id']/@value"/>
                            <xsl:for-each select="/sch:scheduler/sch:tasks/sch:task">
                              <xsl:if test="@id = $task-id/">
                                <strong><xsl:value-of select="label"/></strong>
                              </xsl:if>
                            </xsl:for-each>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:call-template name="tasks">
                              <xsl:with-param name="current-task-id"
                                  select="sch:task/sch:parameter[@name='id']/@value"/>
                            </xsl:call-template>
                          </xsl:otherwise>
                        </xsl:choose>
		      </td>
                      <xsl:choose>
                        <xsl:when test="sch:trigger">
                          <xsl:apply-templates select="sch:trigger"/>
                          <td>
                            <xsl:if test="$documentUri != ''">
                              <input type="submit" name="Action" value="Modify"/>
                            </xsl:if>
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
  
  <xsl:template match="sch:trigger">
    <xsl:choose>
    <xsl:when test="$documentUri != ''">
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
    </xsl:when>
    <xsl:otherwise>
    <td>
      <xsl:value-of select="format-number(sch:parameter[@name='day']/@value, '00')"/>.
      <xsl:value-of select="format-number(sch:parameter[@name='month']/@value, '00')"/>.
      <xsl:value-of select="format-number(sch:parameter[@name='year']/@value, '00')"/>
    </td>
    <td>
      <xsl:value-of select="format-number(sch:parameter[@name='hour']/@value, '00')"/>:<xsl:text/>
      <xsl:value-of select="format-number(sch:parameter[@name='minute']/@value, '00')"/>
    </td>
    </xsl:otherwise>
    </xsl:choose>
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
  <xsl:template match="sch:parameter[@name='documentUri']">
    <xsl:if test="not($documentUri)">
      <a href="{$uri-prefix}{@value}"><xsl:value-of select="@value"/></a>
    </xsl:if>
  </xsl:template>

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
