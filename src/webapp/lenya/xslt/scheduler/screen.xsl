<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:import href="../../../../../xslt/util/page-util.xsl"/>

  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>

  <xsl:variable name="separator" select="','"/>

  <xsl:variable name="referer"><xsl:value-of select="/usecase:schedule/usecase:referer"/></xsl:variable>


  <xsl:template match="/usecase:schedule">

    <page:page>
      <page:title>Schedule</page:title>
      <page:body>
        
        <table class="lenya-table-noborder">
        <tr>
        <td>
        
          
        <not:notification>
          <not:preset>
            <xsl:apply-templates select="not:users/not:user"/>
          </not:preset>
          <not:textarea/>
        </not:notification>
          
        <sch:scheduler-form>
          
          <sch:job tasklabel="Publish">
            <input type="hidden" name="wrapper.task-id" value="publish"/>
            <input type="hidden" name="workflow-event" value="publish"/>
            <xsl:call-template name="task-parameters"/>
          </sch:job>
          
          <sch:job tasklabel="Deactivate">
            <input type="hidden" name="wrapper.task-id" value="deactivateDocument"/>
            <input type="hidden" name="workflow-event" value="deactivate"/>
            <xsl:call-template name="task-parameters"/>
          </sch:job>
          
        </sch:scheduler-form>
        
        <div style="text-align: right">
        <form action="{$referer}"><input type="submit" value="Back to Page"/></form>
        </div>
        </td>
        </tr>
        </table>
          
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template name="task-parameters">
    <input type="hidden" name="document-id" value="{/usecase:schedule/usecase:document-id}"/>
    <input type="hidden" name="document-language" value="{/usecase:schedule/usecase:document-language}"/>
    <input type="hidden" name="user-id" value="{/usecase:schedule/usecase:user-id}"/>
    <input type="hidden" name="ip-address" value="{/usecase:schedule/usecase:ip-address}"/>
    <input type="hidden" name="role-ids" value="{/usecase:schedule/usecase:role-ids}"/>
  </xsl:template>
  

  <xsl:template match="referenced-documents">
    <td class="lenya-entry-caption" valign="top">
      <span class="lenya-form-message-error">This document has links to the <br/>following unpublished documents:</span>
    </td>
    <td valign="top">
      <xsl:for-each select="referenced-document">
        <a target="_blank" href="{@href}"><xsl:value-of select="@id"/><xsl:value-of select="."/></a><br/>
      </xsl:for-each>
    </td>
  </xsl:template>
  

</xsl:stylesheet>  
