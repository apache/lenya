<?xml version="1.0" encoding="UTF-8"?>
<!--
 $Id: deactivate.xsl,v 1.16 2004/01/23 08:44:12 andreas Exp $
 -->
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
  xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:session="http://www.apache.org/xsp/session/2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
  >
  
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  <xsl:variable name="document-id">
    <xsl:value-of select="/page/info/document-id"/>
  </xsl:variable>
  
  <xsl:variable name="language">
    <xsl:value-of select="/page/info/document-language"/>
  </xsl:variable>

  <xsl:variable name="task-id">
    <xsl:value-of select="/page/info/task-id"/>
  </xsl:variable>
  
  <xsl:variable name="request-uri">
    <xsl:value-of select="/page/info/request-uri"/>
  </xsl:variable>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="page">
    <page:page>
      <page:title>Deactivate Document</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
        <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>
  
  
  <xsl:template match="info[not(message)]">
    <form method="get">
      <div class="lenya-box">
        <div class="lenya-box-title">Deactivate Document</div>
        <div class="lenya-box-body">
          
          <input type="hidden" name="lenya.step" value="deactivate"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          <xsl:call-template name="task-parameters"/>
          
          <table class="lenya-table-noborder">
            <tr>
              <td class="lenya-entry-caption">Document:</td>
              <td><xsl:value-of select="$document-id"/> [<xsl:value-of select="$language"/>]</td>
            </tr>
            <tr>
	      <xsl:apply-templates select="inconsistent-documents"/>
            </tr>
            <tr>
              <td/>
              <td>
                <input type="submit" name="lenya.usecase" value="deactivate"/> &#160;
                <input onClick="location.href='{$request-uri}';" type="button" value="Cancel"/>
              </td>
            </tr>
          </table>
        </div>
      </div>
      
      <!--
      <not:notification>
        <not:textarea/>
      </not:notification>
      -->
    </form>
      
    <!--
    <sch:scheduler-form>
      <sch:job tasklabel="Deactivate">
        <input type="hidden" name="wrapper.task-id" value="deactivateDocument"/>
        <xsl:call-template name="task-parameters"/>
      </sch:job>
    </sch:scheduler-form>
    
    <form action="{$request-uri}"><input type="submit" value="Back to Page"/></form>
    -->
      
  </xsl:template>
  
  
  <xsl:template match="info[message]">
    <form method="get">
      <div class="lenya-box">
        <div class="lenya-box-title">Deactivate Document</div>
        <div class="lenya-box-body">
          <table class="lenya-table-noborder">
            <tr>
              <td class="lenya-entry-caption">Document:</td>
              <td><xsl:value-of select="$document-id"/> [<xsl:value-of select="$language"/>]</td>
            </tr>
            <tr>
              <td valign="top" class="lenya-entry-caption">Problem:</td>
              <td>
                <span class="lenya-form-error">This document cannot be deactivated
                unless the following child documents are deactivated:</span>
                <ul>
                  <xsl:apply-templates select="live-child"/>
                </ul>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <input onClick="location.href='{$request-uri}';" type="button" value="Cancel"/>
    </form>
  </xsl:template>
  
  
  <xsl:template match="live-child">
    <li><a href="{@href}"><xsl:value-of select="@id"/> [<xsl:value-of select="@language"/>]</a></li>
  </xsl:template>
  
  <xsl:template match="inconsistent-documents">
    <td class="lenya-entry-caption">
      <span class="lenya-form-message-error">The following documents<br/> have links to this document:</span>
    </td>
    <td valign="top">
      <xsl:for-each select="inconsistent-document">
	<a target="_blank" href="{@href}"><xsl:value-of select="@id"/><xsl:value-of select="."/></a><br/>
      </xsl:for-each>
    </td>
  </xsl:template>
  
  <xsl:template name="task-parameters">
    <input type="hidden" name="document-id" value="{$document-id}"/>
    <input type="hidden" name="document-language" value="{$language}"/>
    <input type="hidden" name="workflow-event" value="{/page/info/workflow-event}"/>
    <input type="hidden" name="user-id" value="{/page/info/user-id}"/>
    <input type="hidden" name="ip-address" value="{/page/info/ip-address}"/>
    <input type="hidden" name="role-ids" value="{/page/info/role-ids}"/>
  </xsl:template>
  
</xsl:stylesheet>
