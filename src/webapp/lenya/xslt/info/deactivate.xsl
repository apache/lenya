<?xml version="1.0" encoding="UTF-8"?>
<!--
 $Id: deactivate.xsl,v 1.10 2003/09/19 13:10:58 andreas Exp $
 -->
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
  xmlns:not="http://apache.org/cocoon/lenya/notification/1.0"
  xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
  xmlns:session="http://www.apache.org/xsp/session/2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:sch="http://apache.org/cocoon/lenya/scheduler/1.0"
  >
  
  <xsl:param name="lenya.event"/>
  
  <xsl:output encoding="ISO-8859-1" indent="yes" version="1.0"/>
  
  <xsl:variable name="document-id">
    <xsl:value-of select="/page/info/document-id"/>
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
          
          <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
          <input type="hidden" name="lenya.step" value="deactivate"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          
          <input type="hidden" name="properties.node.firstdocumentid" value="{$document-id}"/>
          
          <table class="lenya-table-noborder">
            <tr>
              <td class="lenya-entry-caption">Document:</td>
              <td><xsl:value-of select="$document-id"/></td>
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
      
      <not:notification>
        <not:textarea/>
      </not:notification>
      
      <sch:scheduler-form/>
      
    </form>
  </xsl:template>
  
  
  <xsl:template match="info[message]">
    <form method="get">
      <div class="lenya-box">
        <div class="lenya-box-title">Deactivate Document</div>
        <div class="lenya-box-body">
          <table class="lenya-table-noborder">
            <tr>
              <td class="lenya-entry-caption">Document:</td>
              <td><xsl:value-of select="$document-id"/></td>
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
  
  
</xsl:stylesheet>
