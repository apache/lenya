<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"      
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:usecase="http://apache.org/cocoon/lenya/usecase/1.0"
  xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:import href="../util/page-util.xsl"/>

  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>

  <xsl:param name="action" select="'publish'"/>
  <xsl:param name="lenya.event"/>

  <xsl:variable name="separator" select="','"/>

  <xsl:variable name="uris"><xsl:value-of select="/usecase:publish/usecase:uris"/></xsl:variable>
  <xsl:variable name="sources"><xsl:value-of select="/usecase:publish/usecase:sources"/></xsl:variable>
  <xsl:variable name="document-id"><xsl:value-of select="/usecase:publish/usecase:document-id"/></xsl:variable>
  <xsl:variable name="document-language"><xsl:value-of select="/usecase:publish/usecase:language"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/usecase:publish/usecase:task-id"/></xsl:variable>
  <xsl:variable name="referer"><xsl:value-of select="/usecase:publish/usecase:referer"/></xsl:variable>


  <xsl:template match="/usecase:publish[usecase:message]">
    <page:page>
      <page:title><i18n:text>Publish</i18n:text></page:title>
      <page:body>
          <div class="lenya-box">
            <div class="lenya-box-title"><i18n:text>Publish</i18n:text></div>
            <div class="lenya-box-body">
              <table class="lenya-table-noborder">
                <tr>
                  <td class="lenya-entry-caption" valign="top"><i18n:text>Source File(s)</i18n:text>:</td>
                  <td valign="top">
                    <xsl:call-template name="print-list-simple">
                      <xsl:with-param name="list-string" select="$sources"/>
                    </xsl:call-template>
                  </td>
                </tr>
                <tr>
                  <td class="lenya-entry-caption" valign="top">URI(s):</td>
                  <td valign="top">
                    <xsl:call-template name="print-list-simple">
                      <xsl:with-param name="list-string" select="$uris"/>
                    </xsl:call-template>
                  </td>
                </tr>
                <tr>
                  <td valign="top" class="lenya-entry-caption">Problem:</td>
                  <td>
                    <span class="lenya-form-error">This page cannot be published unless its parent is
                      published:</span>
                    <ul>
                      <li><xsl:apply-templates select="usecase:parent"/></li>
                    </ul>
                  </td>
                </tr>
                <tr>
                  <td/>
                  <td>
                    <input type="button" onClick="location.href='{$referer}';" value="Cancel"/>
                  </td>
                </tr>
              </table>
              
            </div>
          </div>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="usecase:parent">
    <a href="{@href}"><xsl:value-of select="@id"/> [<xsl:value-of select="@language"/>]</a>
  </xsl:template>
        
        
  <xsl:template match="/usecase:publish[not(usecase:message)]">

    <page:page>
      <page:title><i18n:text>Publish</i18n:text></page:title>
      <page:body>
        
        <table class="lenya-table-noborder">
        <tr>
        <td>
        
        <form name="form_publish">
          <input type="hidden" name="lenya.usecase" value="publish"/>          
          <input type="hidden" name="lenya.step" value="publish"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          
          <input type="hidden" name="document-id" value="{$document-id}"/>
          <input type="hidden" name="document-language" value="{$document-language}"/>
          <input type="hidden" name="user-id" value="{/usecase:publish/usecase:user-id}"/>
          <input type="hidden" name="ip-address" value="{/usecase:publish/usecase:ip-address}"/>
          <input type="hidden" name="role-ids" value="{/usecase:publish/usecase:role-ids}"/>
          <input type="hidden" name="workflow-event" value="publish"/>

          <!-- FIXME: The ant taks and the regular task don't use the same parameter names. Another difference is that the regular task requires a leading slash (also compare blog and oscom publication) -->
          <input type="hidden" name="properties.publish.sources" value="{$sources}"/> <!-- Ant Task -->
          <input type="hidden" name="sources" value="{$sources}"/> <!-- Regular Task -->
          <input type="hidden" name="properties.export.uris" value="{$uris}"/> <!-- Ant Task -->
          <input type="hidden" name="uris" value="{$uris}"/> <!-- Regular Task -->
          
          <div class="lenya-box">
            <div class="lenya-box-title"><i18n:text>Publish</i18n:text></div>
            <div class="lenya-box-body">
              <table class="lenya-table-noborder">
                <tr>
                  <td class="lenya-entry-caption" valign="top"><i18n:text>Source File(s)</i18n:text>:</td>
                  <td valign="top">
                    <xsl:call-template name="print-list-simple">
                      <xsl:with-param name="list-string" select="$sources"/>
                    </xsl:call-template>
                  </td>
                </tr>
                <tr>
                  <td class="lenya-entry-caption" valign="top">URI(s):</td>
                  <td valign="top">
                    <xsl:call-template name="print-list-simple">
                      <xsl:with-param name="list-string" select="$uris"/>
                    </xsl:call-template>
                  </td>
                </tr>
                <tr>
	          <xsl:apply-templates select="referenced-documents"/>
                </tr>
                <tr>
                  <td/>
                  <td>
                    <input i18n:attr="value" type="submit" value="Publish"/>
                    <xsl:text> </xsl:text>
                    <input i18n:attr="value" type="button" onClick="location.href='{$referer}';" value="Cancel"/>
                  </td>
                </tr>
              </table>
              
            </div>
          </div>

          <not:notification>
            <not:preset>
              <xsl:apply-templates select="not:users/not:user"/>
            </not:preset>
            <not:textarea/>
          </not:notification>
          
        </form>


        <!--
        <sch:scheduler-form>
          
          <sch:job tasklabel="Publish">
            <input type="hidden" name="properties.publish.sources" value="{$sources}"/>
            <input type="hidden" name="properties.publish.documentid" value="{$document-id}"/>
            <input type="hidden" name="properties.publish.language" value="{$document-language}"/>
            <input type="hidden" name="properties.export.uris" value="{$uris}"/>
            <input type="hidden" name="workflow.event" value="publish"/>
            <input type="hidden" name="wrapper.task-id" value="publish"/>
          </sch:job>
          
          <sch:job tasklabel="Deactivate">
            <input type="hidden" name="properties.node.firstdocumentid" value="{$document-id}"/>
            <input type="hidden" name="properties.node.language" value="{$document-language}"/>
            <input type="hidden" name="workflow.event" value="deactivate"/>
            <input type="hidden" name="wrapper.task-id" value="deactivateDocument"/>
          </sch:job>
          
        </sch:scheduler-form>
        -->
        
        <div style="text-align: right">
        <form action="{$referer}"><input i18n:attr="value" type="submit" value="Back to Page"/></form>
        </div>
        </td>
        </tr>
        </table>
          
      </page:body>
    </page:page>
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
