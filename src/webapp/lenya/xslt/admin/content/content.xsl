<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!-- $Id: content.xsl,v 1.6 2004/03/13 12:42:10 gregor Exp $ -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:session="http://www.apache.org/xsp/session/2.0"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:output version="1.0" indent="yes" encoding="UTF-8"/>
  
  <xsl:variable name="task-id"><xsl:value-of select="/page/info/task-id"/></xsl:variable>
  <xsl:variable name="request-uri"><xsl:value-of select="/page/info/request-uri"/></xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="page">
    <page:page>
      <page:title><i18n:text>Delete Trash</i18n:text></page:title>
      <page:body>
        <xsl:apply-templates select="body"/>
	    <xsl:apply-templates select="info"/>
      </page:body>
    </page:page>
  </xsl:template>

  <xsl:template match="info">
    <div class="lenya-box">
      <div class="lenya-box-title"><i18n:text>Do you really want to delete the trash ?</i18n:text></div>
      <div class="lenya-box-body">
        <form method="get" action="index.html">
          <input type="hidden" name="lenya.usecase" value="deleteTrash"/>
          <input type="hidden" name="lenya.step" value="deleteTrash"/>
          <input type="hidden" name="task-id" value="{$task-id}"/>          
          <input i18n:attr="value" type="submit" value="Yes"/>
          &#160;
          <input i18n:attr="value" type="button" onClick="location.href='{$request-uri}/../';" value="No"/>
        </form>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
  
