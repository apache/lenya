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

<!-- $Id: change-visibility.xsl 42703 2004-09-22 12:57:53Z jaf $ -->

 <xsl:stylesheet version="1.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
   xmlns:page="http://apache.org/cocoon/lenya/cms-page/1.0"
   >
  
  <xsl:param name="requesturi"/>
  <xsl:param name="area"/>
  <xsl:param name="documentid"/>
  <xsl:param name="taskid"/>
  <xsl:param name="lenya.event"/>

  <xsl:template match="/">
    <page:page>
      <page:title><i18n:text>Change document visibility in navigation</i18n:text></page:title>
      <page:body>
	<div class="lenya-box">
	  <div class="lenya-box-title"><i18n:text>Change document visibility in navigation</i18n:text></div>
	  <div class="lenya-box-body">
	    <form method="get" name="change-visibility-form">
	      <xsl:attribute name="action"></xsl:attribute>
	      <input type="hidden" name="task-id" value="{$taskid}"/>
	      <input type="hidden" name="properties.change.visibility.document-id" value="{$documentid}"/>
	      <input type="hidden" name="properties.change.visibility.area" value="{$area}"/>
	      <input type="hidden" name="lenya.usecase" value="change-visibility"/>
	      <input type="hidden" name="lenya.step" value="change-visibility"/>
	      <input type="hidden" name="lenya.event" value="edit"/>
	      
	      <table class="lenya-table-noborder">
		<tr>
		  <td/>
		  <td>
		    <br/>
		    <input i18n:attr="value" type="submit" value="Change" name="Change"/>&#160;
		    <input i18n:attr="value" type="button" onClick="location.href='{$requesturi}';" value="Cancel" name="Cancel"/>
		  </td>
		</tr>
	      </table>
	    </form>
	  </div>
	</div>
      </page:body>
    </page:page>
  </xsl:template>
  
</xsl:stylesheet>