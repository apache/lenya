<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://www.lenya.org/2002/sch" version="1.0">
  
  <xsl:import href="../util/page-util.xsl"/>
  <xsl:import href="util.xsl"/>

  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:param name="publication-id" select="''"/>
  <xsl:param name="documentUri" select="''"/>
  <xsl:param name="documentType" select="''"/>

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
        <a href="{$uri-prefix}/authoring/index.html">
          <strong>To Frontpage</strong>
        </a>
        <xsl:value-of select="$menu-separator"/>
        <a>
          <xsl:attribute name="href">
            <xsl:text/>
            <xsl:value-of select="$uri-prefix"/>
            <xsl:text>/scheduler/pubid/</xsl:text>
            <xsl:value-of select="$publication-id"/>
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
	<title>Scheduler Administrator</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
        <xsl:call-template name="include-css">
          <xsl:with-param name="context-prefix" select="$uri-prefix"/>
        </xsl:call-template>
      </head>
      
      <body>
<!--
<p>
      DEBUG: Current Time: <xsl:value-of select="/sch:scheduler/sch:current-date/sch:day"/><br />
      DEBUG: Current Time: <xsl:value-of select="/sch:scheduler/sch:current-date/sch:second"/>
</p>
-->
        <xsl:apply-templates select="sch:exception"/>
        <h1>Scheduler Administrator</h1>
        <xsl:call-template name="navigation-menu"/>

        <p>
          <strong>Publication:</strong>&#160;&#160;<xsl:value-of select="$publication-id"/>
        </p>
        <br />
        
        
	  <table width="100%" height="3" border="0" cellpadding="0" cellspacing="0">
        
            <tr>
              <td class="table-head">
                <div class="menu">Document</div>
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
			<xsl:value-of select="$uri-prefix"/><xsl:text>/scheduler/pubid/</xsl:text><xsl:value-of select="$publication-id"/>
		      </xsl:attribute>
                      <!-- hidden input fields for parameters -->
                      <xsl:call-template name="parameters-as-inputs"/>
		      <td>
			<xsl:apply-templates select="sch:parameter"/>
                      </td>
		      <td>
                        <xsl:variable name="task-id" select="sch:task/sch:parameter[@name='id']/@value"/>
                        <strong><xsl:value-of select="/sch:scheduler/sch:tasks/sch:task[@id = $task-id]/label"/></strong>
		      </td>
                      <xsl:choose>
                        <xsl:when test="sch:trigger">
                          <xsl:apply-templates select="sch:trigger"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <td colspan="2">
                            <p>The job date has expired.</p>
                          </td>
                        </xsl:otherwise>
                      </xsl:choose>
                      <td>&#160;</td>
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
      <xsl:value-of select="format-number(sch:parameter[@name='day']/@value, '00')"/>.
      <xsl:value-of select="format-number(sch:parameter[@name='month']/@value, '00')"/>.
      <xsl:value-of select="format-number(sch:parameter[@name='year']/@value, '00')"/>
    </td>
    <td>
      <xsl:value-of select="format-number(sch:parameter[@name='hour']/@value, '00')"/>:<xsl:text/>
      <xsl:value-of select="format-number(sch:parameter[@name='minute']/@value, '00')"/>
    </td>
  </xsl:template>
  
  <!-- document URI -->
  <xsl:template match="sch:parameter[@name='documentUri']">
    <a href="{$uri-prefix}/authoring/{@value}"><xsl:value-of select="@value"/></a>
  </xsl:template>

  <!-- job id -->  
  <xsl:template match="sch:parameter[@name='id']">
    <input type="hidden" name="job.id" value="{@value}"/>
  </xsl:template>

<xsl:template match="sch:exception">
<font color="red">EXCEPTION: <xsl:value-of select="@type"/></font> (check the log files)
</xsl:template>

</xsl:stylesheet>  
