<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://www.lenya.org/2002/sch" version="1.0">
  
  <xsl:import href="../util/page-util.xsl"/>
  <xsl:import href="util.xsl"/>

  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:variable name="separator" select="','"/>
  <xsl:variable name="params" select="/sch:scheduler/sch:parameters"/>
  
  <!-- This is a workaround until the final Scheduler interface is specified. -->
  <!--<xsl:variable name="documentUri" select="$params/sch:parameter[@name='task.uris']/@value"/>-->
  <xsl:variable name="documentUri" select="$params/sch:parameter[@name='documentUri']/@value"/>
  
  <xsl:variable name="documentType" select="$params/sch:parameter[@name='documentType']/@value"/>
  <xsl:variable name="context-prefix" select="$params/sch:parameter[@name='context-prefix']/@value"/>
  <xsl:variable name="publication-id" select="$params/sch:parameter[@name='publication-id']/@value"/>
  <xsl:variable name="uri-prefix" select="concat($context-prefix, '/', $publication-id)"/>
  
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
            <xsl:text/>admin-document?<xsl:call-template name="parameters-as-request-parameters"/>
            <xsl:text/>
          </xsl:attribute>
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
              <td class="table-head"><div class="menu">Edit existing jobs</div></td>
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
		    <form method="GET">
                    
		      <td>
                        <!-- hidden input fields for parameters -->
                        <xsl:call-template name="parameters-as-inputs"/>
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
