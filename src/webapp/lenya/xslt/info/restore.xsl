<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id$ -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:import href="../util/waitScreen.xsl"/>
  
  <xsl:output version="1.0" indent="yes" encoding="ISO-8859-1"/>
  
  <xsl:param name="lenya.event"/>
  
  <xsl:variable name="contextprefix"><xsl:value-of select="/page/info/context-prefix"/></xsl:variable>
  <xsl:variable name="document-id"><xsl:value-of select="/page/info/document-id"/></xsl:variable>
  <xsl:variable name="dest-document-id"><xsl:value-of select="/page/info/dest-document-id"/></xsl:variable>
  <xsl:variable name="area"><xsl:value-of select="/page/info/area"/></xsl:variable>
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <xsl:call-template name="wait_script"/>   
      <page:title>Restore Document</page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
        <xsl:call-template name="wait_screen"/>   
      </page:body>
    </page:page>
  </xsl:template>

  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title">Restore Document</div>
      <div class="lenya-box-body">
      <xsl:choose>
		<xsl:when test="exception">		
          <p>
           The document <xsl:value-of select="document-id"/> cannot be restored because <xsl:value-of select="exception"/>
          </p> 
          <input type="button" class="lenya-form-element" onClick="location.href='{$request-uri}';" value="Back" name="Back"/>
      	</xsl:when>
      	<xsl:otherwise>
        <form method="get" name="restore-form">
          <xsl:attribute name="action"></xsl:attribute>
          <input type="hidden" name="lenya.usecase" value="restore"/>
          <input type="hidden" name="lenya.step" value="restore"/>
          <input type="hidden" name="lenya.event" value="{$lenya.event}"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>
          <xsl:call-template name="task-parameters">
            <xsl:with-param name="prefix" select="''"/>
          </xsl:call-template>
          <p>
 	      Do you really want to restore <xsl:value-of select="document-id"/>?
          </p>
          <input type="submit" value="Restore" onclick="wait()" name="Restore"/>&#160;
          <input type="button" onClick="location.href='{$request-uri}';" value="Cancel" name="Cancel"/>
        </form>
    	</xsl:otherwise>
	  </xsl:choose>	
      </div>
    </div>
  </xsl:template>
  
  <xsl:template name="task-parameters">
    <xsl:param name="prefix" select="'task.'"/>
    <input type="hidden" name="{$prefix}properties.node.firstdocumentid" value="{$document-id}"/>
    <input type="hidden" name="{$prefix}properties.node.secdocumentid" value="{$dest-document-id}"/>
    <input type="hidden" name="{$prefix}properties.firstarea" value="{$area}"/>
    <input type="hidden" name="{$prefix}properties.secarea" value="authoring"/>
  </xsl:template>

</xsl:stylesheet>
  
